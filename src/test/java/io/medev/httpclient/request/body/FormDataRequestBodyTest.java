package io.medev.httpclient.request.body;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import io.medev.httpclient.Endpoint;
import io.medev.httpclient.Response;
import io.medev.httpclient.client.HTTPClient;
import io.medev.httpclient.client.SimpleHTTPClient;
import io.medev.httpclient.parser.NoOpResponseParser;
import io.medev.httpclient.parser.ResponseParser;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static io.medev.httpclient.Endpoint.HTTP;
import static org.junit.Assert.assertEquals;

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
    public void simpleMultipartRequest() throws Exception {
        stubFor(post(urlEqualTo("/test"))
                .withMultipartRequestBody(aMultipart()
                        .withName("some-field")
                        .withBody(equalTo("some-value")))
                .willReturn(aResponse()
                        .withStatus(299)));

        RequestBody body = new FormDataRequestBody(Collections.singleton(
                FormDataParameter.forText("some-field", "some-value")
        ));

        Response<Void> response = this.baseEndpoint.resolve("test")
                .post()
                .body(body)
                .execute(this.client, this.parser);

        assertEquals(299, response.getResponseCode());

        // seems not to work here -> replaced with stub and custom response code
//        verify(exactly(1), postRequestedFor(urlEqualTo("/test"))
//                .withAllRequestBodyParts(aMultipart()
//                        .withName("some-field")
//                        .withBody(equalTo("some-value"))));
    }

    @Test
    public void multipleValuesMultipartRequest() throws Exception {
        stubFor(post(urlEqualTo("/test"))
                .withMultipartRequestBody(aMultipart()
                        .withName("some-field")
                        .withBody(equalTo("some-value")))
                .withMultipartRequestBody(aMultipart()
                        .withName("another-field")
                        .withBody(binaryEqualTo(new byte[]{123, 19, 20, 73})))
                .willReturn(aResponse()
                        .withStatus(299)));

        RequestBody body = new FormDataRequestBody(Arrays.asList(
                FormDataParameter.forText("some-field", "some-value"),
                FormDataParameter.forBytes("another-field", "application/octet-stream", new byte[]{123, 19, 20, 73})
        ));

        Response<Void> response = this.baseEndpoint.resolve("test")
                .post()
                .body(body)
                .execute(this.client, this.parser);

        assertEquals(299, response.getResponseCode());

        // seems not to work here -> replaced with stub and custom response code
//        verify(exactly(1), postRequestedFor(urlEqualTo("/test"))
//                .withAllRequestBodyParts(aMultipart()
//                        .withName("some-field")
//                        .withBody(equalTo("some-value"))));
    }
}