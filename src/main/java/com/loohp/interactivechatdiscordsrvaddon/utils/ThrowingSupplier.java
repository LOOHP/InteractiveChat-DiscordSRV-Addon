package com.loohp.interactivechatdiscordsrvaddon.utils;

@FunctionalInterface
public interface ThrowingSupplier<R> {

    R get() throws Throwable;

}
