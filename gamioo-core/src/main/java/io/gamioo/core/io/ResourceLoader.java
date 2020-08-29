package io.gamioo.core.io;


import java.net.URL;

public class ResourceLoader {

    public UrlResource getResource(String location) {
        URL url = this.getClass().getClassLoader().getResource(location);
        return new UrlResource(url);
    }
}

