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

package io.gamioo.poker.entity;

import com.alibaba.fastjson.annotation.JSONField;
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
    //private final static String URI_HTTP = "ws://{0}:{1}/websocket";
    private final static String URI_HTTPS = "wss://{0}:{1}/websocket";
    private final static String URI_HTTP = "http://{0}:{1}/socket.io/?EIO=3&transport=websocket";

    @JSONField(name = "id")
    private int id;
    @JSONField(name = "scheme")
    private String scheme;
    @JSONField(serialize = false)
    private URI uri;
    @JSONField(name = "ip")
    private String ip;
    @JSONField(name = "port")
    private int port;
    @JSONField(name = "interval")
    private int interval;
    @JSONField(serialize = false)
    private String url;
    @JSONField(name = "text")
    private boolean text;
    @JSONField(serialize = false)
    private int error;


    @Override
    public void parse() {
        if (url == null) {
            url = MessageFormat.format(StringUtils.equals(HttpScheme.HTTP.name(), scheme) ? URI_HTTP : URI_HTTPS, ip, String.valueOf(port));
        }
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

    @Override
    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    @Override
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isText() {
        return text;
    }

    public void setText(boolean text) {
        this.text = text;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getError() {
        return error;
    }

    public void setError(int error) {
        this.error = error;
    }

    public void increaseError() {
        this.error++;
    }
}
