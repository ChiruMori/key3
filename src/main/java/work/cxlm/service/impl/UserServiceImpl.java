package work.cxlm.service.impl;

import cn.hutool.core.util.RandomUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import work.cxlm.cache.AbstractStringCacheStore;
import work.cxlm.config.QfzsProperties;
import work.cxlm.exception.*;
import work.cxlm.model.entity.Joining;
import work.cxlm.model.entity.User;
import work.cxlm.model.params.UserParam;
import work.cxlm.model.support.CreateCheck;
import work.cxlm.model.support.UpdateCheck;
import work.cxlm.rpc.RpcClient;
import work.cxlm.rpc.code2session.Code2SessionParam;
import work.cxlm.rpc.code2session.Code2SessionResponse;
import work.cxlm.model.support.QfzsConst;
import work.cxlm.model.vo.PageUserVO;
import work.cxlm.model.vo.PasscodeVO;
import work.cxlm.repository.UserRepository;
import work.cxlm.security.context.SecurityContextHolder;
import work.cxlm.security.token.AuthToken;
import work.cxlm.security.util.SecurityUtils;
import work.cxlm.service.JoiningService;
import work.cxlm.service.UserService;
import work.cxlm.service.base.AbstractCrudService;
import work.cxlm.utils.*;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static work.cxlm.service.AdminService.ACCESS_TOKEN_EXPIRED_SECONDS;
import static work.cxlm.service.AdminService.REFRESH_TOKEN_EXPIRED_DAYS;

/**
 * created 2020/10/22 16:00
 *
 * @author ryanwang
 * @author johnniang
 * @author cxlm
 */
@Service
@Slf4j
public class UserServiceImpl extends AbstractCrudService<User, Integer> implements UserService {

    private final UserRepository userRepository;
    private final QfzsProperties qfzsProperties;
    private final ApplicationEventPublisher eventPublisher;
    private final AbstractStringCacheStore cacheStore;
    private final JoiningService joiningService;

    public UserServiceImpl(UserRepository userRepository,
                           ApplicationEventPublisher eventPublisher,
                           QfzsProperties qfzsProperties,
                           AbstractStringCacheStore cacheStore,
                           JoiningService joiningService) {
        super(userRepository);
        this.userRepository = userRepository;
        this.eventPublisher = eventPublisher;
        this.qfzsProperties = qfzsProperties;
        this.cacheStore = cacheStore;
        this.joiningService = joiningService;
    }

    @Override
    public String getOpenIdByCode(@NonNull String code) {
        Code2SessionParam param = new Code2SessionParam(qfzsProperties.getAppId(), qfzsProperties.getAppSecret(),
                code, "authorization_code");
        Code2SessionResponse response = RpcClient.getUrl(qfzsProperties.getAppRequestUrl(), Code2SessionResponse.class, param);
        if (response.getOpenid() == null) {
            throw new ServiceException("错误的响应").setErrorData(response);
        }
        return response.getOpenid();
    }

    @Override
    public AuthToken login(@Nullable String openId) {
        if (openId == null) {
            log.debug("openId 为空，无法登录");
            return null;
        }

        final User nowUser = userRepository.findByWxId(openId).orElseThrow(
                () -> new BadRequestException("查询不到您的信息，请您完善信息后使用").setErrorData(openId));

        if (SecurityContextHolder.getContext().isAuthenticated()) {
            throw new BadRequestException("您已登录，无需重复登录");
        }
        log.info("[{}]-[{}] 登录系统", nowUser.getRealName(), ServletUtils.getRequestIp());
        return buildAuthToken(nowUser);
    }

    private AuthToken buildAuthToken(@NonNull User user) {
        Assert.notNull(user, "User must not be null");

        // Generate new token
        AuthToken token = new AuthToken();

        token.setAccessToken(QfzsUtils.randomUUIDWithoutDash());
        token.setExpiredIn(ACCESS_TOKEN_EXPIRED_SECONDS);
        token.setRefreshToken(QfzsUtils.randomUUIDWithoutDash());

        // Cache those tokens, just for clearing
        cacheStore.putAny(SecurityUtils.buildAccessTokenKey(user), token.getAccessToken(), ACCESS_TOKEN_EXPIRED_SECONDS, TimeUnit.SECONDS);
        cacheStore.putAny(SecurityUtils.buildRefreshTokenKey(user), token.getRefreshToken(), REFRESH_TOKEN_EXPIRED_DAYS, TimeUnit.DAYS);

        // Cache those tokens with open id
        cacheStore.putAny(SecurityUtils.buildAccessTokenKey(token.getAccessToken()), user.getWxId(), ACCESS_TOKEN_EXPIRED_SECONDS, TimeUnit.SECONDS);
        cacheStore.putAny(SecurityUtils.buildRefreshTokenKey(token.getRefreshToken()), user.getWxId(), REFRESH_TOKEN_EXPIRED_DAYS, TimeUnit.DAYS);

        return token;
    }


