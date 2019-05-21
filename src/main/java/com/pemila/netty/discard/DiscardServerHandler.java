package com.pemila.netty.discard;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;

import java.nio.charset.Charset;

/**
 * 服务消息处理器
 * @author 月在未央
 * @date 2019/5/21 13:46
 */
public class DiscardServerHandler extends ChannelInboundHandlerAdapter {
    // 继承 ChannelInboundHandlerAdapter
    // ChannelInboundHandlerAdapter 是 ChannelInboundHandler的实现
    // ChannelInboundHandler提供了可以覆盖的各种事件处理程序方法

    /**  重写channelRead的方法，用于处理收到的数据*/
//    @Override
//    public void channelRead(ChannelHandlerContext ctx,Object msg){
//        try{
//            /*
//             ByteBuf in = (ByteBuf) msg;
//            // 输出参数
//            System.out.print(in.toString(CharsetUtil.US_ASCII));
//            // 或者使用while循环
//            while (in.isReadable()){
//                System.out.print((char) in.readByte());
//                System.out.flush();
//            }
//            */
//            // discard服务器，不做任何数据处理
//
//        }finally {
//            // 传入的参数 Object msg是一个ReferenceCounted对象
//            // 必须明确的将其释放掉
//            ReferenceCountUtil.release(msg);
//        }
//    }

    /*
        Echo Server 使用如下channelRead  */
        @Override
        public void channelRead(ChannelHandlerContext ctx,Object msg){
            ctx.writeAndFlush(msg);
        }


    /** 重写exceptionCaught方法，进行异常处理*/
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // 发生异常时做异常处理关闭连接
        // to do something
        cause.printStackTrace();
        ctx.close();
    }
}
