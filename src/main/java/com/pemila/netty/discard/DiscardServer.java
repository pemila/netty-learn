package com.pemila.netty.discard;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @author 月在未央
 * @date 2019/5/21 14:16
 */
public class DiscardServer {
    private int port;

    public DiscardServer(int port){
        this.port = port;
    }

    public void run() throws InterruptedException {
        // 创建bossGroup 用来接收请求
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        // 创建workerGroup 用于处理接收到的请求
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            // 创建ServerBootstrap，用于配置服务器信息
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup,workerGroup)
                    // 指定channel类型为 NioServerSocketChannel
                    .channel(NioServerSocketChannel.class)
                    // 初始话channel并向其所属的pipeline添加一个新的数据处理组件
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new DiscardServerHandler());
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG,128)
                    .childOption(ChannelOption.SO_KEEPALIVE,true);

            // 绑定端口并开始接收连接
            ChannelFuture future = bootstrap.bind(port).sync();

            //等待，直到服务器socket关闭
            // 或者使用closeFuture
            future.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        // 创建discard服务器并启动
        new DiscardServer(8080).run();
    }
}
