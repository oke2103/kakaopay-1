package com.juns.pay.runner;

import java.sql.Connection;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class H2DatabaseRunner implements ApplicationRunner {

    @Autowired
    DataSource dataSource;

    @Value("${spring.jpa.hibernate.ddl-auto}")
    private String hibernateDDLAuto;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        try (Connection connection = this.dataSource.getConnection()) {
            log.info("* jdbc url : {}", connection.getMetaData().getURL());
            log.info("* jdbc username : {}", connection.getMetaData().getUserName());
            log.info("* hibernateDDLAuto : " + this.hibernateDDLAuto);
            log.info("============================================================================================");
            log.info("============================================================================================");
        }
    }
}
