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

package io.gamioo.robot;

import com.alibaba.fastjson.JSON;
import io.gamioo.core.concurrent.NameableThreadFactory;
import io.gamioo.core.lang.Cache;
import io.gamioo.core.util.FileUtils;
import io.gamioo.core.util.StringUtils;
import io.gamioo.core.util.TelnetUtils;
import io.gamioo.core.util.ThreadUtils;
import io.gamioo.robot.entity.Target;
import io.gamioo.robot.entity.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * some description
 *
 * @author Allen Jiang
 * @since 1.0.0
 */
public class H5Robot {
    private static final Logger logger = LogManager.getLogger(H5Robot.class);
    private ScheduledExecutorService stat = Executors.newScheduledThreadPool(1, new NameableThreadFactory("stat"));
    private ScheduledExecutorService connect = Executors.newScheduledThreadPool(1, new NameableThreadFactory("connect"));
    private Target target;
    private List<User> userList;
    public static Map<Integer, WebSocketClient> clientStore = new ConcurrentHashMap<>();

    private long lastUserId;

    public void init() {
        stat.scheduleAtFixedRate(() -> {
            try {
                int total = 0;
                int connect = 0;
                Date now = new Date();
                for (WebSocketClient e : clientStore.values()) {
                    if (e.isLegal()) {
                        total++;
//                        if (!e.isOnline()) {
////                            if (DateUtils.addSeconds(e.getLastRecvTime(), 10).before(now)) {
////                                e.disconnect();
////                            clientStore.remove(e.getId());
////                            }
//                        }

                        if (e.isConnected()) {
                            connect++;
                        }
                    }

                }
                logger.warn("连接数 num={},active={}", total, connect);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }

        }, userList.size() * 300, 10000, TimeUnit.MILLISECONDS);


    }

    public void end() {
        connect.scheduleAtFixedRate(() -> {
            try {
                for (WebSocketClient e : clientStore.values()) {
                    if (!e.isConnected()) {
                        //能通信再连
                        if (lastUserId != e.getUserId()) {
                            ThreadUtils.sleep(e.getError() * 5);
                            logger.warn("开始重连  id={},userId={}", e.getId(), e.getUserId());
                            lastUserId = e.getUserId();
                            if (TelnetUtils.isConnected(this.target.getIp(), this.target.getPort())) {
                                e.connect();
                            }
                            break;
                        } else {
                            lastUserId = 0;
                        }
                    }
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }

        }, target.getInterval(), target.getInterval(), TimeUnit.MILLISECONDS);
    }


    public static void main(String[] args) {
        int group = Integer.parseInt(args[0]);
        int area = Integer.parseInt(args[1]);

        String version = H5Robot.class.getPackage().getImplementationVersion();
        logger.info("start working version={},group={},area={}", version, group, area);
        H5Robot robot = new H5Robot();

        Cache cache = robot.getCache("cache.json");
        RedisManager redisManager = new RedisManager();
        redisManager.init(cache);
        List<User> userList = redisManager.getUserList(group, area);
        Collections.sort(userList);
        robot.setUserList(userList);
        Target target = redisManager.getTarget(area);
        robot.setTarget(target);
        robot.init();
        //redisManager.updateTarget(target);

        robot.handle();
        robot.end();
        logger.info("end working");
    }

    public void handle() {
        int id = 0;
        logger.warn("待处理玩家数 num={}", userList.size());
        for (User user : userList) {
            try {
                WebSocketClient client = new WebSocketClient(++id, user, target);
                ThreadUtils.sleep(target.getInterval());
                client.connect();
            } catch (Exception e) {
                logger.error("size={}", userList.size());
                logger.error(e.getMessage(), e);
            }
        }
    }


//    public void handleX() {
//        int id = 0;
//        for (int i = 0; i < target.getNumber(); i++) {
//            try {
//                WebSocketClient client = new WebSocketClient(++id, userList.get(id - 1), target);
//                ThreadUtils.sleep(target.getInterval());
//                client.connect();
//            } catch (Exception e) {
//                logger.error("size={}", userList.size());
//                logger.error(e.getMessage(), e);
//            }
//        }
//    }

    public List<User> getUserList(String path) {
        List<User> ret = new ArrayList<>();
        File file = FileUtils.getFile(path);
        try {
            List<String> content = FileUtils.readLines(file);
            content.forEach((value) -> {
                String[] array = StringUtils.split(value, ",");
                User user = new User();
                user.setId(Integer.parseInt(array[0]));
                user.setToken(array[1]);
                ret.add(user);
            });

        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return ret;
    }

    public Target getTarget(String path) {
        Target ret = null;
        File file = FileUtils.getFile(path);
        try {
            String content = FileUtils.readFileToString(file);
            ret = JSON.parseObject(content, Target.class);
            boolean connected = TelnetUtils.isConnected(ret.getIp(), ret.getPort());
            if (connected) {
                ret.parse();
            } else {
                logger.error("目标无法通信 target={}", ret);
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return ret;
    }

    public Cache getCache(String path) {
        Cache ret = null;
        File file = FileUtils.getFile(path);
        try {
            String content = FileUtils.readFileToString(file);
            ret = JSON.parseObject(content, Cache.class);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return ret;

    }


    public void setTarget(Target target) {
        this.target = target;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
    }
}
