package com.pemila.nio.nioserver.message;

import com.pemila.nio.nioserver.socket.Socket;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 月在未央
 * @date 2019/5/20 17:35
 */
public class MessageWriter {

    private List<Message> writeQueue = new ArrayList<>();

    private Message messageInProcess = null;

    private int bytesWritten = 0;

    public MessageWriter(){}

    public void enqueue(Message message){
        if(this.messageInProcess == null){
            this.messageInProcess = message;
        }else{
            this.writeQueue.add(message);
        }
    }

    public void write(Socket socket, ByteBuffer byteBuffer) throws IOException {
        byteBuffer.put(this.messageInProcess.sharedArray,this.messageInProcess.offset+this.bytesWritten,this.messageInProcess.length-this.bytesWritten);
        byteBuffer.flip();

        this.bytesWritten +=socket.write(byteBuffer);
        byteBuffer.clear();

        if(bytesWritten >= this.messageInProcess.length){
            if(this.writeQueue.size() > 0 ){
                this.messageInProcess = this.writeQueue.remove(0);
            } else {
                this.messageInProcess = null;
            }
        }
    }

    public boolean isEmpty(){
        return this.writeQueue.isEmpty() && this.messageInProcess == null;
    }

}
