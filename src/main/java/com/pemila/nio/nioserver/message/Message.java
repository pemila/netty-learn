package com.pemila.nio.nioserver.message;

import java.nio.ByteBuffer;

/**
 * @author 月在未央
 * @date 2019/5/20 17:13
 */
public class Message {
    private MessageBuffer messageBuffer = null;

    /** socket连接id*/
    public long socketId = 0;

    public byte[] sharedArray = null;
    /** 消息开始位置在shareArray中的偏移*/
    public int offset;
    /** sharedArray中分配给此消息的字节大小*/
    public int capacity;
    public int length;


    public Object metaData = null;

    public Message(MessageBuffer messageBuffer){
        this.messageBuffer = messageBuffer;
    }

    public int writeToMessage(ByteBuffer byteBuffer){
        int remaining = byteBuffer.remaining();

        while(this.length + remaining > capacity){
            if(!this.messageBuffer.expandMessage(this)) {
                return -1;
            }
        }

        int bytesToCopy = Math.min(remaining, this.capacity - this.length);
        byteBuffer.get(this.sharedArray, this.offset + this.length, bytesToCopy);
        this.length += bytesToCopy;

        return bytesToCopy;
    }

    public int writeToMessage(byte[] byteArray){
        return writeToMessage(byteArray, 0, byteArray.length);
    }

    public int writeToMessage(byte[] byteArray, int offset, int length){
        int remaining = length;

        while(this.length + remaining > capacity){
            if(!this.messageBuffer.expandMessage(this)) {
                return -1;
            }
        }

        int bytesToCopy = Math.min(remaining, this.capacity - this.length);
        System.arraycopy(byteArray, offset, this.sharedArray, this.offset + this.length, bytesToCopy);
        this.length += bytesToCopy;
        return bytesToCopy;
    }

    public void writePartialMessageToMessage(Message message, int endIndex){
        int startIndexOfPartialMessage = message.offset + endIndex;
        int lengthOfPartialMessage     = (message.offset + message.length) - endIndex;

        System.arraycopy(message.sharedArray, startIndexOfPartialMessage, this.sharedArray, this.offset, lengthOfPartialMessage);
    }

    public int writeToByteBuffer(ByteBuffer byteBuffer){
        return 0;
    }


}
