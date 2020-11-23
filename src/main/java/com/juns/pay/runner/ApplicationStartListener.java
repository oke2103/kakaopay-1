package com.juns.pay.runner;

import java.text.SimpleDateFormat;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Order(1)
public class ApplicationStartListener implements ApplicationListener<ApplicationStartedEvent> {


    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {

        log.info("============================================================================================");
        log.info("============================================================================================");
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy년 MM월 dd일 HH시 mm분 ss초");
            log.info("현재 시간 : " + format.format(new Date()));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        log.info("============================================================================================");
    }
}
