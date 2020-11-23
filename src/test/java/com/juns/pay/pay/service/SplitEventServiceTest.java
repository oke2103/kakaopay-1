package com.juns.pay.pay.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.juns.pay.common.DefaultResponse;
import com.juns.pay.common.Policy;
import com.juns.pay.common.ResultResponse;
import com.juns.pay.common.enumeration.ResultEnum;
import com.juns.pay.split.controller.request.CreateSplitEventRequest;
import com.juns.pay.split.controller.response.ReceiveSplitEventResponse;
import com.juns.pay.split.domain.SplitEventDTO;
import com.juns.pay.split.domain.SplitEventTokenDTO;
import com.juns.pay.utils.TimeAndDateUtil;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

public class SplitEventServiceTest extends SetupServiceTest {

    /*
     * 뿌리기 기능 테스트
     * token : 뿌리기 요청건에 대한 3자리 문자열로 이루어진 고유 token
     * */
    @Test
    public void test_split() {
        long currentTime = TimeAndDateUtil.getCurrentTimeMiliSec();
        final String token = this.splitEventService.splitRandomly(this.fromUser, this.room, new CreateSplitEventRequest(1000, 3), currentTime);
        assertThat(token).isNotEmpty();
        assertThat(token).hasSize(Policy.SPLITEVENT_TOKEN_LENGTH);
    }

    /*
     * 받기 기능 테스트
     * 뿌리기 User : fromUser
     * 받기 User : toUser
     * */
    @Test
    public void test_receive() {
        long currentTime = TimeAndDateUtil.getCurrentTimeMiliSec();
        long availableTime = TimeAndDateUtil.getCurrentTimeMiliSec();

        final String token = this.splitEventService.splitRandomly(this.fromUser, this.room, new CreateSplitEventRequest(1000, 3), currentTime);
        ResponseEntity responseEntity = this.userSplitEventService.receiveSplitRandomly(this.toUser, this.room, new SplitEventTokenDTO(token), currentTime);

        assertThat(responseEntity.getBody()).isInstanceOf(ReceiveSplitEventResponse.class);
        ReceiveSplitEventResponse response = (ReceiveSplitEventResponse) responseEntity.getBody();

        assert response != null;
        assert response.getResultCode() == ResultEnum.OK.getCode();
        assert response.getReceiveAmount() >= 0L;
    }

    /*
     * 자신이 뿌리기한 건은 자신이 받을 수 없음
     * 뿌리기 User : fromUser
     * 받기 User : fromUser
     * */
    @Test
    public void testReceive_fail_1() {
        long currentTime = TimeAndDateUtil.getCurrentTimeMiliSec();
        long availableTime = TimeAndDateUtil.getCurrentTimeMiliSec();

        final String token = this.splitEventService.splitRandomly(this.fromUser, this.room, new CreateSplitEventRequest(1000, 3), currentTime);
        ResponseEntity responseEntity = this.userSplitEventService.receiveSplitRandomly(this.fromUser, this.room, new SplitEventTokenDTO(token), availableTime);
        assertThat(responseEntity.getBody()).isInstanceOf(DefaultResponse.class);
        DefaultResponse response = (DefaultResponse) responseEntity.getBody();

        assert response != null;
        assert response.getResultCode() == ResultEnum.SPLITEVENT_CREATOR.getCode();
    }

    /*
     * 뿌리기 건당 한번만 받을 수 있음
     * 뿌리기 User : fromUser
     * 받기 User : toUser
     * */
    @Test
    public void testReceive_fail_2() {
        long currentTime = TimeAndDateUtil.getCurrentTimeMiliSec();
        long availableTime = TimeAndDateUtil.getCurrentTimeMiliSec();

        final String token = this.splitEventService.splitRandomly(this.fromUser, this.room, new CreateSplitEventRequest(1000, 3), currentTime);

        // first
        ResponseEntity firstResponseEntity = this.userSplitEventService.receiveSplitRandomly(this.toUser, this.room, new SplitEventTokenDTO(token), availableTime);

        assertThat(firstResponseEntity.getBody()).isInstanceOf(ReceiveSplitEventResponse.class);
        ReceiveSplitEventResponse firstResponse = (ReceiveSplitEventResponse) firstResponseEntity.getBody();

        assert firstResponse != null;
        assert firstResponse.getResultCode() == ResultEnum.OK.getCode();
        assert firstResponse.getReceiveAmount() >= 0L;

        // second
        ResponseEntity secondResponseEntity = this.userSplitEventService.receiveSplitRandomly(this.toUser, this.room, new SplitEventTokenDTO(token), availableTime);
        assertThat(secondResponseEntity.getBody()).isInstanceOf(DefaultResponse.class);
        DefaultResponse secondResponse = (DefaultResponse) secondResponseEntity.getBody();

        assert secondResponse != null;
        assert secondResponse.getResultCode() == ResultEnum.SPLITEVENT_ALREADY_APPLY.getCode();
    }

