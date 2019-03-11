package io.medev.httpclient.parser;

import io.medev.httpclient.request.Request;
import io.medev.httpclient.client.HTTPClient;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class NoOpResponseParser implements ResponseParser<Void> {
    @Override
    public Void parse(HTTPClient client, Request request, int responseCode, InputStream stream, Map<String, List<String>> headers, String contentType, String charset) throws IOException {
        return null;
    }
}
