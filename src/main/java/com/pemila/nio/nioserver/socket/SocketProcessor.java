package com.pemila.nio.nioserver.socket;

import com.pemila.nio.nioserver.WriteProxy;
import com.pemila.nio.nioserver.message.*;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.*;

/**
 * 连接处理线程
 * @author 月在未央
 * @date 2019/5/20 15:54
 */
public class SocketProcessor implements Runnable{

    private Queue<Socket> inboundSocketQueue = null;
    private IMessageReaderFactory messageReaderFactory = null;

    private MessageBuffer  readMessageBuffer    = null;
    private MessageBuffer  writeMessageBuffer   = null;

    private Queue<Message> outboundMessageQueue = new LinkedList<>();

    private Map<Long, Socket> socketMap = new HashMap<>();

    private ByteBuffer readByteBuffer  = ByteBuffer.allocate(1024 * 1024);
    private ByteBuffer writeByteBuffer = ByteBuffer.allocate(1024 * 1024);
    private Selector   readSelector    = null;
    private Selector writeSelector   = null;

    private IMessageProcessor messageProcessor = null;
    private WriteProxy writeProxy       = null;

    private long nextSocketId = 16 * 1024;

    private Set<Socket> emptyToNonEmptySockets = new HashSet<>();
    private Set<Socket> nonEmptyToEmptySockets = new HashSet<>();

    public SocketProcessor(Queue<Socket> inboundSocketQueue, MessageBuffer readMessageBuffer, MessageBuffer writeMessageBuffer, IMessageReaderFactory messageReaderFactory, IMessageProcessor messageProcessor) throws IOException {
        this.inboundSocketQueue = inboundSocketQueue;
        this.readMessageBuffer    = readMessageBuffer;
        this.writeMessageBuffer   = writeMessageBuffer;
        this.writeProxy           = new WriteProxy(writeMessageBuffer, this.outboundMessageQueue);
        this.messageReaderFactory = messageReaderFactory;
        this.messageProcessor     = messageProcessor;
        this.readSelector         = Selector.open();
        this.writeSelector        = Selector.open();
    }

    @Override
    public void run() {

    }

    public void executeCycle() throws IOException {
        takeNewSockets();
        readFromSockets();
        writeToSockets();
    }

    public void takeNewSockets() throws IOException {
        Socket newSocket = this.inboundSocketQueue.poll();

        while(newSocket != null){
            newSocket.socketId = this.nextSocketId++;
            newSocket.socketChannel.configureBlocking(false);
            newSocket.messageReader = this.messageReaderFactory.createMessageReader();
            newSocket.messageReader.init(this.readMessageBuffer);
            newSocket.messageWriter = new MessageWriter();
            this.socketMap.put(newSocket.socketId, newSocket);
            SelectionKey key = newSocket.socketChannel.register(this.readSelector, SelectionKey.OP_READ);
            key.attach(newSocket);
            newSocket = this.inboundSocketQueue.poll();
        }
    }

    public void readFromSockets() throws IOException {
        int readReady = this.readSelector.selectNow();

        if(readReady > 0){
            Set<SelectionKey> selectedKeys = this.readSelector.selectedKeys();
            Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

            while(keyIterator.hasNext()) {
                SelectionKey key = keyIterator.next();
                readFromSocket(key);
                keyIterator.remove();
            }
            selectedKeys.clear();
        }
    }

    private void readFromSocket(SelectionKey key) throws IOException {
        Socket socket = (Socket) key.attachment();
        socket.messageReader.read(socket, this.readByteBuffer);

        List<Message> fullMessages = socket.messageReader.getMessages();
        if(fullMessages.size() > 0){
            for(Message message : fullMessages){
                message.socketId = socket.socketId;
                //the message processor will eventually push outgoing messages into an IMessageWriter for this socket.
                this.messageProcessor.process(message, this.writeProxy);
            }
            fullMessages.clear();
        }

        if(socket.endOfStreamReached){
            System.out.println("Socket closed: " + socket.socketId);
            this.socketMap.remove(socket.socketId);
            key.attach(null);
            key.cancel();
            key.channel().close();
        }
    }

    public void writeToSockets() throws IOException {

        // Take all new messages from outboundMessageQueue
        takeNewOutboundMessages();

        // Cancel all sockets which have no more data to write.
        cancelEmptySockets();

        // Register all sockets that *have* data and which are not yet registered.
        registerNonEmptySockets();

        // Select from the Selector.
        int writeReady = this.writeSelector.selectNow();

        if(writeReady > 0){
            Set<SelectionKey>      selectionKeys = this.writeSelector.selectedKeys();
            Iterator<SelectionKey> keyIterator   = selectionKeys.iterator();

            while(keyIterator.hasNext()){
                SelectionKey key = keyIterator.next();

                Socket socket = (Socket) key.attachment();

                socket.messageWriter.write(socket, this.writeByteBuffer);

                if(socket.messageWriter.isEmpty()){
                    this.nonEmptyToEmptySockets.add(socket);
                }

                keyIterator.remove();
            }

            selectionKeys.clear();

        }
    }

    private void registerNonEmptySockets() throws ClosedChannelException {
        for(Socket socket : emptyToNonEmptySockets){
            socket.socketChannel.register(this.writeSelector, SelectionKey.OP_WRITE, socket);
        }
        emptyToNonEmptySockets.clear();
    }

    private void cancelEmptySockets() {
        for(Socket socket : nonEmptyToEmptySockets){
            SelectionKey key = socket.socketChannel.keyFor(this.writeSelector);
            key.cancel();
        }
        nonEmptyToEmptySockets.clear();
    }

    private void takeNewOutboundMessages() {
        Message outMessage = this.outboundMessageQueue.poll();
        while(outMessage != null){
            Socket socket = this.socketMap.get(outMessage.socketId);

            if(socket != null){
                MessageWriter messageWriter = socket.messageWriter;
                if(messageWriter.isEmpty()){
                    messageWriter.enqueue(outMessage);
                    nonEmptyToEmptySockets.remove(socket);
                    //not necessary if removed from nonEmptyToEmptySockets in prev. statement.
                    emptyToNonEmptySockets.add(socket);
                } else{
                    messageWriter.enqueue(outMessage);
                }
            }
            outMessage = this.outboundMessageQueue.poll();
        }
    }
}