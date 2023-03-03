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

package io.gamioo.ball;

import io.gamioo.ball.entity.Target;
import io.gamioo.core.util.StringUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpScheme;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.net.ssl.SSLException;
import java.net.URI;
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
    private Date lastSendTime;
    private Date lastRecvTime;
    private final Target target;
    private boolean legal;
    private int error;
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
                    .newHandshaker(uri, WebSocketVersion.V13, null, true, new DefaultHttpHeaders()));
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
                    p.addLast(new HttpObjectAggregator(65536));
                    p.addLast("handle", handler);
                }
            });
            ChannelFuture channelFuture = bootstrap.connect(target.getIp(), target.getPort());
            channelFuture.addListener((ChannelFutureListener) channelFuture1 -> {
                if (!channelFuture1.isSuccess()) {
                    logger.error("连接失败 target={}", target);
                    logger.error("连接失败", channelFuture1.cause());
                    target.increaseError();
                } else {
                    //     logger.info("连接成功 id={},proxy={},target={}", id, proxy,target);
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


    public void sendMessage(Channel channel) {
        Date now = new Date();
        if (isConnected()) {
            if (channel.isWritable()) {

                WebSocketFrame frame = new TextWebSocketFrame("1");
                channel.writeAndFlush(frame);

//                WebSocketFrame frame = new PingWebSocketFrame();
                lastSendTime = now;
            }
        }
    }

    public int getId() {
        return id;
    }

    public Date getLastSendTime() {
        return lastSendTime;
    }


    public Date getLastRecvTime() {
        return lastRecvTime;
    }

    public void setLastRecvTime(Date lastRecvTime) {
        this.lastRecvTime = lastRecvTime;
    }

    public long getUserId() {
        return 0;
    }

    public void increaseError() {
        this.error++;
    }

}
