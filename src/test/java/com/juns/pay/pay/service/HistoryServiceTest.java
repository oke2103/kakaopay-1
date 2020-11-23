package com.juns.pay.pay.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.juns.pay.common.DefaultResponse;
import com.juns.pay.common.Policy;
import com.juns.pay.common.ResultResponse;
import com.juns.pay.common.enumeration.ResultEnum;
import com.juns.pay.controller.split.request.CreateSplitEventRequest;
import com.juns.pay.domain.split.SplitEventDTO;
import com.juns.pay.domain.split.SplitEventTokenDTO;
import com.juns.pay.service.split.ReceiveSplitEventResponse;
import com.juns.pay.utils.TimeAndDateUtil;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

public class HistoryServiceTest extends ServiceTest {

    /*
     * 조회 기능 테스트
     * 뿌리기 User : fromUser
     * 받기 User : toUser
     * */
    @Test
    public void test_history() {
        long currentTime = TimeAndDateUtil.getCurrentTimeMilliSec();
        long availableTime = TimeAndDateUtil.getCurrentTimeMilliSec();

        final String token = this.splitEventService.splitRandomly(this.fromUser, this.room, new CreateSplitEventRequest(1000, 3), currentTime);
        ResponseEntity receiveResponseEntity = this.userSplitEventService.receiveSplitRandomly(this.toUser, this.room, new SplitEventTokenDTO(token), currentTime);

        assertThat(receiveResponseEntity.getBody()).isInstanceOf(ReceiveSplitEventResponse.class);
        ReceiveSplitEventResponse receiveResponse = (ReceiveSplitEventResponse) receiveResponseEntity.getBody();

        assert receiveResponse != null;
        assert receiveResponse.getResultCode() == ResultEnum.OK.getCode();
        assert receiveResponse.getReceiveAmount() >= 0L;

        ResponseEntity responseEntity = this.splitEventService.historySplitEvent(this.fromUser, new SplitEventTokenDTO(token), currentTime);
        assertThat(responseEntity.getBody()).isInstanceOf(ResultResponse.class);
        ResultResponse<SplitEventDTO> response = (ResultResponse) responseEntity.getBody();

        assert response != null;
        assert response.getResultCode() == ResultEnum.OK.getCode();
        assert response.getResult() != null;
        assert response.getResult().getReceiveAmount() == receiveResponse.getReceiveAmount();
        assert response.getResult().getUserSplitEvents().size() == 1;
    }


    /*
     * 뿌린 사용자만 조회 가능
     * 뿌리기 User : fromUser
     * 받기 User : toUser
     * 조회 User : toUser
     * */
    @Test
    public void test_history_fail_1() {
        long currentTime = TimeAndDateUtil.getCurrentTimeMilliSec();

        final String token = this.splitEventService.splitRandomly(this.fromUser, this.room, new CreateSplitEventRequest(1000, 3), currentTime);
        ResponseEntity receiveResponseEntity = this.userSplitEventService.receiveSplitRandomly(this.toUser, this.room, new SplitEventTokenDTO(token), currentTime);

        assertThat(receiveResponseEntity.getBody()).isInstanceOf(ReceiveSplitEventResponse.class);
        ReceiveSplitEventResponse receiveResponse = (ReceiveSplitEventResponse) receiveResponseEntity.getBody();

        assert receiveResponse != null;
        assert receiveResponse.getResultCode() == ResultEnum.OK.getCode();
        assert receiveResponse.getReceiveAmount() >= 0L;

        ResponseEntity responseEntity = this.splitEventService.historySplitEvent(this.toUser, new SplitEventTokenDTO(token), currentTime);
        assertThat(responseEntity.getBody()).isInstanceOf(DefaultResponse.class);
        DefaultResponse response = (DefaultResponse) responseEntity.getBody();

        assert response != null;
        assert response.getResultCode() == ResultEnum.SPLITEVENT_UNAUTHORIZED.getCode();
    }


    /*
     * 유효하지 않은 token으로 조회할 경우 실패
     * 뿌리기 토큰 : token
     * 유효하지 않은 토큰 : invalidToken
     * */
    @Test
    public void test_history_fail_2() {
        long currentTime = TimeAndDateUtil.getCurrentTimeMilliSec();

        final String token = this.splitEventService.splitRandomly(this.fromUser, this.room, new CreateSplitEventRequest(1000, 3), currentTime);
        final String invalidToken = "INV";
        ResponseEntity receiveResponseEntity = this.userSplitEventService.receiveSplitRandomly(this.toUser, this.room, new SplitEventTokenDTO(token), currentTime);

        assertThat(receiveResponseEntity.getBody()).isInstanceOf(ReceiveSplitEventResponse.class);
        ReceiveSplitEventResponse receiveResponse = (ReceiveSplitEventResponse) receiveResponseEntity.getBody();

        assert receiveResponse != null;
        assert receiveResponse.getResultCode() == ResultEnum.OK.getCode();
        assert receiveResponse.getReceiveAmount() >= 0L;

        ResponseEntity responseEntity = this.splitEventService.historySplitEvent(this.toUser, new SplitEventTokenDTO(invalidToken), currentTime);
        assertThat(responseEntity.getBody()).isInstanceOf(DefaultResponse.class);
        DefaultResponse response = (DefaultResponse) responseEntity.getBody();

        assert response != null;
        assert response.getResultCode() == ResultEnum.SPLITEVENT_NOT_EXIST.getCode();
    }


    /*
     * 뿌린 건에 대한 조회는 7일 동안만 조회 가능
     * 조회 시간  : currentTime
     * 조회 시간 : notAvailableTime
     * */
    @Test
    public void test_history_fail_3() {
        long currentTime = TimeAndDateUtil.getCurrentTimeMilliSec();
        long notAvailableTime = currentTime + Policy.SPLITEVENT_SEARCHABLE_PERIOD + 1L;

        final String token = this.splitEventService.splitRandomly(this.fromUser, this.room, new CreateSplitEventRequest(1000, 3), currentTime);
        ResponseEntity receiveResponseEntity = this.userSplitEventService.receiveSplitRandomly(this.toUser, this.room, new SplitEventTokenDTO(token), currentTime);

        assertThat(receiveResponseEntity.getBody()).isInstanceOf(ReceiveSplitEventResponse.class);
        ReceiveSplitEventResponse receiveResponse = (ReceiveSplitEventResponse) receiveResponseEntity.getBody();

        assert receiveResponse != null;
        assert receiveResponse.getResultCode() == ResultEnum.OK.getCode();
        assert receiveResponse.getReceiveAmount() >= 0L;

        ResponseEntity responseEntity = this.splitEventService.historySplitEvent(this.toUser, new SplitEventTokenDTO(token), notAvailableTime);
        assertThat(responseEntity.getBody()).isInstanceOf(DefaultResponse.class);
        DefaultResponse response = (DefaultResponse) responseEntity.getBody();

        assert response != null;
        assert response.getResultCode() == ResultEnum.SPLITEVENT_UNAUTHORIZED.getCode();
    }

}
