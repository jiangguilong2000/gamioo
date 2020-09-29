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

import com.alibaba.fastjson.annotation.JSONField;

import java.util.Date;

/**
 * 代理地址
 *
 * @author Allen Jiang
 * @since 1.0.0
 */
public class Proxy extends  Server{
    @JSONField(name = "ip")
    private String ip;
    @JSONField(name = "port")
    private int port;

    @JSONField(name = "city")
    private String city;
    @JSONField(name = "isp")
    private String isp;
    @JSONField(name = "expire_time", format = "yyyy-MM-dd HH:mm:ss")
    private Date expireTime;

    public void parse()  {
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

    public Date getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Date expireTime) {
        this.expireTime = expireTime;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getIsp() {
        return isp;
    }

    public void setIsp(String isp) {
        this.isp = isp;
    }
}
