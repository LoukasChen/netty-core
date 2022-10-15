package com.csp.netty.nio;

import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

/**
 * @author csp
 * @date 2022/10/11
 */
public class Client {

    public static void main(String[] args) throws Exception {
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.connect(new InetSocketAddress(8080));
        System.out.println("client");
    }
}
