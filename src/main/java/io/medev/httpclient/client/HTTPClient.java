package io.medev.httpclient.client;

import io.medev.httpclient.request.Request;
import io.medev.httpclient.Response;
import io.medev.httpclient.parser.ResponseParser;

import java.io.IOException;

public interface HTTPClient {

    <T> Response<T> execute(Request request, ResponseParser<? extends T> parser) throws IOException;
}
