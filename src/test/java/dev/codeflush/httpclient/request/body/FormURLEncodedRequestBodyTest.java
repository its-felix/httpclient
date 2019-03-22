package dev.codeflush.httpclient.request.body;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import dev.codeflush.httpclient.Endpoint;
import dev.codeflush.httpclient.client.SimpleHTTPClient;
import dev.codeflush.httpclient.parser.NoOpResponseParser;
import dev.codeflush.httpclient.client.HTTPClient;
import dev.codeflush.httpclient.parser.ResponseParser;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Collections;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static junit.framework.TestCase.assertTrue;

public class FormURLEncodedRequestBodyTest {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(wireMockConfig().dynamicPort().dynamicHttpsPort(), false);

    private Endpoint baseEndpoint;
    private HTTPClient client;
    private ResponseParser<Void> parser;

    @Before
    public void setupMockServer() {
        this.baseEndpoint = Endpoint.forHostAndPort(Endpoint.HTTP, "localhost", this.wireMockRule.port());
        this.client = new SimpleHTTPClient();
        this.parser = new NoOpResponseParser();

        this.wireMockRule.resetAll();
    }

    @Test
    public void simpleURLEncodedRequestBody() throws Exception {
        Map<String, String> urlEncodedRequestBody = Collections.singletonMap("some-key", "some-value");
        RequestBody body = RequestBody.forMap(urlEncodedRequestBody);

        assertTrue(body instanceof FormURLEncodedRequestBody);

        this.baseEndpoint.resolve("test")
                .post()
                .body(body)
                .execute(this.client, this.parser);

        verify(exactly(1), postRequestedFor(urlEqualTo("/test"))
                .withRequestBody(equalTo("some-key=some-value")));
    }

    @Test
    public void urlEncodedRequestBodyWithSpecialCharacters() throws Exception {
        Map<String, String> urlEncodedRequestBody = Collections.singletonMap("some key", "some value");
        RequestBody body = RequestBody.forMap(urlEncodedRequestBody);

        assertTrue(body instanceof FormURLEncodedRequestBody);

        this.baseEndpoint.resolve("test")
                .post()
                .body(body)
                .execute(this.client, this.parser);

        verify(exactly(1), postRequestedFor(urlEqualTo("/test"))
                .withRequestBody(equalTo("some+key=some+value")));
    }
}