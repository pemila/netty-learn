package com.pemila.netty.base.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

/**
 * 处理客户端连接
 * @author 月在未央
 * @date 2019/5/7 11:13
 */
public class EchoServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg){
        ByteBuf in = (ByteBuf) msg;
        System.out.println("server channelRead....; received: "+in.toString(CharsetUtil.UTF_8));
        ctx.write(msg);
    }


    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        System.out.println("server channelRead complete...");
        //写入空buffer,并刷新写入区域，完成后关闭 sock channel 连接
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        System.out.println("server occur exception: "+ cause.getMessage());
        cause.printStackTrace();
        //关闭发生异常的连接
        ctx.close();
    }
}
