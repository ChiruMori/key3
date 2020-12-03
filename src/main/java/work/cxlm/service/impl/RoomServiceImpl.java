package work.cxlm.service.impl;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import work.cxlm.exception.ForbiddenException;
import work.cxlm.model.entity.Room;
import work.cxlm.repository.BaseRepository;
import work.cxlm.repository.RoomRepository;
import work.cxlm.service.RoomService;
import work.cxlm.service.base.AbstractCrudService;

import java.util.Optional;

/**
 * @program: myfont
 * @author: beizi
 * @create: 2020-11-23 20:52
 * @application :
 * @Version 1.0
 **/
@Service
@Slf4j
public class RoomServiceImpl extends AbstractCrudService<Room, Integer> implements RoomService {

    protected RoomServiceImpl(RoomRepository roomRepository) {
        super(roomRepository);
        this.roomRepository = roomRepository;
    }
    private final RoomRepository roomRepository;

    @Override
    public @NonNull Optional<Room> findByRoomId(@NonNull Integer roomId) {

        return roomRepository.findById(roomId);
    }
}
