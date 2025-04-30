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
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

public interface ResourcePackFile extends AutoCloseable {

    ResourcePackFile getPackRootFile();

    String getName();

    String getParent();

    default boolean hasParent() {
        return getParent() != null;
    }

    ResourcePackFile getParentFile();

    String getAbsolutePath();

    String getPath();

    boolean exists();

    boolean isDirectory();

    Collection<ResourcePackFile> listFilesAndFolders();

    default Collection<ResourcePackFile> listFilesRecursively() {
        return listFilesRecursively(null);
    }

    Collection<ResourcePackFile> listFilesRecursively(String[] extensions);

    ResourcePackFile getChild(String name);

    InputStream getInputStream() throws IOException;

    default String getRelativePathFrom(ResourcePackFile from) {
        return new File(from.getAbsolutePath()).toPath().relativize(new File(this.getAbsolutePath()).toPath()).toString().replace("\\", "/");
    }

    @Override
    void close();

}
