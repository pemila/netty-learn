package com.pemila.netty.time.codec;

import com.pemila.netty.time.UnixTime;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author 月在未央
 * @date 2019/5/22 11:39
 */
//public class TimeEncoder extends ChannelOutboundHandlerAdapter {
//
//    @Override
//    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
//        UnixTime time = (UnixTime) msg;
//        ByteBuf encoded = ctx.alloc().buffer(4);
//        encoded.writeInt((int) time.value());
//        ctx.write(encoded,promise);
//    }
//}

public class TimeEncoder extends MessageToByteEncoder<UnixTime>{

    @Override
    protected void encode(ChannelHandlerContext ctx, UnixTime msg, ByteBuf out) throws Exception {
        out.writeInt((int) msg.value());
    }
}