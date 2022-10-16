package com.csp.nio;


import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * @author csp
 * @date 2022/10/11
 */
public class FileWalkTreeApi {

    public static void main(String[] args) throws IOException {
        // 遍历目录下的所有目录及文件
        Files.walkFileTree(Paths.get(""), new SimpleFileVisitor<Path>() {

            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                System.out.println("---->" + dir);
                return super.preVisitDirectory(dir, attrs);
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                System.out.println("---->" + file);
                return super.visitFile(file, attrs);
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                System.out.println("---->" + file);
                return super.visitFileFailed(file, exc);
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                System.out.println("---->" + dir);
                return super.postVisitDirectory(dir, exc);
            }
        });
    }
}
