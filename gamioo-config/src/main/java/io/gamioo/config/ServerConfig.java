package io.gamioo.config;

import com.alibaba.fastjson.JSON;
import io.gamioo.core.lang.Cache;
import io.gamioo.core.lang.Server;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class ServerConfig {
    private String content;
    /**
     * 服务器类型
     */
    private String type;
    private int tcpPort;
    private int webPort;
    private int innerPort;
    /**
     * 外网IP
     */
    private String externalIp;
    /**
     * 本地策划数据存放地址
     */
    private String local;
    private boolean debug;
    private int saveInterval;
    private int offlineInterval;

    private int maxConcurrentUser;//单区最高在线人数
    private int maxRegisterUser;//单区最大注册人数

    private int heartBeat;//网关心跳检测时间(0 不进行心跳检测)
    private boolean compress;//网关数据包压缩
    private int compressThreshold;//压缩阀值
    private boolean crypto;//网关数据包加密

    /**
     * rpc client: 超时
     */
    private int rpcTimeout;
    private Map<String, Object> root;
    private List<Cache> cacheList = new ArrayList<>();
    private Server server;

    private Properties dbConfig = new Properties();

    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }

    public void add(Cache cache) {
        cacheList.add(cache);
    }

    public List<Cache> getCacheList() {
        return cacheList;
    }

    public void setCacheList(List<Cache> cacheList) {
        this.cacheList = cacheList;
    }

    public String getExternalIp() {
        return externalIp;
    }

    public void setExternalIp(String externalIp) {
        this.externalIp = externalIp;
    }

    public int getRpcTimeout() {
        return rpcTimeout;
    }

    public void setRpcTimeout(int rpcTimeout) {
        this.rpcTimeout = rpcTimeout;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getTcpPort() {
        return tcpPort;
    }

    public void setTcpPort(int tcpPort) {
        this.tcpPort = tcpPort;
    }

    public int getWebPort() {
        return webPort;
    }

    public void setWebPort(int webPort) {
        this.webPort = webPort;
    }

    public String getLocal() {
        return local;
    }


    public void setLocal(String local) {
        this.local = local;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public int getSaveInterval() {
        return saveInterval;
    }

    public void setSaveInterval(int saveInterval) {
        this.saveInterval = saveInterval;
    }

    public int getOfflineInterval() {
        return offlineInterval;
    }

    public void setOfflineInterval(int offlineInterval) {
        this.offlineInterval = offlineInterval;
    }

    public int getInnerPort() {
        return innerPort;
    }

    public void setInnerPort(int innerPort) {
        this.innerPort = innerPort;
    }


    public Properties getDbConfig() {
        return dbConfig;
    }

    public void setDbConfig(Properties dbConfig) {
        this.dbConfig = dbConfig;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void refreshContent() {
        this.content = JSON.toJSONString(root);
    }

    public Map<String, Object> getRoot() {
        return root;
    }

    public void setRoot(Map<String, Object> root) {
        this.root = root;
    }

    public int getMaxConcurrentUser() {
        return maxConcurrentUser;
    }

    public void setMaxConcurrentUser(int maxConcurrentUser) {
        this.maxConcurrentUser = maxConcurrentUser;
    }

    public int getMaxRegisterUser() {
        return maxRegisterUser;
    }

    public void setMaxRegisterUser(int maxRegisterUser) {
        this.maxRegisterUser = maxRegisterUser;
    }

    public int getHeartBeat() {
        return heartBeat;
    }

    public void setHeartBeat(int heartBeat) {
        this.heartBeat = heartBeat;
    }

    public boolean isCompress() {
        return compress;
    }

    public void setCompress(boolean compress) {
        this.compress = compress;
    }

    public int getCompressThreshold() {
        return compressThreshold;
    }

    public void setCompressThreshold(int compressThreshold) {
        this.compressThreshold = compressThreshold;
    }

    public boolean isCrypto() {
        return crypto;
    }

    public void setCrypto(boolean crypto) {
        this.crypto = crypto;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

}
