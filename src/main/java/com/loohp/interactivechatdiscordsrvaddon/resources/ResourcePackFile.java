package com.loohp.interactivechatdiscordsrvaddon.resources;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Paths;
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

    default String getRelativePathFrom(ResourcePackFile from) {
        return Paths.get(new File(from.getAbsolutePath()).toURI()).relativize(Paths.get(new File(this.getAbsolutePath()).toURI())).toString().replace("\\", "/");
    }

    @Override
    void close();

}
