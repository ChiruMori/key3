package work.cxlm.model.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.validator.constraints.Length;
import work.cxlm.model.entity.support.TimeIdGenerator;
import work.cxlm.model.enums.TimeState;

import javax.persistence.*;
import java.util.Date;

/**
 * 时段实体类
 * created 2020/11/16 19:32
 *
 * @author Chiru
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "time_period", indexes = @Index(name = "room_id_index", columnList = "room_id"))
@ToString
@NoArgsConstructor
public class TimePeriod extends BaseEntity {

    /**
     * ID 编码方式：年年年年月月日日时时分分活动室ID
     * 如：2020123021120001 表示 2020.12.30 21:12，活动室 0001 的时段
     * 注意，本版本实现中，分钟数只有 00，日后可能有其他实现，所以预留两位分钟位
     */
    @Id
    private Long id;

    /**
     * 时段开始时间
     */
    @Column(name = "start_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date startTime;

    /*
     * 时段结束时间
     */
//    @Column(name = "end_time")
//    @Temporal(TemporalType.TIMESTAMP)
//    private Date endTime;

    /**
     * 关联的活动室 ID
     */
    @Column(name = "room_id", nullable = false)
    private Integer roomId;

    /**
     * 预订了当前时段的用户，可以为空，
     */
    @Column(name = "user_id")
    private Integer userId;

    /**
     * 显示在时间表格中的文本
     */
    @Column(name = "show_text", nullable = false)
    @Length(max = 8)
    private String showText;

    /**
     * 当前时段是否已签到
     */
    @Column(name = "signed")
    @ColumnDefault("0")
    private Boolean signed;

    /**
     * 用户是否在当前时段迟到，缺席并不算迟到
     */
    @Column(name = "late")
    @ColumnDefault("1")
    private Boolean late;

    /**
     * 当前时间段的状态：空闲、预定、预定且被关注、禁用
     */
    @Column(name = "state")
    @ColumnDefault("0") // 默认空闲
    private TimeState state;

    @Override
    protected void prePersist() {
        super.prePersist();
        if (showText == null) {
            showText = StringUtils.EMPTY;
        }
    }

    public TimePeriod(Long id, Date startTime) {
        this.id = id;
        this.startTime = startTime;
    }

    public TimePeriod(Long id) {
        this.id = id;
        this.startTime = TimeIdGenerator.decodeIdToDate(id);
        this.roomId = (int) (id % 10000);
    }
}
