package com.csp.netty;

import io.netty.channel.EventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.Future;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @author csp
 * @date 2022/10/23
 */
@Slf4j
public class NettyFutureApi {

    public static void main(String[] args) throws Exception {
        NioEventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        EventLoop eventLoop = eventLoopGroup.next();
        Future<Integer> future = eventLoop.submit(() -> {
            TimeUnit.SECONDS.sleep(1);
            return 1;
        });
        log.info("now:{}", future.getNow());
        log.info("await:{}", future.await());
        log.info("sync:{}", future.sync());

        future.addListener(f -> log.info("callback getNow:{}", f.getNow()));

        eventLoopGroup.shutdownGracefully();
    }

}
