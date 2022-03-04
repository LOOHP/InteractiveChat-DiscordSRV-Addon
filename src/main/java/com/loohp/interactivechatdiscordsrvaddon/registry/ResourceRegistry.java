/*
 * This file is part of InteractiveChatDiscordSrvAddon.
 *
 * Copyright (C) 2022. LoohpJames <jamesloohp@gmail.com>
 * Copyright (C) 2022. Contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.loohp.interactivechatdiscordsrvaddon.registry;

public class ResourceRegistry {

    static {
        int resourcePackVersion;
        String itemTextureLocation;
        String blockTextureLocation;
        try {
            Class.forName("org.bukkit.plugin.java.JavaPlugin");
            resourcePackVersion = com.loohp.interactivechatdiscordsrvaddon.utils.ResourcePackUtils.getServerResourcePackVersion();
            itemTextureLocation = com.loohp.interactivechat.InteractiveChat.version.isLegacy() ? "minecraft:items/" : "minecraft:item/";
            blockTextureLocation = com.loohp.interactivechat.InteractiveChat.version.isLegacy() ? "minecraft:blocks/" : "minecraft:block/";
        } catch (ClassNotFoundException | NoClassDefFoundError e) {
            resourcePackVersion = 8;
            itemTextureLocation = "minecraft:item/";
            blockTextureLocation = "minecraft:block/";
        }
        RESOURCE_PACK_VERSION = resourcePackVersion;
        ITEM_TEXTURE_LOCATION = itemTextureLocation;
        BLOCK_TEXTURE_LOCATION = blockTextureLocation;
    }

    public static final String DEFAULT_NAMESPACE = "minecraft";

    public static final int RESOURCE_PACK_VERSION;

    public static final String BANNER_TEXTURE_LOCATION = "minecraft:entity/banner/";
    public static final String SHIELD_TEXTURE_LOCATION = "minecraft:entity/shield/";
    public static final String ITEM_TEXTURE_LOCATION;
    public static final String ARMOR_TEXTURE_LOCATION = "minecraft:models/armor/";
    public static final String BLOCK_TEXTURE_LOCATION;
    public static final String ENTITY_TEXTURE_LOCATION = "minecraft:entity/";
    public static final String MISC_TEXTURE_LOCATION = "minecraft:misc/";
    public static final String GUI_TEXTURE_LOCATION = "minecraft:gui/";
    public static final String MAP_TEXTURE_LOCATION = "minecraft:map/";
    public static final String COLORMAP_TEXTURE_LOCATION = "minecraft:colormap/";
    public static final String IC_BLOCK_TEXTURE_LOCATION = "minecraft:interactivechatdiscordsrvaddon/block/";
    public static final String IC_GUI_TEXTURE_LOCATION = "minecraft:interactivechatdiscordsrvaddon/gui/";
    public static final String IC_MISC_TEXTURE_LOCATION = "minecraft:interactivechatdiscordsrvaddon/misc/";

    public static final String IC_OLD_BASE_BLOCK_MODEL = "minecraft:interactivechatdiscordsrvaddon/block/block";
    public static final String IC_OLD_BASE_ITEM_MODEL = "minecraft:interactivechatdiscordsrvaddon/item/generated";

    public static final String ITEM_MODEL_LOCATION = "minecraft:item/";
    public static final String BUILTIN_ENTITY_MODEL_LOCATION = "minecraft:interactivechatdiscordsrvaddon/builtin_entity/";

    public static final String SKIN_TEXTURE_PLACEHOLDER = "minecraft:interactivechatdiscordsrvaddon/skin";
    public static final String SKIN_FULL_TEXTURE_PLACEHOLDER = "minecraft:interactivechatdiscordsrvaddon/skin_full";
    public static final String BOOTS_TEXTURE_PLACEHOLDER = "minecraft:interactivechatdiscordsrvaddon/boots";
    public static final String LEGGINGS_TEXTURE_PLACEHOLDER = "minecraft:interactivechatdiscordsrvaddon/leggings";
    public static final String CHESTPLATE_TEXTURE_PLACEHOLDER = "minecraft:interactivechatdiscordsrvaddon/chestplate";
    public static final String HELMET_TEXTURE_PLACEHOLDER = "minecraft:interactivechatdiscordsrvaddon/helmet";

    public static final String BANNER_BASE_TEXTURE_PLACEHOLDER = "minecraft:interactivechatdiscordsrvaddon/banner_base";
    public static final String BANNER_PATTERNS_TEXTURE_PLACEHOLDER = "minecraft:interactivechatdiscordsrvaddon/banner_patterns";
    public static final String SHIELD_BASE_TEXTURE_PLACEHOLDER = "minecraft:interactivechatdiscordsrvaddon/shield_base";
    public static final String SHIELD_PATTERNS_TEXTURE_PLACEHOLDER = "minecraft:interactivechatdiscordsrvaddon/shield_patterns";

    public static final String LEGACY_BED_TEXTURE_PLACEHOLDER = "minecraft:interactivechatdiscordsrvaddon/legacy_bed";

    public static final String LEATHER_HELMET_PLACEHOLDER = ITEM_TEXTURE_LOCATION + "leather_helmet";
    public static final String LEATHER_CHESTPLATE_PLACEHOLDER = ITEM_TEXTURE_LOCATION + "leather_chestplate";
    public static final String LEATHER_LEGGINGS_PLACEHOLDER = ITEM_TEXTURE_LOCATION + "leather_leggings";
    public static final String LEATHER_BOOTS_PLACEHOLDER = ITEM_TEXTURE_LOCATION + "leather_boots";
    public static final String LEATHER_HORSE_ARMOR_PLACEHOLDER = ITEM_TEXTURE_LOCATION + "leather_horse_armor";

    public static final String SPAWN_EGG_PLACEHOLDER = ITEM_TEXTURE_LOCATION + "spawn_egg";
    public static final String SPAWN_EGG_OVERLAY_PLACEHOLDER = ITEM_TEXTURE_LOCATION + "spawn_egg_overlay";

    public static final String TIPPED_ARROW_HEAD_PLACEHOLDER = ITEM_TEXTURE_LOCATION + "tipped_arrow_head";
    public static final String POTION_OVERLAY_PLACEHOLDER = ITEM_TEXTURE_LOCATION + "potion_overlay";

    public static final String MAP_MARKINGS_LOCATION = ITEM_TEXTURE_LOCATION + "filled_map_markings";

    public static final String GRASS_COLORMAP_LOCATION = COLORMAP_TEXTURE_LOCATION + "grass";
    public static final String FOLIAGE_COLORMAP_LOCATION = COLORMAP_TEXTURE_LOCATION + "foliage";

    public static final String UNKNOWN_PACK_ICON_LOCATION = MISC_TEXTURE_LOCATION + "unknown_pack";

    public static final double ENCHANTMENT_GLINT_FACTOR = 190.0 / 255.0;

}
