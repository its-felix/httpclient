package io.medev.httpclient.request;

import io.medev.httpclient.Endpoint;
import io.medev.httpclient.RequestMethod;
import io.medev.httpclient.Response;
import io.medev.httpclient.client.HTTPClient;
import io.medev.httpclient.parser.ResponseParser;
import io.medev.httpclient.request.body.RequestBody;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class Request {

    private final Endpoint endpoint;
    private final RequestMethod method;
    private final Charset charset;
    private final Map<String, String> urlParameters;
    private final Map<String, String> headers;
    private final RequestBody body;
    private final Object lock;
    private volatile URL requestURL;

    public Request(Endpoint endpoint, RequestMethod method, Charset charset, Map<String, String> urlParameters, Map<String, String> headers, RequestBody body) {
        this.endpoint = Objects.requireNonNull(endpoint);
        this.method = Objects.requireNonNull(method);
        this.charset = Objects.requireNonNull(charset);
        this.urlParameters = Objects.requireNonNull(urlParameters);
        this.headers = Objects.requireNonNull(headers);
        this.body = body;
        this.lock = new Object();
        this.requestURL = null;
    }

    public RequestMethod getMethod() {
        return this.method;
    }

    public Map<String, String> getHeaders() {
        return this.headers;
    }

    public RequestBody getBody() {
        return this.body;
    }

    public URL getRequestURL() {
        if (this.requestURL == null) {
            synchronized (this.lock) {
                if (this.requestURL == null) {
                    this.requestURL = buildRequestURL();
                }
            }
        }

        return this.requestURL;
    }

    public <T> Response<T> execute(HTTPClient client, ResponseParser<? extends T> parser) throws IOException {
        return client.execute(this, parser);
    }

    private URL buildRequestURL() {
        URL url = this.endpoint.getURL();

        if (!this.urlParameters.isEmpty()) {
            String query = url.getQuery();

            if (query == null || query.isEmpty()) {
                query = buildQueryString();
            } else {
                query += "&" + buildQueryString();
            }

            try {
                url = new URL(url.getProtocol(), url.getHost(), url.getPort(), url.getPath() + "?" + query);
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        }

        return url;
    }

    private String buildQueryString() {
        return this.urlParameters.entrySet().stream()
                .map(this::buildParameter)
                .collect(Collectors.joining("&"));
    }

    private String buildParameter(Map.Entry<String, String> entry) {
        String result = encode(entry.getKey());
        String value = entry.getValue();

        if (value != null) {
            result += "=" + encode(value);
        }

        return result;
    }

    private String encode(String str) {
        try {
            return URLEncoder.encode(str, this.charset.name());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public interface Builder<T extends Builder<T>> {

        static Builder<?> create(Endpoint endpoint, RequestMethod method) {
            return new BuilderImpl(endpoint, method);
        }

        T charset(Charset charset);
        T parameter(String key, String value);
        T parameter(String key);
        T header(String key, String value);
        Template template();
        Request build();
    }

    public interface Template {

        Builder<?> enrich();
        default Request build() {
            return enrich().build();
        }
    }

    public interface BuilderWithBody<T extends BuilderWithBody<T>> extends Builder<T> {

        static BuilderWithBody<?> create(Endpoint endpoint, RequestMethod method) {
            return new BuilderWithBodyImpl(endpoint, method);
        }

        TemplateWithBody template();
        T body(RequestBody body);
    }

    public interface TemplateWithBody extends Template {

        BuilderWithBody<?> enrich();
    }

    private static abstract class ABuilder<T extends ABuilder<T>> implements Builder<T> {

        protected final Endpoint endpoint;
        protected final RequestMethod method;
        protected Charset charset;
        protected Map<String, String> urlParameters;
        protected Map<String, String> headers;
        protected RequestBody body;

        public ABuilder(Endpoint endpoint, RequestMethod method) {
            this.endpoint = endpoint;
            this.method = method;
            this.charset = Charset.defaultCharset();
            this.urlParameters = new LinkedHashMap<>();
            this.headers = new LinkedHashMap<>();
            this.body = null;
        }

        @Override
        public T charset(Charset charset) {
            this.charset = charset;
            return self();
        }

        @Override
        public T parameter(String key, String value) {
            this.urlParameters.put(key, value);
            return self();
        }

        @Override
        public T parameter(String key) {
            return parameter(key, null);
        }

        @Override
        public T header(String key, String value) {
            this.headers.put(key, value);
            return self();
        }

        @Override
        public Request build() {
            return new Request(this.endpoint, this.method, this.charset, this.urlParameters, this.headers, this.body);
        }

        protected abstract T self();
    }

    private static abstract class ATemplate<T extends Builder<T>> implements Template {

        protected final Endpoint endpoint;
        protected final RequestMethod method;
        protected final Charset charset;
        protected final Map<String, String> urlParameters;
        protected final Map<String, String> headers;
        protected final RequestBody body;

        public ATemplate(Endpoint endpoint, RequestMethod method, Charset charset, Map<String, String> urlParameters, Map<String, String> headers, RequestBody body) {
            this.endpoint = endpoint;
            this.method = method;
            this.charset = charset;
            this.urlParameters = urlParameters;
            this.headers = headers;
            this.body = body;
        }

        @Override
        public T enrich() {
            T builder = createBuilder().charset(this.charset);

            for (Map.Entry<String, String> entry : this.urlParameters.entrySet()) {
                builder = builder.parameter(entry.getKey(), entry.getValue());
            }

            for (Map.Entry<String, String> entry : this.headers.entrySet()) {
                builder = builder.header(entry.getKey(), entry.getValue());
            }

            return builder;
        }

        protected abstract T createBuilder();
    }

    private static class BuilderImpl extends ABuilder<BuilderImpl> {

        public BuilderImpl(Endpoint endpoint, RequestMethod method) {
            super(endpoint, method);
        }

        @Override
        public Template template() {
            return new TemplateImpl(this.endpoint, this.method, this.charset, this.urlParameters, this.headers, this.body);
        }

        @Override
        protected BuilderImpl self() {
            return this;
        }
    }

    private static class TemplateImpl extends ATemplate<BuilderImpl> {

        public TemplateImpl(Endpoint endpoint, RequestMethod method, Charset charset, Map<String, String> urlParameters, Map<String, String> headers, RequestBody body) {
            super(endpoint, method, charset, urlParameters, headers, body);
        }

        @Override
        protected BuilderImpl createBuilder() {
            return new BuilderImpl(this.endpoint, this.method);
        }
    }

    private static class BuilderWithBodyImpl extends ABuilder<BuilderWithBodyImpl> implements BuilderWithBody<BuilderWithBodyImpl> {

        public BuilderWithBodyImpl(Endpoint endpoint, RequestMethod method) {
            super(endpoint, method);
            this.body = null;
        }

        @Override
        public BuilderWithBodyImpl body(RequestBody body) {
            this.body = body;
            return this;
        }

        @Override
        public TemplateWithBody template() {
            return new TemplateWithBodyImpl(this.endpoint, this.method, this.charset, this.urlParameters, this.headers, this.body);
        }

        @Override
        protected BuilderWithBodyImpl self() {
            return this;
        }
    }

    private static class TemplateWithBodyImpl extends ATemplate<BuilderWithBodyImpl> implements TemplateWithBody {

        public TemplateWithBodyImpl(Endpoint endpoint, RequestMethod method, Charset charset, Map<String, String> urlParameters, Map<String, String> headers, RequestBody body) {
            super(endpoint, method, charset, urlParameters, headers, body);
        }

        @Override
        public BuilderWithBodyImpl enrich() {
            return super.enrich().body(this.body);
        }

        @Override
        protected BuilderWithBodyImpl createBuilder() {
            return new BuilderWithBodyImpl(this.endpoint, this.method);
        }
    }
}
