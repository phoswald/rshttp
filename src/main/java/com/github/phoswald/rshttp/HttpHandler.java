package com.github.phoswald.rshttp;

public interface HttpHandler {

    public HttpResponse handle(HttpRequest request);
}
