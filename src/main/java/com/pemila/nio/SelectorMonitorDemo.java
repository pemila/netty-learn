package com.pemila.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

/**
 * 创建ServerSocket
 * 获取ServerSocketChannel
 * 注册通道到Selector上并监听事件
 * @author 月在未央
 * @date 2019/5/16 18:17
 */
public class SelectorMonitorDemo {

    public static void main(String[] args) throws IOException {
        // 创建Selector
        Selector selector = Selector.open();
        // 打开一个ServerSocketChannel通道
        ServerSocketChannel channel = ServerSocketChannel.open();
        // 为通道设定监听地址
        channel.bind(new InetSocketAddress("127.0.0.1",30000));
        // 设置Channel为非阻塞
        channel.configureBlocking(false);
        // 将channel注册到Selector上并监听ACCPET事件
        SelectionKey key = channel.register(selector,SelectionKey.OP_ACCEPT,"ServerSocket-test");
        while (true){
            // 获取当前就绪的通道数
            int readChannels = selector.select();
            if(readChannels==0){
                continue;
            }
            // 获取已就绪通道集合
            Set selectedKeys = selector.selectedKeys();
            Iterator keyIterator = selectedKeys.iterator();
            while (keyIterator.hasNext()){
                //已就绪通道处理
                SelectionKey selectionKey = (SelectionKey) keyIterator.next();
                if(selectionKey.isAcceptable()) {
                    // a connection was accepted by a ServerSocketChannel.
                    System.out.println(selectionKey.attachment()+" accepted!");
                } else if (selectionKey.isConnectable()) {
                    // a connection was established with a remote server.
                } else if (selectionKey.isReadable()) {
                    // a channel is ready for reading
                } else if (selectionKey.isWritable()) {
                    // a channel is ready for writing
                }
                // 处理结束从集合中移除
                keyIterator.remove();
            }
        }
    }
}
