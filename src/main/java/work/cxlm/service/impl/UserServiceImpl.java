package work.cxlm.service.impl;

import cn.hutool.core.util.RandomUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import work.cxlm.cache.AbstractStringCacheStore;
import work.cxlm.config.QfzsProperties;
import work.cxlm.event.LogEvent;
import work.cxlm.exception.*;
import work.cxlm.model.entity.Club;
import work.cxlm.model.entity.Joining;
import work.cxlm.model.entity.User;
import work.cxlm.model.entity.id.JoiningId;
import work.cxlm.model.enums.LogType;
import work.cxlm.model.enums.UserRole;
import work.cxlm.model.params.LogParam;
import work.cxlm.model.params.UserLoginParam;
import work.cxlm.model.params.UserParam;
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
import work.cxlm.service.ClubService;
import work.cxlm.service.JoiningService;
import work.cxlm.service.UserService;
import work.cxlm.service.base.AbstractCacheCrudService;
import work.cxlm.utils.*;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
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
public class UserServiceImpl extends AbstractCacheCrudService<User, Integer> implements UserService {

    private JoiningService joiningService;
    private ClubService clubService;

    private final UserRepository userRepository;
    private final QfzsProperties qfzsProperties;
    private final ApplicationEventPublisher eventPublisher;
    private final AbstractStringCacheStore cacheStore;


    public UserServiceImpl(UserRepository userRepository,
                           ApplicationEventPublisher eventPublisher,
                           QfzsProperties qfzsProperties,
                           AbstractStringCacheStore cacheStore) {
        super(userRepository, cacheStore, User::getId, User.class, "user.map.cache");
        this.userRepository = userRepository;
        this.eventPublisher = eventPublisher;
        this.qfzsProperties = qfzsProperties;
        this.cacheStore = cacheStore;
    }

    @Autowired
    public void setJoiningService(JoiningService joiningService) {
        this.joiningService = joiningService;
    }

    @Autowired
    public void setClubService(ClubService clubService) {
        this.clubService = clubService;
    }

    @Override
    public String getOpenIdBy(@NonNull UserLoginParam loginParam) {
        if (!StringUtils.isEmpty(loginParam.getWxId())) {
            return loginParam.getWxId();
        }
        if (StringUtils.isEmpty(loginParam.getCode())) {
            throw new MissingPropertyException("必须指定 code 或 open Id 以完成登录");
        }
        Code2SessionParam param = new Code2SessionParam(qfzsProperties.getAppId(), qfzsProperties.getAppSecret(),
                loginParam.getCode(), "authorization_code");
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
        eventPublisher.publishEvent(new LogEvent(this, new LogParam(nowUser.getId(), LogType.MINI_LOGGED_IN, "用户登录小程序")));
        return buildAuthToken(nowUser, StringUtils.EMPTY, User::getWxId);
    }

    @Override
    public <T> AuthToken buildAuthToken(@NonNull User user, String keyPrefix, Function<User, T> converter) {
        // 清除原 Token 确保不会生成冗余的 Token
        clearUserToken(user, keyPrefix);

        // Generate new token
        AuthToken token = new AuthToken();

        token.setAccessToken(QfzsUtils.randomUuidWithoutDash());
        token.setExpiredIn(ACCESS_TOKEN_EXPIRED_SECONDS);
        token.setRefreshToken(QfzsUtils.randomUuidWithoutDash());

        // Cache those tokens, just for clearing
        cacheStore.putAny(SecurityUtils.buildAccessTokenKey(user, keyPrefix), token.getAccessToken(), ACCESS_TOKEN_EXPIRED_SECONDS, TimeUnit.SECONDS);
        cacheStore.putAny(SecurityUtils.buildRefreshTokenKey(user, keyPrefix), token.getRefreshToken(), REFRESH_TOKEN_EXPIRED_DAYS, TimeUnit.DAYS);

        // Cache those tokens with open id
        cacheStore.putAny(SecurityUtils.buildAccessTokenKey(token.getAccessToken(), keyPrefix), converter.apply(user), ACCESS_TOKEN_EXPIRED_SECONDS, TimeUnit.SECONDS);
        cacheStore.putAny(SecurityUtils.buildRefreshTokenKey(token.getRefreshToken(), keyPrefix), converter.apply(user), REFRESH_TOKEN_EXPIRED_DAYS, TimeUnit.DAYS);

        return token;
    }

