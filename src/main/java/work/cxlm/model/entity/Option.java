package work.cxlm.model.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.GenericGenerator;
import work.cxlm.model.enums.OptionType;

import javax.persistence.*;

/**
 * created 2020/11/9 15:12
 *
 * @author Chiru
 */
@Data
@Entity
@Table(name = "system_options")
@ToString
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Option extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "custom-id")
    @GenericGenerator(name = "custom-id", strategy = "work.cxlm.model.entity.support.CustomIdGenerator")
    private Integer id;

    @Column(name = "option_key", length = 100, nullable = false)
    private String key;

    @Lob
    @Column(name = "option_value", nullable = false)
    private String value;

}
