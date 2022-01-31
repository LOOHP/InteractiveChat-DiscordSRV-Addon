package com.loohp.interactivechatdiscordsrvaddon.resources;

import com.loohp.interactivechat.libs.org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ResourcePackSystemFile implements ResourcePackFile {

    private File root;
    private File file;

    public ResourcePackSystemFile(File file) {
        this.root = file;
        this.file = file;
    }

    private ResourcePackSystemFile(File root, File file) {
        this.root = root;
        this.file = file;
    }

    public File getFile() {
        return file;
    }

    @Override
    public String getName() {
        return file.getName();
    }

    @Override
    public String getParent() {
        if (root.equals(file)) {
            return null;
        }
        return file.getParent();
    }

    @Override
    public ResourcePackFile getParentFile() {
        if (getParent() == null) {
            return null;
        }
        return new ResourcePackSystemFile(root, file.getParentFile());
    }

    @Override
    public String getPath() {
        return file.getPath();
    }

    @Override
    public boolean exists() {
        return file.exists();
    }

    @Override
    public boolean isDirectory() {
        return file.isDirectory();
    }

    @Override
    public Collection<ResourcePackFile> listFilesAndFolders() {
        return Stream.of(file.listFiles()).map(each -> new ResourcePackSystemFile(root, each)).collect(Collectors.toSet());
    }

    @Override
    public String getAbsolutePath() {
        return file.getAbsolutePath();
    }

    @Override
    public ResourcePackFile getChild(String name) {
        return new ResourcePackSystemFile(root, new File(file, name));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((file == null) ? 0 : file.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ResourcePackSystemFile)) {
            return false;
        }
        ResourcePackSystemFile other = (ResourcePackSystemFile) obj;
        if (file == null) {
            return other.file == null;
        } else {
            return file.equals(other.file);
        }
    }

    @Override
    public InputStream getInputStream() {
        try {
            return new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Collection<ResourcePackFile> listFilesRecursively(String[] extensions) {
        return FileUtils.listFiles(file, extensions, true).stream().map(each -> new ResourcePackSystemFile(root, each)).collect(Collectors.toList());
    }

    @Override
    public void close() {

    }

}
