package io.medev.httpclient.request.body;

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.Charset;

public interface FormDataParameter extends RequestBody {

    String getName();
    boolean isBinaryTransferEncoding();

    static FormDataParameter forFile(String name, String contentType, File file) {
        return new InputStreamFormDataParameter(name, contentType, () -> new FileInputStream(file));
    }

    static FormDataParameter forFile(String name, File file) {
        return forFile(name, "application/octet-stream", file);
    }

    static FormDataParameter forBytes(String name, String contentType, byte[] bytes) {
        return new ByteArrayFormDataParameter(name, contentType, bytes);
    }

    static FormDataParameter forText(String name, String contentType, String text, Charset charset) {
        return new StringFormDataParameter(name, contentType, text, charset);
    }

    static FormDataParameter forText(String name, String text, Charset charset) {
        return forText(name, "text/plain", text, charset);
    }

    static FormDataParameter forText(String name, String text) {
        return forText(name, text, Charset.defaultCharset());
    }

    static FormDataParameter forJson(String name, String json, Charset charset) {
        return forText(name, "application/json", json, charset);
    }

    static FormDataParameter forJson(String name, String json) {
        return forJson(name, json, Charset.defaultCharset());
    }
}
