package com.juns.pay.pay.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.juns.pay.common.DefaultResponse;
import com.juns.pay.common.Policy;
import com.juns.pay.common.enumeration.ResultEnum;
import com.juns.pay.controller.split.request.CreateSplitEventRequest;
import com.juns.pay.domain.split.SplitEventTokenDTO;
import com.juns.pay.service.split.ReceiveSplitEventResponse;
import com.juns.pay.utils.TimeAndDateUtil;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

public class ReceiveServiceTest extends ServiceTest {

    /*
     * 받기 기능 테스트
     * 뿌리기 User : fromUser
     * 받기 User : toUser
     * */
    @Test
    public void test_receive() {
        long currentTime = TimeAndDateUtil.getCurrentTimeMilliSec();
        long availableTime = TimeAndDateUtil.getCurrentTimeMilliSec();

        final String token = this.splitEventService.splitRandomly(this.fromUser, this.room, new CreateSplitEventRequest(1000, 3), currentTime);
        ResponseEntity responseEntity = this.userSplitEventService.receiveSplitRandomly(this.toUser, this.room, new SplitEventTokenDTO(token), currentTime);

        assertThat(responseEntity.getBody()).isInstanceOf(ReceiveSplitEventResponse.class);
        ReceiveSplitEventResponse response = (ReceiveSplitEventResponse) responseEntity.getBody();

        assert response != null;
        assert response.getResultCode() == ResultEnum.OK.getCode();
        assert response.getReceiveAmount() >= 0L;
    }


    /*
     * 뿌리기 건당 한번만 받을 수 있음
     * 뿌리기 User : fromUser
     * 받기 User : toUser
     * */
    @Test
    public void test_receive_fail_1() {
        long currentTime = TimeAndDateUtil.getCurrentTimeMilliSec();
        long availableTime = TimeAndDateUtil.getCurrentTimeMilliSec();

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
     * 자신이 뿌리기한 건은 자신이 받을 수 없음
     * 뿌리기 User : fromUser
     * 받기 User : fromUser
     * */
    @Test
    public void test_receive_fail_2() {
        long currentTime = TimeAndDateUtil.getCurrentTimeMilliSec();
        long availableTime = TimeAndDateUtil.getCurrentTimeMilliSec();

        final String token = this.splitEventService.splitRandomly(this.fromUser, this.room, new CreateSplitEventRequest(1000, 3), currentTime);
        ResponseEntity responseEntity = this.userSplitEventService.receiveSplitRandomly(this.fromUser, this.room, new SplitEventTokenDTO(token), availableTime);
        assertThat(responseEntity.getBody()).isInstanceOf(DefaultResponse.class);
        DefaultResponse response = (DefaultResponse) responseEntity.getBody();

        assert response != null;
        assert response.getResultCode() == ResultEnum.SPLITEVENT_CREATOR.getCode();
    }

    /*
     * 뿌리기가 호출된 대화방과 동일한 대화방에 속한 사용자만이 받을 수 있음
     * 뿌리기 User : fromUser
     * 받기 User : notAttedee
     * */
    @Test
    public void test_receive_fail_3() {
        long currentTime = TimeAndDateUtil.getCurrentTimeMilliSec();
        long availableTime = TimeAndDateUtil.getCurrentTimeMilliSec();

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
    public void test_receive_fail_4() {
        long currentTime = TimeAndDateUtil.getCurrentTimeMilliSec();
        long notAvailableTime = currentTime + Policy.SPLITEVENT_EXPIRED_TIME + 1L;

        final String token = this.splitEventService.splitRandomly(this.fromUser, this.room, new CreateSplitEventRequest(1000, 3), currentTime);

        ResponseEntity responseEntity = this.userSplitEventService.receiveSplitRandomly(this.toUser, this.room, new SplitEventTokenDTO(token), notAvailableTime);

        assertThat(responseEntity.getBody()).isInstanceOf(DefaultResponse.class);
        DefaultResponse response = (DefaultResponse) responseEntity.getBody();

        assert response != null;
        assert response.getResultCode() == ResultEnum.SPLITEVENT_EXPIRED.getCode();
    }

}
