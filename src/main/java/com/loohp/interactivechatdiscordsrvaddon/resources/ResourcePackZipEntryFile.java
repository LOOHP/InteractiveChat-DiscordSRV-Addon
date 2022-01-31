package com.loohp.interactivechatdiscordsrvaddon.resources;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ResourcePackZipEntryFile implements ResourcePackFile {

    private String absoluteRootPath;
    private ZipFile zipRoot;
    private String zipPath;
    private boolean isDirectory;
    private ZipEntry zipEntry;

    public ResourcePackZipEntryFile(File resourcePackZip) throws IOException {
        this.absoluteRootPath = resourcePackZip.getAbsolutePath();
        this.zipRoot = new ZipFile(resourcePackZip);
        this.zipPath = "";
        this.isDirectory = true;
        this.zipEntry = null;
    }

    private ResourcePackZipEntryFile(String absoluteRootPath, ZipFile zipRoot, String zipPath, boolean isDirectory, ZipEntry zipEntry) {
        this.absoluteRootPath = absoluteRootPath;
        this.zipRoot = zipRoot;
        this.zipPath = zipPath;
        this.isDirectory = isDirectory;
        this.zipEntry = zipEntry;
    }

    public ZipFile getZipRoot() {
        return zipRoot;
    }

    public boolean hasZipEntry() {
        return zipEntry != null;
    }

    public ZipEntry getZipEntry() {
        return zipEntry;
    }

    @Override
    public String getName() {
        String path = getPath();
        if (path.contains("/")) {
            path = path.substring(path.lastIndexOf("/") + 1);
        }
        return path;
    }

    @Override
    public String getParent() {
        String path = getPath();
        if (path.contains("/")) {
            path = path.substring(0, path.lastIndexOf("/"));
        } else {
            return null;
        }
        return path;
    }

    @Override
    public ResourcePackFile getParentFile() {
        return new ResourcePackZipEntryFile(absoluteRootPath, zipRoot, getParent(), true, null);
    }

    @Override
    public String getPath() {
        return zipPath;
    }

    @Override
    public String getAbsolutePath() {
        String path = getPath();
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        if (absoluteRootPath.contains("\\")) {
            path = path.replace("/", "\\");
        }
        return absoluteRootPath + path;
    }

    @Override
    public boolean exists() {
        return isDirectory || zipEntry != null;
    }

    @Override
    public boolean isDirectory() {
        return isDirectory;
    }

    @Override
    public Collection<ResourcePackFile> listFilesAndFolders() {
        Set<ResourcePackFile> set = new HashSet<>();
        Enumeration<? extends ZipEntry> itr = zipRoot.entries();
        while (itr.hasMoreElements()) {
            ZipEntry entry = itr.nextElement();
            if (!entry.isDirectory()) {
                String entryPath = entry.getName();
                if (!entryPath.equals(zipPath) && entryPath.startsWith(zipPath)) {
                    String relativePath = entryPath.replace(zipPath, "");
                    if (relativePath.startsWith("/")) {
                        relativePath = relativePath.substring(1);
                    }
                    if (relativePath.contains("/")) {
                        String folderName = relativePath.substring(0, relativePath.indexOf("/"));
                        set.add(new ResourcePackZipEntryFile(absoluteRootPath, zipRoot, (zipPath.isEmpty() ? zipPath : (zipPath + "/")) + folderName, true, null));
                    } else {
                        set.add(new ResourcePackZipEntryFile(absoluteRootPath, zipRoot, entryPath, false, entry));
                    }
                }
            }
        }
        return set;
    }

    @Override
    public ResourcePackFile getChild(String name) {
        for (ResourcePackFile entry : listFilesAndFolders()) {
            ResourcePackZipEntryFile zipEntryFile = (ResourcePackZipEntryFile) entry;
            if (zipEntryFile.zipPath.equals((zipPath.isEmpty() ? zipPath : (zipPath + "/")) + name)) {
                return zipEntryFile;
            }
        }
        return new ResourcePackZipEntryFile(absoluteRootPath, zipRoot, (zipPath.isEmpty() ? zipPath : (zipPath + "/")) + name, false, null);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((zipPath == null) ? 0 : zipPath.hashCode());
        result = prime * result + ((zipRoot == null) ? 0 : zipRoot.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ResourcePackZipEntryFile)) {
            return false;
        }
        ResourcePackZipEntryFile other = (ResourcePackZipEntryFile) obj;
        if (zipPath == null) {
            if (other.zipPath != null) {
                return false;
            }
        } else if (!zipPath.equals(other.zipPath)) {
            return false;
        }
        if (zipRoot == null) {
            return other.zipRoot == null;
        } else {
            return zipRoot.equals(other.zipRoot);
        }
    }

    @Override
    public InputStream getInputStream() {
        try {
            return zipRoot.getInputStream(zipEntry);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Collection<ResourcePackFile> listFilesRecursively(String[] extensions) {
        List<ResourcePackFile> list = new ArrayList<>();
        Enumeration<? extends ZipEntry> itr = zipRoot.entries();
        while (itr.hasMoreElements()) {
            ZipEntry entry = itr.nextElement();
            if (!entry.isDirectory()) {
                String entryPath = entry.getName();
                if ((this.zipEntry == null || !entry.getName().equals(this.zipEntry.getName())) && entryPath.startsWith(zipPath)) {
                    if (extensions == null || Stream.of(extensions).anyMatch(each -> entryPath.endsWith("." + each))) {
                        list.add(new ResourcePackZipEntryFile(absoluteRootPath, zipRoot, entryPath, false, entry));
                    }
                }
            }
        }
        return list;
    }

    @Override
    public void close() {
        try {
            zipRoot.close();
        } catch (IOException e) {
        }
    }

}
