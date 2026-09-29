import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import storage.ConcurrentRouteStorage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class App {
    private final static Logger logger = LogManager.getLogger(App.class);

    public static void main(String[] args) {
        new HttpRoutingServer(loadProperties(), new ConcurrentRouteStorage());
    }

    public static Properties loadProperties() {
        // 1. Fetch the configurable home dir, defaulting to a /var/opt/ path if not provided
        String appHome = System.getProperty("app.home", "/var/opt/java-traffic-control");

        // 2. Resolve the properties file inside that home directory
        File configFile = new File(appHome, "config.properties");

        Properties props = new Properties();

        // 3. Stream and load the file from the absolute disk path
        try (FileInputStream in = new FileInputStream(configFile)) {
            props.load(in);
        } catch (IOException e) {
            logger.error("Error while reading config.properties, unable to start service");
        }
        return props;
    }
}
