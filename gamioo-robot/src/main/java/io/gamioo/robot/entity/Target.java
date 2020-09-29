/*
 * Copyright 2015-2020 Gamioo Authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.gamioo.robot.entity;

import io.gamioo.core.util.StringUtils;
import io.netty.handler.codec.http.HttpScheme;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.MessageFormat;

/**
 * some description
 *
 * @author Allen Jiang
 * @since 1.0.0
 */
public class Target extends Server {
    private static final Logger logger = LogManager.getLogger(Target.class);
    private final static String URI_HTTP = "ws://{0}:{1}/websocket";
    private final static String URI_HTTPS = "wss://{0}:{1}/websocket";
    private String scheme;
    private URI uri;
    private String ip;
    private int port;
    private int interval;


    public void parse(String value) {
        String[] array = StringUtils.split(value, ":");
        scheme = array[0];
        ip = array[1];
        port = Integer.parseInt(array[2]);
        interval = Integer.parseInt(array[3]);
        String url = MessageFormat.format(StringUtils.equals(HttpScheme.HTTP.name(), scheme) ? URI_HTTP : URI_HTTPS, ip, String.valueOf(port));
        try {
            uri = new URI(url);
        } catch (URISyntaxException e) {
            logger.error(e.getMessage(), e);
            return;
        }
        if (!"ws".equalsIgnoreCase(uri.getScheme()) && !"wss".equalsIgnoreCase(uri.getScheme())) {
            logger.error("Only WS(S) is supported");
            return;
        }


    }


    public URI getUri() {
        return uri;
    }


    public String getScheme() {
        return scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }
}
