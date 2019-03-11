package io.medev.httpclient.request.body;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Objects;
import java.util.UUID;

public class FormDataRequestBody implements RequestBody {

    private final Collection<? extends FormDataParameter> parameters;
    private final String boundary;

    public FormDataRequestBody(Collection<? extends FormDataParameter> parameters) {
        this.parameters = Objects.requireNonNull(parameters);
        this.boundary = UUID.randomUUID().toString().replace("-", "");
    }

    @Override
    public String getContentType() {
        return "multipart/form-data; boundary=" + this.boundary;
    }

    @Override
    public void write(OutputStream out) throws IOException {
        byte[] boundaryBytes = ("--" + this.boundary + "\r\n").getBytes(StandardCharsets.UTF_8);

        for (FormDataParameter parameter : this.parameters) {
            out.write(boundaryBytes);
            writeString(out, "Content-Disposition: ");

            String nameEncoded = URLEncoder.encode(parameter.getName(), "UTF-8");

            if (parameter.isBinaryTransferEncoding()) {
                writeString(out, "form-data; name=\"" + nameEncoded + "\"; filename=\"" + nameEncoded + "\"\r\n");
            } else {
                writeString(out, "form-data; name=\"" + nameEncoded + "\"\r\n");
            }

            writeString(out, "Content-Type: " + parameter.getContentType() + "\r\n");

            if (parameter.isBinaryTransferEncoding()) {
                writeString(out, "Content-Transfer-Encoding: binary\r\n");
            }

            writeString(out, "\r\n");
            parameter.write(out);
            writeString(out, "\r\n");
        }

        writeString(out, "--" + this.boundary + "--\r\n");
    }

    private void writeString(OutputStream out, String str) throws IOException {
        out.write(str.getBytes(StandardCharsets.UTF_8));
    }
}
