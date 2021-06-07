package com.loohp.interactivechatdiscordsrvaddon.objectholders;

import net.md_5.bungee.api.ChatColor;

public enum AdvancementType {
	
	TASK(ChatColor.GREEN, "chat.type.advancement.task"), CHALLENGE(ChatColor.DARK_PURPLE, "chat.type.advancement.challenge"), GOAL(ChatColor.GREEN, "chat.type.advancement.goal");
	
	private static final AdvancementType[] VALUES = values();
	
    private ChatColor color;
    private String translationKey;

    private AdvancementType(ChatColor color, String translationKey) {
        this.color = color;
        this.translationKey = translationKey;
    }

    public ChatColor getColor() {
        return this.color;
    }
    
    public String getTranslationKey() {
    	return translationKey;
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
