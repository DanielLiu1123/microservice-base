package com.freemanan.microservicebase.grpc;

import static com.freemanan.microservicebase.grpc.GrpcProperties.PREFIX;

import com.freemanan.microservicebase.core.Const;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Freeman
 * @since 1.0.0
 */
@Data
@ConfigurationProperties(PREFIX)
public class GrpcProperties {

    public static final String PREFIX = Const.CONFIGURATION_PROPERTIES_PREFIX + ".grpc";

    private Client client = new Client();
    private Server server = new Server();

    /**
     * Whether to enable grpc tracing.
     */
    private boolean tracingEnabled = true;

    @Data
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
        private Map<String, String> channels = new LinkedHashMap<>();
    }

    @Data
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

        // ob
        private Health health = new Health();
        private Observability observability = new Observability();
    }

    @Data
    public static class Health {

        private DataSource dataSource = new DataSource();
        private Redis redis = new Redis();

        @Data
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

        @Data
        public static class Redis {

            /**
             * Whether to enable Redis health check, enabled by default.
             */
            private boolean enabled = true;
        }
    }

    @Data
    public static class Observability {

        private Metrics metrics = new Metrics();

        @Data
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
