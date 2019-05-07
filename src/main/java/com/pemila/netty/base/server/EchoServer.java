package com.pemila.netty.base.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * 服务器监听连接
 * @author 月在未央
 * @date 2019/5/7 10:57
 */
public class EchoServer {

    private final int port;

    public EchoServer(int port){
        this.port = port;
    }

    public void start() throws InterruptedException {
        // 用于接收连接
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        // 用于处理已接受的连接
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
                //绑定线程池
            serverBootstrap.group(bossGroup,workerGroup)
                    //指定使用的channel
                    .channel(NioServerSocketChannel.class)
                    //绑定监听端口
                    .localAddress(this.port)
                    //绑定客户端连接时触发的操作
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) {
                            System.out.println("connect...; Client:"+socketChannel.remoteAddress());
                            //客户端触发操作
                            socketChannel.pipeline().addLast(new EchoServerHandler());
                        }
                    });
            //服务器异步创建绑定
            ChannelFuture channelFuture = serverBootstrap.bind().sync();
            System.out.println(EchoServer.class+" start and listen on "+channelFuture.channel().localAddress());
            //关闭服务器通道
            channelFuture.channel().closeFuture().sync();
        } finally {
            //释放线程池资源
            bossGroup.shutdownGracefully().sync();
            workerGroup.shutdownGracefully().sync();
        }
    }


    public static void main(String[] args) throws InterruptedException {
        new EchoServer(61111).start();
    }


}
