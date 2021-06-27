package io.gamioo.robot;

import com.alibaba.fastjson.JSON;
import io.gamioo.redis.Redis;
import io.gamioo.redis.RedisConstant;
import io.gamioo.robot.entity.User;

public class NewUser {

    public static void main(String[] args) {

        Redis redis = new Redis("106.53.236.196", 6386, "com.123");
        //System.out.println("hello ketty");
        //  int[] array = new int[]{266373};
        //22391
        int[] array = new int[]{22391};
        for (int i = 0; i < array.length; i++) {
            User user = new User();
            user.setGroup(9);
            user.setArea(1);
            user.setId(array[i]);
            user.setRmb(50);
            user.setPriority(5);
            user.setToken("bO4BwPnFwpWAGY+OrQ0fV4A4RjikeUu4YWZDZy8Xurg=");
            user.setTokenB("132311817043202015620572");
            redis.hset(RedisConstant.KEY_ROBOT, String.valueOf(user.getId()), JSON.toJSONString(user));
        }
    }

}
