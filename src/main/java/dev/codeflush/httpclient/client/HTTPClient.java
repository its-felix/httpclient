package dev.codeflush.httpclient.client;

import dev.codeflush.httpclient.Response;
import dev.codeflush.httpclient.request.Request;
import dev.codeflush.httpclient.parser.ResponseParser;

import java.io.IOException;

public interface HTTPClient {

    <T> Response<T> execute(Request request, ResponseParser<? extends T> parser) throws IOException;
}
