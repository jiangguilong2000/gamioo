package io.gamioo.pomelo;

import com.alibaba.fastjson.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * some description
 *
 * @author Allen Jiang
 * @since 1.0.0
 */
public class LeleMain {
    private static final Logger LOGGER = LogManager.getLogger(LeleMain.class);

    public static void main(String[] args) throws InterruptedException {
        PlatformClient platformClient = new PlatformClient("https://lele8k.game1617.com", 16000);
        Map<String, Object> map = new HashMap<>();
        map.put("group", "game80");
        map.put("playerid", "oxmtuw_GQXpDC5Ox9UhJxOBGznJM");
        map.put("formid", "3");
        map.put("url", "https://lele8k.game1617.com/index.html?mo=80&n_vn=3005369640&nvilele8k=1198454532&d1lele8k=10");
        JSONObject ret = platformClient.get4https("/newapiex/getws.aspx", map);
        LOGGER.info("ret={}", ret);
        String signData = ret.getString("signdata");

        GameHandshakeSuccessHandler handshakeSuccessHandler = new GameHandshakeSuccessHandler();
        handshakeSuccessHandler.setSignData(signData);
        GameErrorHandler errorHandler = new GameErrorHandler();

        GameClient client = new GameClient();
        client.init(handshakeSuccessHandler, errorHandler);


    }
}
