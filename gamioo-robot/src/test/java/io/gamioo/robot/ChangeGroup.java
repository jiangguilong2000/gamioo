package io.gamioo.robot;

import io.gamioo.redis.RedisConstant;
import io.gamioo.robot.entity.User;

/**
 * 换组
 *
 * @author Allen Jiang
 * @since 1.0.0
 */
public class ChangeGroup {
    public static void main(String[] args) {
        int[] array = new int[]{194655, 229438, 260081};
        RedisManager redisManager = new RedisManager();
        redisManager.init(RedisConstant.REDIS_TYPE_GLOBAL, "106.53.236.195", 6385, 0, "com.123");
        for (int i = 0; i < array.length; i++) {
            User user = redisManager.getUser(array[i]);
            user.setGroup(9);
            redisManager.updateUser(user);
        }
    }
}
