package io.medev.httpclient.request.body;

import io.medev.httpclient.InputStreamSupplier;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class InputStreamRequestBody implements RequestBody {

    private final String contentType;
    private final InputStreamSupplier inputStreamSupplier;

    public InputStreamRequestBody(String contentType, InputStreamSupplier inputStreamSupplier) {
        this.contentType = contentType;
        this.inputStreamSupplier = inputStreamSupplier;
    }

    @Override
    public String getContentType() {
        return this.contentType;
    }

    @Override
    public void write(OutputStream out) throws IOException {
        try (InputStream in = this.inputStreamSupplier.getInputStream()) {
            byte[] buffer = new byte[8192];
            int bytesRead;

            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }
    }
}
