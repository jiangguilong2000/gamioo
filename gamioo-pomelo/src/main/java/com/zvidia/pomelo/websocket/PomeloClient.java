package com.zvidia.pomelo.websocket;

import com.zvidia.pomelo.exception.PomeloException;
import com.zvidia.pomelo.protobuf.ProtoBuf;
import com.zvidia.pomelo.protocol.PomeloMessage;
import com.zvidia.pomelo.protocol.PomeloPackage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.java_websocket.client.DefaultSSLWebSocketClientFactory;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import javax.net.ssl.*;
import java.net.URI;
import java.nio.ByteBuffer;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: jiangzm
 * Date: 13-8-8
 * Time: 下午10:04
 * To change this template use File | Settings | File Templates.
 */
public class PomeloClient extends WebSocketClient {
    private static final Logger LOGGER = LogManager.getLogger(PomeloClient.class);
    public static final String HANDSHAKE_RES_HOST_KEY = "host";
    public static final String HANDSHAKE_RES_PORT_KEY = "port";
    public static final String HANDSHAKE_RES_CODE_KEY = "code";

    private long heartbeatInterval = 0;
    private long heartbeatTimeout = 0;
    private long nextHeartbeatTimeout = 0;

    private JSONObject protos;

    private JSONObject dict;

    private JSONObject abbrs;

    private Map<Integer, String> routeMap = new HashMap<Integer, String>();

    private Map<Integer, OnDataHandler> onDataHandlerMap = new HashMap<>();

    private Map<String, OnDataHandler> eventHandlerMap = new HashMap<>();

    private JSONObject clientProtos;

    private JSONObject serverProtos;

    private int protosVersion;

    private ProtoBuf protoBuf;

    private int reqIdIndex = 0;

    private boolean isConnected;

    private OnHandshakeSuccessHandler onHandshakeSuccessHandler;
    private OnErrorHandler onErrorHandler;
    private OnCloseHandler onCloseHandler;
    private OnKickHandler onKickHandler;


    public PomeloClient(URI serverURI) {

        super(serverURI);
        eventHandlerMap.put("fhbk_event", message -> {
            LOGGER.debug("{}", message.getBodyJson());
            //    flag = true;
        });

        trustAllHosts(this);
    }


