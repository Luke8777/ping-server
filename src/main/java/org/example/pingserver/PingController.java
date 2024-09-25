package org.example.pingserver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PingController {

    private final PingService pingService;

    @Autowired
    public PingController(PingService pingService) {
        this.pingService = pingService;
        this.pingService.sendPings();  // Start pinging on service startup
    }

    @GetMapping("/ping")
    public String ping() {
        //pingService.sendPings();
        return "Ping service is running";
    }
}