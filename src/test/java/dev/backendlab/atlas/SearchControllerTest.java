package dev.backendlab.atlas;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "atlas.content-root=src/test/resources/content",
                "atlas.auth-token=test-atlas-token",
                "forge.auth-token=test-forge-token"
        }
)
class SearchControllerTest {

    @LocalServerPort
    private int port;

    @Test
    void returnsMatchingLinesAsJson() throws Exception {
        HttpResponse<String> response = get("/search?q=KAFKA");

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.headers().firstValue("content-type")).hasValueSatisfying(
                contentType -> assertThat(contentType).startsWith("application/json")
        );
        assertThat(response.body()).isEqualTo(
                """
                {"query":"KAFKA","count":1,"results":[{"path":"/cv/skills.txt","line_number":2,"line":"messaging: Kafka"}]}"""
        );
    }

    @Test
    void rejectsBlankQuery() throws Exception {
        assertThat(get("/search?q=%20%20").statusCode()).isEqualTo(400);
    }

    @Test
    void rejectsMissingQuery() throws Exception {
        assertThat(get("/search").statusCode()).isEqualTo(400);
    }

    @Test
    void rejectsMissingAuthentication() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/search?q=kafka"))
                .GET()
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient()
                .send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(401);
        assertThat(response.headers().firstValue("www-authenticate"))
                .contains("Bearer");
    }

    private HttpResponse<String> get(String path) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + path))
                .header("Authorization", "Bearer test-atlas-token")
                .GET()
                .build();

        return HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
    }
}
