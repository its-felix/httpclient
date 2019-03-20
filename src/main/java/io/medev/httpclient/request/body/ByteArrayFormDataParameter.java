package io.medev.httpclient.request.body;

public class ByteArrayFormDataParameter extends ByteArrayRequestBody implements FormDataParameter {

    private final String name;

    public ByteArrayFormDataParameter(String name, String contentType, byte[] bytes) {
        super(contentType, bytes);
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public boolean isBinaryTransferEncoding() {
        return true;
    }
}
