package com.freemanan.microservicebase.http;

import static com.freemanan.microservicebase.http.HttpProperties.PREFIX;

import com.freemanan.microservicebase.core.Const;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Freeman
 * @since 1.0.0
 */
@Data
@ConfigurationProperties(PREFIX)
public class HttpProperties {
    public static final String PREFIX = Const.CONFIGURATION_PROPERTIES_PREFIX + ".http";

    private boolean enabled = true;
    /**
     * Whether to enable http tracing.
     */
    private boolean tracingEnabled = true;

    private Observability observability = new Observability();

    @Data
    public static class Observability {
        private Logging logging = new Logging();
        private Metrics metrics = new Metrics();
        private DistributedTracing distributedTracing = new DistributedTracing();

        @Data
        public static class Logging {
            private boolean enabled = true;
            private String[] includePatterns = {"/**"};
            private String[] excludePatterns = {};
        }

        @Data
        public static class Metrics {
            private boolean enabled = true;
        }

        @Data
        public static class DistributedTracing {
            private boolean enabled = true;
        }
    }
}
