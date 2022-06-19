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

import me.lucko.jarrelocator.JarRelocator;
import me.lucko.jarrelocator.Relocation;

import java.io.File;
import java.io.IOException;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class LibraryLoader {

    private static final URLClassLoaderAccess LOADER_ACCESS = URLClassLoaderAccess.create((URLClassLoader) LibraryLoader.class.getClassLoader());
    private static final BiConsumer<File, Throwable> NOOP_LISTENER = (file, e) -> {
    };
    private static final List<Relocation> RELOCATION_RULES = new ArrayList<>();

    static {
        RELOCATION_RULES.add(new Relocation(dot("com{}ibm{}icu"), "com.ibm.icu"));
        RELOCATION_RULES.add(new Relocation(dot("darwin{}"), "darwin."));
        RELOCATION_RULES.add(new Relocation(dot("kotlin{}"), "kotlin."));
        RELOCATION_RULES.add(new Relocation(dot("linux{}"), "linux."));
        RELOCATION_RULES.add(new Relocation(dot("win32{}"), "win32."));
        RELOCATION_RULES.add(new Relocation(dot("net{}jpountz"), "net.jpountz"));
        RELOCATION_RULES.add(new Relocation(dot("org{}checkerframework"), "org.checkerframework"));
        RELOCATION_RULES.add(new Relocation(dot("org{}eclipse"), "org.eclipse"));
        RELOCATION_RULES.add(new Relocation(dot("org{}json"), "org.json"));
        RELOCATION_RULES.add(new Relocation(dot("org{}mapdb"), "org.mapdb"));
    }

    private static String dot(String str) {
        return str.replace("{}", ".");
    }

    public static void loadLibraries(File libsFolder) {
        loadLibraries(libsFolder, NOOP_LISTENER, NOOP_LISTENER);
    }

    public static void loadLibraries(File libsFolder, BiConsumer<File, Throwable> remapListener, BiConsumer<File, Throwable> loadListener) {
        libsFolder.mkdirs();
        for (File jarFile : libsFolder.listFiles()) {
            String jarName = jarFile.getName();
            if (jarName.endsWith(".jar")) {
                String rawName = jarName.substring(0, jarName.length() - 4);
                if (!rawName.endsWith("-remapped")) {
                    File remappedFile = new File(libsFolder, rawName + "-remapped.jar");
                    if (remappedFile.exists()) {
                        continue;
                    }
                    JarRelocator relocator = new JarRelocator(jarFile, remappedFile, RELOCATION_RULES);
                    try {
                        relocator.run();
                        remapListener.accept(jarFile, null);
                    } catch (IOException e) {
                        remapListener.accept(jarFile, e);
                    }
                }
            }
        }
        for (File jarFile : libsFolder.listFiles()) {
            String jarName = jarFile.getName();
            if (jarName.endsWith(".jar")) {
                String rawName = jarName.substring(0, jarName.length() - 4);
                if (rawName.endsWith("-remapped")) {
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

}
