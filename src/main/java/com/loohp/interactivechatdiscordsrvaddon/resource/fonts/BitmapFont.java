package com.loohp.interactivechatdiscordsrvaddon.resource.fonts;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.loohp.interactivechat.libs.net.kyori.adventure.text.format.TextColor;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.format.TextDecoration;
import com.loohp.interactivechatdiscordsrvaddon.graphics.ImageUtils;
import com.loohp.interactivechatdiscordsrvaddon.resource.ResourceManager;

public class BitmapFont extends MinecraftFont {
	
	private ResourceManager manager;
	private String resourceLocation;
	private int height;
	private int ascent;
	private int scale;
	private List<String> chars;
	private Map<String, BufferedImage> charImages;
	
	public BitmapFont(ResourceManager manager, String resourceLocation, int height, int ascent, List<String> chars) {
		this.manager = manager;
		this.resourceLocation = resourceLocation;
		this.height = height;
		this.ascent = ascent;
		this.chars = chars;
		reloadFonts();
	}
	
	@Override
	public void reloadFonts() {
		this.charImages = new HashMap<>();
		if (chars.isEmpty()) {
			return;
		}
		
		BufferedImage fontFileImage = manager.getFontManager().getFontResource(resourceLocation).getTexture();
		
		int yIncrement = fontFileImage.getHeight() / chars.size();
		this.scale = yIncrement / height;
		int y = 0;
		for (String line : chars) {
			if (!line.isEmpty()) {
				int xIncrement = fontFileImage.getWidth() / line.length();
				int x = 0;
				for (int i = 0; i < line.length(); i++) {
					String character = line.substring(i, i + 1);
					int lastX = 3 * scale;
					for (int x0 = x; x0 < x + xIncrement; x0++) {
						for (int y0 = y; y0 < y + yIncrement; y0++) {
							int alpha = (fontFileImage.getRGB(x0, y0) >> 24) & 0xff;
							if (alpha != 0) {
								lastX = x0 - x + 1;
								break;
							}
						}
					}
					if (lastX > 0) {
						charImages.put(character, ImageUtils.copyAndGetSubImage(fontFileImage, x, y, lastX, yIncrement));
					}
					x += xIncrement;
				}
			}
			y += yIncrement;
		}
	}

	public String getResourceLocation() {
		return resourceLocation;
	}

	public int getHeight() {
		return height;
	}

	public int getAscent() {
		return ascent;
	}
	
	public int getScale() {
		return scale;
	}

	public List<String> getChars() {
		return chars;
	}

	@Override
	public boolean canDisplayCharacter(String character) {
		return charImages.containsKey(character);
	}

	@Override
	public FontRenderResult printCharacter(BufferedImage image, String character, int x, int y, float fontSize, TextColor color, List<TextDecoration> decorations) {
		decorations = sortDecorations(decorations);
		Color awtColor = new Color(color.value());
		BufferedImage charImage = ImageUtils.copyImage(charImages.get(character));
		int originalW = charImage.getWidth();
		float scale = fontSize / 8;
		float ascent = this.ascent - 7;
		float descent = height - this.ascent - 1;
		charImage = ImageUtils.resizeImageFillHeight(charImage, Math.round(fontSize + (ascent + descent) * scale));
		int w = charImage.getWidth();
		int h = charImage.getHeight();
		charImage = ImageUtils.changeColorTo(charImage, awtColor);
		int beforeTransformW = w;
		int pixelSize = Math.round((float) beforeTransformW / (float) originalW);
		int strikeSize = (int) (fontSize / 8);
		for (TextDecoration decoration : decorations) {
			switch (decoration) {
			case BOLD:
				BufferedImage boldImage = new BufferedImage(charImage.getWidth() + 2, charImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
				for (int x0 = 0; x0 < charImage.getWidth(); x0++) {
					for (int y0 = 0; y0 < charImage.getHeight(); y0++) {
						int pixelColor = charImage.getRGB(x0, y0);
						int alpha = (pixelColor >> 24) & 0xff;
						if (alpha != 0) {
							boldImage.setRGB(x0, y0, pixelColor);
							boldImage.setRGB(x0 + 1, y0, pixelColor);
							boldImage.setRGB(x0 + 2, y0, pixelColor);
						}
					}
				}
				charImage = boldImage;
				w += 2;
				break;
			case ITALIC:
				int extraWidth = (int) ((double) charImage.getHeight() * (4.0 / 14.0));
				BufferedImage italicImage = new BufferedImage(charImage.getWidth() + extraWidth * 2, charImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
				Graphics2D g = italicImage.createGraphics();
				g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
				g.transform(AffineTransform.getShearInstance(-4.0 / 14.0, 0));
				g.drawImage(charImage, extraWidth, 0, null);
				g.dispose();
				charImage = italicImage;
				break;
			case STRIKETHROUGH:
				charImage = ImageUtils.expandCenterAligned(charImage, 0, 0, 0, pixelSize * this.scale);
				g = charImage.createGraphics();
				g.setColor(awtColor);
				g.fillRect(0, (int) (fontSize / 2), charImage.getWidth(), strikeSize);
				g.dispose();
				break;
			case UNDERLINED:
				charImage = ImageUtils.expandCenterAligned(charImage, 0, strikeSize * 2, 0, pixelSize * this.scale);
				g = charImage.createGraphics();
				g.setColor(awtColor);
				g.fillRect(0, (int) (fontSize), charImage.getWidth(), strikeSize);
				g.dispose();
				break;
			default:
				break;
			}
		}
		Graphics2D g = image.createGraphics();
		g.drawImage(charImage, x, (int) (y - ascent * scale), null);
		g.dispose();
		return new FontRenderResult(image, w, h, pixelSize * this.scale);
	}

}