    @Override
    @Nullable
    public User updateUserByParam(@NonNull UserParam param) {
        User currentUser = SecurityContextHolder.getCurrentUser().orElse(null);
        if (currentUser == null) { // 缓存中没有用户信息
            ValidationUtils.validate(param, CreateCheck.class);
            currentUser = userRepository.findByStudentNo(param.getStudentNo()).orElse(null);
            if (currentUser == null) { // 数据库中也没有用户信息
                throw new NotFoundException("无效的学号，请联系管理员授权后使用");
            }
        } else {
            ValidationUtils.validate(param, UpdateCheck.class);
        }
        param.update(currentUser);
        currentUser = userRepository.save(currentUser);
        log.info("用户 [{}]-[{}] 更新（完善）了信息", currentUser.getRealName(), ServletUtils.getRequestIp());
        return currentUser;
    }

    @Override
    @NonNull
    public Page<PageUserVO> getClubUserPage(Integer clubId, Pageable pageable) {
        User me = SecurityContextHolder.getCurrentUser().orElseThrow(
                () -> new AuthenticationException("用户登录凭证无效"));
        Joining my = joiningService.getJoiningById(me.getId(), clubId).orElseThrow(() -> new BadRequestException("您不属于该社团，无权查看"));
        Page<Joining> allJoining = joiningService.pageAllJoiningByClubId(clubId, pageable);
        return convertJoiningToUserPage(allJoining, my, pageable);
    }

    private Page<PageUserVO> convertJoiningToUserPage(Page<Joining> joiningPage, @Nullable Joining my, Pageable pageable) {
        List<Integer> uids = joiningPage.stream().map(join -> join.getId().getUserId()).collect(Collectors.toList());
        List<User> joinUsers = userRepository.findAllByIdIn(uids, Sort.unsorted());
        Map<Integer, User> uidUserMap = ServiceUtils.convertToMap(joinUsers, User::getId);

        LinkedList<PageUserVO> sortedUserVoList = new LinkedList<>();
        joiningPage.stream().forEach(joining -> {
            // 映射为 PageUserVO 并将自己置顶
            User nowUser = uidUserMap.get(joining.getId().getUserId());
            PageUserVO nowVO = new PageUserVO().convertFrom(nowUser);
            BeanUtils.updateProperties(joining, nowVO);
            if (my != null && Objects.equals(my.getId().getUserId(), nowUser.getId())) {
                sortedUserVoList.addFirst(nowVO);
            } else {
                sortedUserVoList.addLast(nowVO);
            }
        });
        return new PageImpl<>(sortedUserVoList, pageable, joiningPage.getTotalElements());
    }

    @Override
    @NonNull
    public PasscodeVO getPasscode() {
        User currentUser = SecurityContextHolder.getCurrentUser().orElseThrow(
                () -> new AuthenticationException("用户登录凭证无效"));
        if (!currentUser.getRole().isAdminRole()) {
            throw new ForbiddenException("权限不足，拒绝访问");
        }
        String nowPasscode = RandomUtil.randomString(6);
        String passcodeCacheKey = QfzsConst.ADMIN_PASSCODE_PREFIX + currentUser.getId();
        cacheStore.put(passcodeCacheKey, nowPasscode, 5, TimeUnit.MINUTES);
        PasscodeVO passcodeVO = new PasscodeVO();
        passcodeVO.setPasscode(nowPasscode);
        return passcodeVO;
    }

    @Override
    public AuthToken refreshToken(String refreshToken) {
        Assert.hasText(refreshToken, "Refresh token 不能为空");

        String openId = cacheStore.getAny(SecurityUtils.buildRefreshTokenKey(refreshToken), String.class)
                .orElseThrow(() -> new BadRequestException("登录状态已失效，请重新登录").setErrorData(refreshToken));

        // 获取用户信息
        User user = userRepository.findByWxId(openId).orElseThrow(() -> new BadRequestException("用户不存在"));

        // 清除原 Token
        cacheStore.getAny(SecurityUtils.buildAccessTokenKey(user), String.class)
                .ifPresent(accessToken -> cacheStore.delete(SecurityUtils.buildAccessTokenKey(accessToken)));
        cacheStore.delete(SecurityUtils.buildRefreshTokenKey(refreshToken));
        cacheStore.delete(SecurityUtils.buildAccessTokenKey(user));
        cacheStore.delete(SecurityUtils.buildRefreshTokenKey(user));
        // 建立新的 Token
        return buildAuthToken(user);
    }

    @Override
    public User getByOpenId(String openId) {
        return userRepository.findByWxId(openId).orElseThrow(() -> new BadRequestException("非法的 openId"));
    }
}
