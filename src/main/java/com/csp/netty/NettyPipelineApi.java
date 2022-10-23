package com.csp.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * @author csp
 * @date 2022/10/23
 */
@Slf4j
public class NettyPipelineApi {

    public static void main(String[] args) {
        new ServerBootstrap()
                .group(new NioEventLoopGroup())
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {

                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        // pipeline 入站 p1 -> p2 出站 p4 -> p3
                        ch.pipeline()
                                .addLast("p1", new ChannelInboundHandlerAdapter() {
                                    @Override
                                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                        log.info("p1");
                                        super.channelRead(ctx, msg);
                                    }
                                })
                                .addLast("p2", new ChannelInboundHandlerAdapter() {
                                    @Override
                                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                        log.info("p2");
                                        super.channelRead(ctx, msg);
                                        ch.writeAndFlush(ctx.alloc().buffer().writeBytes("next".getBytes()));
                                    }
                                })
                                .addLast("p3", new ChannelOutboundHandlerAdapter() {
                                    @Override
                                    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                                        log.info("p3");
                                        super.write(ctx, msg, promise);
                                    }
                                })
                                .addLast("p4", new ChannelOutboundHandlerAdapter() {
                                    @Override
                                    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                                        log.info("p4");
                                        super.write(ctx, msg, promise);
                                    }
                                })
                        ;
                    }
                }).bind(8080);
    }

}
