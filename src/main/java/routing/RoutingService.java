package routing;

import components.Endpoint;
import components.RoutingRequest;
import components.RoutingResult;
import components.Service;


public interface RoutingService {
    void register(Service service, Endpoint endpoint);

    void deregister(Service service, Endpoint endpoint);

    RoutingResult route(RoutingRequest request);

}
