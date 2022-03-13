/*
 * Copyright 2015-2020 Gamioo Authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.gamioo.lele;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.google.protobuf.UnknownFieldSet;
import io.gamioo.core.util.StringUtils;

import io.gamioo.lele.entity.Target;
import io.gamioo.lele.pomelo.*;
import io.gamioo.lele.protocol.LoginDTO;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpScheme;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.net.ssl.SSLException;
import java.net.URI;
import java.text.MessageFormat;
import java.util.Date;

/**
 * websocket 客户端连接器
 *
 * @author Allen Jiang
 * @since 1.0.0
 */
public class WebSocketClient {
    private static final Logger logger = LogManager.getLogger(WebSocketClient.class);
    private final int id;
    private int reqIdIndex;
    private final Target target;
    private static Bootstrap bootstrap = new Bootstrap();
    private Channel socketChannel;
    private static NioEventLoopGroup group = new NioEventLoopGroup(8);

    static {
        bootstrap.group(group);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.option(ChannelOption.TCP_NODELAY, true);
        bootstrap.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
    }

    public WebSocketClient(int id, Target target) {
        this.id = id;
        this.target = target;
    }

    public void connect() {
        URI uri = target.getUri();
        try {
            final WebSocketClientHandler handler = new WebSocketClientHandler(this, WebSocketClientHandshakerFactory
                    .newHandshaker(uri, WebSocketVersion.V13, null, false, new DefaultHttpHeaders()));
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws SSLException {
                    ChannelPipeline p = ch.pipeline();
                    if (!StringUtils.equals(target.getScheme(), HttpScheme.HTTP.name())) {
                        SslContext context = SslContextBuilder.forClient()
                                .trustManager(InsecureTrustManagerFactory.INSTANCE).build();
                        p.addLast(context.newHandler(ch.alloc(), target.getIp(), target.getPort()));
                    }
                    p.addLast(new HttpClientCodec());
                    p.addLast(new HttpObjectAggregator(10 * 1024 * 1024));
                    p.addLast("frameDecoder", new ProtobufVarint32FrameDecoder());
                    p.addLast("protobufDecoder", new ProtobufDecoder(UnknownFieldSet.getDefaultInstance()));
                    p.addLast("frameEncoder", new ProtobufVarint32LengthFieldPrepender());
                    p.addLast("protobufEncoder", new ProtobufEncoder());
                    p.addLast("handle", handler);
                }
            });
            ChannelFuture channelFuture = bootstrap.connect(target.getIp(), target.getPort());
            channelFuture.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    if (!channelFuture.isSuccess()) {
                        logger.error("连接失败", channelFuture.cause());
                        target.increaseError();
                    } else {
                        logger.info("连接成功 id={},,target={}", id, target);
                    }
                }
            });
            socketChannel = channelFuture.sync().channel();

            if (handler.handshakeFuture() != null) {
                handler.handshakeFuture().addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture channelFuture) throws Exception {

                        if (!channelFuture.isSuccess()) {
                            logger.error("握手失败 target={}", target);
                            logger.error("握手失败", channelFuture.cause());
                            target.increaseError();
                        } else {
                            logger.info("握手成功 id={},target.ip={},target.port={}", id, target.getIp(), target.getPort());
                          sendHandShake();
                          sendMessage(socketChannel);
                        }
                    }
                });

                handler.handshakeFuture().sync();
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }


    public boolean isConnected() {
        if (socketChannel != null) {
            return socketChannel.isActive();
        }
        return false;
    }

    public void disconnect() {
        if (isConnected()) {
            logger.debug("主动断开，id={}", id);
            socketChannel.disconnect();
        }

    }


    //    public void login(Channel channel) {
