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

    public static void main(String[] args) {
        PlatformClient platformClient = new PlatformClient("https://lele8k.game1617.com", 16000);
        Map<String, Object> map = new HashMap<>();
        map.put("group", "game80");
        map.put("url", "https://lele8k.game1617.com/?mo=80&n_vn=6873369640&nvilele8k=6805454532&d1lele8k=10");
        JSONObject ret = platformClient.get4https("/newapi/getws.aspx", map);
        LOGGER.info("ret={}", ret);
        String signName = ret.getString("signname");

        GameHandshakeSuccessHandler handshakeSuccessHandler = new GameHandshakeSuccessHandler();
        handshakeSuccessHandler.setSignName(signName);
        GameErrorHandler errorHandler = new GameErrorHandler();
        GameClient client = new GameClient();
        client.init(handshakeSuccessHandler, errorHandler);
    }
}
