package dev.backendlab.atlas;

import com.fasterxml.jackson.annotation.JsonProperty;

record SearchResult(
        String path,
        @JsonProperty("line_number") int lineNumber,
        String line
) {
}

