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

package com.loohp.interactivechatdiscordsrvaddon.libs;

import java.io.File;
import java.net.URLClassLoader;
import java.util.function.BiConsumer;

public class LibraryLoader {

    private static final URLClassLoaderAccess LOADER_ACCESS = URLClassLoaderAccess.create((URLClassLoader) LibraryLoader.class.getClassLoader());
    private static final BiConsumer<File, Throwable> NOOP_LISTENER = (file, e) -> {
    };

    public static void loadLibraries(File libsFolder) {
        loadLibraries(libsFolder, NOOP_LISTENER);
    }

    public static void loadLibraries(File libsFolder, BiConsumer<File, Throwable> loadListener) {
        libsFolder.mkdirs();
        for (File jarFile : libsFolder.listFiles()) {
            String jarName = jarFile.getName();
            if (jarName.endsWith(".jar")) {
                try {
                    LOADER_ACCESS.addURL(jarFile.toURI().toURL());
                    loadListener.accept(jarFile, null);
                } catch (Throwable e) {
                    loadListener.accept(jarFile, e);
                }
            }
        }
    }

}
