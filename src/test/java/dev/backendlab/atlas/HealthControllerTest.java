package dev.backendlab.atlas;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HealthControllerTest {

    @Test
    void healthReturnsOk() {
        assertThat(new HealthController().health()).isEqualTo("ok");
    }

    @Test
    void statusReturnsUptime() {
        var status = new HealthController(System.nanoTime() - 3_000_000_000L).status();

        assertThat(status).containsEntry("status", "ok");
        assertThat((Long) status.get("uptime_seconds")).isGreaterThanOrEqualTo(3L);
    }
}
