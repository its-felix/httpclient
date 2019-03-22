package dev.codeflush.httpclient.client;

import dev.codeflush.httpclient.InputStreamSupplier;
import dev.codeflush.httpclient.RequestMethod;
import dev.codeflush.httpclient.Response;
import dev.codeflush.httpclient.parser.ResponseParser;
import dev.codeflush.httpclient.request.Request;
import dev.codeflush.httpclient.request.body.RequestBody;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

public class SimpleHTTPClient implements HTTPClient {

    static {
        try {
            Field methodsField = HttpURLConnection.class.getDeclaredField("methods");

            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(methodsField, methodsField.getModifiers() & ~Modifier.FINAL);

            methodsField.setAccessible(true);

            String[] oldMethods = (String[]) methodsField.get(null);
            Set<String> methodsSet = new LinkedHashSet<>(Arrays.asList(oldMethods));

            for (RequestMethod method : RequestMethod.values()) {
                methodsSet.add(method.getName());
            }

            String[] newMethods = methodsSet.toArray(new String[0]);

            methodsField.set(null, newMethods);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

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
        String[] contentTypeFields = parseContentType(conn.getHeaderField("Content-Type"));
        String contentType = contentTypeFields[0];
        String charset = contentTypeFields[1];

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

    public static String[] parseContentType(String contentTypeRaw) {
        String contentType = null;
        String charset = null;

        if (contentTypeRaw != null) {
            String[] split = splitSafeAt(contentTypeRaw, ";");
            contentType = split[0];

            if (!split[1].isEmpty()) {
                split = splitSafeAt(split[1], "charset=");

                if (split[1].length() >= 1) {
                    if (split[1].charAt(0) == '"') {
                        charset = splitSafeAt(split[1].substring(1), "\"")[0];
                    } else {
                        charset = splitSafeAt(split[1], ";")[0];
                    }
                }
            }
        }

        return new String[]{contentType, charset};
    }

    private static String[] splitSafeAt(String src, String target) {
        int index = src.indexOf(target);
        String[] result = new String[2];

        if (index != -1) {
            result[0] = src.substring(0, index);
            result[1] = src.substring(index + target.length());
        } else {
            result[0] = src;
            result[1] = "";
        }

        return result;
    }

    private static void addHeaders(HttpURLConnection conn, Map<String, String> headers) {
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            conn.setRequestProperty(entry.getKey(), entry.getValue());
        }
    }
}
