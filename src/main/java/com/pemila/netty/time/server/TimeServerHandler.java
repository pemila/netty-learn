package com.pemila.netty.time.server;

import com.pemila.netty.time.UnixTime;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @author 月在未央
 * @date 2019/5/21 16:08
 */
public class TimeServerHandler extends ChannelInboundHandlerAdapter {

//    /** 建立连接并准备传输数据时会首先调用channelActive方法*/
//    @Override
//    public void channelActive(final ChannelHandlerContext ctx) {
//        //分配一个新的ByteBuf用于存储时间信息 32bit整数需要至少4bytes的空间
//        final ByteBuf time = ctx.alloc().buffer(4);
//        time.writeInt((int) (System.currentTimeMillis()/1000L+2208988800L));
//
//        // 将创建的消息写入到响应中，注意没有调用java.nio.ByteBuffer.flip方法。
//        // 这是因为ByteBuf使用不同的指针(readIndex和writeIndex)分别对读写位置进行标识。
//        // 而NIO Buffer中只使用position表示当前位置，切换读写模式时需要调用flip方法对position进行切换
//        final ChannelFuture channelFuture = ctx.writeAndFlush(time);
//
//        // 添加一个channelFutureListener,用于监听writeAndFlush操作完成后关闭ctx
//        channelFuture.addListener((ChannelFutureListener) future -> {
//            assert channelFuture == future;
//            ctx.close();
//        });
//
//        // channelFuture.addListener(ChannelFutureListener.CLOSE);
//    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // 要发送的消息写入ctx
        ChannelFuture future = ctx.writeAndFlush(new UnixTime());
        // 监听写入操作直到完成
        future.addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
