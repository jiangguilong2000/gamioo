package io.gamioo.config;


import io.gamioo.common.exception.ServiceException;
import io.gamioo.common.lang.Cache;
import io.gamioo.common.lang.Server;
import io.gamioo.common.util.FileUtils;
import io.gamioo.common.util.JVMUtil;
import io.gamioo.network.util.IPUtil;
import org.apache.commons.collections4.MapUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dom4j.Element;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class ServerConfigManager {
    private static final Logger logger = LogManager.getLogger(ServerConfigManager.class);
    private ServerConfig config = new ServerConfig();
    private Map<String, Object> root;

    public void load(String type, String fileName) throws ServiceException {
        File file = FileUtils.getFile("config/" + fileName);
        if (!file.exists()) {
            throw new ServiceException("config file not found");
        }
        try {
            load(type, file);
        } catch (Exception e) {
            throw new ServiceException("load config file failed: " + file.getAbsolutePath(), e);
        }
    }

    public void load(String type, File file) throws ServiceException {
        logger.info("load config file to start : {}", file.getAbsolutePath());
        Yaml yaml = new Yaml();
        try {
            root = yaml.load(new FileInputStream(file));
            String content = FileUtils.readFileToString(file);
            config.setContent(content);
            //   config.setRoot(root);
            //    config.setContent( JSON.toJSONString(root));
        } catch (IOException e) {
            throw new ServiceException("failed to load config file", e);
        }

        this.parse(type);

        logger.info("load config file to end : {}", file.getAbsolutePath());
    }

    private void parse(String type) {
        config.setType(type);
        int id = MapUtils.getInteger(root, "id");
        Element element = null;
        String name = MapUtils.getString(root, "name");
        String externalIp = MapUtils.getString(root, "externalIp");
        config.setExternalIp(externalIp);
        int tcpPort = MapUtils.getInteger(root, "tcpPort");
        config.setTcpPort(tcpPort);
        int innerPort = MapUtils.getInteger(root, "innerPort");
        config.setInnerPort(innerPort);
        int webPort = MapUtils.getInteger(root, "webPort");
        config.setWebPort(webPort);
        String innerIp = IPUtil.getIP();
        Server server = new Server();
        server.setId(id);
        server.setType(type);
        server.setArgs(JVMUtil.getStartArgs());
        server.setExternalIp(externalIp);
        server.setInnerPort(innerPort);
        server.setInnerIp(innerIp);
        server.setName(name);
        server.setTcpPort(tcpPort);
        server.setWebPort(webPort);
        server.setStartTime(new Date());
        config.setServer(server);
        // game
        {
            Map<String, Object> game = (Map<String, Object>) MapUtils.getObject(root, "game");
            String local = MapUtils.getString(game, "local");
            config.setLocal(local);
            boolean debug = MapUtils.getBoolean(game, "debug");
            config.setDebug(debug);
            int saveInterval = MapUtils.getInteger(game, "saveInterval");
            config.setSaveInterval(saveInterval);
            int offlineInterval = MapUtils.getInteger(game, "offlineInterval");
            config.setOfflineInterval(offlineInterval);
            int maxConcurrentUser = MapUtils.getInteger(game, "maxConcurrentUser");
            config.setMaxConcurrentUser(maxConcurrentUser);
            int maxRegisterUser = MapUtils.getInteger(game, "maxRegisterUser");
            config.setMaxRegisterUser(maxRegisterUser);
        }
        //protocol
        {
            Map<String, Object> protocol = (Map<String, Object>) MapUtils.getObject(root, "protocol");
            int heartBeat = MapUtils.getInteger(protocol, "heartBeat");
            config.setHeartBeat(heartBeat);
            boolean compress = MapUtils.getBoolean(protocol, "compress");
            config.setCompress(compress);
            int compressThreshold = MapUtils.getInteger(protocol, "compressThreshold");
            config.setCompressThreshold(compressThreshold);
            boolean crypto = MapUtils.getBoolean(protocol, "crypto");
            config.setCrypto(crypto);
        }

        //db
        {
            Map<String, String> db = (Map<String, String>) MapUtils.getObject(root, "db");
            Properties dbConfig = new Properties();
            dbConfig.putAll(db);
            config.setDbConfig(dbConfig);
        }

        // redis
        {
            List<Map<String, String>> list = (ArrayList<Map<String, String>>) MapUtils.getObject(root, "cache");
            for (Map<String, String> cache : list) {
                int redisType = MapUtils.getInteger(cache, "type");
                String ip = MapUtils.getString(cache, "ip");
                int port = MapUtils.getInteger(cache, "port");
                int index = MapUtils.getInteger(cache, "index");
                String password = MapUtils.getString(cache, "password");
                config.add(new Cache(redisType, ip, port, index, password));
            }
        }
        // RPC
        {
            Map<String, String> rpc = (Map<String, String>) MapUtils.getObject(root, "rpc");
            int rpcTimeout = MapUtils.getInteger(rpc, "timeout");
            config.setRpcTimeout(rpcTimeout);
        }

    }

    public ServerConfig getServerConfig() {
        return config;
    }


    public Map<String, Object> getRootElement() {
        return root;
    }

}
