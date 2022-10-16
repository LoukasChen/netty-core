package com.csp.nio;

import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @author csp
 * @date 2022/10/9
 */
@Slf4j
public class ByteBufferApi {

    public static void main(String[] args) {
        channel();
        stickyPackage();
    }

    public static void channel() {
        try (FileChannel channel = new FileInputStream("file.txt").getChannel()) {
            // 申请10个字节的缓冲区
            ByteBuffer buffer = ByteBuffer.allocate(10);
            while (true) {
                // 从channel读，向buffer写
                int read = channel.read(buffer);
                log.info("current read bytes is {}", read);
                if (read == -1) {
                    break;
                }
                // 切换为读模式
                buffer.flip();
                // 是否有剩余未读数据
                while (buffer.hasRemaining()) {
                    byte b = buffer.get();
                    log.info("current read char is {}", (char) b);
                }
                // 切换为写模式
                buffer.clear();
            }
        } catch (IOException e) {
        }
    }

    /**
     * 网络发送数据包的粘包和半包问题解决
     * 粘包：多条消息合并成了一条
     * 半包：一条消息被拆分成了多条
     */
    public static void stickyPackage() {
        ByteBuffer buffer = ByteBuffer.allocate(50);
        buffer.put("today is sunday\n i am work busy\n what".getBytes());
        split(buffer);
        buffer.put("about you\n".getBytes());
        split(buffer);
    }

    private static void split(ByteBuffer buffer) {
        buffer.flip();
        for (int i = 0; i < buffer.limit(); i++) {
            if (buffer.get(i) == '\n') {
                int len = i - buffer.position() + 1;
                ByteBuffer target = ByteBuffer.allocate(len);
                for (int j = 0; j < len; j++) {
                    target.put(buffer.get());
                }
            }
        }
        buffer.compact();
    }
}