    // always verify the host - dont check for certificate
    final static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };


    /**
     * Trust every server - dont check for any certificate
     */
    private void trustAllHosts(WebSocketClient appClient) {
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new java.security.cert.X509Certificate[]{};
            }


            @Override
            public void checkClientTrusted(X509Certificate[] chain,
                                           String authType) throws CertificateException {
            }


            @Override
            public void checkServerTrusted(X509Certificate[] chain,
                                           String authType) throws CertificateException {
            }
        }};


        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            appClient.setWebSocketFactory(new DefaultSSLWebSocketClientFactory(sc));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }


    public PomeloClient(URI serverUri, Draft draft) {
        super(serverUri, draft);
    }

    public PomeloClient(URI serverUri, Draft draft, Map<String, String> headers, int connecttimeout) {
        super(serverUri, draft, headers, connecttimeout);
    }

    @Override
    public void connect() {
        super.connect();
    }

    @Override
    public void close() {
        isConnected = false;
        super.close();
    }

    public boolean isConnected() {
        return isConnected;
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        LOGGER.debug("{}", "opened connection");
        if (protoBuf == null) {
            protoBuf = new ProtoBuf();
        }
        try {
            JSONObject jsonObject = HandshakeProvider.handshakeObject();
            byte[] strencode = PomeloPackage.strencode(jsonObject.toString());
            byte[] encode = PomeloPackage.encode(PomeloPackage.TYPE_HANDSHAKE, strencode);
            send(encode);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }

    }

    @Override
    public void onMessage(String message) {
        LOGGER.debug("{}", "received: " + message);
    }

    @Override
    public void onMessage(ByteBuffer buffer) {
        LOGGER.debug("{}", "received buffer: " + buffer);
        byte[] array = buffer.array();
        PomeloPackage.Package decode = PomeloPackage.decode(array);
        LOGGER.debug("{}", "received decode package: " + decode);
        int type = decode.getType();
        switch (type) {
            case PomeloPackage.TYPE_HANDSHAKE: {
                try {
                    handshake(decode);
                } catch (JSONException e) {
                    LOGGER.error(e.getMessage(), e);
                }
                break;
            }
            case PomeloPackage.TYPE_HEARTBEAT: {
                heartbeat(decode);
                break;
            }
            case PomeloPackage.TYPE_DATA: {
                try {
                    onData(decode);
                } catch (PomeloException e) {
                    LOGGER.error(e.getMessage(), e);
                } catch (JSONException e) {
                    LOGGER.error(e.getMessage(), e);
                }
                break;
            }
            case PomeloPackage.TYPE_KICK: {
                onKick(decode);
                break;
            }
        }

        // new package arrived, update the heartbeat timeout
        if (heartbeatTimeout > 0) {
            nextHeartbeatTimeout = new Date().getTime() + heartbeatTimeout;
        }
    }

    public void request(String route, String msg, OnDataHandler onDataHandler) {
        reqIdIndex++;
        try {
            sendMessage(reqIdIndex, route, msg);
            routeMap.put(reqIdIndex, route);
            onDataHandlerMap.put(reqIdIndex, onDataHandler);
        } catch (PomeloException e) {
            LOGGER.error(e.getMessage(), e);
        } catch (JSONException e) {
            LOGGER.error(e.getMessage(), e);
        }

    }

    public void sendMessage(int reqId, String route, String msg) throws PomeloException, JSONException {
        byte[] bytes = defaultEncode(reqId, route, msg);
        byte[] encode = PomeloPackage.encode(PomeloPackage.TYPE_DATA, bytes);
        send(encode);
    }

    private void heartbeat(PomeloPackage.Package decode) {
        if (heartbeatInterval == 0) {
            //no heartbeat
            return;
        }

        long now = new Date().getTime();
        if (nextHeartbeatTimeout > now) {
            return;
        }
        byte[] encode = PomeloPackage.encode(PomeloPackage.TYPE_HEARTBEAT, null);
        send(encode); //临时注掉
        nextHeartbeatTimeout = now + heartbeatTimeout;
    }

    public void sendHeartBeat() {
        LOGGER.debug("{}", "keepalive");
        byte[] encode = PomeloPackage.encode(PomeloPackage.TYPE_HEARTBEAT, null);
        send(encode); //临时注掉
    }

    private void onData(PomeloPackage.Package decode) throws PomeloException, JSONException {
        PomeloMessage.Message message = defaultDecode(decode.getBody());
        int id = message.getId();
        OnDataHandler onDataHandler = onDataHandlerMap.get(id);
        if (onDataHandler != null) {
            onDataHandler.onData(message);
        } else {
            onDataHandler = eventHandlerMap.get(message.getRoute());
            if (onDataHandler != null) {
                onDataHandler.onData(message);
            } else {
                LOGGER.debug("{}", "can not find onDataHander for msg:" + message.getBodyJson());
            }

        }
        //onDataHandlerMap.remove(id);
        //routeMap.remove(id);
        //System.out.println("onData msg :" + message.toString());
    }

    private PomeloMessage.Message defaultDecode(byte[] buffer) throws PomeloException, JSONException {
        PomeloMessage.Message msg = PomeloMessage.decode(buffer);
        if (msg.getId() > 0) {
            int id = msg.getId();
            if (routeMap != null && routeMap.containsKey(id)) {
                msg.setRoute(routeMap.get(id));
                if (msg.getRoute() == null) {
                    throw new PomeloException("msg route can not be null");
                }
            }
        }
        JSONObject jsonObject = deCompose(msg);
        msg.setBodyJson(jsonObject);
        return msg;
    }

    private byte[] defaultEncode(int reqId, String route, String msg) throws PomeloException, JSONException {
        int type = reqId > 0 ? PomeloMessage.TYPE_REQUEST : PomeloMessage.TYPE_NOTIFY;
        byte[] encode = null;
        //compress message by protobuf
        if (clientProtos != null && clientProtos.has(route)) {
            encode = protoBuf.encode(route, msg);
        } else {
            encode = PomeloPackage.strencode(msg);
        }
        int compressRoute = 0;
        if (dict != null && dict.has(route)) {
            route = dict.get(route).toString();
            compressRoute = 1;
        }
        return PomeloMessage.encode(reqId, type, compressRoute, route, encode);
    }

    private JSONObject deCompose(PomeloMessage.Message msg) throws PomeloException, JSONException {
        String route = msg.getRoute();
        int compressRoute = msg.getCompressRoute();
        if (compressRoute > 0) {
            if (abbrs.isNull(route)) {
                return new JSONObject();
            }
            boolean hasRoute = abbrs.has(route);
            route = hasRoute ? abbrs.getString(route) : null;
            msg.setRoute(route);
        }
        if (serverProtos != null && serverProtos.has(route)) {
            String decode = protoBuf.decode(route, msg.getBody());
            return new JSONObject(decode);
        } else {
            String strdecode = PomeloPackage.strdecode(msg.getBody());
            return new JSONObject(strdecode);
        }
    }

    private void onKick(PomeloPackage.Package decode) {
        LOGGER.debug("{}", "on kick");
        if (onKickHandler != null) {
            onKickHandler.onKick();
        }
    }

    private void handshake(PomeloPackage.Package decode) throws JSONException {
        String resStr = PomeloPackage.strdecode(decode.getBody());
        LOGGER.debug("{}", "handshake resStr: " + resStr);
        JSONObject data = new JSONObject(resStr);
        if (data.isNull(HandshakeProvider.RES_CODE_KEY)) {
            LOGGER.debug("{}", "handshake res data error!");
            return;
        }
        int code = data.getInt(HandshakeProvider.RES_CODE_KEY);
        if (HandshakeProvider.RES_OLD_CLIENT == code) {
            LOGGER.debug("{}", "old handshake version!");
            return;
        }
        if (HandshakeProvider.RES_FAIL == code) {
            LOGGER.debug("{}", "handshake fail!");
            return;
        }
        handshakeInit(data);
        //send ack msg
        byte[] ackBytes = PomeloPackage.encode(PomeloPackage.TYPE_HANDSHAKE_ACK, null);
        send(ackBytes);
        isConnected = true;
        if (onHandshakeSuccessHandler != null) {
            onHandshakeSuccessHandler.onSuccess(this, data);
        }
    }

    private void handshakeInit(JSONObject data) throws JSONException {
        if (!data.isNull(HandshakeProvider.HANDSHAKE_SYS_KEY)) {
            JSONObject sys = data.getJSONObject(HandshakeProvider.HANDSHAKE_SYS_KEY);
            if (!sys.isNull(HandshakeProvider.HANDSHAKE_SYS_HEARTBEAT_KEY)) {
                long heartbeat = sys.getLong(HandshakeProvider.HANDSHAKE_SYS_HEARTBEAT_KEY);
                heartbeatInterval = heartbeat * 1000;   // heartbeat interval
                heartbeatTimeout = heartbeatInterval * 2;        // max heartbeat timeout
            } else {
                heartbeatInterval = 0;
                heartbeatTimeout = 0;
            }
        } else {
            heartbeatInterval = 0;
            heartbeatTimeout = 0;
        }
        initData(data);
    }

    private void initData(JSONObject data) throws JSONException {
        if (data == null || data.isNull(HandshakeProvider.HANDSHAKE_SYS_KEY)) {
            LOGGER.debug("{}", "data format error!");
            return;
        }
        JSONObject sys = data.getJSONObject(HandshakeProvider.HANDSHAKE_SYS_KEY);
        if (!sys.isNull(HandshakeProvider.HANDSHAKE_SYS_DICT_KEY)) {
            dict = sys.getJSONObject(HandshakeProvider.HANDSHAKE_SYS_DICT_KEY);
            LOGGER.debug("{}", "sys.dict:" + dict.toString());
            abbrs = new JSONObject();
            Iterator<String> routes = dict.keys();
            while (routes.hasNext()) {
                String route = routes.next();
                String key = dict.get(route).toString();
                abbrs.put(key, route);
            }
        }
        if (!sys.isNull(HandshakeProvider.HANDSHAKE_SYS_PROTOS_KEY)) {
            protos = sys.getJSONObject(HandshakeProvider.HANDSHAKE_SYS_PROTOS_KEY);
            protosVersion = protos.has(HandshakeProvider.HANDSHAKE_SYS_PROTOS_VERSION_KEY) ? protos.getInt(HandshakeProvider.HANDSHAKE_SYS_PROTOS_VERSION_KEY) : 0;
            serverProtos = protos.has(HandshakeProvider.HANDSHAKE_SYS_PROTOS_SERVER_KEY) ? protos.getJSONObject(HandshakeProvider.HANDSHAKE_SYS_PROTOS_SERVER_KEY) : null;
            clientProtos = protos.has(HandshakeProvider.HANDSHAKE_SYS_PROTOS_CLIENT_KEY) ? protos.getJSONObject(HandshakeProvider.HANDSHAKE_SYS_PROTOS_CLIENT_KEY) : null;
            LOGGER.debug("{}", "sys.protos.version:" + protosVersion);
            LOGGER.debug("{}", "sys.protos.server:" + serverProtos.toString());
            LOGGER.debug("{}", "sys.protos.client:" + clientProtos.toString());
            if (protoBuf != null) {
                protoBuf.initProtos(clientProtos, serverProtos);
            }
        }


    }

    @Override
    public void onClose(int code, String msg, boolean remote) {
        LOGGER.debug("{}", "Connection closed by " + (remote ? "remote peer" : "us"));
        if (onCloseHandler != null) {
            onCloseHandler.onClose(code, msg, remote);
        }
    }

    @Override
    public void onError(Exception e) {
        if (onErrorHandler != null) {
            onErrorHandler.onError(e);
        }
    }

    public OnErrorHandler getOnErrorHandler() {
        return onErrorHandler;
    }

    public void setOnErrorHandler(OnErrorHandler onErrorHandler) {
        this.onErrorHandler = onErrorHandler;
    }

    public OnHandshakeSuccessHandler getOnHandshakeSuccessHandler() {
        return onHandshakeSuccessHandler;
    }

    public void setOnHandshakeSuccessHandler(OnHandshakeSuccessHandler onHandshakeSuccessHandler) {
        this.onHandshakeSuccessHandler = onHandshakeSuccessHandler;
    }

    public OnCloseHandler getOnCloseHandler() {
        return onCloseHandler;
    }

    public void setOnCloseHandler(OnCloseHandler onCloseHandler) {
        this.onCloseHandler = onCloseHandler;
    }

    public OnKickHandler getOnKickHandler() {
        return onKickHandler;
    }

    public void setOnKickHandler(OnKickHandler onKickHandler) {
        this.onKickHandler = onKickHandler;
    }
}
