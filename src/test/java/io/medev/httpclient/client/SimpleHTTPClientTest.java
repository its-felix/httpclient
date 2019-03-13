package io.medev.httpclient.client;

import io.medev.httpclient.Endpoint;
import io.medev.httpclient.RequestMethod;
import io.medev.httpclient.Response;
import io.medev.httpclient.parser.StringResponseParser;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockserver.integration.ClientAndServer;

import static io.medev.httpclient.Endpoint.HTTP;
import static org.junit.Assert.assertEquals;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

public class SimpleHTTPClientTest {

    private static ClientAndServer mockServer;
    private static Endpoint baseEndpoint;

    @BeforeClass
    public static void setupMockServer() {
        mockServer = startClientAndServer();
        baseEndpoint = Endpoint.forAddress(HTTP, mockServer.remoteAddress()).resolve(mockServer.contextPath());
    }

    @AfterClass
    public static void shutdownMockServer() {
        mockServer.stop();
    }

    @Test
    public void simpleRequest() throws Exception {
        mockServer.when(
                request()
                        .withMethod("GET")
                        .withPath("/test")
        ).respond(
                response()
                        .withStatusCode(200)
                        .withBody("Success")
        );

        Response<String> response = baseEndpoint.resolve("test")
                .request(RequestMethod.GET)
                .build()
                .execute(new SimpleHTTPClient(), new StringResponseParser());

        assertEquals(200, response.getResponseCode());
        assertEquals("Success", response.getValue());
    }
}