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

import java.io.ByteArrayInputStream;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

public class InputStreamRequestBodyTest {

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
    public void simpleBinaryRequest() throws Exception {
        byte[] bytes = new byte[]{123, 69, -127, 5, 3};
        RequestBody body = new InputStreamRequestBody("application/octet-stream", () -> new ByteArrayInputStream(bytes));

        this.baseEndpoint.resolve("test")
                .post()
                .body(body)
                .execute(this.client, this.parser);

        verify(exactly(1), postRequestedFor(urlEqualTo("/test"))
                .withRequestBody(binaryEqualTo(bytes)));
    }
}