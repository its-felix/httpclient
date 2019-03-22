package dev.codeflush.httpclient;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class EndpointTest {

    @Test
    public void basicResolve() {
        Endpoint endpoint = Endpoint.forHost(Endpoint.HTTPS, "codeflush.dev").resolve("test");
        assertEquals("https://codeflush.dev/test", endpoint.getURL().toString());
    }

    @Test
    public void resolveShouldDoURLEncoding1() {
        Endpoint endpoint = Endpoint.forHost(Endpoint.HTTPS, "codeflush.dev").resolve("test test");
        assertEquals("https://codeflush.dev/test+test", endpoint.getURL().toString());
    }

    @Test
    public void resolveShouldDoURLEncoding2() {
        Endpoint endpoint = Endpoint.forHost(Endpoint.HTTPS, "codeflush.dev").resolve("test€test");
        assertEquals("https://codeflush.dev/test%E2%82%ACtest", endpoint.getURL().toString());
    }

    @Test
    public void resolveFromEndpointEndingWithSlash() {
        Endpoint endpoint = Endpoint.forURL("https://codeflush.dev/").resolve("test");
        assertEquals("https://codeflush.dev/test", endpoint.getURL().toString());
    }

    @Test
    public void resolveFromEndpointWithQuery() {
        Endpoint endpoint = Endpoint.forURL("https://codeflush.dev?myParam=myValue").resolve("test");
        assertEquals("https://codeflush.dev/test?myParam=myValue", endpoint.getURL().toString());
    }
}