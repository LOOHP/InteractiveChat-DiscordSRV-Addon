package com.loohp.interactivechatdiscordsrvaddon.resources;

import com.loohp.interactivechat.libs.net.kyori.adventure.text.Component;
import com.loohp.interactivechatdiscordsrvaddon.registry.ResourceRegistry;

public class ResourcePackInfo {

    private ResourcePackFile file;
    private boolean status;
    private boolean exist;
    private String rejectedReason;
    private String name;
    private int packFormat;
    private Component description;

    private ResourcePackInfo(ResourcePackFile file, String name, boolean status, boolean exist, String rejectedReason, int packFormat, Component description) {
        this.file = file;
        this.name = name;
        this.status = status;
        this.exist = exist;
        this.rejectedReason = rejectedReason;
        this.packFormat = packFormat;
        this.description = description;
    }

    public ResourcePackInfo(ResourcePackFile file, String name, boolean status, String rejectedReason, int packFormat, Component description) {
        this(file, name, status, true, rejectedReason, packFormat, description);
    }

    public ResourcePackInfo(ResourcePackFile file, String name, String rejectedReason) {
        this(file, name, false, false, rejectedReason, -1, null);
    }

    public ResourcePackFile getResourcePackFile() {
        return file;
    }

    public boolean getStatus() {
        return status;
    }

    public String getRejectedReason() {
        return rejectedReason;
    }

    public boolean exists() {
        return exist;
    }

    public String getName() {
        return name;
    }

    public int getPackFormat() {
        return packFormat;
    }

    public int comparePackFormat() {
        return ResourceRegistry.RESOURCE_PACK_VERSION < packFormat ? 1 : (ResourceRegistry.RESOURCE_PACK_VERSION > packFormat ? -1 : 0);
    }

    public Component getDescription() {
        return description;
    }

}
