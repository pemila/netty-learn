package com.pemila.netty.time.codec;

import com.pemila.netty.time.UnixTime;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * 数据解码器，用于解决数据碎片化问题
 * @author 月在未央
 * @date 2019/5/22 10:19
 */
public class TimeDecoder extends ByteToMessageDecoder {
    // ByteToMessageDecoder是用来解决数据碎片化问题的ChannelHandler实现之一。
    // 一旦有新的数据到达，Decoder将使用内部维护的缓存区调用decode()方法

//    @Override
//    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
//        if(in.readableBytes() < 4){
//            // 内部缓存区可读数据不足时，继续等待
//            return;
//        }
//        // 解析成功后将数据添加到out
//        out.add(in.readBytes(4));
//    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        if(in.readableBytes() < 4){
            // 内部缓存区可读数据不足时，继续等待
            return;
        }
        // 解析成功后将数据转为对象添加到out
        out.add(new UnixTime(in.readUnsignedInt()));
    }
}
