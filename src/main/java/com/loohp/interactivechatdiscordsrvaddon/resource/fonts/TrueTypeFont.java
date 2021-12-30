package com.loohp.interactivechatdiscordsrvaddon.resource.fonts;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.font.TextAttribute;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.loohp.interactivechat.libs.net.kyori.adventure.text.format.TextColor;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.format.TextDecoration;
import com.loohp.interactivechatdiscordsrvaddon.resource.ResourceManager;

public class TrueTypeFont extends MinecraftFont {
	
	private ResourceManager manager;
	private String resourceLocation;
	private AffineTransform shift;
	private float size;
	private float oversample;
	private String exclude;
	
	private Font font;
	
	public TrueTypeFont(ResourceManager manager, String resourceLocation, AffineTransform shift, float size, float oversample, String exclude) throws Exception {
		this.manager = manager;
		this.resourceLocation = resourceLocation;
		this.shift = shift;
		this.size = size;
		this.oversample = oversample;
		this.exclude = exclude;
		
		try {
    		GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
    	} catch (Throwable e) {
    		throw new RuntimeException("No fonts provided by the JVM or the Operating System!\nCheck the Q&A section in https://www.spigotmc.org/resources/83917/ for more information", e);
    	}
		reloadFonts();
	}
	
	@Override
	public void reloadFonts() {
		try {
			this.font = Font.createFont(Font.TRUETYPE_FONT, manager.getFontManager().getFontResource(resourceLocation).getFile()).deriveFont(shift).deriveFont(size);
			GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(this.font);
		} catch (FontFormatException | IOException e) {
			e.printStackTrace();
		}
	}

	public String getResourceLocation() {
		return resourceLocation;
	}

	public AffineTransform getShift() {
		return shift;
	}

	public float getSize() {
		return size;
	}

	public float getOversample() {
		return oversample;
	}

	public String getExclude() {
		return exclude;
	}

	public Font getFont() {
		return font;
	}
	
	@Override
	public boolean canDisplayCharacter(String character) {
		if (exclude.contains(character)) {
			return false;
		}
		return font.canDisplayUpTo(character) < 0;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public FontRenderResult printCharacter(BufferedImage image, String character, int x, int y, float fontSize, TextColor color, List<TextDecoration> decorations) {
		decorations = sortDecorations(decorations);
		Graphics2D g = image.createGraphics();
		g.setFont(font);
		int w = g.getFontMetrics().stringWidth(character);
		Font fontToPrint = font.deriveFont(fontSize);
		for (TextDecoration decoration : decorations) {
			switch (decoration) {
			case BOLD:
				fontToPrint = fontToPrint.deriveFont(Font.BOLD);
				break;
			case ITALIC:
				fontToPrint = fontToPrint.deriveFont(Font.ITALIC);
				break;
			case STRIKETHROUGH:
				Map attributes = fontToPrint.getAttributes();
				attributes.put(TextAttribute.STRIKETHROUGH, true);
				fontToPrint = new Font(attributes);
				break;
			case UNDERLINED:
				attributes = fontToPrint.getAttributes();
				attributes.put(TextAttribute.UNDERLINE, true);
				fontToPrint = new Font(attributes);
				break;
			default:
				break;
			}
		}
		g.setColor(new Color(color.value()));
		g.setFont(fontToPrint);
		int height = g.getFontMetrics().getHeight() / 2;
		g.drawString(character, x, y + height);
		int newW = g.getFontMetrics().stringWidth(character);
		FontRenderResult result = new FontRenderResult(image, newW, height, Math.round((float) newW / (float) w));
		g.dispose();
		return result;
	}

}
