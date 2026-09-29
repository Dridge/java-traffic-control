package routes;

import com.sun.net.httpserver.HttpExchange;
import components.Endpoint;
import components.Service;
import storage.ConcurrentRouteStorage;
import utils.EndpointMapper;
import utils.ServiceMapper;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Set;

/**
 * Register and remove specific endpoints for Payments API
 */
public class PaymentsApiHandler extends RoutesHttpHandler {
    private static final Service PAYMENTS_API = new Service("payments-api");

    public PaymentsApiHandler(ConcurrentRouteStorage concurrentRouteStorage, ServiceMapper serviceMapper, EndpointMapper endpointMapper) {
        super(concurrentRouteStorage, serviceMapper, endpointMapper);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();

        switch (method) {
            case "GET":
                getHandle(exchange);
                break;
            case "PUT":
                putHandle(exchange);
                break;
            case "DELETE":
                delete(exchange);
                break;
            default:
                errorHandle(exchange);
                break;
        }
    }

    private void delete(HttpExchange exchange) throws IOException {
        String body = readBody(exchange);

        Endpoint endpoint = endpointMapper.map(body);

        boolean isDeregistered = storage.deregister(PAYMENTS_API, endpoint);
        String response = "{\"success\":" + isDeregistered + "}";
        int statusCode = HttpURLConnection.HTTP_INTERNAL_ERROR;
        if(isDeregistered){
            statusCode = HttpURLConnection.HTTP_OK;
        }
        sendResponse(exchange, statusCode, response);
    }

    private void putHandle(HttpExchange exchange) throws IOException {
        String body = readBody(exchange);

        Endpoint endpoint = endpointMapper.map(body);

        storage.registerRoute(PAYMENTS_API, endpoint);
        String response = "{\"success\":true}";
        sendResponse(exchange, 200, response);
    }

    protected void getHandle(HttpExchange exchange) throws IOException {
        Set<Endpoint> endpointsForService = storage.getRoutes(PAYMENTS_API);
        String response = "{\"" + PAYMENTS_API.name() + "\":\"" + endpointsForService.iterator().next() + "\"}";
        sendResponse(exchange, 200, response);
    }

    private String readBody(HttpExchange exchange) throws IOException {
        return new String(
                exchange.getRequestBody().readAllBytes(),
                StandardCharsets.UTF_8
        );
    }
}
