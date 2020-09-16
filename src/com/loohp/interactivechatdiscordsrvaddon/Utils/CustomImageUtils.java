package com.loohp.interactivechatdiscordsrvaddon.Utils;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class CustomImageUtils {
	
	public static BufferedImage additionNonTransparent(BufferedImage image, BufferedImage imageToAdd) {
		if (imageToAdd.getHeight() < image.getHeight() || imageToAdd.getWidth() < image.getHeight()) {
			return image;
		}
		for (int y = 0; y < image.getHeight(); y++) {
			for (int x = 0; x < image.getWidth(); x++) {
				int value = image.getRGB(x, y);
				Color color = new Color(value, true);
				
				int addValue = imageToAdd.getRGB(x, y);
				Color addColor = new Color(addValue, true);
				if (color.getAlpha() != 0) {
					int red = color.getRed() + addColor.getRed();
					int green = color.getGreen() + addColor.getGreen();
					int blue = color.getBlue() + addColor.getBlue();
					color = new Color(red > 255 ? 255 : red, green > 255 ? 255 : green, blue > 255 ? 255 : blue, color.getAlpha());
					image.setRGB(x, y, color.getRGB());
				}
			}
		}
		return image;
	}
	
	public static BufferedImage darken(BufferedImage image, int value) {
		for (int y = 0; y < image.getHeight(); y++) {
			for (int x = 0; x < image.getWidth(); x++) {
				int colorValue = image.getRGB(x, y);
				Color color = new Color(colorValue, true);
				
				if (color.getAlpha() != 0) {
					int red = color.getRed() - value;
					int green = color.getGreen() - value;
					int blue = color.getBlue() - value;
					color = new Color(red < 0 ? 0 : red, green < 0 ? 0 : green, blue < 0 ? 0 : blue, color.getAlpha());
					image.setRGB(x, y, color.getRGB());
				}
			}
		}
		return image;
	}
	
	public static BufferedImage tint(BufferedImage image, Color tint) {
		for (int y = 0; y < image.getHeight(); y++) {
			for (int x = 0; x < image.getWidth(); x++) {
				int colorValue = image.getRGB(x, y);
				Color color = new Color(colorValue, true);
				
				if (color.getAlpha() != 0) {
					int red = color.getRed() + tint.getRed();
					int green = color.getGreen() + tint.getGreen();
					int blue = color.getBlue() + tint.getBlue();
					color = new Color(red > 255 ? 255 : red, green > 255 ? 255 : green, blue > 255 ? 255 : blue, color.getAlpha());
					image.setRGB(x, y, color.getRGB());
				}
			}
		}
		return image;
	}
	
	public static BufferedImage raiseAlpha(BufferedImage image, int value) {
		for (int y = 0; y < image.getHeight(); y++) {
			for (int x = 0; x < image.getWidth(); x++) {
				int colorValue = image.getRGB(x, y);
				Color color = new Color(colorValue, true);
				
				int alpha = color.getAlpha() + value;
				color = new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha > 255 ? 255 : (alpha < 0 ? 0 : alpha));
				image.setRGB(x, y, color.getRGB());
			}
		}
		return image;
	}
	
	public static BufferedImage squarify(BufferedImage image) {
		if (image.getHeight() == image.getWidth()) {
			return image;
		}
		int size = Math.max(image.getHeight(), image.getWidth());
		int offsetX = (size - image.getWidth()) / 2;
		int offsetY = (size - image.getHeight()) / 2;
		
		BufferedImage newImage = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
		
		for (int y = 0; y < image.getHeight(); y++) {
			for (int x = 0; x < image.getWidth(); x++) {
				int colorValue = image.getRGB(x, y);
				newImage.setRGB(x + offsetX, y + offsetY, colorValue);
			}
		}
		return newImage;
	}
	
	public static BufferedImage copyImage(BufferedImage source){
	    BufferedImage b = new BufferedImage(source.getWidth(), source.getHeight(), source.getType());
	    Graphics g = b.getGraphics();
	    g.drawImage(source, 0, 0, null);
	    g.dispose();
	    return b;
	}

}