    /*
     * 뿌리기 건당 한번만 받을 수 있음
     * 뿌리기 User : fromUser
     * 받기 User : toUser
     * */
    @Test
    public void testReceive_fail_3() {
        long currentTime = TimeAndDateUtil.getCurrentTimeMiliSec();
        long availableTime = TimeAndDateUtil.getCurrentTimeMiliSec();

        final String token = this.splitEventService.splitRandomly(this.fromUser, this.room, new CreateSplitEventRequest(1000, 3), currentTime);

        // first
        ResponseEntity firstResponseEntity = this.userSplitEventService.receiveSplitRandomly(this.toUser, this.room, new SplitEventTokenDTO(token), availableTime);

        assertThat(firstResponseEntity.getBody()).isInstanceOf(ReceiveSplitEventResponse.class);
        ReceiveSplitEventResponse firstResponse = (ReceiveSplitEventResponse) firstResponseEntity.getBody();

        assert firstResponse != null;
        assert firstResponse.getResultCode() == ResultEnum.OK.getCode();
        assert firstResponse.getReceiveAmount() >= 0L;

        // second
        ResponseEntity secondResponseEntity = this.userSplitEventService.receiveSplitRandomly(this.toUser, this.room, new SplitEventTokenDTO(token), availableTime);
        assertThat(secondResponseEntity.getBody()).isInstanceOf(DefaultResponse.class);
        DefaultResponse secondResponse = (DefaultResponse) secondResponseEntity.getBody();

        assert secondResponse != null;
        assert secondResponse.getResultCode() == ResultEnum.SPLITEVENT_ALREADY_APPLY.getCode();
    }

    /*
     * 뿌리기가 호출된 대화방과 동일한 대화방에 속한 사용자만이 받을 수 있음
     * 뿌리기 User : fromUser
     * 받기 User : notAttedee
     * */
    @Test
    public void testReceive_fail_4() {
        long currentTime = TimeAndDateUtil.getCurrentTimeMiliSec();
        long availableTime = TimeAndDateUtil.getCurrentTimeMiliSec();

        final String token = this.splitEventService.splitRandomly(this.fromUser, this.room, new CreateSplitEventRequest(1000, 3), currentTime);

        ResponseEntity responseEntity = this.userSplitEventService.receiveSplitRandomly(this.notAttedee, this.room, new SplitEventTokenDTO(token), availableTime);

        assertThat(responseEntity.getBody()).isInstanceOf(DefaultResponse.class);
        DefaultResponse response = (DefaultResponse) responseEntity.getBody();

        assert response != null;
        assert response.getResultCode() == ResultEnum.ROOM_NOT_ATTENDEE.getCode();
    }

    /*
     * 뿌린 건은 10분간만 유효
     * 뿌리기 User : fromUser
     * 받기 User : toUser
     * 뿌린 시간  : currentTime
     * 받은 시간 : notAvailableTime
     * */
    @Test
    public void testReceive_fail_5() {
        long currentTime = TimeAndDateUtil.getCurrentTimeMiliSec();
        long notAvailableTime = currentTime + Policy.SPLITEVENT_EXPIRED_TIME + 1L;

        final String token = this.splitEventService.splitRandomly(this.fromUser, this.room, new CreateSplitEventRequest(1000, 3), currentTime);

        ResponseEntity responseEntity = this.userSplitEventService.receiveSplitRandomly(this.toUser, this.room, new SplitEventTokenDTO(token), notAvailableTime);

        assertThat(responseEntity.getBody()).isInstanceOf(DefaultResponse.class);
        DefaultResponse response = (DefaultResponse) responseEntity.getBody();

        assert response != null;
        assert response.getResultCode() == ResultEnum.SPLITEVENT_EXPIRED.getCode();
    }

    /*
     * 조회 기능 테스트
     * 뿌리기 User : fromUser
     * 받기 User : toUser
     * */
    @Test
    public void test_history() {
        long currentTime = TimeAndDateUtil.getCurrentTimeMiliSec();
        long availableTime = TimeAndDateUtil.getCurrentTimeMiliSec();

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
        long currentTime = TimeAndDateUtil.getCurrentTimeMiliSec();

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
        long currentTime = TimeAndDateUtil.getCurrentTimeMiliSec();

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
        long currentTime = TimeAndDateUtil.getCurrentTimeMiliSec();
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