    @Override
    public void clearUserToken(@NonNull User user, String keyPrefix) {
        Assert.notNull(user, "目标用户不能为 null");

        // Clear access token
        cacheStore.getAny(SecurityUtils.buildAccessTokenKey(user, keyPrefix), String.class).ifPresent(accessToken -> {
            // Delete token
            cacheStore.delete(SecurityUtils.buildAccessTokenKey(accessToken, keyPrefix));
            cacheStore.delete(SecurityUtils.buildAccessTokenKey(user, keyPrefix));
        });

        // Clear refresh token
        cacheStore.getAny(SecurityUtils.buildRefreshTokenKey(user, keyPrefix), String.class).ifPresent(refreshToken -> {
            cacheStore.delete(SecurityUtils.buildRefreshTokenKey(refreshToken, keyPrefix));
            cacheStore.delete(SecurityUtils.buildRefreshTokenKey(user, keyPrefix));
        });
    }

    @Override
    @Nullable
    public User updateUserByParam(@NonNull UserParam param) {
        // 更新信息的请求在过滤器黑名单中，无法走 Authentication 方法获得用户
        User currentUser = userRepository.findByStudentNo(param.getStudentNo()).orElse(null);
        // 目标用户不存在、数据库中没有用户信息
        if (currentUser == null) {
            throw new NotFoundException("无效的学号，请联系管理员授权后使用");
        }
        // 首次登陆完善学号的情况
        if (!StringUtils.isEmpty(currentUser.getWxId()) && param.getWxId() != null &&
                !Objects.equals(currentUser.getWxId(), param.getWxId())) {
            throw new ForbiddenException("该学号已存在，请联系管理员，并提供您的学号");
        }
        // 表单验证：邮件、通知
        if (param.getReceiveMsg() != null && param.getReceiveMsg() && StringUtils.isEmpty(param.getEmail())) {
            throw new MissingPropertyException("若开启通知，则必须填写邮箱地址");
        }
        param.update(currentUser);
        currentUser = userRepository.save(currentUser);
        // 清除使用的缓存
        afterModified();
        log.info("用户 [{}]-[{}] 更新（完善）了信息", currentUser.getRealName(), ServletUtils.getRequestIp());
        return currentUser;
    }

    @Override
    @NonNull
    public Page<PageUserVO> getClubUserPage(Integer clubId, Pageable pageable) {
        User me = SecurityContextHolder.ensureUser();
        // 系统管理员无视权限
        if (me.getRole() != UserRole.SYSTEM_ADMIN) {
            joiningService.getJoiningById(me.getId(), clubId).orElseThrow(() -> new BadRequestException("您不属于该社团，无权查看"));
        }
        Page<Joining> allJoining = joiningService.pageAllJoiningByClubId(clubId, pageable);
        return convertJoiningToUserPage(allJoining, pageable);
    }

    @Override
    public List<User> getClubUsers(@NonNull Integer clubId) {
        Assert.notNull(clubId, "社团 ID 不能为 null");
        Map<Integer, User> allUserMap = getAllUserMap();
        List<Joining> allJoining = joiningService.listAllJoiningByClubId(clubId);
        return allJoining.stream().
                map(joining -> allUserMap.get(joining.getId().getUserId())).
                filter(user -> user != null && user.getId() != -1).
                collect(Collectors.toList());
    }

    private Page<PageUserVO> convertJoiningToUserPage(Page<Joining> joiningPage, Pageable pageable) {
        List<Integer> uids = joiningPage.stream().map(join -> join.getId().getUserId()).collect(Collectors.toList());
        List<User> joinUsers = userRepository.findAllByIdIn(uids, Sort.unsorted());
        Map<Integer, User> uidUserMap = ServiceUtils.convertToMap(joinUsers, User::getId);

        return ServiceUtils.convertPageElements(joiningPage, pageable, src -> {
            // 映射为 PageUserVO
            User nowUser = uidUserMap.get(src.getId().getUserId());
            PageUserVO nowVO = new PageUserVO().convertFrom(nowUser);
            BeanUtils.updateProperties(src, nowVO);
            return nowVO;
        });
    }

