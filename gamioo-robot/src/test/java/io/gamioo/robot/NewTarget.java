package io.gamioo.robot;

import com.alibaba.fastjson.JSON;
import io.gamioo.redis.Redis;
import io.gamioo.redis.RedisConstant;
import io.gamioo.robot.entity.User;

/**
 * some description
 *
 * @author Allen Jiang
 * @since 1.0.0
 */
public class NewTarget {
    public static void main(String[] args) {
        {
            Redis redis = new Redis("106.53.236.196", 6386, "com.123");
            //System.out.println("hello ketty");
            //  int[] array = new int[]{266373};


            int[] array = new int[]{284740, 186634, 226048, 277064};
            for (int i = 0; i < array.length; i++) {
                User user = new User();
                user.setGroup(9);
                user.setArea(1);
                user.setId(array[i]);
                user.setRmb(30);
                user.setPriority(3);
                user.setToken("WekzatJl1Ne94dkDA3t1wHjHD3a6cBz+OPYjgZJPNbE=");
                user.setTokenB("966324231422182107313174");
                redis.hset(RedisConstant.KEY_ROBOT, String.valueOf(user.getId()), JSON.toJSONString(user));
            }
        }
    }
}
