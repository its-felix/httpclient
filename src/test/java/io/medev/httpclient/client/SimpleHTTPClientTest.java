package io.medev.httpclient.client;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static io.medev.httpclient.Endpoint.HTTP;
import static org.junit.Assert.assertEquals;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import io.medev.httpclient.Endpoint;
import io.medev.httpclient.RequestMethod;
import io.medev.httpclient.Response;
import io.medev.httpclient.parser.StringResponseParser;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class SimpleHTTPClientTest {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(8080);
    private Endpoint baseEndpoint;

    @Before
    public void setupMockServer() {
        this.baseEndpoint = Endpoint.forHostAndPort(HTTP, "localhost", this.wireMockRule.port());
    }

    @Test
    public void simpleRequest() throws Exception {
        stubFor(
              get(urlEqualTo("/test"))
                    .willReturn(
                          aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", "text/plain")
                                .withBody("Success")
                    )
        );

        Response<String> response = baseEndpoint.resolve("test")
                .request(RequestMethod.GET)
                .build()
                .execute(new SimpleHTTPClient(), new StringResponseParser());

        assertEquals(200, response.getResponseCode());
        assertEquals("Success", response.getValue());

        verify(getRequestedFor(urlEqualTo("/test")));
    }
}