package dev.backendlab.atlas;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;

@RestController
class SearchController {

    private final SearchService searchService;
    private final TelemetryDispatcher telemetry;

    SearchController(SearchService searchService, TelemetryDispatcher telemetry) {
        this.searchService = searchService;
        this.telemetry = telemetry;
    }

    @GetMapping("/search")
    SearchResponse search(@RequestParam String q) {
        if (q.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Query must not be blank");
        }

        long started = System.nanoTime();
        int exitCode = 0;
        try {
            return SearchResponse.of(q, searchService.search(q));
        } catch (RuntimeException exception) {
            exitCode = 1;
            throw exception;
        } finally {
            long durationMs = Duration.ofNanos(System.nanoTime() - started).toMillis();
            telemetry.recordSearch(durationMs, exitCode);
        }
    }
}
