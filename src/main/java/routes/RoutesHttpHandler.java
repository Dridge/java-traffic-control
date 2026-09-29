package routes;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import storage.ConcurrentRouteStorage;
import utils.EndpointMapper;
import utils.ServiceMapper;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;

public abstract class RoutesHttpHandler implements HttpHandler {
    ConcurrentRouteStorage storage;
    ServiceMapper serviceMapper;
    EndpointMapper endpointMapper;

    public RoutesHttpHandler(ConcurrentRouteStorage storage, ServiceMapper serviceMapper, EndpointMapper endpointMapper) {
        this.storage = storage;
        this.serviceMapper = serviceMapper;
        this.endpointMapper = endpointMapper;
    }

    protected void getHandle(HttpExchange exchange) throws IOException {
        String response = "{\"status\": \"healthy\", \"message\": \"GET" +
                " request successful, service available\"}";
        sendResponse(exchange, 200, response);
    }

    protected void sendResponse(HttpExchange exchange, int statusCode, String response)
            throws IOException {
        byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, responseBytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
        }
    }

    protected void errorHandle(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(HttpURLConnection.HTTP_NOT_FOUND, -1);
    }
}
