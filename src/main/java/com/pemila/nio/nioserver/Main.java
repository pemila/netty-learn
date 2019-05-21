package com.pemila.nio.nioserver;

import com.pemila.nio.nioserver.message.IMessageProcessor;
import com.pemila.nio.nioserver.message.Message;
import com.pemila.nio.nioserver.message.http.HttpMessageReaderFactory;
import com.pemila.nio.nioserver.other.Server;

import java.io.IOException;

/**
 * @author 月在未央
 * @date 2019/5/20 14:54
 */
public class Main {
    public static void main(String[] args) throws IOException {
        // 响应结果模拟
        String httpResponse = "HTTP/1.1 200 OK\r\n" +
                "Content-Length: 38\r\n" +
                "Content-Type: text/html\r\n" +
                "\r\n" +
                "<html><body>Hello World!</body></html>";

        byte[] httpResponseBytes = httpResponse.getBytes("UTF-8");

        // 消息处理器
        IMessageProcessor messageProcessor = (requestMsg,writeProxy)->{
            System.out.println("Message Received from socket: "+ requestMsg.socketId);

            Message responseMsg = writeProxy.getMessage();

            responseMsg.socketId = requestMsg.socketId;
            responseMsg.writeToMessage(httpResponseBytes);
            writeProxy.enqueue(responseMsg);
        };

        // 创建服务器
        Server server = new Server(9999,new HttpMessageReaderFactory(),messageProcessor);
        // 启动服务器
        server.start();
    }
}
