package utils;

import components.Endpoint;
import components.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Take properties and convert them into a map of Service to a list of endpoints
 */
public class PropertiesRouteConfigMapper {
    public Map<Service, List<Endpoint>> map(Properties properties) {
        return properties.entrySet().stream()
                .flatMap(entry -> Arrays.stream(entry.getValue().toString().split(","))
                    .map(entryPointString -> Map.entry(
                        new Service(entry.getKey().toString()),
                        parseEndPoint(entryPointString)
                    ))
                )
                .collect(Collectors.groupingBy(
                        Map.Entry::getKey,
                        Collectors.mapping(Map.Entry::getValue, Collectors.toList())
                ));
    }

    private Endpoint parseEndPoint(String endPointString) {
        String[] parts = endPointString.split(":");
        return new Endpoint(parts[0], Integer.parseInt(parts[1]));
    }
}
