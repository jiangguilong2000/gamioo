package io.gamioo.robot;

import io.gamioo.redis.RedisConstant;
import io.gamioo.robot.entity.User;

import java.util.List;

/**
 * 换组
 * 把A组变成B组
 *
 * @author Allen Jiang
 * @since 1.0.0
 */
public class ChangeGroupX {
    public static void main(String[] args) {
        RedisManager redisManager = new RedisManager();
        redisManager.init(RedisConstant.REDIS_TYPE_GLOBAL, "106.53.236.195", 6385, 0, "com.123");
        List<User> list = redisManager.getUserList(11);
        for (User user : list) {
            user.setGroup(10);
            redisManager.updateUser(user);
        }
    }
}