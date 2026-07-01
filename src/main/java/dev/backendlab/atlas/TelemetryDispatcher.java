package dev.backendlab.atlas;

import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@Component
class TelemetryDispatcher {

    private static final System.Logger LOGGER =
            System.getLogger(TelemetryDispatcher.class.getName());

    private final ArrayBlockingQueue<TelemetryEvent> queue;
    private final ForgeClient forgeClient;
    private final AtomicLong dropped = new AtomicLong();
    private final Thread worker;

    private volatile boolean running = true;

    TelemetryDispatcher(
            ForgeClient forgeClient,
            @Value("${telemetry.queue-size:1000}") int capacity
    ) {
        this.forgeClient = forgeClient;
        this.queue = new ArrayBlockingQueue<>(Math.max(1, capacity));
        this.worker = Thread.ofPlatform()
                .name("forge-telemetry")
                .daemon(true)
                .start(this::run);
    }

    void recordSearch(long durationMs, int exitCode) {
        boolean accepted = queue.offer(new TelemetryEvent(
                "atlas",
                "search.executed",
                "search",
                durationMs,
                exitCode
        ));
        if (!accepted) {
            long count = dropped.incrementAndGet();
            LOGGER.log(System.Logger.Level.WARNING,
                    "Telemetry event dropped; total dropped: {0}", count);
        }
    }

    long dropped() {
        return dropped.get();
    }

    @PreDestroy
    void close() {
        running = false;
        worker.interrupt();
        try {
            worker.join(1000);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
        }
    }

    private void run() {
        while (running || !queue.isEmpty()) {
            try {
                TelemetryEvent event = queue.poll(100, TimeUnit.MILLISECONDS);
                if (event != null) {
                    forgeClient.send(event);
                }
            } catch (InterruptedException exception) {
                if (running) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }
    }
}

