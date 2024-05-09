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
            resourcePackVersion = 32;
            itemTextureLocation = "minecraft:item/";
            blockTextureLocation = "minecraft:block/";
        }
        RESOURCE_PACK_VERSION = resourcePackVersion;
        ITEM_TEXTURE_LOCATION = itemTextureLocation;
        BLOCK_TEXTURE_LOCATION = blockTextureLocation;
    }

    public static final String DEFAULT_NAMESPACE = "minecraft";
    public static final String ICD_PREFIX = "minecraft:interactivechatdiscordsrvaddon/";

    public static final int RESOURCE_PACK_VERSION;

    public static final String ITEM_COMPONENT_TAG = RESOURCE_PACK_VERSION >= 24 ? "components" : "tag";

    public static final String BANNER_TEXTURE_LOCATION = "minecraft:entity/banner/";
    public static final String SHIELD_TEXTURE_LOCATION = "minecraft:entity/shield/";
    public static final String ITEM_TEXTURE_LOCATION;
    public static final String BLOCK_TEXTURE_LOCATION;
    public static final String ENTITY_TEXTURE_LOCATION = "minecraft:entity/";
    public static final String MISC_TEXTURE_LOCATION = "minecraft:misc/";
    public static final String GUI_TEXTURE_LOCATION = "minecraft:gui/";
    public static final String MAP_TEXTURE_LOCATION = "minecraft:map/";
    public static final String COLORMAP_TEXTURE_LOCATION = "minecraft:colormap/";
    public static final String DEFAULT_SPRITE_LOCATION = "minecraft:";
    public static final String IC_BLOCK_TEXTURE_LOCATION = ICD_PREFIX + "block/";
    public static final String IC_GUI_TEXTURE_LOCATION = ICD_PREFIX + "gui/";
    public static final String IC_MISC_TEXTURE_LOCATION = ICD_PREFIX + "misc/";

    public static final String ARMOR_TEXTURE_LOCATION = "models/armor/";

    public static final String IC_OLD_BASE_BLOCK_MODEL = ICD_PREFIX + "block/block";
    public static final String IC_OLD_BASE_ITEM_MODEL = ICD_PREFIX + "item/generated";

    public static final String ITEM_MODEL_LOCATION = "item/";
    public static final String BUILTIN_ENTITY_MODEL_LOCATION = ICD_PREFIX + "builtin_entity/";

    public static final String ENCHANTMENT_GLINT_ENTITY_LOCATION = ResourceRegistry.MISC_TEXTURE_LOCATION + (RESOURCE_PACK_VERSION < 13 ? "enchanted_item_glint" : "enchanted_glint_entity");
    public static final String ENCHANTMENT_GLINT_ITEM_LOCATION = ResourceRegistry.MISC_TEXTURE_LOCATION + (RESOURCE_PACK_VERSION < 13 ? "enchanted_item_glint" : "enchanted_glint_item");

    public static final String SKIN_TEXTURE_PLACEHOLDER = ICD_PREFIX + "skin";
    public static final String SKIN_FULL_TEXTURE_PLACEHOLDER = ICD_PREFIX + "skin_full";
    public static final String BOOTS_TEXTURE_PLACEHOLDER = ICD_PREFIX + "boots";
    public static final String LEGGINGS_TEXTURE_PLACEHOLDER = ICD_PREFIX + "leggings";
    public static final String CHESTPLATE_TEXTURE_PLACEHOLDER = ICD_PREFIX + "chestplate";
    public static final String HELMET_TEXTURE_PLACEHOLDER = ICD_PREFIX + "helmet";

    public static final String BANNER_BASE_TEXTURE_PLACEHOLDER = ICD_PREFIX + "banner_base";
    public static final String BANNER_PATTERNS_TEXTURE_PLACEHOLDER = ICD_PREFIX + "banner_patterns";
    public static final String SHIELD_BASE_TEXTURE_PLACEHOLDER = ICD_PREFIX + "shield_base";
    public static final String SHIELD_PATTERNS_TEXTURE_PLACEHOLDER = ICD_PREFIX + "shield_patterns";

    public static final String LEGACY_BED_TEXTURE_PLACEHOLDER = ICD_PREFIX + "legacy_bed";

    public static final String MAP_MARKINGS_LOCATION = ITEM_TEXTURE_LOCATION + "filled_map_markings";

    public static final String GRASS_COLORMAP_LOCATION = COLORMAP_TEXTURE_LOCATION + "grass";
    public static final String FOLIAGE_COLORMAP_LOCATION = COLORMAP_TEXTURE_LOCATION + "foliage";

    public static final String DEFAULT_WIDE_SKIN_LOCATION = ENTITY_TEXTURE_LOCATION + (RESOURCE_PACK_VERSION < 12 ? "steve" : "player/wide/steve");
    public static final String DEFAULT_SLIM_SKIN_LOCATION = ENTITY_TEXTURE_LOCATION + (RESOURCE_PACK_VERSION < 12 ? "alex" : "player/slim/alex");

    public static final String DECORATED_POT_SHERD_MINECRAFT_LOCATION = ENTITY_TEXTURE_LOCATION + "decorated_pot/%s";
    public static final String DECORATED_POT_SHERD_LOCATION = "%s" + DECORATED_POT_SHERD_MINECRAFT_LOCATION.substring(DEFAULT_NAMESPACE.length());
    public static final String DECORATED_POT_FACE_PLACEHOLDER = ICD_PREFIX + "decorated_pot/face_%s";

    public static final String TRIM_TEXTURE_LOCATION = "minecraft:trims/";
    public static final String ARMOR_TRIM_LOCATION = TRIM_TEXTURE_LOCATION + "models/armor/%s_%s";
    public static final String ARMOR_TRIM_LEGGINGS_LOCATION = TRIM_TEXTURE_LOCATION + "models/armor/%s_leggings_%s";

    public static final double ENCHANTMENT_GLINT_FACTOR = 190.0 / 255.0;

    public static final int SHIELD_COOLDOWN = 100;
    public static final int ENDER_PEARL_COOLDOWN = 20;
    public static final int CHORUS_FRUIT_COOLDOWN = 20;

    public static final int DEFAULT_DYE_COLOR = 10511680;

}
