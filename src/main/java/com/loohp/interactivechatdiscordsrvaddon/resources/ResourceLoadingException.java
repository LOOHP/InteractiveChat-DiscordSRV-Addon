package com.loohp.interactivechatdiscordsrvaddon.resources;

public class ResourceLoadingException extends RuntimeException {

    public ResourceLoadingException(String message) {
        super(message);
    }

    public ResourceLoadingException(Throwable cause) {
        super(cause);
    }

    public ResourceLoadingException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
