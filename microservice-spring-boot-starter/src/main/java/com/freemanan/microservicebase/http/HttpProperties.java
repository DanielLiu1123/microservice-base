package com.freemanan.microservicebase.http;

import static com.freemanan.microservicebase.http.HttpProperties.PREFIX;

import com.freemanan.microservicebase.core.Const;
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
public class HttpProperties {
    public static final String PREFIX = Const.CONFIGURATION_PROPERTIES_PREFIX + ".http";

    private boolean enabled = true;
    private Logging logging;

    @Getter
    @Setter
    public static class Logging {
        private boolean enabled = true;
        private String[] includePatterns = {"/**"};
        private String[] excludePatterns = {};
    }
}
