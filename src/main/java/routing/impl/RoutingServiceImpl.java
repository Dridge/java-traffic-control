package routing.impl;

import components.Endpoint;
import components.RoutingRequest;
import components.RoutingResult;
import components.Service;
import routing.RoutingService;
import storage.ConcurrentRouteStorage;

import java.util.Set;

public class RoutingServiceImpl implements RoutingService {

    ConcurrentRouteStorage routeStorage;

    public RoutingServiceImpl(ConcurrentRouteStorage routeStorage) {
        this.routeStorage = routeStorage;
    }

    @Override
    public void register(Service service, Endpoint endpoint) {
        routeStorage.registerRoute(service, endpoint);
    }

    @Override
    public void deregister(Service service, Endpoint endpoint) {
        routeStorage.deregister(service, endpoint);
    }

    @Override
    public RoutingResult route(RoutingRequest request) {
        Set<Endpoint> endpointSet = routeStorage.getRoutes(new Service(request.serviceName()));
        if (endpointSet.iterator().hasNext()) {
            Endpoint endpoint = endpointSet.iterator().next();
            RoutingResult result = new RoutingResult(endpoint);
            return result;
        }
        return null;
    }
}
