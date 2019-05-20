package com.pemila.nio.nioserver.message;

import com.pemila.nio.nioserver.socket.Socket;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

/**
 * @author 月在未央
 * @date 2019/5/20 17:32
 */
public interface IMessageReader {

    void init(MessageBuffer readMessageBuffer);

    void read(Socket socket, ByteBuffer byteBuffer) throws IOException;

    List<Message> getMessages();
}
