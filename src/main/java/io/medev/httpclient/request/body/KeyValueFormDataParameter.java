package io.medev.httpclient.request.body;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class KeyValueFormDataParameter implements FormDataParameter {

    private final String key;
    private final String value;
    private final Charset charset;

    public KeyValueFormDataParameter(String key, String value, Charset charset) {
        this.key = key;
        this.value = value;
        this.charset = charset;
    }

    @Override
    public String getName() {
        return this.key;
    }

    @Override
    public boolean isBinaryTransferEncoding() {
        return false;
    }

    @Override
    public String getContentType() {
        return "application/x-www-form-urlencoded; charset=\"" + this.charset.name() + "\"";
    }

    @Override
    public void write(OutputStream out) throws IOException {
        byte[] bytes = URLEncoder.encode(this.value, this.charset.name()).getBytes(StandardCharsets.UTF_8);
        out.write(bytes);
    }
}
