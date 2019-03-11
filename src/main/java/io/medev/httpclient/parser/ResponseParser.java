package io.medev.httpclient.parser;

import io.medev.httpclient.request.Request;
import io.medev.httpclient.client.HTTPClient;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public interface ResponseParser<T> {

    T parse(HTTPClient client, Request request, int responseCode, InputStream stream, Map<String, List<String>> headers, String contentType, String charset) throws IOException;
    default T getFallback(HTTPClient client, Request request) throws IOException {
        return null;
    }
}
