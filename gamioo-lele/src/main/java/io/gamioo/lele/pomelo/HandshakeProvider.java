package io.gamioo.lele.pomelo;


import com.alibaba.fastjson.JSONObject;
import io.gamioo.lele.H5Robot;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: jiangzm
 * Date: 13-8-8
 * Time: 下午10:35
 * To change this template use File | Settings | File Templates.
 */
public class HandshakeProvider {
    private static final Logger logger = LogManager.getLogger(HandshakeProvider.class);
    public static final String JS_WS_CLIENT_TYPE = "js-websocket";
    public static final String JS_WS_CLIENT_VERSION = "1.1.1";

    public static final String HANDSHAKE_SYS_KEY = "sys";
    public static final String HANDSHAKE_SYS_TYPE_KEY = "type";
    public static final String HANDSHAKE_SYS_VERSION_KEY = "version";
    public static final String HANDSHAKE_SYS_HEARTBEAT_KEY = "heartbeat";
    public static final String HANDSHAKE_SYS_DICT_KEY = "dict";
    public static final String HANDSHAKE_SYS_PROTOS_KEY = "protos";
    public static final String HANDSHAKE_SYS_PROTOS_VERSION_KEY = "version";
    public static final String HANDSHAKE_SYS_PROTOS_SERVER_KEY = "server";
    public static final String HANDSHAKE_SYS_PROTOS_CLIENT_KEY = "client";

    public static final String HANDSHAKE_USER_KEY = "user";

    public static final String RES_CODE_KEY = "code";

    public static final int RES_OK = 200;
    public static final int RES_FAIL = 500;
    public static final int RES_OLD_CLIENT = 501;

    public static JSONObject handshakeObject() {
        JSONObject handshake = new JSONObject();
        JSONObject sys = new JSONObject();
        sys.put(HANDSHAKE_SYS_TYPE_KEY, JS_WS_CLIENT_TYPE);
        sys.put(HANDSHAKE_SYS_VERSION_KEY, JS_WS_CLIENT_VERSION);
        JSONObject user = new JSONObject();
        handshake.put(HANDSHAKE_SYS_KEY, sys);
        handshake.put(HANDSHAKE_USER_KEY, user);
        logger.debug("send msg={}", handshake.toJSONString());
        return handshake;
    }
}
