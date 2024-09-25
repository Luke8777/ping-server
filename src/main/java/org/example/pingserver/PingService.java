package org.example.pingserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class PingService {

    private static final Logger logger = LoggerFactory.getLogger(PingService.class);
    private final WebClient webClient = WebClient.create("http://localhost:8081");

    // Rate limiting across Ping instances
    private final AtomicInteger globalRequestCounter = new AtomicInteger(0);
    private final File lockFile = new File("C:\\Users\\Administrator\\Desktop\\ping.lock");

    public void sendPings() {
        // Non-blocking interval to send pings every second
        Mono.delay(Duration.ofSeconds(1))
            .flatMap(tick -> attemptPing())
            .subscribe();
    }

    private Mono<Object> attemptPing() {
        // Check if we are rate-limiting across processes
        if (!acquireRateLimitLock()) {
            logger.info("Request not sent due to rate limit.");
            return Mono.empty();  // Don't proceed if rate-limited
        }

        // Send the request asynchronously to the Pong service
        return webClient.get().uri("/pong")
                .exchangeToMono(response -> {
                    if (response.statusCode().is2xxSuccessful()) {
                        logger.info("Request sent & Pong responded: World");
                    } else if (response.statusCode().value() == 429) {
                        logger.info("Request sent & Pong throttled it.");
                    }
                    return Mono.empty();
                })
                .doOnError(error -> logger.error("Failed to send request", error));
    }

    /**
     * This method tries to acquire a lock to ensure that across all `Ping` instances,
     * only 2 requests per second are allowed.
     */
    private boolean acquireRateLimitLock() {
        try (RandomAccessFile file = new RandomAccessFile(lockFile, "rw");
             FileLock lock = file.getChannel().tryLock()) {

            if (lock != null) {
                // Increase the request count and check if we exceed the limit of 2 requests per second
                if (globalRequestCounter.incrementAndGet() <= 2) {
                    return true;  // Allow the request
                } else {
                    globalRequestCounter.set(0);  // Reset for the next second
                    return false;  // Deny the request
                }
            }
        } catch (Exception e) {
            logger.error("Error while acquiring rate limit lock", e);
        }
        return false;  // If lock not acquired, deny the request
    }
}
