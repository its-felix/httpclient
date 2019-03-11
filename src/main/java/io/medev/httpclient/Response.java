package io.medev.httpclient;

import io.medev.httpclient.client.HTTPClient;
import io.medev.httpclient.parser.ResponseParser;
import io.medev.httpclient.request.Request;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class Response<T> {

    private final HTTPClient client;
    private final Request request;
    private final ResponseParser<? extends T> parser;
    private final int responseCode;
    private final Map<String, List<String>> headers;
    private final String contentType;
    private final String contentTypeCharset;
    private final T value;

    public Response(HTTPClient client, Request request, ResponseParser<? extends T> parser, int responseCode, Map<String, List<String>> headers, String contentType, String contentTypeCharset, T value) {
        this.client = client;
        this.request = request;
        this.parser = parser;
        this.responseCode = responseCode;
        this.headers = headers;
        this.contentType = contentType;
        this.contentTypeCharset = contentTypeCharset;
        this.value = value;
    }

    public HTTPClient getClient() {
        return client;
    }

    public Request getRequest() {
        return request;
    }

    public ResponseParser<? extends T> getParser() {
        return parser;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public Map<String, List<String>> getHeaders() {
        return this.headers;
    }

    public String getContentType() {
        return contentType;
    }

    public String getContentTypeCharset() {
        return contentTypeCharset;
    }

    public T getValue() {
        return value;
    }

    public Response<T> repeat() throws IOException {
        return this.client.execute(this.request, this.parser);
    }
}
