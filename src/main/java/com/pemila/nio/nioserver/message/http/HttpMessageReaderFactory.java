package com.pemila.nio.nioserver.message.http;

import com.pemila.nio.nioserver.message.IMessageReader;
import com.pemila.nio.nioserver.message.IMessageReaderFactory;

/**
 * @author 月在未央
 * @date 2019/5/20 18:12
 */
public class HttpMessageReaderFactory implements IMessageReaderFactory {

    public HttpMessageReaderFactory(){}

    @Override
    public IMessageReader createMessageReader() {
        return new HttpMessageReader();
    }
}
