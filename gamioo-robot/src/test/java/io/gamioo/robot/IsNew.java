package io.gamioo.robot;

import io.gamioo.redis.RedisConstant;
import io.gamioo.robot.entity.User;

/**
 * some description
 * 判断账号是否已经入库
 *
 * @author Allen Jiang
 * @since 1.0.0
 */
public class IsNew {
    public static void main(String[] args) {
        RedisManager redisManager = new RedisManager();
        redisManager.init(RedisConstant.REDIS_TYPE_GLOBAL, "106.53.236.195", 6385, 0, "com.123");
        int[] array = new int[]{179179, 186296, 112401, 287663, 29741, 192590, 23722, 288543, 236947, 187897, 195358, 186062, 206062, 249818, 287744, 196800, 287477, 115204, 287716, 185553, 185508};
        for (int i = 0; i < array.length; i++) {
            User user = redisManager.getUser(array[i]);
            if (user == null) {
                System.out.println(array[i]);
            }

        }

    }
}
