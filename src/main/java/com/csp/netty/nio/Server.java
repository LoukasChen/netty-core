package com.csp.netty.nio;

import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author csp
 * @date 2022/10/11
 */
@Slf4j
public class Server {

    public static void main(String[] args) throws Exception {
//        unblock();
        selector();
    }

    /**
     * 阻塞
     *
     * @throws Exception
     */
    public static void block() throws Exception {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(8080));

        List<SocketChannel> socketChannelList = new ArrayList<>();
        ByteBuffer buffer = ByteBuffer.allocate(16);
        while (true) {
            log.info("socket connecting...");
            // accept 默认阻塞线程，等待客户端连接
            SocketChannel accept = serverSocketChannel.accept();
            log.info("socket connected... {}", accept);
            socketChannelList.add(accept);
            for (SocketChannel socketChannel : socketChannelList) {
                log.info("before read...");
                // read 默认阻塞线程，等待客户端发送数据
                socketChannel.read(buffer);
                buffer.flip();
                while (buffer.hasRemaining()) {
                    char c = (char) buffer.get();
                    log.info(String.valueOf(c));
                }
                buffer.clear();
                log.info("after read...");
            }
        }
    }

    /**
     * 非阻塞
     *
     * @throws Exception
     */
    public static void unblock() throws Exception {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        // 设置非阻塞模式
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.bind(new InetSocketAddress(8080));

        List<SocketChannel> socketChannelList = new ArrayList<>();
        ByteBuffer buffer = ByteBuffer.allocate(16);
        while (true) {
//            log.info("socket connecting...");
            // accept 默认阻塞线程，等待客户端连接
            SocketChannel accept = serverSocketChannel.accept();
            if (accept != null) {
                log.info("socket connected... {}", accept);
                // 设置非阻塞模式
                accept.configureBlocking(false);
                socketChannelList.add(accept);
            }
            for (SocketChannel socketChannel : socketChannelList) {
                // read 默认阻塞线程，等待客户端发送数据
                int read = socketChannel.read(buffer);
                if (read > 0) {
                    buffer.flip();
                    while (buffer.hasRemaining()) {
                        char c = (char) buffer.get();
                        log.info(String.valueOf(c));
                    }
                    buffer.clear();
                    log.info("after read...");
                }
            }
        }
    }

    public static void selector() throws Exception {
        // 创建selector，管理多个channel
        Selector selector = Selector.open();
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        // 设置非阻塞模式
        serverSocketChannel.configureBlocking(false);
        // 建立selector和channel的联系
        // SelectionKey就是事件发生后，知道是什么事件，以及是哪个channel发生的事件
        SelectionKey selectionKey = serverSocketChannel.register(selector, 0, null);
        // 只关注accept事件
        selectionKey.interestOps(SelectionKey.OP_ACCEPT);
        serverSocketChannel.bind(new InetSocketAddress(8080));
        while (true) {
            selector.select();
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                log.info("key:{}", key);
                ServerSocketChannel channel = (ServerSocketChannel) key.channel();
                SocketChannel accept = channel.accept();
                log.info("{}", accept);
            }
        }
    }
}
