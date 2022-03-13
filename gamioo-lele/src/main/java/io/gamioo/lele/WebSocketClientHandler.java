package io.gamioo.lele;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import io.gamioo.core.util.StringUtils;
import io.gamioo.lele.pomelo.HandshakeProvider;
import io.gamioo.lele.pomelo.PomeloPackage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.CharsetUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Allen Jiang
 */
@ChannelHandler.Sharable
public class WebSocketClientHandler extends SimpleChannelInboundHandler<Object> {
    private static final Logger logger = LogManager.getLogger(WebSocketClientHandler.class);
    //public static StringBuilder content=new StringBuilder();
    //public static String CONTENT = "hello soybean";

    public static String CONTENT = "{'cmd':'login','data':{'ServerID':'57173','RoomId':'243158'}}".replaceAll("'",
            "\"");
    private final WebSocketClientHandshaker handshaker;
    private ChannelPromise handshakeFuture;
    private WebSocketClient webSocketClient;


    public WebSocketClientHandler(WebSocketClient webSocketClient, WebSocketClientHandshaker handshaker) {
        this.webSocketClient = webSocketClient;
        this.handshaker = handshaker;
    }

    public ChannelFuture handshakeFuture() {
        return handshakeFuture;
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        // logger.debug("handlerAdded");
        handshakeFuture = ctx.newPromise();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        // logger.debug("channelActive {}",this.id);
        handshaker.handshake(ctx.channel());


    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        logger.info("连接断开 ");
        //    logger.info("连接断开 id={},userId={},lastSendTime={},lastRecvTime={}", this.webSocketClient.getId(), this.webSocketClient.getUserId(), this.webSocketClient.getLastSendTime(), this.webSocketClient.getLastRecvTime());
//        this.webSocketClient.increaseError();
//        this.webSocketClient.setLogin(false);
    }


    @Override
    public void channelRead0(ChannelHandlerContext ctx, Object msg) {
        //  logger.debug("recv id={},msg={}", this.webSocketClient.getId(),msg);
        if (msg instanceof FullHttpResponse) {
            handleHttpRequest(ctx, (FullHttpResponse) msg);
        } else if (msg instanceof WebSocketFrame) {
            handleWebSocketFrame(ctx, (WebSocketFrame) msg);
        }
    }

    /**
     * 第一次握手请求协议处理
     */
    private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpResponse response) {
        Channel ch = ctx.channel();
        if (!handshaker.isHandshakeComplete()) {
            try {
                handshaker.finishHandshake(ch, response);
                logger.debug("client connected userId={}");
                handshakeFuture.setSuccess();


            } catch (WebSocketHandshakeException e) {
                logger.error(e.getMessage(), e);
                handshakeFuture.setFailure(e);
            }
            return;
        }
        throw new IllegalStateException("Unexpected FullHttpResponse (getStatus=" + response.status()
                + ", content=" + response.content().toString(CharsetUtil.UTF_8) + ')');

    }

