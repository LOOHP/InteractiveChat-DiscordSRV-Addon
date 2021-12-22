package com.loohp.interactivechatdiscordsrvaddon.resource;

import java.io.File;

import com.loohp.interactivechatdiscordsrvaddon.resource.models.ModelManager;
import com.loohp.interactivechatdiscordsrvaddon.resource.texture.TextureManager;

public class ResourceManager {
	
	private ModelManager modelManager;
	private TextureManager textureManager;
	
	public ResourceManager() {
		this.modelManager = new ModelManager();
		this.textureManager = new TextureManager();
	}
	
	public void loadResources(File resourcePack) {
		if (!resourcePack.exists() || !resourcePack.isDirectory()) {
			throw new IllegalArgumentException(resourcePack.getAbsolutePath() + " is not a directory.");
		}
		for (File folder : resourcePack.listFiles()) {
			if (folder.isDirectory()) {
				String namespace = folder.getName();
				File models = new File(folder, "models");
				if (models.exists() && models.isDirectory()) {
					modelManager.loadDirectory(namespace, models);
				}
				File textures = new File(folder, "textures");
				if (textures.exists() && textures.isDirectory()) {
					textureManager.loadDirectory(namespace, textures);
				}
			}
		}
	}

	public ModelManager getModelManager() {
		return modelManager;
	}

	public TextureManager getTextureManager() {
		return textureManager;
	}

}
