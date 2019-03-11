package io.medev.httpclient.parser;

import io.medev.httpclient.client.HTTPClient;
import io.medev.httpclient.request.Request;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;

public class SaveFileResponseParser implements ResponseParser<File> {

    private final File destFile;

    public SaveFileResponseParser(File destFile) {
        this.destFile = destFile;
    }

    @Override
    public File parse(HTTPClient client, Request request, int responseCode, InputStream stream, Map<String, List<String>> headers, String contentType, String charset) throws IOException {
        if (responseCode == HttpURLConnection.HTTP_OK) {
            try (FileOutputStream out = new FileOutputStream(this.destFile)) {
                byte[] buffer = new byte[8192];
                int bytesRead;

                while ((bytesRead = stream.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            }

            return this.destFile;
        } else {
            System.out.println(new StringResponseParser().parse(client, request, responseCode, stream, headers, contentType, charset));
            return null;
        }
    }
}
