package com.github.phoswald.rshttp;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

abstract class FilesHandler implements HttpHandler {

    private static final Map<String, String> MIME_TYPES = new HashMap<>();

    static {
        MIME_TYPES.put("html", "text/html");
    }

    private final String baseDirectory;
    private final String relativePrefixDirectory;

    private FilesHandler(String base, String prefix) {
        this.baseDirectory = toDirectory(base);
        this.relativePrefixDirectory = toRelativePath(toDirectory(prefix));
    }

    static HttpHandler forFilesystem(String base, String prefix) {
        Objects.requireNonNull(base, "base");
        Objects.requireNonNull(prefix, "prefix");
        return new FilesHandler(Paths.get(base).toAbsolutePath().toString(), prefix) {
            @Override
            URL resolve(String baseDirectory, String relativeSubPath) throws MalformedURLException {
                return Paths.get(baseDirectory + relativeSubPath).toUri().toURL();
            }
        };
    }

    static HttpHandler forClasspath(String base, String prefix) {
        Objects.requireNonNull(base, "base");
        Objects.requireNonNull(prefix, "prefix");
        return new FilesHandler(toRelativePath(base), prefix) {
            @Override
            URL resolve(String baseDirectory, String relativeSubPath) {
                return getClass().getClassLoader().getResource(baseDirectory + relativeSubPath);
            }
        };
    }

    abstract URL resolve(String baseDirectory, String relativeSubPath) throws MalformedURLException;

    @Override
    public HttpResponse handle(HttpRequest request) {
        try {
            if(request.method() == HttpMethod.GET) {
                String relativeSubPath = toRelativePath(request.path());
                if(relativeSubPath.startsWith(relativePrefixDirectory)) {
                    relativeSubPath = relativeSubPath.substring(relativePrefixDirectory.length());
                } else {
                    return HttpResponse.builder().status(404).build();
                }
                if(relativeSubPath.isEmpty()) {
                    relativeSubPath = "index.html";
                }
                URL url = resolve(baseDirectory, relativeSubPath);
                if(url == null) {
                    return HttpResponse.builder().status(404).build();
                }
                try(InputStream stream = url.openStream()) {
                    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                    int b;
                    while((b = stream.read()) != -1) {
                        buffer.write(b);
                    }
                    byte[] content = buffer.toByteArray();
                    return HttpResponse.builder()
                            .status(200)
                            .header("content-type", getContentType(url.getFile()))
                            .body(content)
                            .build();
                }
            } else {
                return HttpResponse.builder().status(403).build();
            }
        } catch(IOException e) {
            return HttpResponse.builder().status(500).build();
        }
    }

    private static String toRelativePath(String path) {
        if(path.startsWith("/")) {
            return path.substring(1);
        } else {
            return path;
        }
    }

    private static String toDirectory(String path) {
        if(path.endsWith("/")) {
            return path;
        } else {
            return path + "/";
        }
    }

    private static String getContentType(String path) {
        String extension = path.substring(path.lastIndexOf(".") + 1).toLowerCase();
        return MIME_TYPES.getOrDefault(extension, "application/octet-stream");
    }
}
