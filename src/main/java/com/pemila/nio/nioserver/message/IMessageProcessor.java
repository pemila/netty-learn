package com.pemila.nio.nioserver.message;

import com.pemila.nio.nioserver.WriteProxy;

/**
 * @author 月在未央
 * @date 2019/5/20 17:31
 */
public interface IMessageProcessor {

    void process(Message message, WriteProxy writeProxy);
}
