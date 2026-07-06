package dev.backendlab.atlas;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Component
class ForgeClient {

    private static final Duration TIMEOUT = Duration.ofMillis(300);

    private final URI eventsUri;
    private final String authToken;
    private final HttpClient httpClient;

    ForgeClient(
            @Value("${forge.url:}") String forgeUrl,
            @Value("${forge.auth-token}") String authToken
    ) {
        this.eventsUri = forgeUrl.isBlank()
                ? null
                : URI.create(forgeUrl.replaceAll("/+$", "") + "/events");
        this.authToken = authToken;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(TIMEOUT)
                .build();
    }

    void send(TelemetryEvent event) {
        if (eventsUri == null) {
            return;
        }

        String body = """
                {"service":"%s","event":"%s","name":"%s","duration_ms":%d,"exit_code":%d}"""
                .formatted(
                        event.service(),
                        event.event(),
                        event.name(),
                        event.durationMs(),
                        event.exitCode()
                );
        HttpRequest request = HttpRequest.newBuilder(eventsUri)
                .timeout(TIMEOUT)
                .header("Authorization", "Bearer " + authToken)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        try {
            httpClient.send(request, HttpResponse.BodyHandlers.discarding());
        } catch (IOException ignored) {
            // Telemetry must never fail a search request.
        } catch (InterruptedException interrupted) {
            Thread.currentThread().interrupt();
        }
    }
}
