package routes;

import com.sun.net.httpserver.HttpExchange;
import storage.ConcurrentRouteStorage;
import utils.EndpointMapper;
import utils.ServiceMapper;

import java.io.IOException;
import java.util.stream.Collectors;

/**
 * Request information about routes, not specific endpoints registration etc
 */
public class RoutesGeneralHandler extends RoutesHttpHandler {
    public RoutesGeneralHandler(ConcurrentRouteStorage storage, ServiceMapper serviceMapper, EndpointMapper endpointMapper) {
        super(storage, serviceMapper, endpointMapper);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();

        if (method.equals("GET")) {
            getHandle(exchange);
        } else {
            errorHandle(exchange);
        }
    }

    @Override
    protected void getHandle(HttpExchange exchange) throws IOException {
        String availableServices = storage.getServiceNames().stream()
                .map(serviceName -> "\"" + serviceName + "\"")
                .collect(Collectors.joining(","));
        sendResponse(exchange, 200, "{\"available\":{" + availableServices + "}}");
    }
}
