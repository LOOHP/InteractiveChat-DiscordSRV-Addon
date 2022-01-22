package com.loohp.interactivechatdiscordsrvaddon.resources;

import java.io.InputStream;
import java.util.Collection;

public interface ResourcePackFile extends AutoCloseable {

    String getName();

    String getParent();

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

    @Override
    void close();

}
