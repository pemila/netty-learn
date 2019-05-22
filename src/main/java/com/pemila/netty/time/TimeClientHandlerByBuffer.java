package com.pemila.netty.time;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.Date;

/**
 * 当前类 == TimeClientHandler
 * 使用流传输数据时数据被缓存在socket 缓存区，该缓存区是一个字节队列而不是数据包队列。
 * 并发请求时，多个数据包将混合在缓存区中，数据接收端可能无法识别数据包的边界。
 * 因此使用内部缓存区解决可能出现的客户端与服务端数据不一致问题。
 * @author 月在未央
 * @date 2019/5/22 9:54
 */
public class TimeClientHandlerByBuffer extends ChannelInboundHandlerAdapter {
    private ByteBuf buf;

    @Override
    public void handlerAdded(ChannelHandlerContext ctx){
        // channelHandle生命周期开始时
        // 创建一个容量为4的内部缓存区
        buf = ctx.alloc().buffer(4);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx){
        // channelHandle生命周期结束时
        // 释放掉创建的缓存区
        buf.release();
        buf = null;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx,Object msg){
        ByteBuf m = (ByteBuf) msg;
        // 接收到的数据写入内部缓存区
        buf.writeBytes(m);
        m.release(); // 释放参数的缓存区
        // 只有当可读字节数 >=4 时，读取数据，然后关闭，否则等待继续传输
        // 4字节是基于TIME协议的返回值为32bit整数
        if(buf.readableBytes()>= 4 ){
            long currentTimeMillis = ( buf.readUnsignedInt() - 2208988800L) * 1000L;
            System.out.println(new Date(currentTimeMillis));
            ctx.close();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx,Throwable cause){
        cause.printStackTrace();
        ctx.close();
    }
}