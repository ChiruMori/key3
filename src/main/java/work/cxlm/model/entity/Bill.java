package work.cxlm.model.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;
import work.cxlm.model.enums.OptionType;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * created 2020/11/16 22:48
 *
 * @author Chiru
 */
@Entity
@Data
@Table(name = "bill")
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Bill extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "custom-id")
    @GenericGenerator(name = "custom-id", strategy = "work.cxlm.model.entity.support.CustomIdGenerator")
    private Integer id;

    @Column(name = "cost")
    private BigDecimal cost;

    @Column(name = "info")
    private String info;

    @Column(name = "clubId")
    private Integer clubId;

    @Column(name = "authorId")
    private Integer authorId;

    @PrePersist  // 必须添加本方法，否则父类的方法将失效
    public void prePersist() {
        super.prePersist();
    }
}
