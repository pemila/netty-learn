package com.pemila.nio.nioserver.message.http;

import com.pemila.nio.nioserver.message.IMessageReader;
import com.pemila.nio.nioserver.message.Message;
import com.pemila.nio.nioserver.message.MessageBuffer;
import com.pemila.nio.nioserver.socket.Socket;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 月在未央
 * @date 2019/5/20 18:13
 */
public class HttpMessageReader implements IMessageReader {

    private MessageBuffer messageBuffer = null;

    private List<Message> completeMessages = new ArrayList<>();
    private Message nextMessage = null;

    @Override
    public void init(MessageBuffer readMessageBuffer) {
        this.messageBuffer = readMessageBuffer;
        this.nextMessage = messageBuffer.getMessage();
        this.nextMessage.metaData = new HttpHeaders();
    }

    @Override
    public void read(Socket socket, ByteBuffer byteBuffer) throws IOException {
        int bytesRead = socket.read(byteBuffer);
        byteBuffer.flip();

        if(byteBuffer.remaining() == 0){
            byteBuffer.clear();
            return;
        }

        this.nextMessage.writeToMessage(byteBuffer);

        int endIndex = HttpUtil.parseHttpRequest(this.nextMessage.sharedArray, this.nextMessage.offset, this.nextMessage.offset + this.nextMessage.length, (HttpHeaders) this.nextMessage.metaData);
        if(endIndex != -1){
            Message message = this.messageBuffer.getMessage();
            message.metaData = new HttpHeaders();

            message.writePartialMessageToMessage(nextMessage, endIndex);

            completeMessages.add(nextMessage);
            nextMessage = message;
        }
        byteBuffer.clear();

    }

    @Override
    public List<Message> getMessages() {
        return this.completeMessages;
    }
}
