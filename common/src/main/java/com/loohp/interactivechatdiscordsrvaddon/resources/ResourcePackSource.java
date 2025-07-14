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

import java.io.File;
import java.util.Objects;
import java.util.UUID;

public class ResourcePackSource {

    public static ResourcePackSource ofCustom(String name, File resourcePackFile, ResourcePackType type) {
        return new ResourcePackSource(name, resourcePackFile, type, false);
    }

    public static ResourcePackSource ofDefault(String name, File resourcePackFile, ResourcePackType type) {
        return new ResourcePackSource(name, resourcePackFile, type, true);
    }

    private final UUID uuid;
    private final String name;
    private final File resourcePackFile;
    private final ResourcePackType type;
    private final boolean defaultResource;

    private ResourcePackSource(String name, File resourcePackFile, ResourcePackType type, boolean defaultResource) {
        this.name = name;
        this.uuid = UUID.randomUUID();
        this.resourcePackFile = resourcePackFile;
        this.type = type;
        this.defaultResource = defaultResource;
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public File getResourcePackFile() {
        return resourcePackFile;
    }

    public ResourcePackType getType() {
        return type;
    }

    public boolean isDefaultResource() {
        return defaultResource;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ResourcePackSource that = (ResourcePackSource) o;
        return Objects.equals(uuid, that.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(uuid);
    }
}
