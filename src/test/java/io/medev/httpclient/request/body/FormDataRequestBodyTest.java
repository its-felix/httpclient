package io.medev.httpclient.request.body;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import io.medev.httpclient.Endpoint;
import io.medev.httpclient.client.HTTPClient;
import io.medev.httpclient.client.SimpleHTTPClient;
import io.medev.httpclient.parser.NoOpResponseParser;
import io.medev.httpclient.parser.ResponseParser;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import java.util.Collections;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static io.medev.httpclient.Endpoint.HTTP;

public class FormDataRequestBodyTest {

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
    @Ignore
    public void simpleMultipartRequest() throws Exception {
        RequestBody body = new FormDataRequestBody(Collections.singleton(
                FormDataParameter.forText("some-field", "some-value")
        ));

        this.baseEndpoint.resolve("test")
                .post()
                .body(body)
                .execute(this.client, this.parser);

        System.out.println(this.wireMockRule.findUnmatchedRequests().getRequests());

        verify(exactly(1), postRequestedFor(urlEqualTo("/test"))
                .withAnyRequestBodyPart(aMultipart()
                        .withName("some-field")
                        .withHeader("Content-Type", containing("text/plain"))
                        .withBody(equalTo("some-value"))));
    }
}