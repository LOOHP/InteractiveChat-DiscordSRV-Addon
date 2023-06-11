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

package com.loohp.interactivechatdiscordsrvaddon.resources;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ResourcePackZipEntryFile implements ResourcePackFile {

    private final String absoluteRootPath;
    private final ResourcePackZipEntryFile zipRootFile;
    private final ZipFile zipRoot;
    private final String zipPath;
    private final boolean isDirectory;
    private final ZipEntry zipEntry;

    public ResourcePackZipEntryFile(File resourcePackZip) throws IOException {
        this.absoluteRootPath = resourcePackZip.getAbsolutePath();
        this.zipRootFile = this;
        this.zipRoot = new ZipFile(resourcePackZip);
        this.zipPath = "";
        this.isDirectory = true;
        this.zipEntry = null;
    }

    private ResourcePackZipEntryFile(String absoluteRootPath, ResourcePackZipEntryFile zipRootFile, ZipFile zipRoot, String zipPath, boolean isDirectory, ZipEntry zipEntry) {
        this.absoluteRootPath = absoluteRootPath;
        this.zipRootFile = zipRootFile;
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
    public ResourcePackFile getPackRootFile() {
        return zipRootFile;
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
        return new ResourcePackZipEntryFile(absoluteRootPath, zipRootFile, zipRoot, getParent(), true, null);
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
                    String relativePath = entryPath.substring(zipPath.length());
                    if (relativePath.startsWith("/")) {
                        relativePath = relativePath.substring(1);
                    }
                    if (relativePath.contains("/")) {
                        String folderName = relativePath.substring(0, relativePath.indexOf("/"));
                        set.add(new ResourcePackZipEntryFile(absoluteRootPath, zipRootFile, zipRoot, (zipPath.isEmpty() ? zipPath : (zipPath + "/")) + folderName, true, null));
                    } else {
                        set.add(new ResourcePackZipEntryFile(absoluteRootPath, zipRootFile, zipRoot, entryPath, false, entry));
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
        return new ResourcePackZipEntryFile(absoluteRootPath, zipRootFile, zipRoot, (zipPath.isEmpty() ? zipPath : (zipPath + "/")) + name, false, null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ResourcePackZipEntryFile that = (ResourcePackZipEntryFile) o;
        return Objects.equals(absoluteRootPath, that.absoluteRootPath) && Objects.equals(zipRoot, that.zipRoot) && Objects.equals(zipPath, that.zipPath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(absoluteRootPath, zipRoot, zipPath);
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return zipRoot.getInputStream(zipEntry);
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
                    if (extensions == null || Arrays.stream(extensions).anyMatch(each -> entryPath.endsWith("." + each))) {
                        list.add(new ResourcePackZipEntryFile(absoluteRootPath, zipRootFile, zipRoot, entryPath, false, entry));
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
