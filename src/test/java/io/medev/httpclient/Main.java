package io.medev.httpclient;

import io.medev.httpclient.client.HTTPClient;
import io.medev.httpclient.client.SimpleHTTPClient;
import io.medev.httpclient.parser.ResponseParser;
import io.medev.httpclient.parser.SaveFileResponseParser;
import io.medev.httpclient.parser.StringResponseParser;
import io.medev.httpclient.request.Request;
import io.medev.httpclient.request.body.FormDataParameter;
import io.medev.httpclient.request.body.FormDataRequestBody;
import io.medev.httpclient.request.body.KeyValueFormDataParameter;
import io.medev.httpclient.request.body.RequestBody;

import java.io.File;
import java.net.HttpURLConnection;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Main {

    private static final HTTPClient CLIENT = new SimpleHTTPClient(Collections.singletonMap("Accept", "*/*"));
    private static final Endpoint ENDPOINT = Endpoint.forHost(Endpoint.HTTP, "me-dev.io").resolve("tools", "password");;
    private static final ResponseParser<String> PARSER = new StringResponseParser();

    public static void main(String[] args) throws Exception {
        testSomeGetRequest1();
        testSomePostRequest1();
        testSomePostRequest2();
//        testFileUploadAndDownload();

        String html = Endpoint.forHost("youtube.com").resolve("watch")
                .buildGetRequest()
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.121 Safari/537.36")
                .parameter("v", "K5ZHs2nmKsk")
                .build()
                .execute(CLIENT, PARSER)
                .getValue();

        System.out.println(html);
    }

    private static void testSomeGetRequest1() throws Exception {
        Request request = ENDPOINT.buildGetRequest()
                .parameter("length", "10")
                .parameter("seed", "94832904")
                .parameter("characters", "0123456789abcdefghijklmnopqrstuvwxyz")
                .build();

        Response<String> response = request.execute(CLIENT, PARSER);
        System.out.println(response.getValue());
    }

    private static void testSomePostRequest1() throws Exception {
        Request request = ENDPOINT.buildPostRequest()
                .body(
                        new FormDataRequestBody(Arrays.asList(
                                new KeyValueFormDataParameter("length", "10", Charset.defaultCharset()),
                                new KeyValueFormDataParameter("seed", "94832904", Charset.defaultCharset()),
                                new KeyValueFormDataParameter("characters", "0123456789abcdefghijklmnopqrstuvwxyz", Charset.defaultCharset())
                        ))
                )
                .build();

        Response<String> response = request.execute(CLIENT, PARSER);
        System.out.println(response.getValue());
    }

    private static void testSomePostRequest2() throws Exception {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("length", "10");
        parameters.put("seed", "94832904");
        parameters.put("characters", "0123456789abcdefghijklmnopqrstuvwxyz");

        Request request = ENDPOINT.buildPostRequest()
                .body(RequestBody.forMap(parameters))
                .build();

        Response<String> response = request.execute(CLIENT, PARSER);
        System.out.println(response.getValue());
    }

    private static void testFileUploadAndDownload() throws Exception {
        Response<String> uploadResponse = Endpoint.forHost(Endpoint.HTTP, "me-dev.io").resolve("upload")
                .buildPostRequest()
                .header("Accept", "text/plain")
                .body(new FormDataRequestBody(Collections.singleton(
                        FormDataParameter.forFile("file", new File("C:\\Users\\felix\\IdeaProjects\\httpclient\\upload_test.txt"))
                )))
                .build()
                .execute(CLIENT, PARSER);

        System.out.println(uploadResponse.getResponseCode());
        System.out.println(uploadResponse.getValue());

        if (uploadResponse.getResponseCode() == HttpURLConnection.HTTP_OK) {
            Response<File> downloadResponse = Endpoint.forHost(Endpoint.HTTP, "me-dev.io").resolve("download", uploadResponse.getValue(), "name")
                    .buildGetRequest()
                    .header("Accept", "application/octet-stream")
                    .build()
                    .execute(CLIENT, new SaveFileResponseParser(new File("C:\\Users\\felix\\IdeaProjects\\httpclient\\download_test.txt")));

            System.out.println(downloadResponse.getResponseCode());
        }
    }
}
