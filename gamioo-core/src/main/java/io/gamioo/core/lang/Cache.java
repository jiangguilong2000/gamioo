package io.gamioo.core.lang;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * some description
 *
 * @author Allen Jiang
 * @since 1.0.0
 */
public class Cache {
    private int type;
    private String ip;
    private int port;
    private int index;
    private String password;

    public Cache(){

    }
    public Cache(int type, String ip, int port, int index, String password) {
        this.type = type;
        this.ip = ip;
        this.port = port;
        this.index = index;
        this.password = password;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
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

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
