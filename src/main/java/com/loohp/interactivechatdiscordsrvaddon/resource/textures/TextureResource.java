package com.loohp.interactivechatdiscordsrvaddon.resource.textures;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.loohp.interactivechatdiscordsrvaddon.graphics.ImageUtils;

public class TextureResource {
	
	public static final String PNG_MCMETA_SUFFIX = ".png.mcmeta";
	public static final String MCMETA_SUFFIX = ".mcmeta";
	
	private TextureManager manager;
	private String resourceKey;
	private File file;
	private boolean isTexture;
	private BufferedImage texture;

	public TextureResource(TextureManager manager, String resourceKey, File file, boolean isTexture) {
		this.manager = manager;
		this.resourceKey = resourceKey;
		this.file = file;
		this.isTexture = isTexture;
		this.texture = null;
	}
	
	public TextureResource(TextureManager manager, String resourceKey, File file, BufferedImage image) {
		this.manager = manager;
		this.resourceKey = resourceKey;
		this.file = file;
		this.isTexture = true;
		this.texture = image;
	}
	
	public TextureResource(TextureManager manager, String resourceKey, File file) {
		this(manager, resourceKey, file, false);
	}
	
	private void loadImage() {
		if (isTexture && texture == null) {
			try {
				this.texture = ImageIO.read(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public boolean isTexture() {
		return isTexture;
	}
	
	public BufferedImage getTexture(int w, int h) {
		loadImage();
		return ImageUtils.toCompatibleImage(ImageUtils.resizeImageAbs(texture, w, h));
	}

	public BufferedImage getTexture() {
		loadImage();
		return ImageUtils.toCompatibleImage(ImageUtils.copyImage(texture));
	}
	
	public boolean hasFile() {
		return file != null;
	}
	
	public File getFile() {
		return file;
	}
	
	public boolean isTextureMeta() {
		return false;
	}
	
	public boolean hasTextureMeta() {
		if (resourceKey == null || manager == null) {
			return false;
		}
		TextureResource meta = manager.getTexture(resourceKey.contains(".") ? resourceKey + MCMETA_SUFFIX : resourceKey + PNG_MCMETA_SUFFIX, false);
		return meta != null && meta.isTextureMeta();
	}
	
	public TextureMeta getTextureMeta() {
		if (resourceKey == null || manager == null) {
			return null;
		}
		TextureResource meta = manager.getTexture(resourceKey.contains(".") ? resourceKey + MCMETA_SUFFIX : resourceKey + PNG_MCMETA_SUFFIX, false);
		if (meta != null && meta.isTextureMeta()) {
			return (TextureMeta) meta;
		}
		return null;
	}

}
