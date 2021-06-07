package com.loohp.interactivechatdiscordsrvaddon.graphics;

import java.awt.image.BufferedImage;

public class ImageFrame {
	
	private final int delay;
	private final BufferedImage image;
	private final int width;
	private final int height;

	public ImageFrame(BufferedImage image, int delay, int width, int height) {
		this.image = image;
		this.delay = delay;
		this.width = width;
		this.height = height;
	}

	public ImageFrame(BufferedImage image) {
		this.image = image;
		this.delay = 0;
		this.width = image.getWidth();
		this.height = image.getHeight();
	}

	public BufferedImage getImage() {
		return image;
	}

	public int getDelay() {
		return delay;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
}
