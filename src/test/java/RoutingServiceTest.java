import components.Endpoint;
import components.RoutingRequest;
import components.RoutingResult;
import components.Service;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import routing.RoutingService;
import routing.impl.RoutingServiceImpl;
import storage.ConcurrentRouteStorage;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class RoutingServiceTest {
    private RoutingService routingService;
    private ConcurrentRouteStorage routeStorage;

    @BeforeEach
    void setup() throws Exception {
        routeStorage = new ConcurrentRouteStorage();
        routingService = new RoutingServiceImpl(routeStorage);
    }

    @AfterEach
    void tearDown() throws Exception {
        routeStorage = null;
        routingService = null;
    }

    @Test
    public void givenServiceRegisteredWhenRetrievedThenReturnsCorrectEndpoint() {
        routingService.register(new Service("payments-api"), new Endpoint("10.0.1.21", 8080));
        RoutingResult result = routingService.route(new RoutingRequest("payments-api"));

        assertEquals("10.0.1.21", result.endpoint().host());
        assertEquals(8080, result.endpoint().port());
    }

    @Test
    public void givenServiceRegisteredWhenDeletedThenRetrievedReturnsCorrectEndpoint() {
        routingService.register(new Service("payments-api"), new Endpoint("10.0.1.21", 8080));

        routingService.deregister(new Service("payments-api"), new Endpoint("10.0.1.21", 8080));
        RoutingResult result = routingService.route(new RoutingRequest("payments-api"));

        assertNull(result);
        assertEquals(Collections.emptySet(), routeStorage.getServiceNames());
        assertEquals(Collections.emptySet(), routeStorage.getRoutes(new Service("payments-api")));
    }
}