//    @Override
//    public void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
//        //	logger.debug("channelRead0 id={}",this.id);
//        Channel ch = ctx.channel();
//
//
//        WebSocketFrame frame = (WebSocketFrame) msg;
//        if (frame instanceof TextWebSocketFrame) {
//            TextWebSocketFrame textFrame = (TextWebSocketFrame) frame;
//            ch.writeAndFlush(textFrame.text());
//
//            logger.debug("WebSocket Client received message:{} ", textFrame.text());
//        } else if (frame instanceof PongWebSocketFrame) {
//            logger.debug("WebSocket Client received pong");
//        } else if (frame instanceof CloseWebSocketFrame) {
//            logger.debug("WebSocket Client received closing");
//            CloseWebSocketFrame close = (CloseWebSocketFrame) frame.retain();
//            handshaker.close(ctx.channel(), close);
//            return;
//        }
//    }

    private void handleWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {
        //logger.debug("recv content={}", frame);
        // this.webSocketClient.setLastRecvTime(new Date());
        // Check for closing frame
        if (frame instanceof CloseWebSocketFrame) {
            logger.debug("WebSocket Client received closing userId={}");
            CloseWebSocketFrame close = (CloseWebSocketFrame) frame.retain();
            handshaker.close(ctx.channel(), close);
            return;
        }
        if (frame instanceof PingWebSocketFrame) {
            logger.debug("WebSocket Client received ping");
            frame.content().retain();
            // ctx.write(new PingWebSocketFrame(frame.content().retain()));
            return;
        }
        if (frame instanceof PongWebSocketFrame) {
            logger.debug("WebSocket Client received pong");
            //   logger.debug("WebSocket Client received pong userId={}", this.webSocketClient.getUserId());
            frame.content().retain();
            //  ctx.channel().write(new PingWebSocketFrame(frame.content().retain()));
            // PingWebSocketFrame frame=new PingWebSocketFrame();
            //   ctx.channel().writeAndFlush(new PingWebSocketFrame());
            return;
        }
        if (frame instanceof TextWebSocketFrame) {
            // Echo the frame
            String request = ((TextWebSocketFrame) frame).text();
            logger.debug("recv content={}", request);
            frame.retain();
            if (StringUtils.equals(request, "40")) {
                webSocketClient.sendOther();
                //  webSocketClient.login();
            }
            //  ctx.writeAndFlush(frame.retain());
            return;
        }
        if (frame instanceof BinaryWebSocketFrame) {
            // Echo the frame

            ByteBuf msg = frame.content().retain();
            onMessage(msg);

            //  frame.retain();
            // ctx.write(frame.retain());
            return;
        }

    }


    public void onMessage(ByteBuf buffer) {
        logger.debug("received buffer:{} ", buffer);
        byte[] array = new byte[buffer.readableBytes()];
        buffer.readBytes(array);
        PomeloPackage.Package decode = PomeloPackage.decode(array);
        logger.debug("received decode package:{} ", decode);
        int type = decode.getType();
        switch (type) {
            case PomeloPackage.TYPE_HANDSHAKE: {
                try {
                    handshake(decode);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            }
            case PomeloPackage.TYPE_HEARTBEAT: {
                heartbeat(decode);
                break;
            }
            case PomeloPackage.TYPE_DATA: {
                try {

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            }
            case PomeloPackage.TYPE_KICK: {
                //    onKick(decode);
                break;
            }
        }

        // new package arrived, update the heartbeat timeout
        if (heartbeatTimeout > 0) {
            nextHeartbeatTimeout = new Date().getTime() + heartbeatTimeout;
        }
    }


    private Map<Integer, String> routeMap = new HashMap<Integer, String>();
    private long heartbeatInterval = 0;
    private long heartbeatTimeout = 0;


    private long nextHeartbeatTimeout = 0;


    private void heartbeat(PomeloPackage.Package decode) {
        if (heartbeatInterval == 0) {
            //no heartbeat
            return;
        }
        String resStr = PomeloPackage.strdecode(decode.getBody());
        logger.debug("heartbeat resStr:{} ", resStr);
        byte[] encode = PomeloPackage.encode(PomeloPackage.TYPE_HEARTBEAT, null);
        //  this.webSocketClient.sendData(encode);
        nextHeartbeatTimeout = new Date().getTime() + heartbeatTimeout;
    }

    private void handshake(PomeloPackage.Package decode) throws JSONException {
        String resStr = PomeloPackage.strdecode(decode.getBody());
        logger.debug("handshake resStr:{} ", resStr);


        JSONObject data = JSON.parseObject(resStr);
        if (!data.containsKey(HandshakeProvider.RES_CODE_KEY)) {
            System.out.println("handshake res data error!");
            return;
        }
        int code = data.getIntValue(HandshakeProvider.RES_CODE_KEY);
        if (HandshakeProvider.RES_OLD_CLIENT == code) {
            System.out.println("old handshake version!");
            return;
        }
        if (HandshakeProvider.RES_FAIL == code) {
            System.out.println("handshake fail!");
            return;
        }
        handshakeInit(data);
        //send ack msg
        byte[] ackBytes = PomeloPackage.encode(PomeloPackage.TYPE_HANDSHAKE_ACK, null);
        this.webSocketClient.sendData(ackBytes);

//        isConnected = true;
//        if (onHandshakeSuccessHandler != null) {
//            onHandshakeSuccessHandler.onSuccess(this, data);
//        }
    }

    private void handshakeInit(JSONObject data) throws JSONException {
        if (data.containsKey(HandshakeProvider.HANDSHAKE_SYS_KEY)) {
            JSONObject sys = data.getJSONObject(HandshakeProvider.HANDSHAKE_SYS_KEY);
            if (sys.containsKey(HandshakeProvider.HANDSHAKE_SYS_HEARTBEAT_KEY)) {
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

    private JSONObject protos;

    private JSONObject dict;

    private JSONObject abbrs;

    private int protosVersion;
    private JSONObject clientProtos;

    private JSONObject serverProtos;


    private void initData(JSONObject data) throws JSONException {
        if (data == null || !data.containsKey(HandshakeProvider.HANDSHAKE_SYS_KEY)) {
            System.out.println("data format error!");
            return;
        }
        JSONObject sys = data.getJSONObject(HandshakeProvider.HANDSHAKE_SYS_KEY);
        if (sys.containsKey(HandshakeProvider.HANDSHAKE_SYS_DICT_KEY)) {
            dict = sys.getJSONObject(HandshakeProvider.HANDSHAKE_SYS_DICT_KEY);
            System.out.println("sys.dict:" + dict.toString());
            abbrs = new JSONObject();

            dict.forEach((key, value) -> {
                abbrs.put(key, value);
            });
        }
        if (sys.containsKey(HandshakeProvider.HANDSHAKE_SYS_PROTOS_KEY)) {
            protos = sys.getJSONObject(HandshakeProvider.HANDSHAKE_SYS_PROTOS_KEY);
            protosVersion = protos.containsKey(HandshakeProvider.HANDSHAKE_SYS_PROTOS_VERSION_KEY) ? protos.getIntValue(HandshakeProvider.HANDSHAKE_SYS_PROTOS_VERSION_KEY) : 0;
            serverProtos = protos.containsKey(HandshakeProvider.HANDSHAKE_SYS_PROTOS_SERVER_KEY) ? protos.getJSONObject(HandshakeProvider.HANDSHAKE_SYS_PROTOS_SERVER_KEY) : null;
            clientProtos = protos.containsKey(HandshakeProvider.HANDSHAKE_SYS_PROTOS_CLIENT_KEY) ? protos.getJSONObject(HandshakeProvider.HANDSHAKE_SYS_PROTOS_CLIENT_KEY) : null;
            System.out.println("sys.protos.version:" + protosVersion);
            System.out.println("sys.protos.server:" + serverProtos.toString());
            System.out.println("sys.protos.client:" + clientProtos.toString());
//            if (protoBuf != null) {
//                protoBuf.initProtos(clientProtos, serverProtos);
//            }
        }


    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("exceptionCaught,cause={}", cause.getMessage());
        if (!handshakeFuture.isDone()) {
            handshakeFuture.setFailure(cause);
        }
        ctx.close();
    }

//    private void response(ChannelHandlerContext ctx, final WebSocketFrame msg) {
//        // 获取客户端传输过来的消息
//        String content = msg.toString();
//        clients.writeAndFlush(new TextWebSocketFrame("[服务器收到相应]" + LocalDateTime.now() + "接受萨达到消息, 消息为：" + content));
//
//        final Channel inboundChannel = ctx.channel();
//
//        Bootstrap b = new Bootstrap();
//        b.group(inboundChannel.eventLoop()).channel(ctx.channel().getClass())
//                .handler(new SocketHandlerInitializer(inboundChannel));
//
//        ChannelFuture f = b.connect("127.0.0.1", 5688);
//        outboundChannel = f.channel();
//        msg.retain();
//
//        ChannelFuture channelFuture = f.addListener(new ChannelFutureListener() {
//            public void operationComplete(ChannelFuture future) throws Exception {
//                if (future.isSuccess()) {
//                    System.out.println("isSuccess:true");
//                    outboundChannel.writeAndFlush("2222222222");
//                } else {
//                    System.out.println("isSuccess：false");
//                    inboundChannel.close();
//                }
//            }
//        });
//    }

}