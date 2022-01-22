package com.loohp.interactivechatdiscordsrvaddon.objectholders;

import com.loohp.interactivechat.libs.net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;

public class AdvancementData {

    private Component title;
    private Component description;
    private ItemStack item;
    private AdvancementType advancementType;
    private boolean isMinecraft;

    public AdvancementData(Component title, Component description, ItemStack item, AdvancementType advancementType, boolean isMinecraft) {
        this.title = title;
        this.description = description;
        this.item = item;
        this.advancementType = advancementType;
        this.isMinecraft = isMinecraft;
    }

    public Component getTitle() {
        return title;
    }

    public Component getDescription() {
        return description;
    }

    public ItemStack getItem() {
        return item;
    }

    public AdvancementType getAdvancementType() {
        return advancementType;
    }

    public boolean isMinecraft() {
        return isMinecraft;
    }

}
