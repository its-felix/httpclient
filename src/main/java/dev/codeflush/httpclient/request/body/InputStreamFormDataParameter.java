package dev.codeflush.httpclient.request.body;

import dev.codeflush.httpclient.InputStreamSupplier;

public class InputStreamFormDataParameter extends InputStreamRequestBody implements FormDataParameter {

    private final String name;

    public InputStreamFormDataParameter(String name, String contentType, InputStreamSupplier inputStreamSupplier) {
        super(contentType, inputStreamSupplier);
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
