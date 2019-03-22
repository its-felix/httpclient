package dev.codeflush.httpclient.parser;

import dev.codeflush.httpclient.client.HTTPClient;
import dev.codeflush.httpclient.request.Request;

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
