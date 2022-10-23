package com.csp.netty;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

/**
 * @author csp
 * @date 2022/10/17
 */
public class EventLoopApi {

    public static void main(String[] args) {
        // 处理 io事件、普通任务、定时任务
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        // 处理 普通任务、定时任务
//        EventLoopGroup eventLoopGroup = new DefaultEventLoopGroup();
    }
}
