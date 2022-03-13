package io.gamioo.pomelo;

import com.zvidia.pomelo.protocol.PomeloMessage;
import com.zvidia.pomelo.websocket.OnDataHandler;
import com.zvidia.pomelo.websocket.OnHandshakeSuccessHandler;
import com.zvidia.pomelo.websocket.PomeloClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * some description
 *
 * @author Allen Jiang
 * @since 1.0.0
 */
public class GameClient {
    private static final Logger LOGGER = LogManager.getLogger(GameClient.class);
    private PomeloClient connector;
    private boolean flag = false;

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public void hearBeat() {
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(() -> {
            connector.sendHeartBeat();
        }, 5, 20, TimeUnit.SECONDS);
    }

    public void joinRoom(int roomId) {
        JSONObject entity = new JSONObject();
        try {
            entity.put("gameSerId", roomId);
            entity.put("roomID", roomId);
            entity.put("spreadId", 0);
        } catch (JSONException e) {
            LOGGER.error(e.getMessage(), e);
        }

        connector.request("game.fhbk.joinRoom", entity.toString(), message -> {
            JSONObject object = message.getBodyJson();
            int code = object.getInt("code");
            if (code == 200) {
                List<Integer> list = new ArrayList<>();
                for (int i = 0; i < 8; i++) {
                    list.add(i);
                }

                JSONArray array = object.getJSONObject("data").getJSONObject("table").getJSONArray("users");
                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    Integer chairId = obj.getInt("chairId");
                    list.remove(chairId);
                    LOGGER.debug("{}", chairId);
                }

                if (list.size() > 0) {
                    // chat(list.get(0));
                    seat(list.get(0));
                }

            } else {
                LOGGER.debug("{}", object.get("msg") + ":" + roomId);
            }
//            if (object.get("code")) {int
//            }

            //    flag = true;


        });
    }

    /**
     * 落座
     */
    public void seat(int chairId) {

        JSONObject entity = new JSONObject();
        try {
            entity.put("chairId", chairId);
            entity.put("spreadId", 0);
        } catch (JSONException e) {
            LOGGER.error(e.getMessage(), e);
        }

        connector.request("game.fhbk.seat", entity.toString(), message -> {
            JSONObject object = message.getBodyJson();
            int code = object.getInt("code");
            if (code == 200) {
                int chair = object.getInt("chairId");
                LOGGER.debug("{}", chair);
                //   vChat(chair);
                getRoleInfo();
//                for (int i = 0; i < 10; i++) {
//                    chat(chair);
//                }

            } else {
                LOGGER.debug("{}", object.get("msg"));
            }
            //    flag = true;
        });
    }

    public void vChat(int chairId) {

        JSONObject entity = new JSONObject();
        try {
            entity.put("chairId", chairId);
            entity.put("msg", "i love you");
        } catch (JSONException e) {
            LOGGER.error(e.getMessage(), e);
        }

        connector.request("game.fhbk.vchat", entity.toString(), message -> {
            JSONObject object = message.getBodyJson();
            int code = object.getInt("code");
            if (code == 200) {
                LOGGER.debug("{}", object);
            } else {
                LOGGER.debug("{}", object.get("msg"));
            }

            //    flag = true;


        });


    }

    public void chat(int chairId) {
        JSONObject entity = new JSONObject();
        try {
            entity.put("type", 2);//2是语音聊天 0是表情聊天   1 文本聊天
            //  entity.put("index", "https://www.ximalaya.com/");
            entity.put("index", "v-gFdNVaJEakrOwws5_9-cB4Y9aqtSnCD3kLDafrkcXzWI5w9tZaZmGVPBFFSnGp");
            entity.put("text_type", "voice");//wenben
        } catch (JSONException e) {
            LOGGER.error(e.getMessage(), e);
        }

        connector.request("game.fhbk.chat", entity.toString(), message -> {
            JSONObject object = message.getBodyJson();
            int code = object.getInt("code");
            if (code == 200) {
                LOGGER.debug("{}", object);
            } else {
                LOGGER.debug("{}", object.get("msg"));
            }

            //    flag = true;


        });


    }

    public void getRoleInfo() {
        JSONObject entity = new JSONObject();
        try {
            entity.put("chairId", 0);
        } catch (JSONException e) {
            LOGGER.error(e.getMessage(), e);
        }
//{"code":200,"data":{"winCount":"0","noCount":"0","ttCount":"0","runCount":"0","userId":679057}}
        connector.request("game.fhbk.roleInfo", entity.toString(), message -> {
            LOGGER.debug("{}", message.getBodyJson());
            //    flag = true;


        });
    }


    public void getUserInfo() {
        JSONObject entity = new JSONObject();
        try {
            entity.put("userId", "369640");
        } catch (JSONException e) {
            LOGGER.error(e.getMessage(), e);
        }

        connector.request("hall.hallHandler.getUserInfo", entity.toString(), new OnDataHandler() {

            @Override
            public void onData(PomeloMessage.Message message) {
                LOGGER.debug("{}", message.getBodyJson());
                //    flag = true;


            }
        });
    }


    public void init(GameHandshakeSuccessHandler handshakeSuccessHandler, GameErrorHandler errorHandler) {
        handshakeSuccessHandler.init(this);
        errorHandler.init(this);
        try {
            final PomeloClient client = new PomeloClient(new URI("wss://wsf.ydddf.top:9000"));
//            List<Runnable> runs = new ArrayList<Runnable>();
//            runs.add(client);
//            PomeloClientTest.assertConcurrent("test websocket client", runs, 200);
            OnHandshakeSuccessHandler onHandshakeSuccessHandler = (_client, resp) -> {
                JSONObject json = new JSONObject();
                //  json.put("uid", 1);
                client.request("gate.gateHandler.getConnector", json.toString(), message -> {
                    try {
                        JSONObject bodyJson = message.getBodyJson();
                        String host = "wsf.ydddf.top";//bodyJson.getString(PomeloClient.HANDSHAKE_RES_HOST_KEY);
                        String port = "9002";//bodyJson.getString(PomeloClient.HANDSHAKE_RES_PORT_KEY);
                        client.close();
                        connector = new PomeloClient(new URI("wss://" + host + ":" + port));
                        connector.setOnHandshakeSuccessHandler(handshakeSuccessHandler);
                        connector.setOnErrorHandler(errorHandler);
                        connector.connect();
                    } catch (URISyntaxException e) {
                        LOGGER.error(e.getMessage(), e);  //To change body of catch statement use File | Settings | File Templates.
                        flag = true;
                    }
                });
            };
            client.setOnHandshakeSuccessHandler(onHandshakeSuccessHandler);
            client.setOnErrorHandler(errorHandler);
            client.connect();
            while (!flag) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }

            //   joinRoom();

        } catch (URISyntaxException e) {
            LOGGER.error(e.getMessage(), e);  //To change body of catch statement use File | Settings | File Templates.
        }

    }

}
