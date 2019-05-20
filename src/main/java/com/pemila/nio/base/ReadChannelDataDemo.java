package com.pemila.nio.base;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * 使用FileChannel读取数据到Buffer
 * @author 月在未央
 * @date 2019/5/16 11:34
 */
public class ReadChannelDataDemo {

    public static void main(String[] args) throws IOException {
        String path = "E:\\IDEARepos\\netty-learn\\src\\main\\resources\\OdeWestWind.txt";
        RandomAccessFile accessFile = new RandomAccessFile(path,"rw");
        FileChannel channel = accessFile.getChannel();

        //读取数据到Buffer，然后反转Buffer,接着再从Buffer中读取数据
        ByteBuffer byteBuffer = ByteBuffer.allocate(48);
        int bytesRead = channel.read(byteBuffer);
        while (bytesRead !=-1){
            System.out.println("\nRead:"+bytesRead);
            //反转buffer
            byteBuffer.flip();
            while(byteBuffer.hasRemaining()){
                System.out.print((char) byteBuffer.get());
            }
            byteBuffer.clear();
            bytesRead = channel.read(byteBuffer);
        }
        accessFile.close();
    }
}
