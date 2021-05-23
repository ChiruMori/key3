package work.cxlm.task;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import work.cxlm.model.dto.base.OutputConverter;
import work.cxlm.model.entity.*;
import work.cxlm.model.enums.NoticeType;
import work.cxlm.service.*;
import work.cxlm.utils.DateUtils;
import work.cxlm.utils.ServiceUtils;

import java.util.*;

/**
 * created 2020/12/10 23:50
 *
 * @author Chiru
 */
@Component
@EnableAsync
@EnableScheduling
@Slf4j
public class RoomRefreshTask {

    private final RoomService roomService;
    private final NoticeService noticeService;
    private final BelongService belongService;
    private final JoiningService joiningService;
    private final UserService userService;

    private Map<CacheUsingSimpleRoomDTO, Set<Integer>> room2UserMap = null;

    public RoomRefreshTask(RoomService roomService,
                           NoticeService noticeService,
                           BelongService belongService,
                           JoiningService joiningService,
                           UserService userService) {
        this.roomService = roomService;
        this.noticeService = noticeService;
        this.belongService = belongService;
        this.joiningService = joiningService;
        this.userService = userService;
    }

    public void clearCache() {
        room2UserMap = null;
    }

    private void buildCache() {
        room2UserMap = new HashMap<>(8);
        Map<Integer, CacheUsingSimpleRoomDTO> cacheRoomMap = ServiceUtils.convertToMap(roomService.listAll(), Room::getId,
                room -> new CacheUsingSimpleRoomDTO().convertFrom(room));
        List<Belong> belongs = belongService.listAll();
        List<Joining> allJoining = joiningService.listAll();
        // 整理数据：得到活动室 ID 与 社团 ID 的对应关系
        Map<Integer, List<Integer>> room2ClubMap = ServiceUtils.list2ListMap(belongs,
                b -> b.getId().getRoomId(), b -> b.getId().getClubId());
        // 整理数据：得到社团 ID 与 Joining 实例的关系
        Map<Integer, List<Joining>> club2JoiningMap = ServiceUtils.list2ListMap(allJoining,
                j -> j.getId().getClubId(), j -> j);
        // 整理数据：得到活动室 ID 与 Joining 实例的关系
        room2ClubMap.forEach((roomId, clubIds) -> {
            HashSet<Integer> userIdSet = new HashSet<>();
            clubIds.forEach(clubId -> {
                // 社团没有任何人加入但是却拥有活动室的情况
                if (!club2JoiningMap.containsKey(clubId)) {
                    return;
                }
                // 删除不接受通知的用户
                club2JoiningMap.get(clubId).forEach(joining -> {
                    Integer userId = joining.getId().getUserId();
                    User targetUser = userService.getByIdOfNullable(userId);
                    if (null != targetUser) {
                        Boolean receiveMsg = targetUser.getReceiveMsg();
                        if (receiveMsg != null && receiveMsg) {
                            userIdSet.add(userId);
                        }
                    }
                });
            });
            room2UserMap.put(cacheRoomMap.get(roomId), userIdSet);
        });
        log.info("已刷新用户关系映射缓存");
    }

    /**
     * 周日，每小时执行一次，判断活动室是否需要重置
     * 秒 分 时 日 月 星期
     */
    @Async
    // @Scheduled(cron = "0 2 12 16 2 ?")
    @Scheduled(cron = "0 0 * ? * SUN")
    public void notifyUsersAtTheEndOfWeekend() {
        if (room2UserMap == null) {
            buildCache();
        }
        int nowHour = DateUtils.whatHourIsNow();

        LinkedList<Notice> notices = new LinkedList<>();
        StringBuilder sb = new StringBuilder();
        room2UserMap.forEach((simpleRoomDTO, userIdSet) -> {
            if (simpleRoomDTO.getEndHour() != nowHour) {
                return;
            }
            sb.append(simpleRoomDTO.getName()).append("、");
            userIdSet.forEach((userId) -> notices.add(new Notice(NoticeType.TIME_RESET,
                    "活动室 [" + simpleRoomDTO.getName() + "] 新一周的预定已经可以开始了", -1, userId)));
        });
        if (sb.length() != 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        noticeService.notifyByMailInBatch(notices);
        // 当前时间点存在有活动室发生了重置时，记录日志
        if (!StringUtils.isEmpty(sb)) {
            log.info("已向活动室【{}】用户发送重置通知", sb);
        }
    }

    @Data
    @EqualsAndHashCode
    private static class CacheUsingSimpleRoomDTO implements OutputConverter<CacheUsingSimpleRoomDTO, Room> {
        private Integer id;
        private Integer endHour;
        private String name;
    }
}
