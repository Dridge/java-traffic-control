package storage;

import components.Endpoint;
import components.Service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Collectors;

/**
 * An in memory storage for route information, pojo for now
 */
public class ConcurrentRouteStorage {

    private final ConcurrentMap<Service, Set<Endpoint>> routeRegistry = new ConcurrentHashMap<>();

    public void registerRoute(Service service, Endpoint endpoint) {
        // ??? Use CopyOnWriteArraySet for the inner sets to ensure thread-safe iteration/modification
        //routeRegistry.computeIfAbsent(service, ignored -> new CopyOnWriteArraySet<>()).add(endpoint);

        routeRegistry.computeIfAbsent(service, ignored -> ConcurrentHashMap.newKeySet()).add(endpoint);
    }

    public Set<Endpoint> getRoutes(Service service) {
        return routeRegistry.getOrDefault(service, Set.of());
    }

    public Set<String> getServiceNames() {
        return routeRegistry.keySet().stream()
                .map(Service::name)
                .collect(Collectors.toSet());
    }

    public boolean deregister(Service service, Endpoint endpoint) {
        Set<Endpoint> endpoints = routeRegistry.get(service);
        if (endpoints == null) {
            return false;
        }
        if(endpoints.iterator().hasNext()) {
            return endpoints.remove(endpoints.iterator().next());
        }
        return false;
    }
}
