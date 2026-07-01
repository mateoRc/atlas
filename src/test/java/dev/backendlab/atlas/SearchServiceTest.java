package dev.backendlab.atlas;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class SearchServiceTest {

    @TempDir
    private Path contentRoot;

    @Test
    void findsMatchingLinesRecursivelyAndIgnoresCase() throws IOException {
        Path cv = Files.createDirectories(contentRoot.resolve("cv"));
        Files.writeString(cv.resolve("skills.txt"), """
                languages: Java
                messaging: Kafka
                kafka streams
                """);

        var results = new SearchService(contentRoot).search("KAFKA");

        assertThat(results).containsExactly(
                new SearchResult("/cv/skills.txt", 2, "messaging: Kafka"),
                new SearchResult("/cv/skills.txt", 3, "kafka streams")
        );
    }

    @Test
    void returnsEmptyResultsWhenNothingMatches() throws IOException {
        Files.writeString(contentRoot.resolve("skills.txt"), "languages: Java");

        assertThat(new SearchService(contentRoot).search("kafka")).isEmpty();
    }

    @Test
    void returnsEmptyResultsWhenContentDirectoryDoesNotExist() {
        assertThat(new SearchService(contentRoot.resolve("missing")).search("kafka")).isEmpty();
    }
}

