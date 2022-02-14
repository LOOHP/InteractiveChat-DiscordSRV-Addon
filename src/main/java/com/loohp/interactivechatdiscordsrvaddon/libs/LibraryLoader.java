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
