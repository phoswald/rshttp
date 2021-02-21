package com.github.phoswald.rshttp;

import java.util.Optional;

public final class HttpResponse {

    private final int status;
    private final HttpHeaders headers;
    private final byte[] body;

    private HttpResponse(Builder builder) {
        this.status = builder.status;
        this.headers = builder.headers.build();
        this.body = builder.body;
    }

    public static Builder builder() {
        return new Builder();
    }

    public int status() {
        return status;
    }

    public HttpHeaders headers() {
        return headers;
    }

    public Optional<byte[]> body() {
        return Optional.ofNullable(body);
    }

    public static final class Builder {

        private int status = 200;
        private HttpHeaders.Builder headers = HttpHeaders.builder();
        private byte[] body;

        private Builder() { }

        public Builder status(int status) {
            this.status = status;
            return this;
        }

        public Builder header(String name, String value) {
            headers.header(name, value);
            return this;
        }

        public Builder body(byte[] body) {
            this.body = body;
            return this;
        }

        public HttpResponse build() {
            return new HttpResponse(this);
        }
    }
}
