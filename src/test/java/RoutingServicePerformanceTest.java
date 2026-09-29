import components.Endpoint;
import components.Service;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import storage.ConcurrentRouteStorage;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RoutingServicePerformanceTest {
    static HttpRoutingServer routingService;
    static URI apiPaymentUri;
    static ConcurrentRouteStorage storage;
    ExecutorService executor = Executors.newFixedThreadPool(
            Runtime.getRuntime().availableProcessors());

    @BeforeAll
    static void setup() throws Exception {
        Properties properties = new Properties();
        storage = new ConcurrentRouteStorage();
        routingService = new HttpRoutingServer(properties, storage);
        apiPaymentUri = new URI("http://127.0.0.1:8999/routes/api-payments");
    }

    @AfterAll
    static void tearDown() throws Exception {
        routingService.stop();
        routingService = null;
    }

    @AfterEach
    void shutdownExecutor() {
        executor.shutdownNow();
    }

    @Test
    public void givenMultipleRequestsSubmittedRepeatedly_WhenProcessed_ThenReportedEndpointsRemainCorrect()
            throws Exception {
        Future<?> put = executor.submit(() -> {
            simulatePut("90.0.1.21:1010");
        });
        put.get(5, TimeUnit.SECONDS);

        Future<?> delete = executor.submit(() -> {
            try {
                simulateDelete("payments-api", "90.0.1.21:1010");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        delete.get(5, TimeUnit.SECONDS);

        Future<?> putAgain = executor.submit(() -> {
            simulatePut("90.0.1.21:1010");
        });
        putAgain.get(5, TimeUnit.SECONDS);

        assertEquals(Set.of("payments-api"), storage.getServiceNames());
        assertEquals(Set.of(new Endpoint("90.0.1.21", 1010)),
                storage.getRoutes(new Service("payments-api")));
    }

    public void simulatePut(String endpoint) {
        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpRequest putRequest = HttpRequest.newBuilder().uri(apiPaymentUri)
                    .PUT(HttpRequest.BodyPublishers.ofString(endpoint)).build();
            HttpResponse<String> response = client.send(putRequest,
                    HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Put request failed, unexpected error in test set up", e);
        }
    }

    public void simulateDelete(String service, String endpoint) throws Exception {
        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpRequest deleteRequest = HttpRequest.newBuilder().uri(apiPaymentUri)
                    .method("DELETE", HttpRequest.BodyPublishers.ofString(service + "," + endpoint))
                    .build();
            HttpResponse<String> response = client.send(deleteRequest,
                    HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());
            assertTrue(response.body().contains("\"success\":true"));
        }
    }
}
