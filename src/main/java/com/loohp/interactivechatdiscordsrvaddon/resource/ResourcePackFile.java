package com.loohp.interactivechatdiscordsrvaddon.resource;

import java.io.InputStream;
import java.util.Collection;

public interface ResourcePackFile extends AutoCloseable {
	
	public String getName();

	public String getParent();

	public ResourcePackFile getParentFile();
	
	public String getAbsolutePath();

	public String getPath();

	public boolean exists();

	public boolean isDirectory();

	public Collection<ResourcePackFile> listFilesAndFolders();
	
	public ResourcePackFile getChild(String name);
	
	public InputStream getInputStream();
	
	public default Collection<ResourcePackFile> listFilesRecursively() {
		return listFilesRecursively(null);
	}
	
	public Collection<ResourcePackFile> listFilesRecursively(String[] extensions);
	
	@Override
	public void close();

}
