/*
 * This file is part of InteractiveChatDiscordSrvAddon2.
 *
 * Copyright (C) 2020 - 2025. LoohpJames <jamesloohp@gmail.com>
 * Copyright (C) 2020 - 2025. Contributors
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
import com.loohp.interactivechatdiscordsrvaddon.resources.languages.LanguageMeta;

import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ResourcePackInfo {

    public static final String UNKNOWN_PACK_ICON_LOCATION = "minecraft:misc/unknown_pack";

    private final ResourceManager manager;
    private final ResourcePackFile file;
    private final ResourcePackType type;
    private final boolean status;
    private final boolean exist;
    private final String rejectedReason;
    private final Component name;
    private final PackFormat packFormat;
    private final Component description;
    private final Map<String, LanguageMeta> languageMeta;
    private final List<PackOverlay> overlays;
    private final BufferedImage icon;
    private final List<ResourceFilterBlock> resourceFilterBlocks;

    private ResourcePackInfo(ResourceManager manager, ResourcePackFile file, ResourcePackType type, Component name, boolean status, boolean exist, String rejectedReason, PackFormat packFormat, Component description, Map<String, LanguageMeta> languageMeta, List<PackOverlay> overlays, BufferedImage icon, List<ResourceFilterBlock> resourceFilterBlocks) {
        this.manager = manager;
        this.file = file;
        this.type = type;
        this.name = name;
        this.status = status;
        this.exist = exist;
        this.rejectedReason = rejectedReason;
        this.packFormat = packFormat;
        this.description = description;
        this.languageMeta = Collections.unmodifiableMap(languageMeta);
        this.overlays = Collections.unmodifiableList(overlays);
        this.icon = icon;
        this.resourceFilterBlocks = resourceFilterBlocks;
    }

    public ResourcePackInfo(ResourceManager manager, ResourcePackFile file, ResourcePackType type, Component name, boolean status, String rejectedReason, PackFormat packFormat, Component description, Map<String, LanguageMeta> languageMeta, BufferedImage icon, List<ResourceFilterBlock> resourceFilterBlocks, List<PackOverlay> overlays) {
        this(manager, file, type, name, status, true, rejectedReason, packFormat, description, languageMeta, overlays, icon, resourceFilterBlocks);
    }

    public ResourcePackInfo(ResourceManager manager, ResourcePackFile file, ResourcePackType type, Component name, String rejectedReason) {
        this(manager, file, type, name, false, false, rejectedReason, null, null, Collections.emptyMap(), Collections.emptyList(), null, Collections.emptyList());
    }

    public ResourceManager getManager() {
        return manager;
    }

    public ResourcePackFile getResourcePackFile() {
        return file;
    }

    public ResourcePackType getType() {
        return type;
    }

    public int getPackOrder() {
        return manager.getResourcePackInfo().indexOf(this);
    }

    public boolean isValid() {
        return manager.isValid();
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

    public Component getName() {
        return name;
    }

    public PackFormat getPackFormat() {
        return packFormat;
    }

    public int compareServerPackFormat(int localFormat) {
        if (packFormat == null) {
            return 1;
        }
        if (packFormat.isCompatible(localFormat)) {
            return 0;
        }
        return Integer.compare(packFormat.getMajor(), localFormat);
    }

    public Component getDescription() {
        return description;
    }

    public Map<String, LanguageMeta> getLanguageMeta() {
        return languageMeta;
    }

    public List<PackOverlay> getOverlays() {
        return overlays;
    }

    public BufferedImage getRawIcon() {
        return icon;
    }

    public BufferedImage getIcon() {
        return icon == null ? manager.getTextureManager().getTexture(UNKNOWN_PACK_ICON_LOCATION).getTexture() : icon;
    }

    public List<ResourceFilterBlock> getResourceFilterBlocks() {
        return resourceFilterBlocks;
    }

}
