package com.juns.pay.service.room;

import com.juns.pay.common.DefaultResponse;
import com.juns.pay.common.enumeration.ResultEnum;
import com.juns.pay.domain.room.UserRoomDTO;
import com.juns.pay.model.room.Room;
import com.juns.pay.model.room.UserRoom;
import com.juns.pay.model.user.User;
import com.juns.pay.repository.room.RoomRepository;
import com.juns.pay.repository.room.UserRoomRepository;
import com.juns.pay.utils.JWTUtil;
import com.juns.pay.utils.TimeAndDateUtil;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserRoomService {

    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private UserRoomRepository userRoomRepository;

    public void createUserRoom(User user, Room room, int currentTime) {
        UserRoom userRoom = UserRoom.builder()
            .room(room)
            .user(user)
            .timeJoin(currentTime)
            .build();
        this.userRoomRepository.save(userRoom);
    }

    public ResponseEntity join(User user, Room room) {
        if (this.userRoomRepository.findByUserIdAndRoomIdAndTimeLeftNull(user.getId(), room.getId()) == null) {
            this.createUserRoom(user, room, TimeAndDateUtil.getCurrentTimeSec());
            final DefaultResponse response = new DefaultResponse(ResultEnum.OK);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            final DefaultResponse response = new DefaultResponse(ResultEnum.ROOM_ALREADY_ATTENDEE);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }

    public void left(User user, Room room) {
        UserRoom userRoom = this.userRoomRepository.findByUserIdAndRoomIdAndTimeLeftNull(user.getId(), room.getId());
        if (userRoom != null) {
            userRoom.setTimeLeft(TimeAndDateUtil.getCurrentTimeSec());
        }
    }

    public List<UserRoomDTO> getUserRoomList(User user) {
        List<UserRoom> userRooms = this.userRoomRepository.findByUserId(user.getId());
        List<UserRoomDTO> dtos = new ArrayList<>();
        for (UserRoom userRoom : userRooms) {
            dtos.add(userRoom.toDTO());
        }
        return dtos;
    }

    public UserRoomDTO getUserRoom(User user, Room room) {
        UserRoom userRoom = this.userRoomRepository.findByUserIdAndRoomIdAndTimeLeftNull(user.getId(), room.getId());
        if (userRoom != null) {
            return userRoom.toDTO();
        }
        return null;
    }

    public boolean isAttendee(User user, Room room) {
        UserRoomDTO currentUserRoom = this.getUserRoom(user, room);
        return currentUserRoom != null;
    }
}
