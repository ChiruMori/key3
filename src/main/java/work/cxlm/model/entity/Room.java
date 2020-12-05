package work.cxlm.model.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.validator.constraints.Range;
import work.cxlm.model.enums.RoomState;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * 活动室实体类
 * created 2020/11/16 22:18
 *
 * @author Chiru
 */
@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Table(name = "room")
public class Room extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "custom-id")
    @GenericGenerator(name = "custom-id", strategy = "work.cxlm.model.entity.support.CustomIdGenerator")
    private Integer id;

    /**
     * 活动室名
     */
    @Column(name = "name", length = 50)
    private String name;

    /**
     * 每周预约时长上限
     */
    @Column(name = "week_limit")
    private Integer weekLimit;

    /**
     * 每日预约上限
     */
    @Column(name = "day_limit")
    private Integer dayLimit;

    /**
     * 每天可以开始预订的时间（小时、几点）
     */
    @Column(name = "start_hour")
    @Range(min = 0, max = 23)
    private Integer startHour;

    /**
     * 每天结束预订的时间（小时、几点）
     */
    @Column(name = "end_hour")
    @Range(min = 1, max = 24)
    private Integer endHour;

    /*
     * 重设周期，CRON 表达式
     */
//    @Column(name = "reset_cron", length = 50)
//    private String resetCron;

    /**
     * 使用当前活动室是否需要签到
     */
    @Column(name = "need_sign")
    @ColumnDefault("1")
    private Boolean needSign;

    /**
     * 活动室坐标（经度），仅在开启签到时有效
     */
    @Column(name = "longitude")
    private BigDecimal longitude;

    /**
     * 活动室坐标（纬度），仅在开启签到时有效
     */
    @Column(name = "latitude")
    private BigDecimal latitude;

    /**
     * 使用活动室的贡献值消耗（可以为负数）
     */
    @Column(name = "cost")
    private Integer cost;

    @Column(name = "roomState")
    private RoomState roomState;

    @Override
    @PrePersist
    protected void prePersist() {
        super.prePersist();
        if (cost == null) {
            cost = 0;
        }
        if (needSign == null) {
            needSign = false;
        }
        if (startHour == null) {
            startHour = 8;
        }
        if (endHour == null) {
            endHour = 21;
        }
        if (dayLimit == null) {
            dayLimit = 2;
        }
        if (weekLimit == null) {
            weekLimit = 4;
        }
    }
}
