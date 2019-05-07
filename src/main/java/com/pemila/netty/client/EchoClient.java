package com.pemila.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;

/**
 * 建立连接的客户端
 * @author 月在未央
 * @date 2019/5/7 11:34
 */
public class EchoClient {
    private final String host;
    private final int port;

    public EchoClient(){
        this(0);
    }
    public EchoClient(int port){
        this("localhost",port);
    }
    public EchoClient(String host, int port) {
        this.host = host;
        this.port = port;
    }


    public void start() throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup();
        try{
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .remoteAddress(new InetSocketAddress(this.host,this.port))
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            System.out.println("已连接....");
                            socketChannel.pipeline().addLast(new EchoClientHandler());
                        }
                    });
            System.out.println("客户端启动器已创建....");

            //异步连接服务器
            ChannelFuture future = bootstrap.connect().sync();
            System.out.println("异步连接已完成....");

            // 异步等等待连接关闭
            future.channel().closeFuture().sync();
            System.out.println("异步等待关闭连接已完成....");

        }finally {
            // 释放线程池资源
            group.shutdownGracefully().sync();
        }
    }


    public static void main(String[] args) throws InterruptedException {
        new EchoClient("127.0.0.1",61111).start();
    }

}
