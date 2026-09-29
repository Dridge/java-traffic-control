package utils;

import components.Endpoint;

/**
 * A mapper that takes a string and maps it into an Endpoint object.
 */
public class EndpointMapper {
    public Endpoint map(String input) {
        String endpointPart = input.trim();
        if (!endpointPart.contains(":")) {
            throw new IllegalArgumentException("Endpoint part must contains ':'");
        }
        return parseHostPort(endpointPart);
    }

    private Endpoint parseHostPort(String endpointPart) {
        String[] hostAndPort = endpointPart.split(":", 2);
        return new Endpoint(hostAndPort[0].trim(), Integer.parseInt(hostAndPort[1].trim()));
    }
}
