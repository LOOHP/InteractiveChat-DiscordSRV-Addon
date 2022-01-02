package com.loohp.interactivechatdiscordsrvaddon.resource.fonts;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.loohp.interactivechat.libs.net.kyori.adventure.text.format.TextColor;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.format.TextDecoration;
import com.loohp.interactivechatdiscordsrvaddon.resource.ResourceManager;

public abstract class MinecraftFont {
	
	public static final int OBFUSCATE_OVERLAP_COUNT = 2;
	private static final List<TextDecoration> DECORATIONS_ORDER = new ArrayList<>();
	
	static {
		DECORATIONS_ORDER.add(TextDecoration.OBFUSCATED);
		DECORATIONS_ORDER.add(TextDecoration.BOLD);
		DECORATIONS_ORDER.add(TextDecoration.ITALIC);
		DECORATIONS_ORDER.add(TextDecoration.STRIKETHROUGH);
		DECORATIONS_ORDER.add(TextDecoration.UNDERLINED);
	}
	
	protected ResourceManager manager;
	protected FontProvider provider;
	
	public MinecraftFont(ResourceManager manager, FontProvider provider) {
		this.manager = manager;
		this.provider = provider;
	}
	
	protected void setProvider(FontProvider provider) {
		this.provider = provider;
	}
	
	public static List<TextDecoration> sortDecorations(List<TextDecoration> decorations) {
		List<TextDecoration> list = new ArrayList<>(decorations.size());
		for (TextDecoration decoration : DECORATIONS_ORDER) {
			if (decorations.contains(decoration)) {
				list.add(decoration);
			}
		}
		return list;
	}
	
	public abstract boolean canDisplayCharacter(String character);
	
	public abstract FontRenderResult printCharacter(BufferedImage image, String character, int x, int y, float fontSize, TextColor color, List<TextDecoration> decorations);
	
	public abstract void reloadFonts();
	
	public abstract BufferedImage getCharacterImage(String character, float fontSize, TextColor color);
	
	public abstract Collection<String> getDisplayableCharacters();
	
	public static class FontRenderResult {
		
		private BufferedImage image;
		private int width;
		private int height;
		private int spaceWidth;
		
		public FontRenderResult(BufferedImage image, int width, int height, int spaceWidth) {
			this.image = image;
			this.width = width;
			this.height = height;
			this.spaceWidth = spaceWidth;
		}

		public BufferedImage getImage() {
			return image;
		}

		public int getWidth() {
			return width;
		}

		public int getHeight() {
			return height;
		}
		
		public int getSpaceWidth() {
			return spaceWidth;
		}
		
	}
	
}
