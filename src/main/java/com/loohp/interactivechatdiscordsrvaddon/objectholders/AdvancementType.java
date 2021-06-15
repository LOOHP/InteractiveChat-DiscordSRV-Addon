package com.loohp.interactivechatdiscordsrvaddon.objectholders;

import net.md_5.bungee.api.ChatColor;

public enum AdvancementType {
	
	TASK(ChatColor.GREEN, "chat.type.advancement.task"),
	CHALLENGE(ChatColor.DARK_PURPLE, "chat.type.advancement.challenge"),
	GOAL(ChatColor.GREEN, "chat.type.advancement.goal"),
	LEGACY(ChatColor.GREEN, "chat.type.achievement", true);
	
	private static final AdvancementType[] VALUES = values();
	
    private ChatColor color;
    private String translationKey;
    private boolean isLegacy;

    AdvancementType(ChatColor color, String translationKey, boolean isLegacy) {
        this.color = color;
        this.translationKey = translationKey;
        this.isLegacy = isLegacy;
    }
    
    AdvancementType(ChatColor color, String translationKey) {
    	this(color, translationKey, false);
    }

    public ChatColor getColor() {
        return this.color;
    }
    
    public String getTranslationKey() {
    	return translationKey;
    }
    
    public boolean isLegacy() {
    	return isLegacy;
    }
    
    public static AdvancementType fromHandle(Object obj) {
    	for (AdvancementType type : VALUES) {
    		if (type.toString().equalsIgnoreCase(obj.toString())) {
    			return type;
    		}
    	}
    	return null;
    }

}
