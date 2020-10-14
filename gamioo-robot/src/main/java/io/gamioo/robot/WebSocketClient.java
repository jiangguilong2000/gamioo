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

package io.gamioo.robot;

import com.google.protobuf.UnknownFieldSet;
import io.gamioo.core.concurrent.GameThreadFactory;
import io.gamioo.core.util.StringUtils;
import io.gamioo.core.util.TelnetUtils;
import io.gamioo.robot.entity.Message;
import io.gamioo.robot.entity.Proxy;
import io.gamioo.robot.entity.Target;
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
import io.netty.handler.proxy.Socks5ProxyHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.net.ssl.SSLException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.URI;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.*;

/**
 * websocket 客户端连接器
 *
 * @author Allen Jiang
 * @since 1.0.0
 */
public class WebSocketClient {
    private static final Logger logger = LogManager.getLogger(WebSocketClient.class);
    private final int id;
    private final Proxy proxy;
    private final Target target;
    private final long userId;
    private Date lastSendTime;
    private Date lastRecvTime;
    private boolean login;


    private static Bootstrap bootstrap = new Bootstrap();
    private Channel socketChannel;
    private static NioEventLoopGroup group = new NioEventLoopGroup(8);
    private static Map<Integer, ScheduledFuture<?>> store = new ConcurrentHashMap<>();
    private static ScheduledExecutorService pool = Executors.newScheduledThreadPool(8, new GameThreadFactory("robot"));


    static {
        bootstrap.group(group);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.option(ChannelOption.TCP_NODELAY, true);
        bootstrap.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);


    }

    public WebSocketClient(int id,long userId, Proxy proxy, Target target) {
        this.id = id;
        this.userId=userId;
        this.proxy = proxy;
        this.target = target;
    }

    public void connect() {
        URI uri = target.getUri();
        try {
            final WebSocketClientHandler handler = new WebSocketClientHandler(this, WebSocketClientHandshakerFactory
                    .newHandshaker(uri, WebSocketVersion.V13, null, true, new DefaultHttpHeaders()));
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws SSLException {
                    ChannelPipeline p = ch.pipeline();
                    //代理存在就用代理，代理不存在就直连
                    if (proxy != null) {
                        SocketAddress address = new InetSocketAddress(proxy.getIp(), proxy.getPort());
                        Socks5ProxyHandler socks5ProxyHandler = new Socks5ProxyHandler(address);
                        socks5ProxyHandler.setConnectTimeoutMillis(0);
                        p.addFirst("proxy", socks5ProxyHandler);
                    }
                    if (!StringUtils.equals(target.getScheme(), HttpScheme.HTTP.name())) {
                        SslContext context = SslContextBuilder.forClient()
                                .trustManager(InsecureTrustManagerFactory.INSTANCE).build();
                        p.addLast(context.newHandler(ch.alloc(), target.getIp(), target.getPort()));
                    }
                    p.addLast(new HttpClientCodec());
                    p.addLast(new HttpObjectAggregator(8192));
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
                        logger.error("连接失败 id={},proxy={},target={}", id, proxy, target);
                        logger.error("连接失败", channelFuture.cause());
                    } else {
                        //     logger.info("连接成功 id={},proxy={},target={}", id, proxy,target);
                    }
                }
            });
            socketChannel = channelFuture.sync().channel();


            handler.handshakeFuture().addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {

                    if (!channelFuture.isSuccess()) {
                        logger.error("握手失败 id={},proxy={},target={}", id, proxy, target);
                        logger.error("握手失败", channelFuture.cause());
                    } else {
                        logger.info("握手成功 id={},proxy={},target.ip={},target.port={}", id, proxy, target.getIp(), target.getPort());
                        ScheduledFuture<?> future = pool.scheduleWithFixedDelay(() -> {
                            try {
                                sendMessage(socketChannel);
                            } catch (Exception e) {
                                logger.error("exception id={},proxy={}", getId(), proxy);
                                logger.error(e.getMessage(), e);
                            }
                            //	logger.debug("send id={}", this.id);
                        }, 2000, 50000, TimeUnit.MILLISECONDS);
                        store.put(id, future);
                    }
                }
            });


            //       handler.handshakeFuture().sync();


        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }


    public static int getConnectNum() {
        return store.size();
    }

    public boolean isConnected() {
        if (socketChannel != null) {
            return socketChannel.isActive();
        }
        return false;
    }

    public void disconnect() {
        if (isConnected()) {
            socketChannel.disconnect();
        }

    }


    public void login(Channel channel) {
            try {
                Message.ClientRequest_LoginArgs.Builder builder = Message.ClientRequest_LoginArgs.newBuilder();
                //最大值300000000,最小值999999
               // builder.setUserID(target.getId() * 200290 + id-3);
               //  userId=target.getId()*100000+id;
                builder.setUserID(userId);
                builder.setToken("529382015132319205"+userId);
                byte[] content = builder.build().toByteArray();
                ByteBuf raw = Unpooled.wrappedBuffer(content);
                BinaryWebSocketFrame frame = new BinaryWebSocketFrame(raw);
                channel.writeAndFlush(frame);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }


//		//keep.setTimestamp(System.currentTimeMillis());
    }

    public void sendMessage(Channel channel) {
        Date now = new Date();
        if (isConnected()) {
            if (channel.isWritable()) {
                //    logger.debug("send content={}",content);
                logger.debug("send ping id={}", id);
                if (this.target.isText()) {
                    WebSocketFrame frame = new TextWebSocketFrame("1");
                    channel.writeAndFlush(frame);
                }
                if (!login) {
                    this.login(channel);
                    login = true;
                }else{
                    WebSocketFrame frame = new PingWebSocketFrame();
                    channel.writeAndFlush(frame);
                }
                lastSendTime = now;
            }
        } else {
            ScheduledFuture<?> future = store.remove(this.id);
            if (future != null) {
                if (!future.isCancelled()) {
                    future.cancel(false);
                }
            }

            //能通信再连
            if (this.proxy == null||(this.proxy != null && now.before(this.proxy.getExpireTime()))) {
                if (TelnetUtils.isConnected(this.target.getIp(), this.target.getPort())) {
                    this.connect();
                }
            }

        }
    }

    public int getId() {
        return id;
    }

    public Proxy getProxy() {
        return proxy;
    }

    public Target getTarget() {
        return target;
    }

    public Date getLastSendTime() {
        return lastSendTime;
    }

    public void setLastSendTime(Date lastSendTime) {
        this.lastSendTime = lastSendTime;
    }

    public Date getLastRecvTime() {
        return lastRecvTime;
    }

    public void setLastRecvTime(Date lastRecvTime) {
        this.lastRecvTime = lastRecvTime;
    }

    public long getUserId() {
        return userId;
    }

}
