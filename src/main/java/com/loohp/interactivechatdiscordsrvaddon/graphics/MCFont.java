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
	private static Font standardFont;
	private static Font backupFont = new Font(Font.MONOSPACED, Font.PLAIN, 16);
	
	private static double backupOffsetX = -1.0 / 16.0;
	private static double backupOffsetY = -9.0 / 16.0;

    static {
    	String path = InteractiveChatDiscordSrvAddon.plugin.getDataFolder() + "/assets/font/mcfont.ttf";
        try {
        	File file = new File(path);
            standardFont = Font.createFont(Font.TRUETYPE_FONT, file).deriveFont(18F);
            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(standardFont);
            working = true;
        } catch (Exception e) {
        	Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[ICDiscordSrvAddon] Unable to import font from " + path);
            e.printStackTrace();
        }
    }

    public static Font getStandardFont() {
		return standardFont;
	}

	public static Font getBackupFont() {
		return backupFont;
	}

	public static Font getFont(String text, float fontSize) {
        if (standardFont.canDisplayUpTo(text) == -1) {
            return standardFont.deriveFont(fontSize);
        } else {
            return backupFont.deriveFont(fontSize).deriveFont(AffineTransform.getTranslateInstance(backupOffsetX * fontSize, backupOffsetY * fontSize));
        }
    }
    
    public static boolean isWorking() {
    	return working;
    }
    
}
