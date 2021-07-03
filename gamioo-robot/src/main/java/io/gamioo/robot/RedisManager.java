package io.gamioo.robot;

import com.alibaba.fastjson.JSON;
import io.gamioo.core.concurrent.NameableThreadFactory;
import io.gamioo.core.constant.SystemConstant;
import io.gamioo.core.lang.Cache;
import io.gamioo.core.lang.Server;
import io.gamioo.core.lang.ServerInfo;
import io.gamioo.core.util.TelnetUtils;
import io.gamioo.redis.Redis;
import io.gamioo.redis.RedisConstant;
import io.gamioo.robot.entity.RobotInfo;
import io.gamioo.robot.entity.Target;
import io.gamioo.robot.entity.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RedisManager {
    private static final Logger logger = LogManager.getLogger(RedisManager.class);
    private final ExecutorService pool = Executors.newCachedThreadPool(new NameableThreadFactory("async-publish"));

    public RedisManager() {

    }

    /**
     * 默认超时（毫秒）
     */
    public static final int DEFAULT_TIMEOUT = 2000;
    /**
     * 最大尝试次数
     */
    public static final int MAX_ATTEMPTS = 5;
    /**
     * 全局库缓存 ,消息库缓存(用于统计，监控，聊天)
     */
    private Redis globalCache;

    /**
     * 本地库
     */
    private Redis localCache;


    public void init(List<Cache> list) {

        for (Cache e : list) {
            this.init(e);
        }
    }

    public void init(int type, String ip, int port, int index, String password) {
        Cache cache = new Cache(type, ip, port, index, password);
        this.init(cache);
    }

    public void init(Cache cache) {
        switch (cache.getType()) {
            case RedisConstant.REDIS_TYPE_GLOBAL: {
                globalCache = new Redis(cache.getIp(), cache.getPort(), cache.getPassword(), cache.getIndex());
                break;
            }
            case RedisConstant.REDIS_TYPE_LOCAL: {
                localCache = new Redis(cache.getIp(), cache.getPort(), cache.getPassword(), cache.getIndex());
                break;
            }

            default:
                throw new IllegalStateException("Unexpected value: " + cache.getType());
        }

    }

    public Redis getGlobalCache() {
        return globalCache;
    }

    public Redis getLocalCache() {
        return localCache;
    }

    public void publish(int type, Object monitor) {
        String value = JSON.toJSONString(monitor);
        globalCache.publish(RedisConstant.CHANNEL_MONITOR, type + RedisConstant.CHANNEL_SEPARATOR + value);
    }

    public List<User> getUserList(int group, int area) {
        List<User> ret = new ArrayList<>();
        Map<String, String> store = globalCache.hgetAll(RedisConstant.KEY_ROBOT);
        store.forEach((key, value) -> {
            User user = JSON.parseObject(value, User.class);
            if (user != null) {
                if (group == 0) {
                    user.setArea(area);
                    ret.add(user);
                } else {
                    if (user.getGroup() == group) {
                        user.setArea(area);
                        ret.add(user);
                    }
                }
            }
        });
        return ret;
    }

    public List<User> getUserList(int group) {
        List<User> ret = new ArrayList<>();
        Map<String, String> store = globalCache.hgetAll(RedisConstant.KEY_ROBOT);
        store.forEach((key, value) -> {
            User user = JSON.parseObject(value, User.class);
            if (user != null) {
                if (group == 0) {
                    ret.add(user);
                } else {
                    if (user.getGroup() == group) {
                        ret.add(user);
                    }
                }
            }
        });
        return ret;
    }

    public User getUser(int id) {
        User ret = null;
        String value = globalCache.hget(RedisConstant.KEY_ROBOT, String.valueOf(id));
        if (value != null) {
            ret = JSON.parseObject(value, User.class);
        }
        return ret;
    }

    public void updateUser(User user) {
        globalCache.hset(RedisConstant.KEY_ROBOT, String.valueOf(user.getId()), JSON.toJSONString(user));
    }

    public void updateTarget(Target target) {
        globalCache.hset(RedisConstant.KEY_TARGET, String.valueOf(target.getId()), JSON.toJSONString(target));
    }

    public Target getTarget(int id) {
        Target ret = null;
        String value = globalCache.hget(RedisConstant.KEY_TARGET, String.valueOf(id));
        if (value != null) {
            ret = JSON.parseObject(value, Target.class);
            boolean connected = TelnetUtils.isConnected(ret.getIp(), ret.getPort());
            if (connected) {
                ret.parse();
            } else {
                logger.error("目标无法通信 target={}", ret);
            }
        }
        return ret;
    }

    /**
     * 全局CDN管理
     */
    public String getGloBalCDN() {
        String key = RedisConstant.KEY_GLOBAL_CDN;
        return globalCache.get(key);
    }

    // CDN管理
    public String getCDN(String pid) {
        return globalCache.hget(RedisConstant.KEY_CDN, pid);
    }

    public Map<String, String> getCDNStore() {
        return globalCache.hgetAll(RedisConstant.KEY_CDN);
    }

    public String getPlatformList() {
        return globalCache.get(RedisConstant.KEY_PLATFORMS);
    }

    public void registerUser(String pid, String userId, String userName, long id) {
        String key = RedisConstant.KEY_USER + ":" + pid;
        globalCache.hset(key, userId, String.valueOf(id));
//		key = RedisConstant.KEY_USER_NAME_ID;
//		globalCache.hset(key, userName, String.valueOf(id));
        key = RedisConstant.KEY_USER_ID_NAME;
        globalCache.hset(key, String.valueOf(id), userName);
    }

    public boolean containUserId(long userId) {
        return globalCache.hexists(RedisConstant.KEY_USER_ID_NAME, String.valueOf(userId));
    }


    public String getUserName(long userId) {
        return globalCache.hget(RedisConstant.KEY_USER_ID_NAME, String.valueOf(userId));
    }


    public long getUserId(String pid, String userId) {
        long ret = 0;
        String key = RedisConstant.KEY_USER + ":" + pid;
        String value = globalCache.hget(key, userId);
        if (value != null) {
            ret = Long.parseLong(value);
        }
        return ret;
    }


    /**
     * 注册服务器
     */
    public void registerServer(Server server) {
        server.setStatus(SystemConstant.SERVER_STATUS_OPENING);
        String value = JSON.toJSONString(server);
        globalCache.publish(RedisConstant.CHANNEL_ADD_SERVER, value);
        globalCache.hset(server.getType(), String.valueOf(server.getId()), value);
        logger.info("server registered id={},name={}", server.getId(), server.getName());
    }

    /**
     * 注销服务器
     */
    public void stopServer(Server server) {
        server.setStatus(SystemConstant.SERVER_STATUS_STOPED);
        String value = JSON.toJSONString(server);
        globalCache.publish(RedisConstant.CHANNEL_REMOVE_SERVER, value);
        globalCache.hset(server.getType(), String.valueOf(server.getId()), value);
        logger.info("server stopped id={},name={}", server.getId(), server.getName());
    }

    public Server getGlobalServer() {
        Server ret = null;
        String value = globalCache.get(SystemConstant.SERVER_TYPE_GLOBAL);
        if (value != null) {
            ret = JSON.parseObject(value, Server.class);
        }
        return ret;
    }

    /**
     * 根据类型获取服务器列表
     */
    public List<Server> getServerList(String type) {
        List<Server> ret = new ArrayList<>();
        Map<String, String> map = globalCache.hgetAll(type);
        Collection<String> list = map.values();
        for (String e : list) {
            Server server = JSON.parseObject(e, Server.class);
            ret.add(server);
        }
        return ret;
    }

    public void destroy() {
        try {
            globalCache.destory();
            localCache.destory();
        } catch (Exception e) {
            logger.error("close jedisCluster error ", e);
        }
    }

    /**
     * ID序列自增
     */
    public long newId(String key) {
        return globalCache.incr(key);
    }

    /**
     * 获取服务器信息
     */
    public ServerInfo getServerInfo(Server server) {
        String value = globalCache.hget(RedisConstant.KEY_SERVER_INFO, String.valueOf(server.getId()));
        ServerInfo info = null;
        if (value != null) {
            info = JSON.parseObject(value, ServerInfo.class);
            info.setTimes(info.getTimes() + 1);
            logger.info("server information info={}", info);
        } else {
            info = new ServerInfo();
            info.setId(server.getId());
            info.setName(server.getName());
            info.setTimes(1);
            info.setAddTime(new Date());
            logger.info("initialize server information ServerInfo={}", info);
        }
        this.updateServerInfo(info);
        return info;
    }

    public void updateServerInfo(ServerInfo server) {
        globalCache.hset(RedisConstant.KEY_SERVER_INFO, String.valueOf(server.getId()), JSON.toJSONString(server));
    }

    public void updateRobotInfo(int group, int area, String ip, int total, int num, int active) {
        RobotInfo info = new RobotInfo(group, area, ip, total, num, active);
        globalCache.hset(RedisConstant.KEY_ROBOT_INFO, info.buildKey(), JSON.toJSONString(info));

    }

}
