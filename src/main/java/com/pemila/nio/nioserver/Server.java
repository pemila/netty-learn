package com.pemila.nio.nioserver;

import com.pemila.nio.nioserver.message.IMessageProcessor;
import com.pemila.nio.nioserver.message.IMessageReaderFactory;
import com.pemila.nio.nioserver.message.MessageBuffer;
import com.pemila.nio.nioserver.socket.Socket;
import com.pemila.nio.nioserver.socket.SocketAccepter;
import com.pemila.nio.nioserver.socket.SocketProcessor;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * @author 月在未央
 * @date 2019/5/20 15:03
 */
public class Server {

    private SocketAccepter socketAccepter = null;
    private SocketProcessor socketProcessor = null;

    private int tcpPort = 0;
    private IMessageReaderFactory messageReaderFactory = null;
    private IMessageProcessor     messageProcessor = null;

    public Server(int tcpPort, IMessageReaderFactory messageReaderFactory, IMessageProcessor messageProcessor) {
        this.tcpPort = tcpPort;
        this.messageReaderFactory = messageReaderFactory;
        this.messageProcessor = messageProcessor;
    }

    public void start() throws IOException {
        // 创建队列用于存放socket
        Queue<Socket> socketQueue = new ArrayBlockingQueue<>(1024);
        // 创建接收线程
        this.socketAccepter = new SocketAccepter(tcpPort,socketQueue);
        Thread accepterThread = new Thread(this.socketAccepter);
        accepterThread.start();

        // 创建处理线程
        MessageBuffer readBuffer = new MessageBuffer();
        MessageBuffer writeBuffer = new MessageBuffer();
        this.socketProcessor = new SocketProcessor(socketQueue,readBuffer,writeBuffer,this.messageReaderFactory,this.messageProcessor);
        Thread processorThread = new Thread(this.socketProcessor);
        processorThread.start();
    }
}
