package com.github.phoswald.rshttp;

import java.io.IOException;
import java.net.ServerSocket;

public class PortFinder {

    public int findPort() {
        int port = 8000;
        int attempts = 10;
        while(attempts-- > 0) {
            try(ServerSocket socket = new ServerSocket(port)) {
                return port;
            } catch (IOException e) {
                port++;
            }
        }
        throw new IllegalStateException("Unable do find usable port");
    }
}
