package com.github.phoswald.rshttp;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpExchange;

@SuppressWarnings("restriction")
final class HttpServerImpl extends HttpServer {

    private final com.sun.net.httpserver.HttpServer server;

    HttpServerImpl(Builder builder) {
        super(builder);
        try {
            server = com.sun.net.httpserver.HttpServer.create(new InetSocketAddress(builder.port), 0);
            server.createContext("/", new HttpHandlerImpl(builder.handler));
            server.start();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void close() {
        server.stop(0);
    }

    private static final class HttpHandlerImpl implements com.sun.net.httpserver.HttpHandler {

        private final HttpHandler handler;

        private HttpHandlerImpl(HttpHandler handler) {
            this.handler = handler;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            HttpParams params = HttpParams.fromQuery(exchange.getRequestURI().getRawQuery());
            HttpRequest request = HttpRequest.builder()
                    .get()
                    .uri(exchange.getRequestURI())
                    .params(params)
                    .build();

            HttpResponse response = handler.handle(request);

            for(String name : response.headers().names()) {
                for(String value : response.headers().allValues(name)) {
                    exchange.getResponseHeaders().add(name, value);
                }
            }
            if(!response.body().isPresent()) {
                exchange.sendResponseHeaders(response.status(), -1);
            } else {
                exchange.sendResponseHeaders(response.status(), response.body().get().length);
                exchange.getResponseBody().write(response.body().get());
                exchange.getResponseBody().close();
            }
        }
    }
}
