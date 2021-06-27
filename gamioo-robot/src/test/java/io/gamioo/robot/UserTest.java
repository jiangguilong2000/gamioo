package io.gamioo.robot;

import com.alibaba.fastjson.JSON;
import io.gamioo.core.util.FileUtils;
import io.gamioo.core.util.StringUtils;
import io.gamioo.redis.Redis;
import io.gamioo.redis.RedisConstant;
import io.gamioo.robot.entity.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * some description
 *
 * @author Allen Jiang
 * @since 1.0.0
 */
public class UserTest {
    private static final Logger logger = LogManager.getLogger(UserTest.class);

    public static void main(String[] args) {
        Redis redis = new Redis("106.53.236.198", 6385, "123.123");

        UserTest test = new UserTest();
        Map<Integer, User> store = test.getUserList("user.txt");
        Map<Integer, User> storeB = test.getUserList("user2.txt");
        int i = 1;
        int size = 0;
        for (User entry : store.values()) {
            User user = storeB.get(entry.getId());
            if (user != null) {
                entry.setGroup(i);
                entry.setTokenB(user.getToken());
                size++;
                if (size % 20 == 0) {
                    size = 0;
                    i++;
                }
            }
        }
        for (User user : store.values()) {
            redis.hset(RedisConstant.KEY_ROBOT, String.valueOf(user.getId()), JSON.toJSONString(user));
        }
        System.out.println(redis.hlen(RedisConstant.KEY_ROBOT));

    }

    public Map<String, String> getPlayerList(String path) {
        Map<String, String> ret = new HashMap<>();

        File file = FileUtils.getFile(path);
        try {
            List<String> content = FileUtils.readLines(file);
            content.forEach((value) -> {
                ret.put(value, value);
            });

        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return ret;
    }

    public Map<Integer, User> getUserList(String path) {
        Map<Integer, User> ret = new HashMap<>();

        File file = FileUtils.getFile(path);
        try {
            List<String> content = FileUtils.readLines(file);
            content.forEach((value) -> {
                String[] array = StringUtils.split(value, ",");
                User user = new User();
                user.setId(Integer.parseInt(array[0]));
                user.setToken(array[1]);
                ret.put(user.getId(), user);
            });

        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return ret;
    }
}
