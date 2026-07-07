package dev.backendlab.atlas;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
class HealthController {
    private final long startedAtNanos;

    HealthController() {
        this(System.nanoTime());
    }

    HealthController(long startedAtNanos) {
        this.startedAtNanos = startedAtNanos;
    }

    @GetMapping(value = "/healthz", produces = MediaType.TEXT_PLAIN_VALUE)
    String health() {
        return "ok";
    }

    @GetMapping("/status")
    Map<String, Object> status() {
        long uptimeSeconds = TimeUnit.NANOSECONDS.toSeconds(
                System.nanoTime() - startedAtNanos
        );
        return Map.of(
                "status", "ok",
                "uptime_seconds", uptimeSeconds
        );
    }
}
