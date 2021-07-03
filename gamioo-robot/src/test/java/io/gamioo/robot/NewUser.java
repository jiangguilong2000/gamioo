package io.gamioo.robot;

import com.alibaba.fastjson.JSON;
import io.gamioo.redis.Redis;
import io.gamioo.redis.RedisConstant;
import io.gamioo.robot.entity.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class NewUser {


    public static void main(String[] args) {
        Map<String, String> store = new HashMap<>();
        // store.put("132311817043202015620572", "bO4BwPnFwpWAGY+OrQ0fV4A4RjikeUu4YWZDZy8Xurg=");
        //store.put("529382015132319205726300", "2DvAw8rnyfpTvgUy+1+53KV0IPVt+7A+BW9GssNPn3Y=");
        //store.put("785799232319572482153287", "B70Z6+XxPq+4GOGQISsvU/7WpujqeAmB2ZY4GKYv9cQ=");
        //store.put("505384231484382903129285", "KzIw88x/qI2I/DwholEeMmQodUdJngdCxprUYicf230=");

        // store.put("685154231493282061280262", "M4yoa9MuzP5ctD7gewgfzKKPsWdkAkHc9K3klTurYL0=");
        store.put("966324231422182107313174", "WekzatJl1Ne94dkDA3t1wHjHD3a6cBz+OPYjgZJPNbE=");
        // store.put("127159132314572781130880", "56P4996IxStlhbfbTFxCr12hx/2TKpq5IYb+CH83ftw=");
        //  store.put("907227159132319045727410", "0fljz6fENffSlvhD1nxziUw+TUB65swVwK5dZT066r8=");

        Redis redis = new Redis("106.53.236.195", 6385, "com.123");
        //System.out.println("hello ketty");
        //  int[] array = new int[]{266373};
        //22391
        int[] array = new int[]{185508};
        for (int i = 0; i < array.length; i++) {
            for (Entry<String, String> e : store.entrySet()) {
                User user = new User();
                user.setGroup(12);
                user.setArea(1);
                user.setId(array[i]);
                user.setRmb(50);
                user.setPriority(0);
                user.setToken(e.getValue());
                user.setTokenB(e.getKey());
                redis.hset(RedisConstant.KEY_ROBOT, String.valueOf(user.getId()), JSON.toJSONString(user));
            }

        }
    }

}
