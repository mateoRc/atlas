package dev.backendlab.atlas;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Component
class ForgeClient {

    private static final Duration TIMEOUT = Duration.ofMillis(300);

    private final URI eventsUri;
    private final HttpClient httpClient;

    ForgeClient(@Value("${forge.url:}") String forgeUrl) {
        this.eventsUri = forgeUrl.isBlank()
                ? null
                : URI.create(forgeUrl.replaceAll("/+$", "") + "/events");
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(TIMEOUT)
                .build();
    }

    void recordSearch(long durationMs, int exitCode) {
        if (eventsUri == null) {
            return;
        }

        String body = """
                {"service":"atlas","event":"search.executed","name":"search","duration_ms":%d,"exit_code":%d}"""
                .formatted(durationMs, exitCode);
        HttpRequest request = HttpRequest.newBuilder(eventsUri)
                .timeout(TIMEOUT)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        try {
            httpClient.send(request, HttpResponse.BodyHandlers.discarding());
        } catch (Exception ignored) {
            // Telemetry must never fail a search request.
        }
    }
}

