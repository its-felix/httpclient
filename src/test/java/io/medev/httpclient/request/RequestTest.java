package io.medev.httpclient.request;

import io.medev.httpclient.Endpoint;
import io.medev.httpclient.RequestMethod;
import io.medev.httpclient.request.body.RequestBody;
import org.junit.Test;

import static io.medev.httpclient.Endpoint.HTTPS;
import static org.junit.Assert.*;

public class RequestTest {

    @Test
    public void simpleGetRequest() {
        Request request = Endpoint.forHost(HTTPS, "me-dev.io")
                .get()
                .build();

        assertEquals("https://me-dev.io", request.getRequestURL().toString());
    }

    @Test
    public void getRequestWithParameters() {
        Request request = Endpoint.forHost(HTTPS, "me-dev.io")
                .get()
                .parameter("param1", "value1")
                .parameter("param2")
                .parameter("param3")
                .build();

        assertEquals("https://me-dev.io?param1=value1&param2&param3", request.getRequestURL().toString());
    }

    @Test
    public void getRequestWithParametersFromEndpointAndRequest() {
        Request request = Endpoint.forURL("https://me-dev.io?param0=value0")
                .get()
                .parameter("param1", "value1")
                .parameter("param2")
                .parameter("param3")
                .build();

        assertEquals("https://me-dev.io?param0=value0&param1=value1&param2&param3", request.getRequestURL().toString());
    }

    @Test
    public void template() {
        Request.Template template = Endpoint.forHost(HTTPS, "me-dev.io")
                .get()
                .parameter("somePredefinedParameter", "value")
                .template();

        Request request = template.enrich()
                .parameter("someExtraParameter", "value")
                .build();

        assertEquals("https://me-dev.io?somePredefinedParameter=value&someExtraParameter=value", request.getRequestURL().toString());
    }

    @Test
    public void changesDontChangeTheTemplate() {
        Request.Template template = Endpoint.forHost(HTTPS, "me-dev.io")
                .get()
                .parameter("param0", "value0")
                .template();

        Request request = template.enrich()
                .parameter("param0", "overriddenValue")
                .build();

        assertEquals("https://me-dev.io?param0=overriddenValue", request.getRequestURL().toString());

        request = template.enrich()
                .parameter("param1", "value1")
                .build();

        assertEquals("https://me-dev.io?param0=value0&param1=value1", request.getRequestURL().toString());
    }

    @Test
    public void bodyIsOverriden() {
        RequestBody body1 = RequestBody.forText("");
        RequestBody body2 = RequestBody.forText("");

        Request.TemplateWithBody template = Endpoint.forHost(HTTPS, "me-dev.io")
                .post()
                .body(body1)
                .template();

        Request request = template.enrich()
                .body(body2)
                .build();

        assertNotSame(body1, request.getBody());
        assertSame(body2, request.getBody());
    }
}