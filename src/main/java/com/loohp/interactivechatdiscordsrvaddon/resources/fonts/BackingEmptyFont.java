package com.loohp.interactivechatdiscordsrvaddon.resources.fonts;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.loohp.interactivechatdiscordsrvaddon.resources.ResourceManager;

import java.util.Optional;

public class BackingEmptyFont extends LegacyUnicodeFont {
	
	private static final Map<String, GlyphSize> EMPTY_SIZES = new HashMap<>();
	
	static {
		EMPTY_SIZES.put(" ", new GlyphSize((byte) 0, (byte) 6));
	}
	
	public BackingEmptyFont(ResourceManager manager, FontProvider provider) {
		super(manager, provider, EMPTY_SIZES, null);
	}
	
	@Override
	public void reloadFonts() {
		super.reloadFonts();
		for (Entry<String, GlyphSize> entry : getSizes().entrySet()) {
			String character = entry.getKey();
			GlyphSize size = entry.getValue();
			if (size.getEnd() - size.getStart() > 0) {
				charImages.put(character, Optional.of(new BufferedImage(size.getEnd() - size.getStart() + 1, 16, BufferedImage.TYPE_INT_ARGB)));
			}

		}
	}
	
	@Override
	public boolean canDisplayCharacter(String character) {
		return true;
	}
	
}
