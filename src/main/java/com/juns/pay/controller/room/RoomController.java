package com.juns.pay.controller.room;

import com.juns.pay.common.DefaultResponse;
import com.juns.pay.common.HttpHeaderKeyDefine;
import com.juns.pay.common.ResultListResponse;
import com.juns.pay.common.ResultResponse;
import com.juns.pay.common.enumeration.ResultEnum;
import com.juns.pay.controller.room.request.CreateRoomRequest;
import com.juns.pay.domain.room.UserRoomDTO;
import com.juns.pay.model.room.Room;
import com.juns.pay.model.user.User;
import com.juns.pay.service.room.RoomService;
import com.juns.pay.service.room.UserRoomService;
import com.juns.pay.service.user.UserService;
import java.util.List;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/room")
public class RoomController {

    @Autowired
    UserService userService;

    @Autowired
    RoomService roomService;

    @Autowired
    UserRoomService userRoomService;

    @PutMapping("/create")
    public ResponseEntity createRoom(@RequestHeader(HttpHeaderKeyDefine.USER_ID) long userId, @RequestBody @Valid CreateRoomRequest request, Errors errors) {
        if (errors.hasErrors()) {
            return this.badRequest(errors);
        }
        User user = this.userService.getUser(userId);
        if (user == null) {
            final DefaultResponse response = new DefaultResponse(ResultEnum.UNAUTHENTICATED);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        String identifier = this.roomService.createRoom(user, request);
        final ResultResponse<String> response = new ResultResponse<>(ResultEnum.OK, identifier);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/join")
    public ResponseEntity joinRoom(@RequestHeader(HttpHeaderKeyDefine.USER_ID) long userId, @RequestHeader(HttpHeaderKeyDefine.ROOM_ID) String roomIdentifier) {
        User user = this.userService.getUser(userId);
        Room room = this.roomService.getByIdentifier(roomIdentifier);
        if (user == null) {
            final DefaultResponse response = new DefaultResponse(ResultEnum.UNAUTHENTICATED);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        if (room == null || room.isDelete()) {
            final DefaultResponse response = new DefaultResponse(ResultEnum.ROOM_NOT_EXIST);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        return this.userRoomService.join(user, room);

    }

    @GetMapping("/left")
    public ResponseEntity leftRoom(@RequestHeader(HttpHeaderKeyDefine.USER_ID) long userId, @RequestHeader(HttpHeaderKeyDefine.ROOM_ID) String roomIdentifier) {
        User user = this.userService.getUser(userId);
        Room room = this.roomService.getByIdentifier(roomIdentifier);
        if (user == null) {
            final DefaultResponse response = new DefaultResponse(ResultEnum.UNAUTHENTICATED);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        if (room == null) {
            final DefaultResponse response = new DefaultResponse(ResultEnum.ROOM_NOT_EXIST);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        this.userRoomService.left(user, room);
        final DefaultResponse response = new DefaultResponse(ResultEnum.OK);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/list")
    public ResponseEntity getRoomList(@RequestHeader(HttpHeaderKeyDefine.USER_ID) long userId) {
        User user = this.userService.getUser(userId);
        if (user == null) {
            final DefaultResponse response = new DefaultResponse(ResultEnum.UNAUTHENTICATED);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        List<UserRoomDTO> dtos = this.userRoomService.getUserRoomList(user);
        final ResultListResponse<UserRoomDTO> response = new ResultListResponse<>(ResultEnum.OK, dtos);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private ResponseEntity badRequest(Errors errors) {
        return ResponseEntity.badRequest().body(errors);
    }
}
