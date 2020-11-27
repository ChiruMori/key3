package work.cxlm.model.entity;

import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * created 2020/11/16 22:09
 *
 * @author Chiru
 */
@Entity
@Data
@Table(name = "club")
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Club extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "custom-id")
    @GenericGenerator(name = "custom-id", strategy = "work.cxlm.model.entity.support.CustomIdGenerator")
    private Integer id;

    /**
     * 社团名
     */
    @Column(name = "name", length = 50)
    private String name;

    /**
     * 社团当前资产（经费）
     */
    @Column(name = "assets")
    private BigDecimal assets;

    /**
     * 社团是否对成员公开经费信息
     */
    @Column(name = "bill_enabled")
    @ColumnDefault("1")
    private Boolean billEnabled;

    // 保存之前执行
    @Override
    public void prePersist() {
        super.prePersist();
        if (assets == null) { // 必须加这个条件，否则将导致新建 Club 时传入的初始经费被覆盖
            assets = new BigDecimal(0);
        }
    }
}
