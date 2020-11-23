package com.juns.pay.split.service;

import com.juns.pay.asset.service.UserAssetService;
import com.juns.pay.common.DefaultResponse;
import com.juns.pay.common.Policy;
import com.juns.pay.common.ResultResponse;
import com.juns.pay.common.enumeration.ResultEnum;
import com.juns.pay.room.model.Room;
import com.juns.pay.split.controller.request.CreateSplitEventRequest;
import com.juns.pay.split.domain.SplitEventDTO;
import com.juns.pay.split.domain.SplitEventTokenDTO;
import com.juns.pay.split.enumeration.SplitEventStatus;
import com.juns.pay.split.model.SplitEvent;
import com.juns.pay.split.repository.SplitEventRepository;
import com.juns.pay.user.model.User;
import com.juns.pay.utils.RandomTokenGenerationUtil;
import io.jsonwebtoken.lang.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Transactional
@Service
public class SplitEventService {

    @Autowired
    private SplitEventRepository splitEventRepository;

    @Autowired
    private UserAssetService userAssetService;

    public String splitRandomly(User user, Room room, CreateSplitEventRequest request, long currentTime) {

        String token = RandomTokenGenerationUtil.generateToken(Policy.SPLITEVENT_TOKEN_LENGTH);
        this.userAssetService.send(user, request.getAmount());
        SplitEvent splitEvent = SplitEvent.builder()
            .amount(request.getAmount())
            .maxCount(request.getMaxCount())
            .room(room)
            .createUser(user)
            .status(SplitEventStatus.NONE)
            .timeCreate(currentTime)
            .timeExpire(currentTime + Policy.SPLITEVENT_EXPIRED_TIME)
            .token(token)
            .build();
        this.splitEventRepository.save(splitEvent);
        return token;
        
    }

    public ResponseEntity historySplitEvent(User user, SplitEventTokenDTO request, long currentTime) {
        SplitEvent event;

        List<SplitEvent> events = this.splitEventRepository.findByToken(request.getToken());
        if (Collections.isEmpty(events)) {
            // 존재하지 않는 이벤트 token
            final DefaultResponse response = new DefaultResponse(ResultEnum.SPLITEVENT_NOT_EXIST);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        event = events.stream()
            .filter(e -> e.getCreateUser().equals(user))
            .findFirst()
            .orElse(null);

        if (event == null) {
            // 다른사람의 뿌리기건은 조회할 수 없음
            final DefaultResponse response = new DefaultResponse(ResultEnum.SPLITEVENT_UNAUTHORIZED);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        if (!event.isSearchable(currentTime)) {
            final DefaultResponse response = new DefaultResponse(ResultEnum.SPLITEVENT_NOT_SEARCHABLE_PERIOD);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        SplitEventDTO dto = event.toDTO();
        final ResultResponse<SplitEventDTO> response = new ResultResponse<>(ResultEnum.OK, dto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public SplitEvent getSplitEvent(Room room, String eventToken) {
        return this.splitEventRepository.findByRoomIdAndToken(room.getId(), eventToken);
    }
}
