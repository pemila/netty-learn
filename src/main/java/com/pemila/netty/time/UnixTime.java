package com.pemila.netty.time;

import java.util.Date;

/**
 * @author 月在未央
 * @date 2019/5/22 11:20
 */
public class UnixTime {

    private final long value;

    public UnixTime(long value) {
        this.value = value;
    }

    public UnixTime(){
        this(System.currentTimeMillis()/1000L+ 2208988800L);
    }

    public long value(){
        return value;
    }

    @Override
    public String toString() {
        return new Date((value() - 2208988800L) * 1000L).toString();
    }
}
