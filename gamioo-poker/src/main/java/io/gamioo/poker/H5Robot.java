package io.gamioo.poker;

import io.gamioo.core.concurrent.NameableThreadFactory;
import io.gamioo.core.util.IPUtil;
import io.gamioo.core.util.TelnetUtils;
import io.gamioo.poker.entity.Target;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Date;
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
    private WebSocketClient client;

    public static void main(String[] args) {
        H5Robot robot = new H5Robot();
        Target target = robot.getTarget();
        robot.initClient(target);


        logger.info("end working");
    }

    public void sendMessage() {
        if (client.isConnected()) {
            client.sendMessage();
        }
    }

    public void initClient(Target target) {
         client = new WebSocketClient(1, target);
        client.connect();
        stat.scheduleAtFixedRate(() -> {
            try {
                this.sendMessage();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }

        }, 5000, 10000, TimeUnit.MILLISECONDS);
    }

    public Target getTarget() {
        Target target = new Target();
        target.setId(1);
        target.setScheme("http");
        target.setIp("fhs3.gameyuyan.com");
        target.setPort(10006);
        target.setText(true);
        boolean connected = TelnetUtils.isConnected(target.getIp(), target.getPort());
        if (connected) {
            target.parse();
        } else {
            logger.error("目标无法通信 target={}", target);
        }
        return target;
    }
}
