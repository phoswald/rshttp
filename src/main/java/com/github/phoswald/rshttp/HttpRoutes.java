package com.github.phoswald.rshttp;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HttpRoutes implements HttpHandler {

    private final List<Entry> entries;

    HttpRoutes(Builder[] entries) {
        this.entries = Stream.of(entries)
                .map(Entry::new)
                .collect(Collectors.toList());
    }

    @Override
    public HttpResponse handle(HttpRequest request) {
        for(Entry entry : entries) {
            Optional<HttpParams> match = entry.match(request);
            if(match.isPresent()) {
                    HttpRequest requestWithParams = request.toBuilder()
                            .params(match.get())
                            .build();
                return entry.methods.get(request.method()).handle(requestWithParams);
            }
        }
        return HttpServer.defaultHandler().handle(request);
    }

    public static class Entry {

        private final Pattern route;
        private final Map<HttpMethod, HttpHandler> methods;

        private Entry(Builder builder) {
            this.route = Objects.requireNonNull(builder.route, "route");
            this.methods = new LinkedHashMap<>(builder.methods);
        }

        Optional<HttpParams> match(HttpRequest request) {
            Matcher matcher = route.matcher(request.path());
            if(matcher.matches() && methods.containsKey(request.method())) {
                HttpParams.Builder params = HttpParams.builder();
                for(int group = 1; group <= matcher.groupCount(); group++) {
                    params.param("$" + group, matcher.group(group));
                }
                return Optional.of(params.build());
            } else {
                return Optional.empty();
            }
        }
    }

    public static class Builder {

        private Pattern route;
        private Map<HttpMethod, HttpHandler> methods = new LinkedHashMap<>();

        Builder() { }

        public Builder route(String route) {
            this.route = Pattern.compile(route);
            return this;
        }

        public Builder get(HttpHandler handler) {
            return add(HttpMethod.GET, handler);
        }

        public Builder head(HttpHandler handler) {
            return add(HttpMethod.HEAD, handler);
        }

        public Builder post(HttpHandler handler) {
            return add(HttpMethod.POST, handler);
        }

        public Builder put(HttpHandler handler) {
            return add(HttpMethod.PUT, handler);
        }

        public Builder patch(HttpHandler handler) {
            return add(HttpMethod.PATCH, handler);
        }

        public Builder delete(HttpHandler handler) {
            return add(HttpMethod.DELETE, handler);
        }

        public Builder toFileSystem(String base) {
            String prefix = route.toString();
            route = Pattern.compile(prefix + ".*");
            return add(HttpMethod.GET, FilesHandler.forFilesystem(base, prefix));
        }

        public Builder toClasspath(String base) {
            String prefix = route.toString();
            route = Pattern.compile(prefix + ".*");
            return add(HttpMethod.GET, FilesHandler.forClasspath(base, prefix));
        }

        private Builder add(HttpMethod method, HttpHandler handler) {
            this.methods.put(method, handler);
            return this;
        }
    }
}
