package com.freemanan.microservicebase.grpc;

import static com.freemanan.microservicebase.grpc.GrpcProperties.PREFIX;

import com.freemanan.microservicebase.core.Const;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Freeman
 * @since 1.0.0
 */
@Getter
@Setter
@ConfigurationProperties(PREFIX)
public class GrpcProperties {

    public static final String PREFIX = Const.CONFIGURATION_PROPERTIES_PREFIX + ".grpc";

    private Client client = new Client();
    private Server server = new Server();

    @Getter
    @Setter
    public static class Client {

        /**
         * Whether to enable gRPC client.
         */
        private boolean enabled = true;
        /**
         * Whether to enable server parameter validation.
         */
        private boolean validationEnabled = true;
        /**
         * gRPC client config.
         * <p> e.g. service-name: localhost:9090
         */
        private Map<String, String> stubs = new LinkedHashMap<>();
    }

    @Getter
    @Setter
    public static class Server {

        /**
         * Whether to enable gRPC server.
         */
        private boolean enabled = true;
        /**
         * Whether to enable debug mode, only enabled in dev.
         */
        private boolean debugEnabled = false;
        /**
         * Whether to enable exception handler.
         */
        private boolean exceptionHandlerEnabled = true;
        /**
         * Whether to enable server parameter validation.
         */
        private boolean validationEnabled = true;
        /**
         * The gRPC server port to bind to, or 0 to use a random port, default is 8080.
         */
        private int port = 8080;

        private Health health = new Health();
        private Monitoring monitoring = new Monitoring();
    }

    @Getter
    @Setter
    public static class Health {

        private DataSource dataSource = new DataSource();
        private Redis redis = new Redis();

        @Getter
        @Setter
        public static class DataSource {

            /**
             * Whether to enable DataSource health check, enabled by default.
             */
            private boolean enabled = true;
            /**
             * Validation query SQL, default is "SELECT 1;".
             */
            private String validationQuery = "SELECT 1;";
        }

        @Getter
        @Setter
        public static class Redis {

            /**
             * Whether to enable Redis health check, enabled by default.
             */
            private boolean enabled = true;
        }
    }

    @Getter
    @Setter
    public static class Monitoring {

        private Metrics metrics = new Metrics();

        @Getter
        @Setter
        public static class Metrics {

            /**
             * Whether to enable metrics, enabled by default.
             */
            private boolean enabled = true;
            /**
             * The Http server port to bind to, or 0 to use a random port, default is 9999.
             */
            private int port = 9999;
        }
    }
}
