package io.gamioo.pomelo;

import com.zvidia.pomelo.websocket.OnHandshakeSuccessHandler;
import com.zvidia.pomelo.websocket.PomeloClient;
import io.gamioo.core.concurrent.NameableThreadFactory;
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
import java.util.concurrent.ScheduledExecutorService;
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
    private boolean room;

    private ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1, new NameableThreadFactory("chat"));

    public boolean isRoom() {
        return room;
    }

    public void setRoom(boolean room) {
        this.room = room;
    }

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

    public void joinRoom(GameType type, int roomId) {

        JSONObject entity = new JSONObject();
        try {
            entity.put("gameSerId", roomId);
            entity.put("roomID", roomId);
            entity.put("spreadId", 0);
            //  entity.put("spreadId", 369640);
        } catch (JSONException e) {
            LOGGER.error(e.getMessage(), e);
        }

        connector.request(type.getPrefix() + "joinRoom", entity.toString(), message -> {
            JSONObject object = message.getBodyJson();
            int code = object.getInt("code");
            if (code == 200) {
                room = true;
                LOGGER.info("进入房间成功 type={}, roomId={}", type.getName(), roomId);
                List<Integer> list = new ArrayList<>();
                for (int i = 0; i < 8; i++) {
                    list.add(i);
                }

                JSONArray array = object.getJSONObject("data").getJSONObject("table").getJSONArray("users");

                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    Integer chairId = obj.getInt("chairId");
                    Integer state = obj.getInt("state");
                    if (state == 3) {
                        list.remove(chairId);
                        LOGGER.debug("{} 已有人", chairId);
                    }
                }

                if (list.size() == 0) {
                    //      showEnemyCards();
                    executorService.scheduleAtFixedRate(() -> {
                        chat();
                    }, 0, 18, TimeUnit.SECONDS);


                    //      seat(list.get(0));
                } else {
                    LOGGER.warn("人没坐齐，先不说 num={}", list.size());
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
                executorService.scheduleAtFixedRate(() -> {
                    //    chat();
                }, 0, 18, TimeUnit.SECONDS);
                //    vChat(chair);
                //      getRoleInfo();
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

    public void chat() {
        LOGGER.info("开始说话");
        JSONObject entity = new JSONObject();
        try {
            entity.put("type", 2);//2是语音聊天 0是表情聊天   1 文本聊天
            //  entity.put("index", "https://www.ximalaya.com/");
            // entity.put("index", "VDEHjopP8zlbgDcLPUK6_sxNtMH5Fg9x2bDa6_Lkjgj3Eix8kO5vRYHXb63q_oh9");//叫声
            // entity.put("index", "dD3RerMOX0fS9rdIJnE_9h7c8C5iV09RZ52eBuInZiRS9EQL7TgCxnzEi5Ted-3d");//这里网警中队
            entity.put("index", "_Lp-hLAyspmRtW2sBEnvd-IBGU-szWunhJ3FqHPS7eJbmjno5vnKqNJFMw6F3HK2");//这里网警中队
            //_Lp-hLAyspmRtW2sBEnvdwOth6vc9qG2GYFXRlzMufU-M2RFsWGsqjXdU2c9kPWm
            //_Lp-hLAyspmRtW2sBEnvd-IBGU-szWunhJ3FqHPS7eJbmjno5vnKqNJFMw6F3HK2
            //_Lp-hLAyspmRtW2sBEnvd2tDWg65sSWwdxZYJsXiky3PFVO91k6nU9GtGpGgc6vN

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


    public void showEnemyCards() {
        LOGGER.info("showEnemyCards");
        JSONObject entity = new JSONObject();
        try {
            entity.put("overTable", "");
        } catch (JSONException e) {
            LOGGER.error(e.getMessage(), e);
        }

        connector.request("game.fhbk.showEnemyCards", entity.toString(), message -> {
            LOGGER.debug("{}", message.getBodyJson());
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


    public void getUserInfo(int userId) {
        JSONObject entity = new JSONObject();
        try {
            entity.put("userId", userId);
        } catch (JSONException e) {
            LOGGER.error(e.getMessage(), e);
        }

        connector.request("hall.hallHandler.getUserInfo", entity.toString(), message -> {
            LOGGER.debug("{}", message.getBodyJson());
            //    flag = true;


        });
    }


    public void init(GameHandshakeSuccessHandler handshakeSuccessHandler, GameErrorHandler errorHandler) {
        handshakeSuccessHandler.init(this);
        errorHandler.init(this);
        try {
            final PomeloClient client = new PomeloClient(new URI("wss://lele8k.game1617.com:9000"));
//            List<Runnable> runs = new ArrayList<Runnable>();
//            runs.add(client);
//            PomeloClientTest.assertConcurrent("test websocket client", runs, 200);
            OnHandshakeSuccessHandler onHandshakeSuccessHandler = (_client, resp) -> {
                JSONObject json = new JSONObject();
                //  json.put("uid", 1);
                client.request("gate.gateHandler.getConnector", json.toString(), message -> {
                    try {
                        JSONObject bodyJson = message.getBodyJson();
                        String host = "lele8k.game1617.com";//bodyJson.getString(PomeloClient.HANDSHAKE_RES_HOST_KEY);
                        String port = "9011";//bodyJson.getString(PomeloClient.HANDSHAKE_RES_PORT_KEY);
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