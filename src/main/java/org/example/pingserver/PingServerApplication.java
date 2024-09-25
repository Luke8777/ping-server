package org.example.pingserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PingServerApplication {

    private static final Logger logger = LoggerFactory.getLogger(PingServerApplication.class);

    public static void main(String[] args) {
        logger.info("Starting");
        SpringApplication.run(PingServerApplication.class, args);
    }

}
