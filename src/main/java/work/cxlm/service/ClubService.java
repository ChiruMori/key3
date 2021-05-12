package work.cxlm.service;

import org.springframework.transaction.annotation.Transactional;
import work.cxlm.model.dto.ClubDTO;
import work.cxlm.model.entity.Club;
import work.cxlm.model.params.ClubParam;
import work.cxlm.model.vo.ClubRoomMapVO;
import work.cxlm.service.base.CrudService;

import java.util.List;
import java.util.Map;

/**
 * created 2020/11/21 15:31
 *
 * @author Chiru
 */
public interface ClubService extends CrudService<Club, Integer> {

    /**
     * 查询社团信息，如果用户权限不足则抛出异常
     *
     * @param clubId 指定的社团 id
     * @return 活动室信息 DTO
     */
    ClubDTO getManagedClubInfo(Integer clubId);

    /**
     * 创建新社团
     *
     * @param clubParam 表单
     * @return clubDTO 包装的 Club 实体属性
     */
    ClubDTO newClubByParam(ClubParam clubParam);

    /**
     * 修改社团信息
     *
     * @param clubParam 表单
     * @return clubDTO 包装的 Club 实体属性
     */
    @Transactional(rollbackFor = Exception.class)
    ClubDTO updateByParam(ClubParam clubParam);

    /**
     * 删除社团及其相关信息
     *
     * @param clubId 社团 ID
     */
    @Transactional(rollbackFor = Exception.class)
    void deleteClub(Integer clubId);

    /**
     * 获取全部社团的 ID -> Club 映射
     *
     * @return 全部社团的 ID, 实体 Map
     */
    Map<Integer, Club> getAllClubMap();

    /**
     * 获取当前用户的全部社团
     *
     * @return 当前授权上下文用户加入的全部社团
     */
    List<ClubDTO> listUserClubs();

    /**
     * 构建社团、活动室映射
     *
     * @return 当前授权上下文用户加入的全部社团与社团活动室的对应关系
     */
    ClubRoomMapVO buildClubRoomMapOfUser();
}
