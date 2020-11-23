package com.juns.pay.service.split;

import com.juns.pay.common.DefaultResponse;
import com.juns.pay.common.enumeration.ResultEnum;
import com.juns.pay.domain.split.SplitEventTokenDTO;
import com.juns.pay.model.enumeration.SplitEventStatus;
import com.juns.pay.model.room.Room;
import com.juns.pay.model.split.SplitEvent;
import com.juns.pay.model.split.UserSplitEvent;
import com.juns.pay.model.user.User;
import com.juns.pay.repository.split.UserSplitEventRepository;
import com.juns.pay.service.room.UserRoomService;
import com.juns.pay.service.userasset.UserAssetService;
import java.util.Random;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Transactional
@Service
public class UserSplitEventService {

    private static final Random random = new Random();

    @Autowired
    private SplitEventService splitEventService;

    @Autowired
    private UserSplitEventRepository userSplitEventRepository;

    @Autowired
    private UserAssetService userAssetService;

    @Autowired
    private UserRoomService userRoomService;

    private double splitRandom(double amount, int count) {
        Random random = new Random();
        if (count == 1 || amount < count) {
            return Double.parseDouble(String.format("%.2f", amount));
        }
        double equalyAmount = amount / count;
        double rn;
        if (random.nextInt() % 2 == 0 && amount - equalyAmount > 0) {
            rn = (random.nextInt((int) ((amount - equalyAmount) * 100)) / 100.0);
        } else if (equalyAmount > 0) {
            rn = (random.nextInt((int) ((equalyAmount) * 100)) / 100.0) * (-1);
        } else {
            return amount;
        }
        double receiveAmount = equalyAmount + rn;
        return Double.parseDouble(String.format("%.2f", receiveAmount));
    }

    public ResponseEntity receiveSplitRandomly(User user, Room room, SplitEventTokenDTO request, long currentTime) {

        SplitEvent event = this.splitEventService.getSplitEvent(room, request.getToken());

        if (!this.userRoomService.isAttendee(user, room)) {
            // 뿌리기가 호출된 대화방과 동일한 대화방에 속한 사용자만이 받을 수 있음
            DefaultResponse response = new DefaultResponse(ResultEnum.ROOM_NOT_ATTENDEE);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        if (user.equals(event.getCreateUser())) {
            // 자신이 뿌리기한 건은 자신이 받을 수 없음
            DefaultResponse response = new DefaultResponse(ResultEnum.SPLITEVENT_CREATOR);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        UserSplitEvent userEvent = this.getUserSplitEvent(user, event);
        if (userEvent != null) {
            // 이미 뿌리기 이벤트 참여 이력 존재하는 유저
            DefaultResponse response = new DefaultResponse(ResultEnum.SPLITEVENT_ALREADY_APPLY);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        if (event.getStatus(currentTime) == SplitEventStatus.EXPIRED) {
            // 유효시간인 10분이 지난 이벤트
            DefaultResponse response = new DefaultResponse(ResultEnum.SPLITEVENT_EXPIRED);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        if (event.getStatus(currentTime) == SplitEventStatus.COMPLETE) {
            // 뿌릴 인원 초과
            DefaultResponse response = new DefaultResponse(ResultEnum.SPLITEVENT_MEMBER_MAX_COUNT_EXCESS);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        double amount = event.getRemainAmount();
        int count = event.getRemainCount();
        double receiveAmount = this.splitRandom(amount, count);

        UserSplitEvent userSplitEvent = UserSplitEvent
            .builder()
            .toUser(user)
            .splitEvent(event)
            .receiveAmount(receiveAmount)
            .timeReceive(currentTime)
            .build();
        this.userSplitEventRepository.save(userSplitEvent);
        this.userAssetService.receive(user, receiveAmount);
        ReceiveSplitEventResponse response = new ReceiveSplitEventResponse(ResultEnum.OK, userSplitEvent.getReceiveAmount());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public UserSplitEvent getUserSplitEvent(User user, SplitEvent event) {
        return this.userSplitEventRepository.findByToUserIdAndSplitEventId(user.getId(), event.getId());
    }
}
