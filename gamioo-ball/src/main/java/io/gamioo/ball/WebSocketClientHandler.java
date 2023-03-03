package io.gamioo.ball;

import io.netty.channel.*;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.CharsetUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Date;

/**
 * @author Allen Jiang
 */
@ChannelHandler.Sharable
public class WebSocketClientHandler extends SimpleChannelInboundHandler<Object> {
    private static final Logger logger = LogManager.getLogger(WebSocketClientHandler.class);
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
        logger.info("连接断开 id={},userId={},lastSendTime={},lastRecvTime={}", this.webSocketClient.getId(), this.webSocketClient.getUserId(), this.webSocketClient.getLastSendTime(), this.webSocketClient.getLastRecvTime());
        this.webSocketClient.increaseError();
    }


    @Override
    public void channelRead0(ChannelHandlerContext ctx, Object msg) {
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
                logger.debug("client connected userId={}", this.webSocketClient.getUserId());
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
        this.webSocketClient.setLastRecvTime(new Date());
        // Check for closing frame
        if (frame instanceof CloseWebSocketFrame) {
            logger.debug("WebSocket Client received closing userId={}", this.webSocketClient.getUserId());
            CloseWebSocketFrame close = (CloseWebSocketFrame) frame.retain();
            handshaker.close(ctx.channel(), close);
            return;
        }
        if (frame instanceof PingWebSocketFrame) {
            logger.debug("WebSocket Client received ping");
            frame.content().retain();
            //   ctx.write(new PingWebSocketFrame(frame.content().retain()));
            return;
        }
        if (frame instanceof PongWebSocketFrame) {
            logger.debug("WebSocket Client received pong userId={}", this.webSocketClient.getUserId());
            frame.content().retain();
            //  ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
            return;
        }
        if (frame instanceof TextWebSocketFrame) {
            // Echo the frame
            String request = ((TextWebSocketFrame) frame).text();
            logger.debug("recv content={}", request);
            frame.retain();
            //  ctx.writeAndFlush(frame.retain());
            return;
        }
        if (frame instanceof BinaryWebSocketFrame) {
            // Echo the frame

            // String request = ((BinaryWebSocketFrame) frame).toString();
            // System.out.println(request);
            BinaryWebSocketFrame content = (BinaryWebSocketFrame) frame;

            frame.retain();
            //  ctx.write(frame.retain());
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