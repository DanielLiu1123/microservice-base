package com.freemanan.microservicebase.core.tracing;

/**
 * @author Freeman
 * @since 1.0.0
 */
public class Header {
    private String key;
    private String value;

    public static Header of(String key, String value) {
        Header header = new Header();
        header.key = key;
        header.value = value;
        return header;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
