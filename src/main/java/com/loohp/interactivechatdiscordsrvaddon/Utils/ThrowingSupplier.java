package com.loohp.interactivechatdiscordsrvaddon.Utils;

@FunctionalInterface
public interface ThrowingSupplier<R> {
	
	R get() throws Throwable;
	
}
