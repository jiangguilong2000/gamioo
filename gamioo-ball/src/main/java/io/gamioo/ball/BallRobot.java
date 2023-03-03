package io.gamioo.ball;

import io.gamioo.ball.entity.Target;

/**
 * some description
 *
 * @author Allen Jiang
 * @since 1.0.0
 */
public class BallRobot {
    public static void main(String[] args) {
        Target target = new Target();
        target.setId(1);
        target.setIp("47.100.124.15");
        target.setPort(80);
        target.setScheme("http");
        target.parse();
        WebSocketClient client = new WebSocketClient(1, target);
        client.connect();

    }
}
