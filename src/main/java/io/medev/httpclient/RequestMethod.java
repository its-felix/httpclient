package io.medev.httpclient;

public enum RequestMethod {

    HEAD("HEAD", false),
    GET("GET", false),
    POST("POST", true),
    PUT("PUT", true),
    PATCH("PATCH", true),
    DELETE("DELETE", false);

    public static final RequestMethod DEFAULT = GET;

    private final String name;
    private final boolean supportsRequestBody;

    RequestMethod(String name, boolean supportsRequestBody) {
        this.name = name;
        this.supportsRequestBody = supportsRequestBody;
    }

    public String getName() {
        return this.name;
    }

    public boolean supportsRequestBody() {
        return this.supportsRequestBody;
    }
}
