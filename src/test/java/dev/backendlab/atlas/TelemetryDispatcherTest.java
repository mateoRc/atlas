package dev.backendlab.atlas;

import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

class TelemetryDispatcherTest {

    @Test
    void deliversTelemetryInBackground() throws Exception {
        CountDownLatch delivered = new CountDownLatch(1);
        AtomicReference<TelemetryEvent> received = new AtomicReference<>();
        ForgeClient client = new ForgeClient("", "test-forge-token") {
            @Override
            void send(TelemetryEvent event) {
                received.set(event);
                delivered.countDown();
            }
        };
        TelemetryDispatcher dispatcher = new TelemetryDispatcher(client, 10);

        dispatcher.recordSearch(8, 0);

        assertThat(delivered.await(1, TimeUnit.SECONDS)).isTrue();
        assertThat(received.get()).isEqualTo(
                new TelemetryEvent("atlas", "search.executed", "search", 8, 0)
        );
        dispatcher.close();
    }
}
