package io.medev.httpclient.client;

import io.medev.httpclient.*;
import io.medev.httpclient.parser.ResponseParser;
import io.medev.httpclient.request.Request;
import io.medev.httpclient.request.body.RequestBody;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

public class SimpleHTTPClient implements HTTPClient {

    private final Map<String, String> headers;

    public SimpleHTTPClient(Map<String, String> headers) {
        this.headers = Objects.requireNonNull(headers);
    }

    public SimpleHTTPClient() {
        this(Collections.emptyMap());
    }

    @Override
    public <T> Response<T> execute(Request request, ResponseParser<? extends T> parser) throws IOException {
        URL url = request.getRequestURL();
        URLConnection _conn = url.openConnection();

        if (!(_conn instanceof HttpURLConnection)) {
            throw new IllegalArgumentException("not a http request");
        }

        HttpURLConnection conn = (HttpURLConnection) _conn;
        addHeaders(conn, this.headers);
        addHeaders(conn, request.getHeaders());

        RequestMethod method = request.getMethod();
        conn.setRequestMethod(method.getName());

        if (method.supportsRequestBody()) {
            RequestBody body = request.getBody();

            if (body != null) {
                conn.setRequestProperty("Content-Type", body.getContentType());
                body.getContentLength().map(Object::toString).ifPresent((v) -> conn.setRequestProperty("Content-Length", v));

                conn.setDoOutput(true);

                try (OutputStream out = conn.getOutputStream()) {
                    body.write(out);
                }
            }
        }

        int responseCode = conn.getResponseCode();
        Map<String, List<String>> responseHeaders = conn.getHeaderFields();
        String contentType = null;
        String charset = null;

        if (responseHeaders.containsKey("Content-Type")) {
            List<String> contentTypeFields = responseHeaders.get("Content-Type");

            if (!contentTypeFields.isEmpty()) {
                contentType = contentTypeFields.get(0);
            }

            if (contentTypeFields.size() >= 2) {
                String secondPart = contentTypeFields.get(1);
                int index = secondPart.indexOf('=');

                if (index != -1) {
                    charset = secondPart.substring(index + 1);
                }
            }
        }

        InputStreamSupplier inputStreamSupplier;
        if (responseCode < 400) {
            inputStreamSupplier = conn::getInputStream;
        } else {
            inputStreamSupplier = conn::getErrorStream;
        }

        T value;
        try (InputStream stream = inputStreamSupplier.getInputStream()) {
            if (stream != null) {
                value = parser.parse(this, request, responseCode, stream, responseHeaders, contentType, charset);
            } else {
                value = parser.getFallback(this, request);
            }
        }

        return new Response<>(this, request, parser, responseCode, responseHeaders, contentType, charset, value);
    }

    private static void addHeaders(HttpURLConnection conn, Map<String, String> headers) {
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            conn.setRequestProperty(entry.getKey(), entry.getValue());
        }
    }

}
