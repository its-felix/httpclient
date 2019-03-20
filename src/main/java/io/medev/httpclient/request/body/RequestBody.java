package io.medev.httpclient.request.body;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public interface RequestBody {

    String getContentType();
    default Optional<Integer> getContentLength() {
        return Optional.empty();
    }
    void write(OutputStream out) throws IOException;

    static RequestBody forFile(String contentType, File file) {
        return new InputStreamRequestBody(contentType, () -> new FileInputStream(file));
    }

    static RequestBody forFile(File file) {
        return forFile("application/octet-stream", file);
    }

    static RequestBody forBytes(String contentType, byte[] bytes) {
        return new ByteArrayRequestBody(contentType, bytes);
    }

    static RequestBody forText(String contentType, String text, Charset charset) {
        return forBytes(contentType + "; charset=\"" + charset.name() + "\"", text.getBytes(charset));
    }

    static RequestBody forText(String text, Charset charset) {
        return forText("text/plain", text, charset);
    }

    static RequestBody forText(String text) {
        return forText(text, Charset.defaultCharset());
    }

    static RequestBody forJson(String json, Charset charset) {
        return forText("application/json", json, charset);
    }

    static RequestBody forJson(String json) {
        return forJson(json, Charset.defaultCharset());
    }

    static RequestBody forMap(Map<String, String> body, Charset charset) {
        String bodyStr = body.entrySet().stream()
                .map((entry) -> {
                    String k = entry.getKey();
                    String v = entry.getValue();
                    String result;

                    try {
                        result = URLEncoder.encode(k, charset.name());

                        if (v != null) {
                            result += "=" + URLEncoder.encode(v, charset.name());
                        }
                    } catch (UnsupportedEncodingException e) {
                        throw new RuntimeException(e);
                    }

                    return result;
                }).collect(Collectors.joining("&"));

        return new FormURLEncodedRequestBody(bodyStr.getBytes(StandardCharsets.UTF_8), "application/x-www-form-urlencoded; charset=\"" + charset.name() + "\"");
    }

    static RequestBody forMap(Map<String, String> body) {
        return forMap(body, Charset.defaultCharset());
    }
}
