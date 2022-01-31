package com.loohp.interactivechatdiscordsrvaddon.resources;

import java.io.File;
import java.io.InputStream;
import java.util.Collection;

public interface ResourcePackFile extends AutoCloseable {

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

    ResourcePackFile getChild(String name);

    InputStream getInputStream();

    default Collection<ResourcePackFile> listFilesRecursively() {
        return listFilesRecursively(null);
    }

    Collection<ResourcePackFile> listFilesRecursively(String[] extensions);

    default String getRelativePathFrom(ResourcePackFile from) {
        return new File(from.getAbsolutePath()).toPath().relativize(new File(this.getAbsolutePath()).toPath()).toString().replace("\\", "/");
    }

    @Override
    void close();

}
