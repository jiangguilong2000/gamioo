package io.gamioo.pomelo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * some description
 *
 * @author Allen Jiang
 * @since 1.0.0
 */
public class LeleMain {
    private static final Logger LOGGER = LogManager.getLogger(LeleMain.class);

    public static void main(String[] args) throws InterruptedException {


        GameHandshakeSuccessHandler handshakeSuccessHandler = new GameHandshakeSuccessHandler();
 

        GameErrorHandler errorHandler = new GameErrorHandler();

        GameClient client = new GameClient();
        client.init(handshakeSuccessHandler, errorHandler);

    }
}
