package work.cxlm.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import work.cxlm.model.entity.Belong;
import work.cxlm.model.entity.Club;
import work.cxlm.model.entity.Room;
import work.cxlm.model.entity.id.BelongId;
import work.cxlm.repository.BelongRepository;
import work.cxlm.service.BelongService;
import work.cxlm.service.ClubService;
import work.cxlm.service.RoomService;
import work.cxlm.service.base.AbstractCrudService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * program: myfont
 * @author beizi
 * @author Chiru
 * create: 2020-11-23 15:20
 * application :
 * Version 1.0
 **/
@Service
public class BelongServiceImpl extends AbstractCrudService<Belong, BelongId> implements BelongService {

    private final BelongRepository belongRepository;

    private ClubService clubService;
    private RoomService roomService;

    public BelongServiceImpl(BelongRepository belongRepository) {
        super(belongRepository);
        this.belongRepository = belongRepository;
    }

    @Autowired
    public void setClubService(ClubService clubService) {
        this.clubService = clubService;
    }

    @Autowired
    public void setRoomService(RoomService roomService) {
        this.roomService = roomService;
    }

    @Override
    @NonNull
    public List<Club> listRoomClubs(@NonNull Integer roomId) {
        Assert.notNull(roomId, "活动室编号不能为空!");
        List<Belong> allRoomBelongs = belongRepository.findAllByIdRoomId(roomId);

        Map<Integer, Club> allClubMap = clubService.getAllClubMap();
        return allRoomBelongs.stream().
                map(belong -> allClubMap.get(belong.getId().getClubId())).
                collect(Collectors.toList());
    }

    @Override
    @NonNull
    public List<Room> listClubRooms(@Nullable Integer clubId) {
        Assert.notNull(clubId, "社团编号不能为空!");

        List<Belong> allClubBelongs = belongRepository.findAllByIdClubId(clubId);
        Map<Integer, Room> roomMap = roomService.getAllRoomMap();
        return allClubBelongs.stream().
                map(belong -> roomMap.get(belong.getId().getRoomId())).
                collect(Collectors.toList());
    }
}
