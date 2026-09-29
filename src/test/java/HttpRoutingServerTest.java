import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import storage.ConcurrentRouteStorage;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpRoutingServerTest {
    public static final String PAYMENTS_API = "payments-api";
    public static final String IP_ADDRESS_PORT = "127.0.0.1:8080";
    static HttpRoutingServer routingService;

    @BeforeAll
    static void setup() throws Exception {
        Properties properties = new Properties();
        properties.setProperty(PAYMENTS_API, IP_ADDRESS_PORT);
        routingService = new HttpRoutingServer(properties, new ConcurrentRouteStorage());
    }

    @AfterAll
    static void tearDown() throws Exception {
        routingService.stop();
        routingService = null;
    }

    @Test
    public void givenRoutesRequestReceivedWhenGetSentThenServiceResponds200() throws Exception {
        URL url = new URI("http://127.0.0.1:8999/routes/").toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setDoOutput(true);
        assertEquals(HttpURLConnection.HTTP_OK, connection.getResponseCode());
    }

    @Test
    public void givenServiceRegisteredWhenRoutesRequestReceivedThenServiceRespondsWithCorrectDetails() throws Exception {
        try(HttpClient client = HttpClient.newHttpClient()) {
            // Given
            URI uri = new URI("http://127.0.0.1:8999/routes/api-payments/");
            HttpRequest putRequest = HttpRequest.newBuilder()
                    .uri(uri)
                    .header("Accept", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString("90.0.1.21:1010"))
                    .build();
            HttpResponse<String> response = client.send(putRequest,
                    HttpResponse.BodyHandlers.ofString());
            assertEquals(HttpURLConnection.HTTP_OK, response.statusCode());

            // When
            uri = new URI("http://127.0.0.1:8999/routes/");
            HttpRequest getRequest = HttpRequest.newBuilder()
                    .uri(uri)
                    .header("Content-Type", "application/json")
                    .GET()
                    .build();
            response = client.send(getRequest, HttpResponse.BodyHandlers.ofString());

            // Then
            assertEquals("{\"available\":{\"" + PAYMENTS_API + "\"}}",
                    response.body());
        }
    }

    @Test
    public void givenRoutesRequestReceivedWhenPutSentThenServiceResponds404() throws Exception {
        URL url = new URI("http://127.0.0.1:8999/routes/").toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        assertEquals(HttpURLConnection.HTTP_NOT_FOUND, connection.getResponseCode());

        connection = (HttpURLConnection) url.openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod("HEAD");
        assertEquals(HttpURLConnection.HTTP_NOT_FOUND, connection.getResponseCode());

        connection = (HttpURLConnection) url.openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod("OPTIONS");
        assertEquals(HttpURLConnection.HTTP_NOT_FOUND, connection.getResponseCode());

        connection = (HttpURLConnection) url.openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod("PUT");
        assertEquals(HttpURLConnection.HTTP_NOT_FOUND, connection.getResponseCode());

        connection = (HttpURLConnection) url.openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod("DELETE");
        assertEquals(HttpURLConnection.HTTP_NOT_FOUND, connection.getResponseCode());

        connection = (HttpURLConnection) url.openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod("TRACE");
        assertEquals(HttpURLConnection.HTTP_NOT_FOUND, connection.getResponseCode());
    }

    @Test
    public void givenApiPaymentsRoutesRegistersEndpointWhenGetRequestThenReturnRegisteredEndpoints() throws Exception {
        try(HttpClient client = HttpClient.newHttpClient()) {

            // Given
            URI apiPaymentUri = new URI("http://127.0.0.1:8999/routes/api-payments");

            HttpRequest putRequest = HttpRequest.newBuilder()
                    .uri(apiPaymentUri)
                    .PUT(HttpRequest.BodyPublishers.ofString("90.0.1.21:1010"))
                    .build();

            HttpResponse<String> response = client.send(
                    putRequest,
                    HttpResponse.BodyHandlers.ofString());

            assertEquals(HttpURLConnection.HTTP_OK, response.statusCode());

            // When
            HttpRequest getRequest = HttpRequest.newBuilder()
                    .uri(apiPaymentUri)
                    .GET()
                    .build();
            response = client.send(getRequest, HttpResponse.BodyHandlers.ofString());

            // Then
            assertEquals(HttpURLConnection.HTTP_OK, response.statusCode());
            assertEquals("{\"" + PAYMENTS_API + "\":\"90.0.1.21:1010\"}", response.body());
        }

    }
}
