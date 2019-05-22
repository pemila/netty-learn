package com.pemila.netty.time.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

import java.util.List;

/**
 * == TimeDecoder
 * 继承ReplayingDecoder
 * @author 月在未央
 * @date 2019/5/22 10:54
 */
public class TimeDecoderReplay extends ReplayingDecoder<Void> {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        out.add(in.readBytes(4));
    }
}
