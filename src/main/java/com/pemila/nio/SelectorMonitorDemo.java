package com.pemila.nio;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * 注册时间到Selector上并监听事件
 * @author 月在未央
 * @date 2019/5/16 18:17
 */
public class SelectorMonitorDemo {

    public static void main(String[] args) throws IOException {
        // 创建Selector
        Selector selector = Selector.open();
        // 创建一个Channel
        ServerSocketChannel channel = ServerSocketChannel.open();
        // 设置Channel为非阻塞
        channel.configureBlocking(false);
        // 将channel注册到Selector上并监听ACCPET事件
        SelectionKey key = channel.register(selector,SelectionKey.OP_ACCEPT);
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
                if(key.isAcceptable()) {
                    // a connection was accepted by a ServerSocketChannel.
                } else if (key.isConnectable()) {
                    // a connection was established with a remote server.
                } else if (key.isReadable()) {
                    // a channel is ready for reading
                } else if (key.isWritable()) {
                    // a channel is ready for writing
                }
                // 处理结束从集合中移除
                keyIterator.remove();
            }
        }
    }
}
