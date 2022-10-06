package com.llw.microservicebase.web;

import com.llw.microservicebase.common.Const;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import static com.llw.microservicebase.web.WebProperties.PREFIX;

/**
 * @author Freeman
 * @since 1.0.0
 */
@Getter
@Setter
@ConfigurationProperties(PREFIX)
public class WebProperties {
    public static final String PREFIX = Const.CONFIGURATION_PROPERTIES_PREFIX + ".web";

    private boolean enabled = true;
    private Logging logging;
    private ExceptionHandler exceptionHandler;

    @Getter
    @Setter
    static class Logging {
        private boolean enabled = true;
    }

    @Getter
    @Setter
    static class ExceptionHandler {
        private boolean enabled = true;
    }
}
