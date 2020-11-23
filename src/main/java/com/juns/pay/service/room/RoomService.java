package com.juns.pay.service.room;

import com.juns.pay.controller.room.request.CreateRoomRequest;
import com.juns.pay.model.room.Room;
import com.juns.pay.model.user.User;
import com.juns.pay.repository.room.RoomRepository;
import com.juns.pay.utils.JWTUtil;
import com.juns.pay.utils.TimeAndDateUtil;
import java.util.List;
import javax.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Transactional
public class RoomService {

    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private UserRoomService userRoomService;

    public String createRoom(User user, CreateRoomRequest request) {
        int currentTime = TimeAndDateUtil.getCurrentTimeSec();
        Room room = Room.builder()
            .name(request.getName())
            .timeCreate(currentTime)
            .createUser(user)
            .build();
        this.roomRepository.save(room);
        String identifier = this.getToken(room);
        room.setIdentifier(identifier);
        this.roomRepository.flush();
        this.userRoomService.createUserRoom(user, room, currentTime);
        return identifier;
    }

    public Room getByIdentifier(String roomId) {
        return this.roomRepository.findByIdentifier(roomId);
    }

    private String getToken(Room room) {
        long roomId = room.getId();
        String token = null;
        try {
            token = this.jwtUtil.doGenerateToken(roomId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return token;
    }

    public List<Room> getAll() {
        return this.roomRepository.findAll();
    }
}
