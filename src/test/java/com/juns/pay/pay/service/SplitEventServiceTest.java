package com.juns.pay.pay.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.juns.pay.common.Policy;
import com.juns.pay.controller.split.request.CreateSplitEventRequest;
import com.juns.pay.utils.TimeAndDateUtil;
import org.junit.Test;

public class SplitEventServiceTest extends ServiceTest {

    /*
     * 뿌리기 기능 테스트
     * token : 뿌리기 요청건에 대한 3자리 문자열로 이루어진 고유 token
     * */
    @Test
    public void test_split() {
        long currentTime = TimeAndDateUtil.getCurrentTimeMilliSec();
        final String token = this.splitEventService.splitRandomly(this.fromUser, this.room, new CreateSplitEventRequest(1000, 3), currentTime);
        assertThat(token).isNotEmpty();
        assertThat(token).hasSize(Policy.SPLITEVENT_TOKEN_LENGTH);
    }

}
