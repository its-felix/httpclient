package io.medev.httpclient;

import io.medev.httpclient.request.Request;

import java.io.UnsupportedEncodingException;
import java.net.*;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

public class Endpoint {

    public static final String HTTP = "http";
    public static final String HTTPS = "https";

    private final URL url;

    public Endpoint(URL url) {
        this.url = Objects.requireNonNull(url);
    }

    public static Endpoint forURL(String urlStr) {
        URL url;
        try {
            url = new URL(urlStr);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        return new Endpoint(url);
    }

    public static Endpoint forHostAndPort(String protocol, String host, int port) {
        URL url;
        try {
            url = new URL(protocol, host, port, "");
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        return new Endpoint(url);
    }

    public static Endpoint forHostAndPort(String host, int port) {
        return forHostAndPort(HTTPS, host, port);
    }

    public static Endpoint forAddress(String protocol, InetSocketAddress address) {
        return forHostAndPort(address.getHostString(), address.getPort());
    }

    public static Endpoint forAddress(InetSocketAddress address) {
        return forAddress(HTTPS, address);
    }

    public static Endpoint forHost(String protocol, String host) {
        URL url;
        try {
            url = new URL(protocol, host, "");
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        return new Endpoint(url);
    }

    public static Endpoint forHost(String host) {
        return forHost(HTTPS, host);
    }

    public URL getURL() {
        return this.url;
    }

    public Endpoint resolve(String... children) {
        String file = this.url.getPath();
        String query = this.url.getQuery();

        if (!file.startsWith("/")) {
            file += "/";
        }

        file += Arrays.stream(children)
                .map(Endpoint::encode)
                .collect(Collectors.joining("/"));

        if (query != null) {
            file += "?" + query;
        }

        URL url;
        try {
            url = new URL(this.url.getProtocol(), this.url.getHost(), this.url.getPort(), file);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        return new Endpoint(url);
    }

    public Request.Builder<?> request(RequestMethod.RequestMethodWithoutBody method) {
        return Request.Builder.create(this, method);
    }

    public Request.BuilderWithBody<?> request(RequestMethod.RequestMethodWithBody method) {
        return Request.BuilderWithBody.create(this, method);
    }

    private static String encode(String str) {
        try {
            return URLEncoder.encode(str, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
