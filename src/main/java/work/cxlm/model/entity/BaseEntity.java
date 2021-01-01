package work.cxlm.model.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import work.cxlm.utils.QfzsDateUtils;

import javax.persistence.*;
import java.util.Date;

/**
 * @author cxlm
 * created 2020/10/17 14:30
 * <p>
 * 所有实体类的基类，封装了创建、更新时间的字段
 * 不映射到单独的数据表
 */
@Data
@ToString
@EqualsAndHashCode
@MappedSuperclass
public abstract class BaseEntity {

    @Column(name = "create_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;

    @Column(name = "update_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateTime;

    /**
     * 保存之前执行
     */
    @PrePersist
    protected void prePersist() {
        Date now = QfzsDateUtils.now();
        if (createTime == null) {
            createTime = now;
        }

        if (updateTime == null) {
            updateTime = now;
        }
    }

    @PreUpdate
    @PreRemove
    protected void preUpdate() {
        updateTime = QfzsDateUtils.now();
    }
}
