package com.loohp.interactivechatdiscordsrvaddon.resources.textures;

import com.loohp.interactivechatdiscordsrvaddon.resources.ResourcePackFile;

public class TextureMeta extends TextureResource {
	
	private TextureAnimation animation;
	private TextureProperties properties;
	
	public TextureMeta(TextureManager manager, String resourceKey, ResourcePackFile file, TextureAnimation animation, TextureProperties properties) {
		super(manager, resourceKey, file, false);
		this.animation = animation;
		this.properties = properties;
	}

	public TextureAnimation getAnimation() {
		return animation;
	}

	public TextureProperties getProperties() {
		return properties;
	}
	
	public boolean hasAnimation() {
		return animation != null;
	}

	public boolean hasProperties() {
		return properties != null;
	}
	
	@Override
	public boolean isTextureMeta() {
		return true;
	}
	
	@Override
	public boolean hasTextureMeta() {
		return false;
	}
	
	@Override
	public TextureMeta getTextureMeta() {
		return null;
	}

}
