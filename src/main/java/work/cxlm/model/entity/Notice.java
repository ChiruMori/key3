package work.cxlm.model.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import work.cxlm.model.enums.NoticeType;

import javax.persistence.*;

/**
 * created 2020/11/16 23:04
 *
 * @author Chiru
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "notice")
@NoArgsConstructor
public class Notice extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "custom-id")
    @GenericGenerator(name = "custom-id", strategy = "work.cxlm.model.entity.support.CustomIdGenerator")
    private Long id;

    @Column(name = "type")
    private NoticeType type;

    @Column(name = "title", length = 50)
    private String title;

    @Column(name = "content")
    private String content;

    /**
     * 有可能是用户、也可能是公告
     */
    @Column(name = "src_id")
    private Integer srcId;

    @Column(name = "target_user_id")
    private Integer targetUserId;

    @Override
    protected void prePersist() {
        super.prePersist();
    }

    public Notice(NoticeType type, String content, Integer srcId, Integer targetUserId) {
        this.type = type;
        this.title = type.getTitle();
        this.content = content;
        this.srcId = srcId;
        this.targetUserId = targetUserId;
    }
}
