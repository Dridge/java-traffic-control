import com.sun.net.httpserver.HttpServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import routes.PaymentsApiHandler;
import routes.RoutesGeneralHandler;
import storage.ConcurrentRouteStorage;
import utils.EndpointMapper;
import utils.ServiceMapper;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Properties;

public class HttpRoutingServer {
    private final static Logger logger = LogManager.getLogger(HttpRoutingServer.class);
    HttpServer server;
    ConcurrentRouteStorage storage;
    ServiceMapper serviceMapper;
    EndpointMapper endpointMapper;

    public HttpRoutingServer(Properties serviceProperties, ConcurrentRouteStorage storage) {
        this.storage = storage;
        serviceMapper = new ServiceMapper();
        endpointMapper = new EndpointMapper();
        try {
            server = HttpServer.create();
            server.createContext("/routes/", new RoutesGeneralHandler(storage, serviceMapper, endpointMapper));
            server.createContext("/routes/api-payments", new PaymentsApiHandler(storage, serviceMapper, endpointMapper));
            InetSocketAddress address = new InetSocketAddress(8999);
            server.bind(address, 0);
            logger.info("Server started on port 8999");
            server.start();
        } catch (IOException e) {
            logger.info("Server failed to start", e);
        }
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
        }
    }
}
