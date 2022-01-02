package com.loohp.interactivechatdiscordsrvaddon.resource.fonts;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.loohp.interactivechat.libs.net.kyori.adventure.text.format.TextColor;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.format.TextDecoration;
import com.loohp.interactivechatdiscordsrvaddon.graphics.ImageUtils;
import com.loohp.interactivechatdiscordsrvaddon.resource.ResourceManager;
import com.loohp.interactivechatdiscordsrvaddon.resource.textures.TextureResource;
import com.loohp.interactivechatdiscordsrvaddon.utils.ComponentStringUtils;

public class LegacyUnicodeFont extends MinecraftFont {
	
	private static final BufferedImage MISSING_CHARACTER = new BufferedImage(6, 16, BufferedImage.TYPE_INT_ARGB);
	
	static {
		Graphics2D g = MISSING_CHARACTER.createGraphics();
		g.setColor(Color.WHITE);
		g.drawRect(1, 1, 4, 14);
		g.dispose();
	}

	private Map<String, GlyphSize> sizes;
	private String template;
	private Map<String, BufferedImage> charImages;
	
	public LegacyUnicodeFont(ResourceManager manager, FontProvider provider, Map<String, GlyphSize> sizes, String template) {
		super(manager, provider);
		this.sizes = sizes;
		this.template = template;
		reloadFonts();
	}
	
	@Override
	public void reloadFonts() {
		this.charImages = new HashMap<>();
		
		for (int i = 0; i < 0x10000; i += 0x100) {
			TextureResource resource = manager.getFontManager().getFontResource(template.replaceFirst("%s", getSectionSubstring(i)));
			if (resource == null) {
				continue;
			}
			BufferedImage fontFileImage = resource.getTexture(256, 256);
			int u = 0;
			for (int y = 0; y < 256; y += 16) {
				for (int x = 0; x < 256; x += 16) {
					String character = new String(Character.toChars(i + u));
					GlyphSize size = sizes.get(character);
					if (size.getEnd() - size.getStart() > 0) {
						charImages.put(character, ImageUtils.copyAndGetSubImage(fontFileImage, x + size.getStart(), y, size.getEnd() - size.getStart() + 1, 16));
					}
					u++;
				}
			}
		}
	}
	
	public static String getSectionSubstring(int i) {
		return String.format("%04x", i).substring(0, 2);
	}

	public Set<String> getCharacterSets() {
		return charImages.keySet();
	}
	
	public Map<String, GlyphSize> getSizes() {
		return sizes;
	}

	public String getTemplate() {
		return template;
	}
	
	@Override
	public boolean canDisplayCharacter(String character) {
		return true;
	}

	@Override
	public FontRenderResult printCharacter(BufferedImage image, String character, int x, int y, float fontSize, TextColor color, List<TextDecoration> decorations) {
		decorations = sortDecorations(decorations);
		Color awtColor = new Color(color.value());
		BufferedImage charImage = ImageUtils.copyImage(charImages.getOrDefault(character, ImageUtils.copyImage(MISSING_CHARACTER)));
		int originalW = charImage.getWidth();
		charImage = ImageUtils.resizeImageFillHeight(charImage, Math.round(fontSize));
		int w = charImage.getWidth();
		int h = charImage.getHeight();
		charImage = ImageUtils.changeColorTo(charImage, awtColor);
		int beforeTransformW = w;
		int pixelSize = Math.round((float) beforeTransformW / (float) originalW);
		int strikeSize = (int) (fontSize / 8);
		for (TextDecoration decoration : decorations) {
			switch (decoration) {
			case OBFUSCATED:
				Graphics2D g = charImage.createGraphics();
				for (int i = 0; i < OBFUSCATE_OVERLAP_COUNT; i++) {
					String magicCharater = ComponentStringUtils.toMagic(provider, character);
					BufferedImage magicImage = provider.forCharacter(magicCharater).getCharacterImage(magicCharater, fontSize, color);
					g.drawImage(magicImage, 0, 0, charImage.getWidth(), charImage.getHeight(), null);
				}
				g.dispose();
				break;
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
				g = italicImage.createGraphics();
				g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
				g.transform(AffineTransform.getShearInstance(-4.0 / 14.0, 0));
				g.drawImage(charImage, extraWidth, 0, null);
				g.dispose();
				charImage = italicImage;
				break;
			case STRIKETHROUGH:
				charImage = ImageUtils.expandCenterAligned(charImage, 0, 0, 0, pixelSize);
				g = charImage.createGraphics();
				g.setColor(awtColor);
				g.fillRect(0, (int) (fontSize / 2), charImage.getWidth(), strikeSize);
				g.dispose();
				break;
			case UNDERLINED:
				charImage = ImageUtils.expandCenterAligned(charImage, 0, strikeSize * 2, 0, pixelSize);
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
		g.drawImage(charImage, x, y, null);
		g.dispose();
		return new FontRenderResult(image, w, h, pixelSize);
	}
	
	@Override
	public BufferedImage getCharacterImage(String character, float fontSize, TextColor color) {
		Color awtColor = new Color(color.value());
		BufferedImage charImage = ImageUtils.copyImage(charImages.getOrDefault(character, ImageUtils.copyImage(MISSING_CHARACTER)));
		charImage = ImageUtils.resizeImageFillHeight(charImage, Math.round(fontSize));
		charImage = ImageUtils.changeColorTo(charImage, awtColor);
		return charImage;
	}
	
	@Override
	public Collection<String> getDisplayableCharacters() {
		return Collections.unmodifiableSet(charImages.keySet());
	}
	
	public static class GlyphSize {
		
		private byte start;
		private byte end;
		
		public GlyphSize(byte start, byte end) {
			this.start = start;
			this.end = end;
		}
		
		public byte getStart() {
			return start;
		}
		
		public byte getEnd() {
			return end;
		}
		
	}
	
}
