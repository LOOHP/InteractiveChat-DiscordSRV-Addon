package com.loohp.interactivechatdiscordsrvaddon.resource;

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
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

public class ResourcePackZipEntryFile implements ResourcePackFile {
	
	private ZipFile zipRoot;
	private String path;
	private boolean isDirectory;
	private ZipEntry entry;
	
	public ResourcePackZipEntryFile(File resourcePackZip) throws ZipException, IOException {
		this.zipRoot = new ZipFile(resourcePackZip);
		this.path = "";
		this.isDirectory = true;
		this.entry = null;
	}

	public ResourcePackZipEntryFile(ZipFile zipRoot, String path, boolean isDirectory, ZipEntry entry) {
		this.zipRoot = zipRoot;
		this.path = path;
		this.isDirectory = isDirectory;
		this.entry = entry;
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
		return new ResourcePackZipEntryFile(zipRoot, getParent(), true, null);
	}

	@Override
	public String getPath() {
		return path;
	}

	@Override
	public String getAbsolutePath() {
		String path = getPath();
		if (!path.startsWith("/")) {
			path = "/" + path;
		}
		String prepend = zipRoot.getName();
		if (prepend.contains("\\")) {
			path = path.replace("/", "\\");
		}
		return prepend + path;
	}

	@Override
	public boolean exists() {
		return isDirectory || entry != null;
	}

	@Override
	public boolean isDirectory() {
		return isDirectory;
	}

	@Override
	public Collection<ResourcePackFile> listFiles() {
		Set<ResourcePackFile> set = new HashSet<>();
		Enumeration<? extends ZipEntry> itr = zipRoot.entries();
		while (itr.hasMoreElements()) {
			ZipEntry entry = itr.nextElement();
			String entryPath = entry.getName();
			if (!entryPath.equals(path) && entryPath.startsWith(path)) {
				String relativePath = entryPath.replace(path, "");
				if (relativePath.startsWith("/")) {
					relativePath = relativePath.substring(1);
				}
				if (relativePath.contains("/")) {
					String folderName = relativePath.substring(0, relativePath.indexOf("/"));
					set.add(new ResourcePackZipEntryFile(zipRoot, (path.isEmpty() ? path : (path + "/")) + folderName, true, null));
				} else {
					set.add(new ResourcePackZipEntryFile(zipRoot, entryPath, false, entry));
				}
			}
		}
		return set;
	}

	@Override
	public ResourcePackFile getChild(String name) {
		for (ResourcePackFile entry : listFiles()) {
			ResourcePackZipEntryFile zipEntryFile = (ResourcePackZipEntryFile) entry;
			if (zipEntryFile.path.equals((path.isEmpty() ? path : (path + "/")) + name)) {
				return zipEntryFile;
			}
		}
		return new ResourcePackZipEntryFile(zipRoot, (path.isEmpty() ? path : (path + "/")) + name, false, null);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((path == null) ? 0 : path.hashCode());
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
		if (path == null) {
			if (other.path != null) {
				return false;
			}
		} else if (!path.equals(other.path)) {
			return false;
		}
		if (zipRoot == null) {
			if (other.zipRoot != null) {
				return false;
			}
		} else if (!zipRoot.equals(other.zipRoot)) {
			return false;
		}
		return true;
	}

	@Override
	public InputStream getInputStream() {
		try {
			return zipRoot.getInputStream(entry);
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
			String entryPath = entry.getName();
			if (!entryPath.equals(path) && entryPath.startsWith(path)) {
				if (extensions == null || Stream.of(extensions).anyMatch(each -> entryPath.endsWith("." + each))) {
					list.add(new ResourcePackZipEntryFile(zipRoot, entryPath, false, entry));
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
			e.printStackTrace();
		}
	}

}
