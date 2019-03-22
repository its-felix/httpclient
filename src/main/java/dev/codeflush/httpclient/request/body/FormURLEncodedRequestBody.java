package dev.codeflush.httpclient.request.body;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

public class FormURLEncodedRequestBody implements RequestBody {

    private final byte[] body;
    private final String contentType;

    public FormURLEncodedRequestBody(byte[] body, String contentType) {
        this.body = Objects.requireNonNull(body);
        this.contentType = Objects.requireNonNull(contentType);
    }

    @Override
    public String getContentType() {
        return this.contentType;
    }

    @Override
    public void write(OutputStream out) throws IOException {
        out.write(this.body);
    }
}
