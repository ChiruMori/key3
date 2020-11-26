package work.cxlm.model.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;
import work.cxlm.model.enums.LogType;

import javax.persistence.*;

/**
 * created 2020/10/22 17:03
 *
 * @author cxlm
 */
@Data
@Entity
@ToString
@EqualsAndHashCode(callSuper = true)
@Table(name = "system_logs", indexes = {
        @Index(name = "logs_create_time", columnList = "create_time"),
        @Index(name = "logs_fk_key", columnList = "log_key"),
})
public class Log extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "custom-id")
    @GenericGenerator(name = "custom-id", strategy = "work.cxlm.model.entity.support.CustomIdGenerator")
    private Long id;

    @Column(name = "type", nullable = false)
    private LogType type;

    @Column(name = "content", length = 1023, nullable = false)
    private String content;

    @Column(name="ip", length=127)
    private String ip;

    /* 一般作为外键使用，关联产生当前日志的东西，通常为社团，系统级日志时为 admin id */
    @Column(name = "log_key")
    private Integer logKey;
}
