package work.cxlm.model.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * created 2020/11/16 22:49
 *
 * @author Chiru
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "announcement")
@NoArgsConstructor
public class Announcement extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "custom-id")
    @GenericGenerator(name = "custom-id", strategy = "work.cxlm.model.entity.support.CustomIdGenerator")
    private Integer id;

    @Column(name = "title", length = 50)
    private String title;

    @Column(name = "content")
    private String content;

    @Column(name = "club_id")
    private Integer clubId;

    @Column(name = "publisher_id")
    private Integer publisherId;
}
