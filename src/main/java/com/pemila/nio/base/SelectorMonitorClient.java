package com.pemila.nio.base;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * socket客户端
 * @author 月在未央
 * @date 2019/5/17 9:57
 */
public class SelectorMonitorClient {
    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("127.0.0.1", 30000);
        OutputStream os = socket.getOutputStream();
        PrintWriter print = new PrintWriter(os);
        print.write("hello,server!");
        print.close();
        os.close();
    }
}
