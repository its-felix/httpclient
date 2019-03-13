package io.medev.httpclient.client;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import io.medev.httpclient.Endpoint;
import io.medev.httpclient.RequestMethod;
import io.medev.httpclient.parser.NoOpResponseParser;
import io.medev.httpclient.parser.ResponseParser;
import io.medev.httpclient.request.body.RequestBody;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.io.OutputStream;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static io.medev.httpclient.Endpoint.HTTP;
import static io.medev.httpclient.client.SimpleHTTPClient.parseContentType;
import static org.junit.Assert.assertArrayEquals;

public class SimpleHTTPClientTest {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(wireMockConfig().dynamicPort().dynamicHttpsPort(), false);

    private Endpoint baseEndpoint;
    private HTTPClient client;
    private ResponseParser<Void> parser;

    @Before
    public void setupMockServer() {
        this.baseEndpoint = Endpoint.forHostAndPort(HTTP, "localhost", this.wireMockRule.port());
        this.client = new SimpleHTTPClient();
        this.parser = new NoOpResponseParser();

        this.wireMockRule.resetAll();
    }

    @Test
    public void simpleHeadRequest() throws Exception {
        this.baseEndpoint.resolve("test")
                .request(RequestMethod.HEAD)
                .build()
                .execute(this.client, this.parser);

        verify(exactly(1), headRequestedFor(urlEqualTo("/test")));
    }

    @Test
    public void simpleGetRequest() throws Exception {
        this.baseEndpoint.resolve("test")
                .request(RequestMethod.GET)
                .build()
                .execute(this.client, this.parser);

        verify(exactly(1), getRequestedFor(urlEqualTo("/test")));
    }

    @Test
    public void simplePostRequest() throws Exception {
        this.baseEndpoint.resolve("test")
                .request(RequestMethod.POST)
                .build()
                .execute(this.client, this.parser);

        verify(exactly(1), postRequestedFor(urlEqualTo("/test")));
    }

    @Test
    public void simplePutRequest() throws Exception {
        this.baseEndpoint.resolve("test")
                .request(RequestMethod.PUT)
                .build()
                .execute(this.client, this.parser);

        verify(exactly(1), putRequestedFor(urlEqualTo("/test")));
    }

    @Test
    public void simplePatchRequest() throws Exception {
        this.baseEndpoint.resolve("test")
                .request(RequestMethod.PATCH)
                .build()
                .execute(this.client, this.parser);

        verify(exactly(1), patchRequestedFor(urlEqualTo("/test")));
    }


    @Test
    public void simpleDeleteRequest() throws Exception {
        this.baseEndpoint.resolve("test")
                .request(RequestMethod.DELETE)
                .build()
                .execute(this.client, this.parser);

        verify(exactly(1), deleteRequestedFor(urlEqualTo("/test")));
    }


    @Test
    public void requestWithBody() throws Exception {
        // i'm not testing the RequestBody implementations here!
        RequestBody body = new RequestBody() {
            @Override
            public String getContentType() {
                return "my-content-type";
            }

            @Override
            public void write(OutputStream out) throws IOException {
                // just some bytes i had in mind
                out.write(new byte[]{-128, 12, 0, 69, 125});
            }
        };

        this.baseEndpoint.resolve("test")
                .request(RequestMethod.POST)
                .body(body)
                .build()
                .execute(this.client, this.parser);

        verify(exactly(1), postRequestedFor(urlEqualTo("/test"))
                .withHeader("Content-Type", equalTo("my-content-type"))
                .withRequestBody(binaryEqualTo(new byte[]{-128, 12, 0, 69, 125})));
    }

    @Test
    public void parseContentType1() {
        String[] result = parseContentType("text/plain");
        assertArrayEquals(new String[]{"text/plain", null}, result);
    }

    @Test
    public void parseContentType2() {
        String[] result = parseContentType("text/plain; charset=UTF-8");
        assertArrayEquals(new String[]{"text/plain", "UTF-8"}, result);
    }

    @Test
    public void parseContentType3() {
        String[] result = parseContentType("text/plain; charset=\"UTF-8\"");
        assertArrayEquals(new String[]{"text/plain", "UTF-8"}, result);
    }

    @Test
    public void parseContentType4() {
        String[] result = parseContentType("text/plain; charset=UTF-8; some more values");
        assertArrayEquals(new String[]{"text/plain", "UTF-8"}, result);
    }

    @Test
    public void parseContentType5() {
        String[] result = parseContentType("text/plain; charset=\"UTF-8\"; some more values");
        assertArrayEquals(new String[]{"text/plain", "UTF-8"}, result);
    }

    @Test
    public void parseContentType6() {
        String[] result = parseContentType("text/plain; some more values; charset=UTF-8");
        assertArrayEquals(new String[]{"text/plain", "UTF-8"}, result);
    }

    @Test
    public void parseContentType7() {
        String[] result = parseContentType("text/plain; some more values; charset=\"UTF-8\"");
        assertArrayEquals(new String[]{"text/plain", "UTF-8"}, result);
    }

    @Test
    public void parseContentType8() {
        String[] result = parseContentType("text/plain; some more values; charset=UTF-8; some more values");
        assertArrayEquals(new String[]{"text/plain", "UTF-8"}, result);
    }

    @Test
    public void parseContentType9() {
        String[] result = parseContentType("text/plain; some more values; charset=\"UTF-8\"; some more values");
        assertArrayEquals(new String[]{"text/plain", "UTF-8"}, result);
    }

    @Test
    public void parseContentType10() {
        String[] result = parseContentType("text/plain; key=value; charset=\"UTF-8\"; some more values");
        assertArrayEquals(new String[]{"text/plain", "UTF-8"}, result);
    }
}