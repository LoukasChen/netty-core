package com.csp.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import lombok.extern.slf4j.Slf4j;

/**
 * @author csp
 * @date 2022/10/16
 */
@Slf4j
public class NettyClient {

    public static void main(String[] args) throws Exception {
        ChannelFuture channelFuture = new Bootstrap()
                .group(new NioEventLoopGroup())
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new StringEncoder());
                    }
                })
                .connect("localhost", 8080);

        // 方案一：同步等待连接
//        channelFuture = channelFuture.sync();
//        Channel channel = channelFuture.channel();
//        log.info("channel:{}", channel);
//        channel.writeAndFlush("test netty connected");

        // 方案二：异步回调
        channelFuture.addListener((ChannelFutureListener) future -> {
            Channel channel = channelFuture.channel();
            log.info("channel:{}", channel);
            channel.writeAndFlush("test netty connected");
        });
    }

}
