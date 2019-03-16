package io.medev.httpclient;

public interface RequestMethod {

    RequestMethodWithoutBody HEAD = new RequestMethodWithoutBody("HEAD");
    RequestMethodWithoutBody GET = new RequestMethodWithoutBody("GET");
    RequestMethodWithBody POST = new RequestMethodWithBody("POST");
    RequestMethodWithBody PUT = new RequestMethodWithBody("PUT");
    RequestMethodWithBody PATCH = new RequestMethodWithBody("PATCH");
    RequestMethodWithoutBody DELETE = new RequestMethodWithoutBody("DELETE");

    String getName();
    boolean supportsRequestBody();

    RequestMethod[] VALUES = new RequestMethod[]{HEAD, GET, POST, PUT, PATCH, DELETE};
    static RequestMethod[] values() {
        return VALUES;
    }

    class RequestMethodWithBody implements RequestMethod {

        private final String name;

        private RequestMethodWithBody(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return this.name;
        }

        @Override
        public boolean supportsRequestBody() {
            return true;
        }
    }

    class RequestMethodWithoutBody implements RequestMethod {

        private final String name;

        private RequestMethodWithoutBody(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return this.name;
        }

        @Override
        public boolean supportsRequestBody() {
            return false;
        }
    }
}
