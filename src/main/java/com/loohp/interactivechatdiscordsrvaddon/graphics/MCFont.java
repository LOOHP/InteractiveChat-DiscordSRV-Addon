package com.loohp.interactivechatdiscordsrvaddon.graphics;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.geom.AffineTransform;
import java.io.File;

import org.bukkit.Bukkit;

import com.loohp.interactivechatdiscordsrvaddon.InteractiveChatDiscordSrvAddon;

import net.md_5.bungee.api.ChatColor;

public class MCFont {
	
	private static boolean working = false;
	private static Font mcFont;
	private static Font uniFont;
	
	private static double uniFontOffsetX = -1.0 / 16.0;
	private static double uniFontOffsetY = -3.5 / 16.0;

    static {
    	reloadFonts();
    }
    
    public synchronized static void reloadFonts() {
    	String mcfontPath = InteractiveChatDiscordSrvAddon.plugin.getDataFolder() + "/assets/font/mcfont.ttf";
    	String unifontPath = InteractiveChatDiscordSrvAddon.plugin.getDataFolder() + "/assets/font/unifont.ttf";
    	
        try {
        	File mcfontFile = new File(mcfontPath);
            mcFont = Font.createFont(Font.TRUETYPE_FONT, mcfontFile).deriveFont(18F);
            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(mcFont);
            
            File unifontFile = new File(unifontPath);
            uniFont = Font.createFont(Font.TRUETYPE_FONT, unifontFile).deriveFont(16F);
            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(uniFont);
            
            working = true;
        } catch (Exception e) {
        	Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[ICDiscordSrvAddon] Unable to import font from " + mcfontPath);
            e.printStackTrace();
        }
    }

    public static Font getStandardFont() {
		return mcFont;
	}

	public static Font getBackupFont() {
		return uniFont;
	}

	public static Font getFont(String text, float fontSize) {
        if (mcFont.canDisplayUpTo(text) == -1) {
            return mcFont.deriveFont(fontSize);
        } else {
            return uniFont.deriveFont(fontSize).deriveFont(AffineTransform.getTranslateInstance(uniFontOffsetX * fontSize, uniFontOffsetY * fontSize));
        }
    }
    
    public static boolean isWorking() {
    	return working;
    }
    
}
