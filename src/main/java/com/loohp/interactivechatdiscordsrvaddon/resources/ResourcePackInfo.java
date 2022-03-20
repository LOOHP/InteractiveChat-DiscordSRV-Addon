/*
 * This file is part of InteractiveChatDiscordSrvAddon.
 *
 * Copyright (C) 2022. LoohpJames <jamesloohp@gmail.com>
 * Copyright (C) 2022. Contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.loohp.interactivechatdiscordsrvaddon.resources;

import com.loohp.interactivechat.libs.net.kyori.adventure.text.Component;
import com.loohp.interactivechatdiscordsrvaddon.registry.ResourceRegistry;
import com.loohp.interactivechatdiscordsrvaddon.resources.languages.LanguageMeta;

import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.Map;

public class ResourcePackInfo {

    private ResourceManager manager;
    private ResourcePackFile file;
    private boolean status;
    private boolean exist;
    private String rejectedReason;
    private String name;
    private int packFormat;
    private Component description;
    private Map<String, LanguageMeta> languageMeta;
    private BufferedImage icon;

    private ResourcePackInfo(ResourceManager manager, ResourcePackFile file, String name, boolean status, boolean exist, String rejectedReason, int packFormat, Component description, Map<String, LanguageMeta> languageMeta, BufferedImage icon) {
        this.manager = manager;
        this.file = file;
        this.name = name;
        this.status = status;
        this.exist = exist;
        this.rejectedReason = rejectedReason;
        this.packFormat = packFormat;
        this.description = description;
        this.languageMeta = Collections.unmodifiableMap(languageMeta);
        this.icon = icon;
    }

    public ResourcePackInfo(ResourceManager manager, ResourcePackFile file, String name, boolean status, String rejectedReason, int packFormat, Component description, Map<String, LanguageMeta> languageMeta, BufferedImage icon) {
        this(manager, file, name, status, true, rejectedReason, packFormat, description, languageMeta, icon);
    }

    public ResourcePackInfo(ResourceManager manager, ResourcePackFile file, String name, String rejectedReason) {
        this(manager, file, name, false, false, rejectedReason, -1, null, Collections.emptyMap(), null);
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
        return Integer.compare(packFormat, ResourceRegistry.RESOURCE_PACK_VERSION);
    }

    public Component getDescription() {
        return description;
    }

    public Map<String, LanguageMeta> getLanguageMeta() {
        return languageMeta;
    }

    public BufferedImage getRawIcon() {
        return icon;
    }

    public BufferedImage getIcon() {
        return icon == null ? manager.getTextureManager().getTexture(ResourceRegistry.UNKNOWN_PACK_ICON_LOCATION).getTexture() : icon;
    }

}
