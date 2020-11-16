package work.cxlm.model.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.ColumnDefault;
import work.cxlm.model.entity.id.JoiningId;

import javax.persistence.*;

/**
 * 关系实体：用户加入某社团
 * created 2020/11/16 22:38
 *
 * @author Chiru
 */
@Data
@Entity
@ToString
@EqualsAndHashCode(callSuper = true)
@Table(name = "joining")
public class Joining extends BaseEntity {

    @Id
    private JoiningId id;

    @Column(name = "admin")
    @ColumnDefault("0")
    private Boolean admin;

    @Column(name = "position")
    private String position;

    @Column(name = "receive_info")
    @ColumnDefault("1")
    private Boolean receiveInfo;

    @Column(name = "total")
    private Integer total;

    @Column(name = "point")
    private Integer point;

    @Override
    public void prePersist() {
        super.prePersist();
        position = "打酱油的";
        total = 0;
        point = 0;
    }
}
