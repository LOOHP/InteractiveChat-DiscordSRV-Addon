package com.loohp.interactivechatdiscordsrvaddon.Utils;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class CustomImageUtils {
	
	public static BufferedImage additionNonTransparent(BufferedImage image, BufferedImage imageToAdd) {
		for (int y = 0; y < image.getHeight() && y < imageToAdd.getHeight(); y++) {
			for (int x = 0; x < image.getWidth() && x < imageToAdd.getWidth(); x++) {
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
	
	public static BufferedImage multiply(BufferedImage image, BufferedImage imageOnTop) {
		for (int y = 0; y < image.getHeight() && y < imageOnTop.getHeight(); y++) {
			for (int x = 0; x < image.getWidth() && x < imageOnTop.getWidth(); x++) {
				int value = image.getRGB(x, y);
				Color color = new Color(value, true);
				
				int multiplyValue = imageOnTop.getRGB(x, y);
				Color multiplyColor = new Color(multiplyValue, true);
				
				int red = (int) Math.round((double) color.getRed() / 255 * (double) multiplyColor.getRed());
				int green = (int) Math.round((double) color.getGreen() / 255 * (double) multiplyColor.getGreen());
				int blue = (int) Math.round((double) color.getBlue() / 255 * (double) multiplyColor.getBlue());
				color = new Color(red, green, blue, color.getAlpha());
				image.setRGB(x, y, color.getRGB());
			}
		}
		
		return image;
	}
	
	public static BufferedImage changeColorTo(BufferedImage image, Color color) {
		for (int y = 0; y < image.getHeight(); y++) {
			for (int x = 0; x < image.getWidth(); x++) {
				int colorValue = image.getRGB(x, y);
				Color oriColor = new Color(colorValue, true);
				
				Color newColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), oriColor.getAlpha());
				image.setRGB(x, y, newColor.getRGB());
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
	
	public static BufferedImage xor(BufferedImage bottom, BufferedImage top, int alpha) {
		for (int y = 0; y < bottom.getHeight(); y++) {
			for (int x = 0; x < bottom.getWidth(); x++) {
				int bottomValue = bottom.getRGB(x, y);
				Color bottomColor = new Color(bottomValue, true);
				
				int topValue = top.getRGB(x, y);
				Color topColor = new Color(topValue, true);
				
				Color color = new Color(bottomColor.getRed() ^ topColor.getRed(), bottomColor.getGreen() ^ topColor.getGreen(), bottomColor.getBlue() ^ topColor.getBlue(), bottomColor.getAlpha() ^ (topColor.getAlpha() * alpha / 255));
				bottom.setRGB(x, y, color.getRGB());
			}
		}
		return bottom;
	}
	
	public static BufferedImage appendImageRight(BufferedImage source, BufferedImage append, int middleGap, int rightSpace) {
	    BufferedImage b = new BufferedImage(source.getWidth() + append.getWidth() + middleGap + rightSpace, Math.max(source.getHeight(), append.getHeight()), BufferedImage.TYPE_INT_ARGB);
	    Graphics2D g = b.createGraphics();
	    g.drawImage(source, 0, 0, null);
	    g.drawImage(append, source.getWidth() + middleGap, 0, null);
	    g.dispose();
	    return b;
	}
	
	public static BufferedImage copyImage(BufferedImage source){
	    BufferedImage b = new BufferedImage(source.getWidth(), source.getHeight(), BufferedImage.TYPE_INT_ARGB);
	    Graphics2D g = b.createGraphics();
	    g.drawImage(source, 0, 0, null);
	    g.dispose();
	    return b;
	}
	
	public static BufferedImage copyAndGetSubImage(BufferedImage source, int x, int y, int w, int h) {
		BufferedImage img = source.getSubimage(x, y, w, h); //fill in the corners of the desired crop location here
		BufferedImage copyOfImage = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = copyOfImage.createGraphics();
		g.drawImage(img, 0, 0, null);
		g.dispose();
		return copyOfImage;
	}

}
