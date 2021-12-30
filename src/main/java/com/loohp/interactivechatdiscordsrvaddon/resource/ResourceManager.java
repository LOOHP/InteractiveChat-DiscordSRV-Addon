package com.loohp.interactivechatdiscordsrvaddon.resource;

import java.io.File;

import com.loohp.interactivechatdiscordsrvaddon.resource.fonts.FontManager;
import com.loohp.interactivechatdiscordsrvaddon.resource.models.ModelManager;
import com.loohp.interactivechatdiscordsrvaddon.resource.textures.TextureManager;

public class ResourceManager {
	
	private ModelManager modelManager;
	private TextureManager textureManager;
	private FontManager fontManager;
	
	public ResourceManager() {
		this.modelManager = new ModelManager(this);
		this.textureManager = new TextureManager(this);
		this.fontManager = new FontManager(this);
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
				File font = new File(folder, "font");
				if (font.exists() && font.isDirectory()) {
					fontManager.loadDirectory(namespace, font);
				}
			}
		}
		fontManager.reloadFonts();
	}

	public ModelManager getModelManager() {
		return modelManager;
	}

	public TextureManager getTextureManager() {
		return textureManager;
	}

	public FontManager getFontManager() {
		return fontManager;
	}

}
