package work.cxlm.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import work.cxlm.cache.AbstractStringCacheStore;
import work.cxlm.config.QfzsProperties;
import work.cxlm.exception.ForbiddenException;
import work.cxlm.model.entity.Belong;
import work.cxlm.model.entity.Club;
import work.cxlm.model.entity.Room;
import work.cxlm.repository.BelongRepository;
import work.cxlm.repository.RoomRepository;
import work.cxlm.service.BelongService;
import work.cxlm.service.ClubService;
import work.cxlm.service.base.AbstractCrudService;

import java.util.ArrayList;
import java.util.List;

/**
 * @program: myfont
 * @author: beizi
 * @create: 2020-11-23 15:20
 * @application :
 * @Version 1.0
 **/
@Service
public class BelongServiceImpl extends AbstractCrudService<Belong, Integer> implements BelongService {
    private final BelongRepository belongRepository;
    private final QfzsProperties qfzsProperties;
    private final ApplicationEventPublisher eventPublisher;
    private final AbstractStringCacheStore cacheStore;
    private RoomRepository roomRepository;
    private ClubService clubService;

    @Autowired
    public void setClubService(ClubService clubService) {
        this.clubService = clubService;
    }

    public BelongServiceImpl(BelongRepository belongRepository,
                             QfzsProperties qfzsProperties,
                             ApplicationEventPublisher eventPublisher,
                             AbstractStringCacheStore cacheStore) {
        super(belongRepository);
        this.belongRepository = belongRepository;
        this.qfzsProperties = qfzsProperties;
        this.eventPublisher = eventPublisher;
        this.cacheStore = cacheStore;
    }


    @Override
    @NonNull
    public List<Club> listAllRoomsBelongClub(@NonNull Integer roomId) {
        Assert.notNull(roomId,"活动室编号不能为空!");
        List<Belong> allByIdRoomId = belongRepository.findAllByIdRoomId(roomId);
        List<Club> clubs=new ArrayList<>();
        for (Belong belong : allByIdRoomId) {
            Club club=clubService.allClubsByClubId(belong.getId().getClubId());
            clubs.add(club);
        }
        return clubs;
    }

    @Override
    @NonNull
    public List<Room> listAllClubHasRoom(@Nullable Integer clubId) {
        Assert.notNull(clubId,"社团编号不能为空!");
        List<Belong> allByIdRoomId = belongRepository.findAllByIdRoomId(clubId);
        List<Room> rooms=new ArrayList<>();
        for (Belong belong : allByIdRoomId) {
            Room room=roomRepository.findById(belong.getId().getRoomId()).orElseThrow(()->new ForbiddenException("找不到该房间编号的活动室"));
            rooms.add(room);
        }
        return rooms;
    }
}
