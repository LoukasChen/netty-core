package com.csp.nio;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
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
            // select方法，没有事件发生，线程阻塞，有事件发生，线程恢复运行
            // select在事件未处理时，不会阻塞，事件发生后要么处理，要么取消，不能不处理
            selector.select();
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                // 处理key时，需要将当前的key移除
                iterator.remove();
                log.info("key:{}", key);
                if (key.isAcceptable()) {
                    ServerSocketChannel channel = (ServerSocketChannel) key.channel();
                    SocketChannel accept = channel.accept();
                    accept.configureBlocking(false);
                    SelectionKey scKey = accept.register(selector, 0, null);
                    scKey.interestOps(SelectionKey.OP_READ);
                } else if (key.isReadable()) {
                    try {
                        // TLV Type Length Value
                        SocketChannel channel = (SocketChannel) key.channel();
                        ByteBuffer buffer = ByteBuffer.allocate(4);
                        int read = channel.read(buffer);
                        if (read == -1) {
                            key.cancel();
                        } else {
                            buffer.flip();
                            log.info("{}", Charset.defaultCharset().decode(buffer));
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        key.cancel();
                    }
                }
                // 事件取消
//                key.cancel();
            }
        }
    }
}