    @Override
    @NonNull
    public PasscodeVO getPasscode() {
        User currentUser = SecurityContextHolder.ensureUser();
        if (!currentUser.getRole().isAdminRole()) {
            throw new ForbiddenException("权限不足，拒绝访问");
        }
        String nowPasscode = RandomUtil.randomString(6);
        String passcodeCacheKey = QfzsConst.ADMIN_PASSCODE_PREFIX + currentUser.getId();
        cacheStore.putAny(passcodeCacheKey, nowPasscode, 5, TimeUnit.MINUTES);
        PasscodeVO passcodeVO = new PasscodeVO();
        passcodeVO.setPasscode(nowPasscode);
        return passcodeVO;
    }

    @Override
    public <T> AuthToken refreshToken(String refreshToken, String keyPrefix, Function<User, T> idGetter,
                                      Function<T, User> userGetter, Class<T> idType) {
        Assert.hasText(refreshToken, "Refresh token 不能为空");

        T usingId = cacheStore.getAny(SecurityUtils.buildRefreshTokenKey(refreshToken, keyPrefix), idType)
                .orElseThrow(() -> new BadRequestException("登录状态已失效，请重新登录").setErrorData(refreshToken));

        // 获取用户信息
        User user = userGetter.apply(usingId);

        // 清除原 Token
        cacheStore.getAny(SecurityUtils.buildAccessTokenKey(user, keyPrefix), String.class)
                .ifPresent(accessToken -> cacheStore.delete(SecurityUtils.buildAccessTokenKey(accessToken, keyPrefix)));
        cacheStore.delete(SecurityUtils.buildRefreshTokenKey(refreshToken, keyPrefix));
        cacheStore.delete(SecurityUtils.buildAccessTokenKey(user, keyPrefix));
        cacheStore.delete(SecurityUtils.buildRefreshTokenKey(user, keyPrefix));
        // 建立新的 Token
        return buildAuthToken(user, keyPrefix, idGetter);
    }

    @Override
    public User getByOpenId(String openId) {
        return userRepository.findByWxId(openId).orElseThrow(() -> new BadRequestException("非法的 openId"));
    }

    @Override
    public Optional<User> getByStudentNo(Long studentNo) {
        return userRepository.findByStudentNo(studentNo);
    }

    @Override
    public boolean managerOfClub(@NonNull Integer userId, @NonNull Club club) {
        Assert.notNull(club, "社团不能为 null");
        return managerOfClub(getById(userId), club);
    }

    @Override
    public boolean managerOfClub(@NonNull User admin, @NonNull Club club) {
        Assert.notNull(admin, "用户不能为 null");
        Assert.notNull(club, "社团不能为 null");

        if (admin.getRole().isSystemAdmin()) {
            return true;
        }
        if (admin.getRole().isNormalRole()) {
            return false;
        }
        JoiningId jid = new JoiningId(admin.getId(), club.getId());
        Joining joining = joiningService.getByIdOfNullable(jid);
        return joining != null && joining.getAdmin();
    }

    @Override
    public boolean managerOfClub(User admin, Integer clubId) {
        Club targetClub = clubService.getById(clubId);
        return managerOfClub(admin, targetClub);
    }

    @Override
    public boolean managerOf(@NonNull Integer userId, @NonNull User other) {
        Assert.notNull(userId, "用户 id 不能为 null");
        Assert.notNull(other, "另一用户不能为 null");
        return managerOf(userRepository.getOne(userId), other);
    }

    @Override
    public boolean managerOf(@NonNull User admin, @NonNull User other) {
        Assert.notNull(admin, "用户不能为 null");
        Assert.notNull(other, "另一用户不能为 null");

        // 系统管理员无视权限
        if (admin.getRole() == UserRole.SYSTEM_ADMIN) {
            return true;
        }

        List<Joining> adminJoin = joiningService.listAllJoiningByUserId(admin.getId());
        List<Joining> otherJoin = joiningService.listAllJoiningByUserId(other.getId());
        return adminJoin.stream().anyMatch(adminJoining ->
                // 管理员在当前社团中为管理员角色
                adminJoining.getAdmin() && otherJoin.stream().anyMatch(otherJoining ->
                        // 管理员加入的社团也被 other 用户加入
                        Objects.equals(adminJoining.getId().getClubId(), otherJoining.getId().getClubId())
                )
        );
    }

    @Override
    public Map<Integer, User> getAllUserMap() {
        return ServiceUtils.convertToMap(listAll(), User::getId);
    }

    @Override
    @NonNull
    public User getByWxIdIgnoreCache(@NonNull String openId) {
        Assert.notNull(openId, "openId 不能为 null");
        return userRepository.findByWxId(openId).orElseThrow(() -> new NotFoundException("不存在该用户"));
    }
}
