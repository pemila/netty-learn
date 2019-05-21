package com.pemila.netty.time;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.Objects;

/**
 * @author 月在未央
 * @date 2019/5/21 18:12
 */
public class TimeClient {
    private int port;
    private String host;

    public TimeClient(String host,int port){
        this.port = port;
        this.host = Objects.isNull(host)?"127.0.0.1":host;
    }

    public void request() throws InterruptedException {
        EventLoopGroup loopGroup = new NioEventLoopGroup();

        try {
            // 客户端使用Bootstrap而不是ServerBootstrap
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(loopGroup)
                    // 客户端指定NioSocketChannel而不是ServerSocketChannel
                    .channel(NioSocketChannel.class)
                    // 客户端只需要指定option即可（客户端channel无parent channel）
                    .option(ChannelOption.SO_KEEPALIVE,true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new TimeClientHandler());
                        }
                    });

            // 客户端必须使用 connect方法 而不是 bind方法
            ChannelFuture channelFuture = bootstrap.connect(host,port).sync();
            channelFuture.channel().closeFuture().sync();
        }finally {
            loopGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        new TimeClient(null,8080).request();
    }
}
