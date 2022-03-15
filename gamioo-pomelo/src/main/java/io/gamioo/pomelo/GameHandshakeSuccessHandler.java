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
    private String signName;

    public String getSignName() {
        return signName;
    }

    public void setSignName(String signName) {
        this.signName = signName;
    }

    public void init(GameClient client) {
        this.gameClient = client;
    }

    @Override
    public void onSuccess(PomeloClient client, JSONObject jsonObject) {
        try {
            this.gameClient.hearBeat();

            JSONObject entity = new JSONObject();
            //oR9cD1lM5cuXYI-P6nhEE0rFLz40
            //  entity.put("_id", "oxmtuw2bSnGwlEC-IZGs7ftrvFaM@ll2020");
            // entity.put("_id", "oxmtuw8bSnGwlEC-IZGs7ftrvFaM@ll2020");


            //  entity.put("_id", "oxmtuw5BSnGwlEC-IZGs7ftrvFaM@ll2020");
            entity.put("_id", "oxmtuw_GQXpDC5Ox9UhJxOBGznJM@ll2020");

            // entity.put("name", "鱼香Rose");
            entity.put("name", "\u963f\u9f99\\u20\u1be4\u2075\u1d4d\\u28\ud83d\udcad");
            //  entity.put("name", "\u963f\u9f99\\u20\u1be4\u2075\u1d4d\\u20\ud83d\udcad");
            //     entity.put("name", "鱼香Rose");
            entity.put("sex", "1");
            //  String value = RandomStringUtils.random(99999999, " ");
//            StringBuilder sb = new StringBuilder();
//            for (int i = 0; i < 99999999; i++) {
//                sb.append("a");
//            }
            //     String value = RandomStringUtils.randomAlphanumeric(99999999);
            //  value = MessageFormat.format("https://thirdwx.qlogo.cn/mmopen/vi_32/{0}/132", sb.toString());
            //   entity.put("headurl", value);
//                eval("("+req.query.input+")");
//
//                console.log("spreadid:", "eval(console.debug(GlobalData.openid))")

            //   entity.put("headurl", "eval(\"console.debug(GlobalData.openid)\")");
            entity.put("headurl", "https://thirdwx.qlogo.cn/mmopen/vi_32/y6E5GpvgQicwocia23oqIibpWBqEbCgny2JGsrAaCK1xSSPQbCXOzBZjN9iboicCqnQ1SHB3943qYZHibSWqAUpHdZ4A/132");
            entity.put("agentId", "454532");
            entity.put("formId", 3);
            entity.put("verison", "143");
            entity.put("ip", "::ffff:101.228.60.137");
            entity.put("dis", 0);
            entity.put("smslogon", 0);
            entity.put("protocol", "https://");
            entity.put("hostname", "lele8k.game1617.com");
            entity.put("connectIP", "wsf.ydddf.top");
            entity.put("passname", "31376101647185013681");
            entity.put("signname", this.signName);
            entity.put("signData", "oxmtuw_GQXpDC5Ox9UhJxOBGznJM@ll202031376101647185013681");
            entity.put("wsId", 0);
            client.request("connector.entryHandler.login", entity.toString(), message -> {
                LOGGER.debug("{}", message.getBodyJson());


                //    joinRoom(103798);
                // getUserInfo();
                //  flag = true;


            });
        } catch (JSONException e) {
            LOGGER.error(e.getMessage(), e);
            this.gameClient.setFlag(true);

        }
    }


    public void onSuccessX(PomeloClient client, JSONObject jsonObject) {
        try {
            this.gameClient.hearBeat();

            JSONObject entity = new JSONObject();
            //oR9cD1lM5cuXYI-P6nhEE0rFLz40
            //  entity.put("_id", "oxmtuw2bSnGwlEC-IZGs7ftrvFaM@ll2020");
            //  entity.put("_id", "oxmtuw8BSnGwlEC-IZGs7ftrvFaM@ll2020");
            entity.put("_id", "oxmtuw_GQXpDC5Ox9UhJxOBGznJM@agent");

            //  entity.put("name", "鱼香Rose");
            entity.put("name", "Rose");
            //     entity.put("name", "鱼香Rose");
            entity.put("sex", "1");
            //  String value = RandomStringUtils.random(99999999, " ");
//            StringBuilder sb = new StringBuilder();
//            for (int i = 0; i < 99999999; i++) {
//                sb.append("a");
//            }
            //     String value = RandomStringUtils.randomAlphanumeric(99999999);
            //  value = MessageFormat.format("https://thirdwx.qlogo.cn/mmopen/vi_32/{0}/132", sb.toString());
            //   entity.put("headurl", value);
//                eval("("+req.query.input+")");
//
//                console.log("spreadid:", "eval(console.debug(GlobalData.openid))")

            //   entity.put("headurl", "eval(\"console.debug(GlobalData.openid)\")");
            entity.put("headurl", "https://thirdwx.qlogo.cn/mmopen/vi_32/Q0j4TwGTfTKVxmKqbePL0dxibYWoo2YGTT6dNQF4Yr1gq7uthk46t3iaCvXP3aIa4kpG2XCtpQDt1ibM44PxQ9uag1/132");
            entity.put("agentId", "464532");
            entity.put("formId", 2);
            entity.put("verison", "88");
            entity.put("ip", "::ffff:101:228.60.137");
            entity.put("dis", 0);
            entity.put("smslogon", 0);
            entity.put("protocol", "https://");
            entity.put("hostname", "lele8k.game1617.com");
            entity.put("connectIP", "wsf.ydddf.top");
            entity.put("wsId", -1);
            client.request("connector.entryHandler.login", entity.toString(), message -> {
                LOGGER.debug("{}", message.getBodyJson());


                //    joinRoom(103798);
                // getUserInfo();
                //  flag = true;


            });
        } catch (JSONException e) {
            LOGGER.error(e.getMessage(), e);
            this.gameClient.setFlag(true);

        }
    }
}
