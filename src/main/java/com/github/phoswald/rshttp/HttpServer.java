package com.github.phoswald.rshttp;

public abstract class HttpServer implements AutoCloseable {

    protected HttpServer(Builder builder) { }

    public static HttpServer.Builder builder() {
        return new Builder();
    }

    @Override
    public abstract void close();

    public static final class Builder {

        int port = 8080;
        HttpHandler handler = defaultHandler();

        public Builder port(int port) {
            this.port = port;
            return this;
        }

        public Builder handler(HttpHandler handler) {
            this.handler = handler;
            return this;
        }

        public HttpServer build() {
            return new HttpServerImpl(this);
        }
    }

    public static HttpHandler defaultHandler() {
        return request -> HttpResponse.builder().status(404).build();
    }

    public static HttpHandler routes(HttpRoutes.Builder... entries) {
        return new HttpRoutes(entries);
    }

    public static HttpRoutes.Builder route(String route) {
        return new HttpRoutes.Builder().route(route);
    }
}
