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
        assertThat(new HealthController(System.nanoTime() - 3_000_000_000L).status())
                .containsEntry("status", "ok")
                .containsEntry("uptime_seconds", 3L);
    }
}
