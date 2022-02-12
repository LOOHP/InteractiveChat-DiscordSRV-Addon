package com.loohp.interactivechatdiscordsrvaddon.resources;

import com.loohp.interactivechat.libs.org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ResourcePackSystemFile implements ResourcePackFile {

    private ResourcePackSystemFile root;
    private File file;
    private Set<InputStream> streams;

    public ResourcePackSystemFile(File file) {
        this.root = this;
        this.file = file;
        this.streams = new HashSet<>();
    }

    private ResourcePackSystemFile(ResourcePackSystemFile root, File file) {
        this.root = root;
        this.file = file;
        this.streams = null;
    }

    public File getFile() {
        return file;
    }

    public ResourcePackSystemFile getResourceSystemRoot() {
        return root;
    }

    public boolean isResourceSystemRoot() {
        return root == this;
    }

    @Override
    public String getName() {
        return file.getName();
    }

    @Override
    public String getParent() {
        if (isResourceSystemRoot()) {
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
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ResourcePackSystemFile that = (ResourcePackSystemFile) o;
        if (!Objects.equals(isResourceSystemRoot(), that.isResourceSystemRoot())) {
            return false;
        }
        if (isResourceSystemRoot()) {
            return Objects.equals(file, that.file);
        }
        return Objects.equals(root, that.root) && Objects.equals(file, that.file);
    }

    @Override
    public int hashCode() {
        if (isResourceSystemRoot()) {
            return Objects.hash(file);
        }
        return Objects.hash(root, file);
    }

    @Override
    public InputStream getInputStream() throws IOException {
        InputStream stream = new FileInputStream(file);
        root.streams.add(stream);
        return stream;
    }

    @Override
    public Collection<ResourcePackFile> listFilesRecursively(String[] extensions) {
        return FileUtils.listFiles(file, extensions, true).stream().map(each -> new ResourcePackSystemFile(root, each)).collect(Collectors.toList());
    }

    @Override
    public void close() {
        for (InputStream stream : root.streams) {
            try {
                stream.close();
            } catch (IOException ignore) {
            }
        }
    }

}
