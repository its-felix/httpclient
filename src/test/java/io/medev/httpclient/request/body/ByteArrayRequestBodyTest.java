package io.medev.httpclient.request.body;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import io.medev.httpclient.Endpoint;
import io.medev.httpclient.client.HTTPClient;
import io.medev.httpclient.client.SimpleHTTPClient;
import io.medev.httpclient.parser.NoOpResponseParser;
import io.medev.httpclient.parser.ResponseParser;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static io.medev.httpclient.Endpoint.HTTP;
import static org.junit.Assert.assertTrue;

public class ByteArrayRequestBodyTest {

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
    public void simpleBinaryRequest() throws Exception {
        byte[] bytes = new byte[]{123, 69, -127, 5, 3};
        RequestBody body = RequestBody.forBytes("application/octet-stream", bytes);

        this.baseEndpoint.resolve("test")
                .post()
                .body(body)
                .execute(this.client, this.parser);

        verify(exactly(1), postRequestedFor(urlEqualTo("/test"))
                .withRequestBody(binaryEqualTo(bytes)));
    }

    @Test
    public void simpleTextRequest() throws Exception {
        RequestBody body = RequestBody.forText("Hello World");

        assertTrue(body instanceof ByteArrayRequestBody);

        this.baseEndpoint.resolve("test")
                .post()
                .body(body)
                .execute(this.client, this.parser);

        verify(exactly(1), postRequestedFor(urlEqualTo("/test"))
                .withHeader("Content-Type", containing("text/plain"))
                .withRequestBody(equalTo("Hello World")));
    }
}