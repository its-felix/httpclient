package io.medev.httpclient.parser;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

import static org.junit.Assert.*;

public class StringResponseParserTest {

    private static final ResponseParser<String> PARSER = new StringResponseParser();

    @Test
    public void simpleParsing() throws Exception {
        String src = "Hello World";
        byte[] bytes = src.getBytes(StandardCharsets.UTF_8);

        String result = PARSER.parse(null, null, 200, new ByteArrayInputStream(bytes), Collections.emptyMap(), "text/plain", "UTF-8");

        assertEquals(src, result);
    }

    @Test
    public void usingCorrectCharset() throws Exception {
        String src = "Hello World";
        byte[] bytes = src.getBytes(StandardCharsets.UTF_16);

        String result = PARSER.parse(null, null, 200, new ByteArrayInputStream(bytes), Collections.emptyMap(), "text/plain", "UTF-16");

        assertEquals(src, result);
    }

    @Test
    public void worksWithoutCharset() throws Exception {
        String src = "Hello World";
        byte[] bytes = src.getBytes();

        String result = PARSER.parse(null, null, 200, new ByteArrayInputStream(bytes), Collections.emptyMap(), "text/plain", null);

        assertEquals(src, result);
    }
}