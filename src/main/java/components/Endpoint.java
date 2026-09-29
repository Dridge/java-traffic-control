package components;

public record Endpoint(String host, int port) {
    @Override
    public String toString() {
        return host + ":" + port;
    }
}
