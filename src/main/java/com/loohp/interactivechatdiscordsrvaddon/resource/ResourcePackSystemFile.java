package com.loohp.interactivechatdiscordsrvaddon.resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import com.loohp.interactivechat.libs.org.apache.commons.io.FileUtils;

public class ResourcePackSystemFile implements ResourcePackFile {
	
	private File file;

	public ResourcePackSystemFile(File file) {
		this.file = file;
	}

	@Override
	public String getName() {
		return file.getName();
	}

	@Override
	public String getParent() {
		return file.getParent();
	}

	@Override
	public ResourcePackFile getParentFile() {
		return new ResourcePackSystemFile(file.getParentFile());
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
		return Arrays.asList(file.listFiles()).stream().map(each -> new ResourcePackSystemFile(each)).collect(Collectors.toSet());
	}

	@Override
	public String getAbsolutePath() {
		return file.getAbsolutePath();
	}

	@Override
	public ResourcePackFile getChild(String name) {
		return new ResourcePackSystemFile(new File(file, name));
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
			if (other.file != null) {
				return false;
			}
		} else if (!file.equals(other.file)) {
			return false;
		}
		return true;
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
		return FileUtils.listFiles(file, extensions, true).stream().map(each -> new ResourcePackSystemFile(each)).collect(Collectors.toList());
	}

	@Override
	public void close() {
		
	}

}
