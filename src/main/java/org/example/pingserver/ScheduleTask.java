package org.example.pingserver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduleTask {

    @Autowired
    private PingService pingService;

    @Scheduled(cron = "0/1 * * * * ?") //每1秒执行一次
    public void task() {
        pingService.sendPings();
    }


}
