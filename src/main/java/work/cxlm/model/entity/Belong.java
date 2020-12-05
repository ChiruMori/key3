package work.cxlm.model.entity;

import lombok.*;
import work.cxlm.model.entity.id.BelongId;

import javax.persistence.*;

/**
 * 关系实体，活动室属于某个社团
 * created 2020/11/16 22:49
 *
 * @author Chiru
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString
@Entity
@Table(name = "belong")
@AllArgsConstructor
@NoArgsConstructor
public class Belong extends BaseEntity {

    @Id
    private BelongId id;

    public Belong(Integer clubId, Integer roomId) {
        this.id = new BelongId(clubId, roomId);
    }

}
