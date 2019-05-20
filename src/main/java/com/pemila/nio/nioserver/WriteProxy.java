package com.pemila.nio.nioserver;

import com.pemila.nio.nioserver.message.Message;
import com.pemila.nio.nioserver.message.MessageBuffer;

import java.util.Queue;

/**
 * Created by jjenkov on 22-10-2015.
 */
public class WriteProxy {

    private MessageBuffer messageBuffer = null;
    private Queue        writeQueue     = null;

    public WriteProxy(MessageBuffer messageBuffer, Queue writeQueue) {
        this.messageBuffer = messageBuffer;
        this.writeQueue = writeQueue;
    }

    public Message getMessage(){
        return this.messageBuffer.getMessage();
    }

    boolean enqueue(Message message){
        return this.writeQueue.offer(message);
    }

}