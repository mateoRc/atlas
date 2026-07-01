package dev.backendlab.atlas;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@Service
class SearchService {

    private final Path contentRoot;

    SearchService(@Value("${atlas.content-root:/app/content}") Path contentRoot) {
        this.contentRoot = contentRoot.toAbsolutePath().normalize();
    }

    List<SearchResult> search(String query) {
        if (!Files.isDirectory(contentRoot)) {
            return List.of();
        }

        String normalizedQuery = query.toLowerCase(Locale.ROOT);
        List<SearchResult> results = new ArrayList<>();

        try (var paths = Files.walk(contentRoot)) {
            paths.filter(Files::isRegularFile)
                    .sorted(Comparator.comparing(Path::toString))
                    .forEach(path -> scan(path, normalizedQuery, results));
        } catch (IOException exception) {
            throw new UncheckedIOException("Could not read content directory", exception);
        }

        return List.copyOf(results);
    }

    private void scan(Path path, String query, List<SearchResult> results) {
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            int lineNumber = 0;
            String line;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.toLowerCase(Locale.ROOT).contains(query)) {
                    results.add(new SearchResult(apiPath(path), lineNumber, line));
                }
            }
        } catch (IOException exception) {
            throw new UncheckedIOException("Could not read content file: " + path, exception);
        }
    }

    private String apiPath(Path path) {
        return "/" + contentRoot.relativize(path).toString().replace('\\', '/');
    }
}
