package com.pemila.nio.nioserver.socket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Queue;

/**
 * 连接接收线程
 * @author 月在未央
 * @date 2019/5/20 15:25
 */
public class SocketAccepter implements Runnable{

    private int tcpPort = 0;
    private ServerSocketChannel serverSocket = null;

    private Queue<Socket> socketQueue = null;

    public SocketAccepter(int tcpPort,Queue<Socket> socketQueue){
        this.tcpPort = tcpPort;
        this.socketQueue = socketQueue;
    }

    @Override
    public void run() {
        try {
            // 创建ServerSocketChannel并指定监听端口
            this.serverSocket = ServerSocketChannel.open();
            this.serverSocket.bind(new InetSocketAddress(tcpPort));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        // 轮询获取ScoketChannel并加入到Socket队列
        while (true){
            try {
                // 从ServerSocketChannel获取SocketChannel
                SocketChannel socketChannel = this.serverSocket.accept();
                System.out.println("Socket accepted: "+socketChannel);
                // 将获得的SocketChannel加入到Socket队列中
                this.socketQueue.add(new Socket(socketChannel));
            } catch (IOException e) {
                e.printStackTrace();
            }

        }


    }
}
