package com.loohp.interactivechatdiscordsrvaddon.objectholders;

import java.util.concurrent.atomic.AtomicInteger;

public class IDProvider {

    private AtomicInteger counter;

    public IDProvider() {
        this.counter = new AtomicInteger(0);
    }

    public int getNext() {
        return counter.getAndUpdate(i -> i >= Integer.MAX_VALUE ? 0 : i + 1);
    }

}
