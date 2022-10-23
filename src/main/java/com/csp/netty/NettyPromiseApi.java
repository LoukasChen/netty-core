package com.csp.netty;

import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.DefaultPromise;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @author csp
 * @date 2022/10/23
 */
@Slf4j
public class NettyPromiseApi {

    public static void main(String[] args) throws Exception {
        NioEventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        DefaultPromise<Integer> promise = new DefaultPromise<>(eventLoopGroup.next());

        new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            promise.setSuccess(1);
//            promise.setFailure(new RuntimeException("throw exception"));
        }).start();
        log.info("{}", promise.get());
    }
}
