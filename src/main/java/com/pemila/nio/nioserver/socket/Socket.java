package com.pemila.nio.nioserver.socket;

import com.pemila.nio.nioserver.message.IMessageReader;
import com.pemila.nio.nioserver.message.MessageWriter;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * @author 月在未央
 * @date 2019/5/20 15:11
 */
public class Socket {

    public long socketId;

    public IMessageReader messageReader = null;
    public MessageWriter messageWriter = null;

    public SocketChannel socketChannel = null;

    public boolean endOfStreamReached = false;

    public Socket(){}

    /** 构造方法*/
    public Socket(SocketChannel socketChannel){
        this.socketChannel = socketChannel;
    }

    /** 读取channel中的数据写入到Buffer中，返回读取到的字节数*/
    public int read(ByteBuffer byteBuffer) throws IOException {
        int bytesRead = this.socketChannel.read(byteBuffer);
        int totalBytesRead = bytesRead;
        while (bytesRead>0){
            bytesRead = this.socketChannel.read(byteBuffer);
            totalBytesRead += bytesRead;
        }
        if(bytesRead == -1){
            // -1表示已读取到数据末尾
            this.endOfStreamReached = true;
        }
        return totalBytesRead;
    }

    /** 将ByteBuffer中的数据写入到channel中，返回写入的字节数*/
    public int write(ByteBuffer byteBuffer) throws IOException {
        int bytesWritten = this.socketChannel.write(byteBuffer);
        int totalBytesWritten = bytesWritten;
        while (bytesWritten>0&&byteBuffer.hasRemaining()){
            // 已写数据大于0 且 buffer中还有数据，则继续向channel写入
            bytesWritten = this.socketChannel.write(byteBuffer);
            totalBytesWritten += bytesWritten;
        }
        return totalBytesWritten;
    }


}
