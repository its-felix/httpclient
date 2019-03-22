package dev.codeflush.httpclient;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class EndpointTest {

    @Test
    public void basicResolve() {
        Endpoint endpoint = Endpoint.forHost(Endpoint.HTTPS, "me-dev.io").resolve("test");
        assertEquals("https://me-dev.io/test", endpoint.getURL().toString());
    }

    @Test
    public void resolveShouldDoURLEncoding1() {
        Endpoint endpoint = Endpoint.forHost(Endpoint.HTTPS, "me-dev.io").resolve("test test");
        assertEquals("https://me-dev.io/test+test", endpoint.getURL().toString());
    }

    @Test
    public void resolveShouldDoURLEncoding2() {
        Endpoint endpoint = Endpoint.forHost(Endpoint.HTTPS, "me-dev.io").resolve("test€test");
        assertEquals("https://me-dev.io/test%E2%82%ACtest", endpoint.getURL().toString());
    }

    @Test
    public void resolveFromEndpointEndingWithSlash() {
        Endpoint endpoint = Endpoint.forURL("https://me-dev.io/").resolve("test");
        assertEquals("https://me-dev.io/test", endpoint.getURL().toString());
    }

    @Test
    public void resolveFromEndpointWithQuery() {
        Endpoint endpoint = Endpoint.forURL("https://me-dev.io?myParam=myValue").resolve("test");
        assertEquals("https://me-dev.io/test?myParam=myValue", endpoint.getURL().toString());
    }
}