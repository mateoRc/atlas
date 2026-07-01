package dev.backendlab.atlas;

record TelemetryEvent(
        String service,
        String event,
        String name,
        long durationMs,
        int exitCode
) {
}

