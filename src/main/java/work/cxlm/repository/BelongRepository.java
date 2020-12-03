package work.cxlm.repository;

import org.springframework.lang.NonNull;
import work.cxlm.model.entity.Belong;

import java.util.List;

/**
 * @program: myfont
 * @author: beizi
 * @create: 2020-11-23 15:11
 * @application :
 * @Version 1.0
 **/
public interface BelongRepository extends BaseRepository<Belong,Integer> {

    /*
    * 通过联合主键查询拥有roomId的belong实例
    *
    * */
    List<Belong> findAllByIdRoomId(@NonNull Integer roomId);

    /*
    * 通过联合主键查询拥有clubId的belong实例
    *
    * */
    List<Belong> findAllByIdClubId(@NonNull Integer clubId);
}
