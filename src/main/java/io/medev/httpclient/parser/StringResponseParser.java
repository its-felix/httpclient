package io.medev.httpclient.parser;

import io.medev.httpclient.request.Request;
import io.medev.httpclient.client.HTTPClient;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

public class StringResponseParser implements ResponseParser<String> {

    private static final Charset DEFAULT_CHARSET = Charset.defaultCharset();

    @Override
    public String parse(HTTPClient client, Request request, int responseCode, InputStream stream, Map<String, List<String>> headers, String contentType, String charsetName) throws IOException {
        Charset charset;
        if (charsetName != null) {
            charset = Charset.forName(charsetName);
        } else {
            charset = DEFAULT_CHARSET;
        }

        StringBuilder sb = new StringBuilder();

        byte[] buffer = new byte[8192];
        int bytesRead;

        while ((bytesRead = stream.read(buffer)) != -1) {
            sb.append(new String(buffer, 0, bytesRead, charset));
        }

        return sb.toString();
    }
}
