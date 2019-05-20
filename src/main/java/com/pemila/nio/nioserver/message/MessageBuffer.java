package com.pemila.nio.nioserver.message;

import com.pemila.nio.nioserver.QueueIntFlip;

/**
 * @author 月在未央
 * @date 2019/5/20 15:53
 */
public class MessageBuffer {

    private static int KB = 1024;
    private static int MB = KB * 1024;

    private static int CAPACITY_SMALL = 4 * KB;
    private static int CAPACITY_MEDIUM = 128 * KB;
    private static int CAPACITY_LARGE = 1024 * KB;

    /** 可容纳1024条4KB消息，总大小4MB*/
    byte[]  smallMessageBuffer  = new byte[1024 *   4 * KB];
    /** 可容纳127条124KB消息,总大小16MB*/
    byte[]  mediumMessageBuffer = new byte[128  * 128 * KB];
    /** 可容纳16条1MB消息，总大小16MB*/
    byte[]  largeMessageBuffer  = new byte[16 * MB];

    /** 1024个空闲section*/
    QueueIntFlip smallMessageBufferFreeBlocks  = new QueueIntFlip(1024);
    /** 128个空闲section*/
    QueueIntFlip mediumMessageBufferFreeBlocks = new QueueIntFlip(128);
    /** 16个空闲section*/
    QueueIntFlip largeMessageBufferFreeBlocks  = new QueueIntFlip(16);

    public MessageBuffer() {
        // 将所有free section加入到队列中
        for(int i=0; i<smallMessageBuffer.length; i+= CAPACITY_SMALL){
            this.smallMessageBufferFreeBlocks.put(i);
        }
        for(int i=0; i<mediumMessageBuffer.length; i+= CAPACITY_MEDIUM){
            this.mediumMessageBufferFreeBlocks.put(i);
        }
        for(int i=0; i<largeMessageBuffer.length; i+= CAPACITY_LARGE){
            this.largeMessageBufferFreeBlocks.put(i);
        }
    }

    public Message getMessage() {
        int nextFreeSmallBlock = this.smallMessageBufferFreeBlocks.take();

        if(nextFreeSmallBlock == -1){
            return null;
        }
        Message message = new Message(this);

        message.sharedArray = this.smallMessageBuffer;
        message.capacity    = CAPACITY_SMALL;
        message.offset      = nextFreeSmallBlock;
        message.length      = 0;

        return message;
    }

    public boolean expandMessage(Message message){
        if(message.capacity == CAPACITY_SMALL){
            return moveMessage(message, this.smallMessageBufferFreeBlocks, this.mediumMessageBufferFreeBlocks, this.mediumMessageBuffer, CAPACITY_MEDIUM);
        } else if(message.capacity == CAPACITY_MEDIUM){
            return moveMessage(message, this.mediumMessageBufferFreeBlocks, this.largeMessageBufferFreeBlocks, this.largeMessageBuffer, CAPACITY_LARGE);
        } else {
            return false;
        }
    }

    private boolean moveMessage(Message message, QueueIntFlip srcBlockQueue, QueueIntFlip destBlockQueue, byte[] dest, int newCapacity) {
        int nextFreeBlock = destBlockQueue.take();
        if(nextFreeBlock == -1){
            return false;
        }
        System.arraycopy(message.sharedArray, message.offset, dest, nextFreeBlock, message.length);
        //free smaller block after copy
        srcBlockQueue.put(message.offset);
        message.sharedArray = dest;
        message.offset      = nextFreeBlock;
        message.capacity    = newCapacity;
        return true;
    }

}
