package com.loohp.interactivechatdiscordsrvaddon.resources;

public abstract class AbstractManager {
	
	protected ResourceManager manager;
	
	public AbstractManager(ResourceManager manager) {
		this.manager = manager;
	}
	
	public ResourceManager getManager() {
		return manager;
	}
	
	public boolean isValid() {
		return manager.isValid();
	}

	protected abstract void loadDirectory(String namespace, ResourcePackFile root);

}
