package io.gamioo.pomelo;

import com.zvidia.pomelo.websocket.OnErrorHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * some description
 *
 * @author Allen Jiang
 * @since 1.0.0
 */
public class GameErrorHandler implements OnErrorHandler {
    private static final Logger LOGGER = LogManager.getLogger(GameErrorHandler.class);
    private GameClient gameClient;

    public void init(GameClient client) {
        this.gameClient = client;
    }

    @Override
    public void onError(Exception e) {
        //To change body of implemented methods use File | Settings | File Templates.
        LOGGER.error(e.getMessage(), e);
        this.gameClient.setFlag(true);
    }
}
