package io.medev.httpclient.parser;

import org.junit.Test;

import java.io.*;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.*;

public class SaveFileResponseParserTest {

    private static final File FILE;
    private static final ResponseParser<File> PARSER;

    static {
        try {
            FILE = File.createTempFile("test", "");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        PARSER = new SaveFileResponseParser(FILE);
    }

    @Test
    public void savesFileCorrectly() throws IOException {
        byte[] bytes = new byte[]{123, 12, 33, 19, 19, 12, 0, -127, -5};
        File result = PARSER.parse(null, null, 200, new ByteArrayInputStream(bytes), Collections.emptyMap(), "application/octet-stream", null);

        assertNotNull(result);

        try (InputStream in = new FileInputStream(result)) {
            byte[] buffer = new byte[bytes.length + 1];// keep space for 1 byte more if the parser doesnt work correctly
            int bytesRead = in.read(buffer);

            assertEquals(bytes.length, bytesRead);
            assertArrayEquals(bytes, Arrays.copyOfRange(buffer, 0, bytesRead));
        }

        FILE.delete();
    }

    @Test
    public void doesntSaveFileOnNotOk() throws Exception {
        File result = PARSER.parse(null, null, 404, new ByteArrayInputStream(new byte[0]), Collections.emptyMap(), "application/octet-stream", null);
        assertNull(result);
    }
}