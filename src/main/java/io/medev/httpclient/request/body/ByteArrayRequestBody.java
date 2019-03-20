package io.medev.httpclient.request.body;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Optional;

public class ByteArrayRequestBody implements RequestBody {

    private final String contentType;
    private final byte[] bytes;

    public ByteArrayRequestBody(String contentType, byte[] bytes) {
        this.contentType = contentType;
        this.bytes = bytes;
    }

    @Override
    public String getContentType() {
        return this.contentType;
    }

    @Override
    public Optional<Integer> getContentLength() {
        return Optional.of(bytes.length);
    }

    @Override
    public void write(OutputStream out) throws IOException {
        out.write(this.bytes);
    }
}
