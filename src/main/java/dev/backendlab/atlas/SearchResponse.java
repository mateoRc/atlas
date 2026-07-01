package dev.backendlab.atlas;

import java.util.List;

record SearchResponse(String query, int count, List<SearchResult> results) {

    static SearchResponse of(String query, List<SearchResult> results) {
        return new SearchResponse(query, results.size(), results);
    }
}

