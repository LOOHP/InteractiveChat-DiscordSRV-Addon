package com.loohp.interactivechatdiscordsrvaddon.graphics;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;

import com.loohp.interactivechat.libs.org.json.simple.JSONArray;
import com.loohp.interactivechat.libs.org.json.simple.JSONObject;
import com.loohp.interactivechat.libs.org.json.simple.parser.JSONParser;
import com.loohp.interactivechatdiscordsrvaddon.InteractiveChatDiscordSrvAddon;

import net.md_5.bungee.api.ChatColor;

public class MCFont {
	
	private static boolean working = false;
	
	private static final Map<String, MCFont> FONTS = new LinkedHashMap<>();
	private static final List<String> ORDER = new ArrayList<>();
    
    public synchronized static void reloadFonts() {
    	try {
    		GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
    	} catch (Throwable e) {
    		throw new RuntimeException("No fonts provided by the JVM or the Operating System!\nCheck the Q&A section in https://www.spigotmc.org/resources/83917/ for more information", e);
    	}
        try {
        	File fontFolder = new File(InteractiveChatDiscordSrvAddon.plugin.getDataFolder() + "/assets/font");
        	if (!fontFolder.exists()) {
        		working = false;
        		return;
        	}
        	File fontDataFile = new File(fontFolder, "font.json");
        	if (!fontDataFile.exists()) {
        		working = false;
        		return;
        	}
        	InputStreamReader reader = new InputStreamReader(new FileInputStream(fontDataFile), StandardCharsets.UTF_8);
        	JSONObject json = (JSONObject) new JSONParser().parse(reader);
        	reader.close();
        	
        	Map<String, MCFont> fonts = new LinkedHashMap<>();
        	List<String> order = new ArrayList<>();
        	
        	int i = 0;
        	for (Object obj : (JSONArray) json.get("providers")) {
        		i++;
        		try {
	        		JSONObject provider = (JSONObject) obj;
	        		String identifier = provider.get("identifier").toString();
	                int type = getFontType(provider.get("type").toString());
	                File file = new File(fontFolder, provider.get("file").toString());
	                float size = (float) (double) provider.get("size");
	                JSONArray offsetArray = (JSONArray) provider.get("offset");
	                double offsetX = (double) offsetArray.get(0);
	                double offsetY = (double) offsetArray.get(1);
	                
	                Font font = Font.createFont(type, file).deriveFont(size);
	                GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(font);
	                
	                MCFont mcfont = new MCFont(identifier, type, file, size, offsetX, offsetY, font);
	                fonts.put(identifier, mcfont);
	                order.add(identifier);
        		} catch (Throwable e) {
        			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[ICDiscordSrvAddon] Unable to load font provider " + i);
        			e.printStackTrace();
        		}
        	}
        	
        	Bukkit.getScheduler().runTask(InteractiveChatDiscordSrvAddon.plugin, () -> {
        		FONTS.clear();
        		FONTS.putAll(fonts);
            	ORDER.clear();
            	ORDER.addAll(order);
        	});

            working = true;
        } catch (Throwable e) {
        	Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[ICDiscordSrvAddon] Unable to load fonts");
            e.printStackTrace();
            working = false;
        }
    }

    public static MCFont getMCFont(String id) {
		return FONTS.get(id);
	}

	public static Font getFont(String text, float fontSize) {
		for (int i = 0; i < ORDER.size(); i++) {
			MCFont mcfont = FONTS.get(ORDER.get(i));
	        if (mcfont.getFont().canDisplayUpTo(text) == -1 || i >= ORDER.size() - 1) {
	            Font font = mcfont.getFont().deriveFont(fontSize);
	            if (mcfont.getOffsetX() != 0 || mcfont.getOffsetY() != 0) {
	            	return font.deriveFont(AffineTransform.getTranslateInstance(mcfont.getOffsetX() * fontSize, mcfont.getOffsetY() * fontSize));
	            } else {
	            	return font;
	            }
	        }
		}
		return null;
    }
    
    public static boolean isWorking() {
    	return working;
    }
    
    private static int getFontType(String name) {
    	switch (name.toUpperCase()) {
    	case "TRUETYPE_FONT":
    		return Font.TRUETYPE_FONT;
    	case "TYPE1_FONT":
    		return Font.TYPE1_FONT;
    	default:
    		return -1;
    	}
    }
    
    private String identifier;
    private int type;
    private File file;
    private float size;
    private double offsetX;
    private double offsetY;
    private Font font;
    
	public MCFont(String identifier, int type, File file, float size, double offsetX, double offsetY, Font font) {
		this.identifier = identifier;
		this.type = type;
		this.file = file;
		this.size = size;
		this.offsetX = offsetX;
		this.offsetY = offsetY;
		this.font = font;
	}

	public String getIdentifier() {
		return identifier;
	}

	public int getType() {
		return type;
	}

	public File getFile() {
		return file;
	}

	public float getSize() {
		return size;
	}

	public double getOffsetX() {
		return offsetX;
	}

	public double getOffsetY() {
		return offsetY;
	}

	public Font getFont() {
		return font;
	}
    
}
