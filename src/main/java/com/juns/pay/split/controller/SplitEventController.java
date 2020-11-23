package com.juns.pay.split.controller;

import com.juns.pay.common.DefaultResponse;
import com.juns.pay.common.HttpHeaderKeyDefine;
import com.juns.pay.common.enumeration.ResultEnum;
import com.juns.pay.room.model.Room;
import com.juns.pay.room.service.RoomService;
import com.juns.pay.split.controller.request.CreateSplitEventRequest;
import com.juns.pay.split.domain.SplitEventTokenDTO;
import com.juns.pay.split.service.SplitEventService;
import com.juns.pay.split.service.UserSplitEventService;
import com.juns.pay.user.model.User;
import com.juns.pay.user.service.UserService;
import com.juns.pay.utils.TimeAndDateUtil;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/split")
public class SplitEventController {

    @Autowired
    SplitEventService splitEventService;

    @Autowired
    UserSplitEventService userSplitEventService;

    @Autowired
    UserService userService;

    @Autowired
    RoomService roomService;

    @PutMapping("/randomly")
    public ResponseEntity splitRandomly(@RequestHeader(HttpHeaderKeyDefine.USER_ID) long userId, @RequestHeader(HttpHeaderKeyDefine.ROOM_ID) String roomId, @RequestBody @Valid CreateSplitEventRequest request, Errors errors) {
        if (errors.hasErrors()) {
            return this.badRequest(errors);
        }
        long currentTime = TimeAndDateUtil.getCurrentTimeMiliSec();
        User user = this.userService.getUser(userId);
        Room room = this.roomService.getByIdentifier(roomId);
        if (user == null) {
            final DefaultResponse response = new DefaultResponse(ResultEnum.UNAUTHENTICATED);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        if (room == null) {
            final DefaultResponse response = new DefaultResponse(ResultEnum.ROOM_NOT_EXIST);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        return this.splitEventService.splitRandomly(user, room, request, currentTime);
    }


    @PostMapping("/history")
    public ResponseEntity historySplitEvent(@RequestHeader(HttpHeaderKeyDefine.USER_ID) long userId, @RequestBody @Valid SplitEventTokenDTO request, Errors errors) {
        if (errors.hasErrors()) {
            return this.badRequest(errors);
        }
        long currentTime = TimeAndDateUtil.getCurrentTimeMiliSec();
        User user = this.userService.getUser(userId);
        if (user == null) {
            final DefaultResponse response = new DefaultResponse(ResultEnum.UNAUTHENTICATED);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        return this.splitEventService.historySplitEvent(user, request, currentTime);
    }

    @PutMapping("/receive")
    public ResponseEntity receiveSplitEvent(@RequestHeader(HttpHeaderKeyDefine.USER_ID) long userId, @RequestHeader(HttpHeaderKeyDefine.ROOM_ID) String roomId, @RequestBody @Valid SplitEventTokenDTO request, Errors errors) {
        if (errors.hasErrors()) {
            return this.badRequest(errors);
        }
        long currentTime = TimeAndDateUtil.getCurrentTimeMiliSec();
        User user = this.userService.getUser(userId);
        Room room = this.roomService.getByIdentifier(roomId);
        if (user == null) {
            final DefaultResponse response = new DefaultResponse(ResultEnum.UNAUTHENTICATED);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        if (room == null) {
            final DefaultResponse response = new DefaultResponse(ResultEnum.ROOM_NOT_ATTENDEE);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        return this.userSplitEventService.receiveSplitRandomly(user, room, request, currentTime);

    }

    private ResponseEntity badRequest(Errors errors) {
        return ResponseEntity.badRequest().body(errors);
    }

}