//        try {
//            Message.ClientRequest_LoginArgs.Builder builder = Message.ClientRequest_LoginArgs.newBuilder();
//            builder.setUserID(user.getId());
//            if (this.user.getArea() == 1) {
//                builder.setToken(user.getToken());
//            } else {
//                builder.setToken(user.getTokenB());
//            }
//            byte[] content = builder.build().toByteArray();
//            ByteBuf raw = Unpooled.wrappedBuffer(content);
//            BinaryWebSocketFrame frame = new BinaryWebSocketFrame(raw);
//            channel.writeAndFlush(frame);
//        } catch (Exception e) {
//            logger.error(e.getMessage(), e);
//        }
//    }
//
    public void sendMessage(Channel channel) {
        Date now = new Date();
        if (isConnected()) {
            if (channel.isWritable()) {
                this.login();
            }
        }
    }

    public void sendOther() {
        WebSocketFrame frame = new TextWebSocketFrame("game_ping");

        socketChannel.writeAndFlush(frame);
        logger.debug("send game_ping");
    }

    public void sendMessage() {
        PingWebSocketFrame frame = new PingWebSocketFrame();
        socketChannel.writeAndFlush(frame);
        logger.debug("send ping");
    }

    public void sendHandShake() {
        try {
            JSONObject jsonObject = HandshakeProvider.handshakeObject();
            byte[] strencode = PomeloPackage.strencode(jsonObject.toString());
            byte[] encode = PomeloPackage.encode(PomeloPackage.TYPE_HANDSHAKE, strencode);
            this.sendData(encode);
            //  send(encode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendData(byte[] array) {
        ByteBuf byteBuf = Unpooled.buffer(array.length);
        byteBuf.writeBytes(array);
        BinaryWebSocketFrame frame = new BinaryWebSocketFrame(byteBuf);
        socketChannel.writeAndFlush(frame);
    }

    public void login() {
        JSONObject entity = new JSONObject();
        entity.put("_id", "oR9cD1lM5cuXYI-P6nhEE0rFLz40");
        entity.put("name", "回眸昨日");
        entity.put("headurl", "https://thirdwx.qlogo.cn/mmopen/vi_32/Q0j4TwGTfTKVxmKqbePL0dxibYWoo2YGTT6dNQF4Yr1gq7uthk46t3iaCvXP3aIa4kpG2XCtpQDt1ibM44PxQ9uag/132");
        entity.put("agentId","369641");
        entity.put("formId",3);
        entity.put("verison","12112");
        entity.put("ip","110.123.12.234");
        entity.put("dis",1);
        entity.put("smslogon",1);
        entity.put("protocol","https");
        entity.put("hostname","lele8k.game1617.com");
        entity.put("connectIP","lele8k.game1617.com");
        entity.put("wsId",2);

        this.request("connector.entryHandler.login", entity.toJSONString(), new OnDataHandler() {

            @Override
            public void onData(PomeloMessage.Message message) {
                logger.debug("message：{}", message);
            }
        });


//        //42["login",{"token":"930e424586d3670123cfe191b744d4d0","roomid":"616000","time":1638552497052,"sign":"9f7911eab9dd32309a5b3faef9a59d20"}]
//        LoginDTO dto = new LoginDTO();
//        dto.setSign("3276efcce6937dd08177e28dff9e0ae8");
//        dto.setRoomId("638077");
//        dto.setTime(1638592090672L);
//        dto.setToken("365f801a0c9f4fa05a4b9cdd9fd7ff76");
//        String content = MessageFormat.format("41[\"login\",{0}]", JSON.toJSONString(dto));
//        logger.debug("send {}", content);
//        WebSocketFrame frame = new TextWebSocketFrame(content);
//        socketChannel.writeAndFlush(frame);
    }

    public void request(String route, String msg, OnDataHandler onDataHandler) {
        reqIdIndex++;
        sendMessage(reqIdIndex, route, msg);
//        routeMap.put(reqIdIndex, route);
//        onDataHandlerMap.put(reqIdIndex, onDataHandler);
    }

    public void sendMessage(int reqId, String route, String msg) {
        try {
            byte[] bytes = defaultEncode(reqId, route, msg);
            byte[] encode = PomeloPackage.encode(PomeloPackage.TYPE_DATA, bytes);
            this.sendData(encode);
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
        }

    }

    private byte[] defaultEncode(int reqId, String route, String msg) throws PomeloException, JSONException {
        int type = reqId > 0 ? PomeloMessage.TYPE_REQUEST : PomeloMessage.TYPE_NOTIFY;
        byte[] encode = PomeloPackage.strencode(msg);
        int compressRoute = 0;
//        if (dict != null && dict.has(route)) {
//            route = dict.get(route).toString();
//            compressRoute = 1;
//        }
        return PomeloMessage.encode(reqId, type, compressRoute, route, encode);
    }


//    public User getUser() {
//        return user;
//    }
//
//    public void setUser(User user) {
//        this.user = user;
//    }
//
//    public int getId() {
//        return id;
//    }
//
//    public Date getLastSendTime() {
//        return lastSendTime;
//    }
//
//
//    public Date getLastRecvTime() {
//        return lastRecvTime;
//    }
//
//    public void setLastRecvTime(Date lastRecvTime) {
//        this.lastRecvTime = lastRecvTime;
//    }
//
//    public long getUserId() {
//        return user.getId();
//    }
//
//    public void setLogin(boolean login) {
//        this.login = login;
//    }
//
//    public boolean isLegal() {
//        return legal;
//    }
//
//    public void setLegal(boolean legal) {
//        this.legal = legal;
//    }
//
//    public int getError() {
//        return error;
//    }
//
//    public void increaseError() {
//        this.error++;
//    }
//
//    public void setOnline(boolean online) {
//        this.online = online;
//    }
}
