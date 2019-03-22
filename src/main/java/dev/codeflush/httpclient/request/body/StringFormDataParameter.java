package dev.codeflush.httpclient.request.body;

import java.nio.charset.Charset;

public class StringFormDataParameter extends ByteArrayFormDataParameter {

    public StringFormDataParameter(String name, String contentType, String content, Charset charset) {
        super(name, contentType + "; charset=\"" + charset.name() + "\"", content.getBytes(charset));
    }

    @Override
    public boolean isBinaryTransferEncoding() {
        return false;
    }
}
