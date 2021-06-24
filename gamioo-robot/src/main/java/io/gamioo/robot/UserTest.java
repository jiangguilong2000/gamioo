package io.gamioo.robot;

import io.gamioo.core.util.FileUtils;
import io.gamioo.core.util.StringUtils;
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
        UserTest test = new UserTest();
        Map<String, User> store = test.getUserList("user.txt");
        Map<String, String> playerStore = test.getPlayerList("player.txt");
        Map<String, String> remain = new HashMap<>();
        Map<String, String> token = new HashMap<>();
        for (User e : store.values()) {
            token.put(e.getToken(), e.getToken());
        }
        for (String e : playerStore.keySet()) {
            if (store.containsKey(e)) {
                User user = store.get(e);

                System.out.println(user.getId() + "," + user.getToken());
            } else {
                remain.put(e, e);

            }
        }
        for (String e : remain.keySet()) {
            System.out.println(e);
        }

        System.out.println("................................");
        for (String e : token.keySet()) {
            System.out.println(e);
        }
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

    public Map<String, User> getUserList(String path) {
        Map<String, User> ret = new HashMap<>();

        File file = FileUtils.getFile(path);
        try {
            List<String> content = FileUtils.readLines(file);
            content.forEach((value) -> {
                String[] array = StringUtils.split(value, ",");
                User user = new User();
                user.setId(Long.parseLong(array[0]));
                user.setToken(array[1]);
                ret.put(String.valueOf(user.getId()), user);
            });

        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return ret;
    }
}
