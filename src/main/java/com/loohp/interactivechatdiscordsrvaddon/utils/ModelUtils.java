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

package com.loohp.interactivechatdiscordsrvaddon.utils;

import com.loohp.blockmodelrenderer.render.Face;
import com.loohp.blockmodelrenderer.render.Point3D;
import com.loohp.blockmodelrenderer.utils.MathUtils;
import com.loohp.interactivechat.InteractiveChat;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.Component;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import com.loohp.interactivechat.objectholders.ICMaterial;
import com.loohp.interactivechat.utils.ChatColorUtils;
import com.loohp.interactivechat.utils.MCVersion;
import com.loohp.interactivechatdiscordsrvaddon.graphics.ImageUtils;
import com.loohp.interactivechatdiscordsrvaddon.registry.ResourceRegistry;
import com.loohp.interactivechatdiscordsrvaddon.resources.models.ModelFace.ModelFaceSide;
import org.bukkit.Material;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ModelUtils {

    private static final Map<String, String> LEGACY_MODEL_NAME = new HashMap<>();

    static {
        LEGACY_MODEL_NAME.put("ANVIL", "anvil_intact");
        LEGACY_MODEL_NAME.put("AZURE_BLUET", "houstonia");
        LEGACY_MODEL_NAME.put("BAT_SPAWN_EGG", "spawn_egg");
        LEGACY_MODEL_NAME.put("BLACK_BANNER", "banner");
        LEGACY_MODEL_NAME.put("BLACK_BED", "bed");
        LEGACY_MODEL_NAME.put("BLACK_DYE", "dye_black");
        LEGACY_MODEL_NAME.put("BLACK_TERRACOTTA", "black_stained_hardened_clay");
        LEGACY_MODEL_NAME.put("BLAZE_SPAWN_EGG", "spawn_egg");
        LEGACY_MODEL_NAME.put("BLUE_BANNER", "banner");
        LEGACY_MODEL_NAME.put("BLUE_BED", "bed");
        LEGACY_MODEL_NAME.put("BLUE_TERRACOTTA", "blue_stained_hardened_clay");
        LEGACY_MODEL_NAME.put("BONE_MEAL", "dye_white");
        LEGACY_MODEL_NAME.put("BRICKS", "brick_block");
        LEGACY_MODEL_NAME.put("BROWN_BANNER", "banner");
        LEGACY_MODEL_NAME.put("BROWN_BED", "bed");
        LEGACY_MODEL_NAME.put("BROWN_TERRACOTTA", "brown_stained_hardened_clay");
        LEGACY_MODEL_NAME.put("GREEN_DYE", "dye_green");
        LEGACY_MODEL_NAME.put("CARROTS", "carrot");
        LEGACY_MODEL_NAME.put("CARVED_PUMPKIN", "pumpkin");
        LEGACY_MODEL_NAME.put("CAVE_SPIDER_SPAWN_EGG", "spawn_egg");
        LEGACY_MODEL_NAME.put("CHICKEN_SPAWN_EGG", "spawn_egg");
        LEGACY_MODEL_NAME.put("CHIPPED_ANVIL", "anvil_slightly_damaged");
        LEGACY_MODEL_NAME.put("CHISELED_STONE_BRICKS", "chiseled_stonebrick");
        LEGACY_MODEL_NAME.put("COBBLESTONE_STAIRS", "stone_stairs");
        LEGACY_MODEL_NAME.put("COBWEB", "web");
        LEGACY_MODEL_NAME.put("COCOA", "dye_brown");
        LEGACY_MODEL_NAME.put("COCOA_BEANS", "dye_brown");
        LEGACY_MODEL_NAME.put("COD_SPAWN_EGG", "spawn_egg");
        LEGACY_MODEL_NAME.put("COW_SPAWN_EGG", "spawn_egg");
        LEGACY_MODEL_NAME.put("CRACKED_STONE_BRICKS", "cracked_stonebrick");
        LEGACY_MODEL_NAME.put("CREEPER_HEAD", "skull_creeper");
        LEGACY_MODEL_NAME.put("CREEPER_SPAWN_EGG", "spawn_egg");
        LEGACY_MODEL_NAME.put("CUT_RED_SANDSTONE", "smooth_red_sandstone");
        LEGACY_MODEL_NAME.put("CUT_SANDSTONE", "smooth_sandstone");
        LEGACY_MODEL_NAME.put("CYAN_BANNER", "banner");
        LEGACY_MODEL_NAME.put("CYAN_BED", "bed");
        LEGACY_MODEL_NAME.put("CYAN_DYE", "dye_cyan");
        LEGACY_MODEL_NAME.put("CYAN_TERRACOTTA", "cyan_stained_hardened_clay");
        LEGACY_MODEL_NAME.put("DAMAGED_ANVIL", "anvil_very_damaged");
        LEGACY_MODEL_NAME.put("YELLOW_DYE", "dye_yellow");
        LEGACY_MODEL_NAME.put("DOLPHIN_SPAWN_EGG", "spawn_egg");
        LEGACY_MODEL_NAME.put("DONKEY_SPAWN_EGG", "spawn_egg");
        LEGACY_MODEL_NAME.put("DRAGON_HEAD", "skull_dragon");
        LEGACY_MODEL_NAME.put("DROWNED_SPAWN_EGG", "spawn_egg");
        LEGACY_MODEL_NAME.put("ELDER_GUARDIAN_SPAWN_EGG", "spawn_egg");
        LEGACY_MODEL_NAME.put("ENCHANTED_GOLDEN_APPLE", "golden_apple");
        LEGACY_MODEL_NAME.put("ENDERMAN_SPAWN_EGG", "spawn_egg");
        LEGACY_MODEL_NAME.put("ENDERMITE_SPAWN_EGG", "spawn_egg");
        LEGACY_MODEL_NAME.put("END_STONE_BRICKS", "end_bricks");
        LEGACY_MODEL_NAME.put("EVOKER_SPAWN_EGG", "spawn_egg");
        LEGACY_MODEL_NAME.put("FIREWORK_ROCKET", "fireworks");
        LEGACY_MODEL_NAME.put("FIREWORK_STAR", "firework_charge");
        LEGACY_MODEL_NAME.put("GHAST_SPAWN_EGG", "spawn_egg");
        LEGACY_MODEL_NAME.put("GLISTERING_MELON_SLICE", "speckled_melon");
        LEGACY_MODEL_NAME.put("GRASS_BLOCK", "grass");
        LEGACY_MODEL_NAME.put("DIRT_PATH", "grass_path");
        LEGACY_MODEL_NAME.put("GRAY_BANNER", "banner");
        LEGACY_MODEL_NAME.put("GRAY_BED", "bed");
        LEGACY_MODEL_NAME.put("GRAY_DYE", "dye_gray");
        LEGACY_MODEL_NAME.put("GRAY_TERRACOTTA", "gray_stained_hardened_clay");
        LEGACY_MODEL_NAME.put("GREEN_BANNER", "banner");
        LEGACY_MODEL_NAME.put("GREEN_BED", "bed");
        LEGACY_MODEL_NAME.put("GREEN_TERRACOTTA", "green_stained_hardened_clay");
        LEGACY_MODEL_NAME.put("GUARDIAN_SPAWN_EGG", "spawn_egg");
        LEGACY_MODEL_NAME.put("HORSE_SPAWN_EGG", "spawn_egg");
        LEGACY_MODEL_NAME.put("HUSK_SPAWN_EGG", "spawn_egg");
        LEGACY_MODEL_NAME.put("INFESTED_CHISELED_STONE_BRICKS", "chiseled_brick_monster_egg");
        LEGACY_MODEL_NAME.put("INFESTED_COBBLESTONE", "cobblestone_monster_egg");
        LEGACY_MODEL_NAME.put("INFESTED_CRACKED_STONE_BRICKS", "cracked_brick_monster_egg");
        LEGACY_MODEL_NAME.put("INFESTED_MOSSY_STONE_BRICKS", "mossy_brick_monster_egg");
        LEGACY_MODEL_NAME.put("INFESTED_STONE", "stone_monster_egg");
        LEGACY_MODEL_NAME.put("INFESTED_STONE_BRICKS", "stone_brick_monster_egg");
        LEGACY_MODEL_NAME.put("INK_SAC", "dye_black");
        LEGACY_MODEL_NAME.put("JACK_O_LANTERN", "lit_pumpkin");
        LEGACY_MODEL_NAME.put("LAPIS_LAZULI", "dye_blue");
        LEGACY_MODEL_NAME.put("LARGE_FERN", "double_fern");
        LEGACY_MODEL_NAME.put("LIGHT_BLUE_BANNER", "banner");
        LEGACY_MODEL_NAME.put("LIGHT_BLUE_BED", "bed");
        LEGACY_MODEL_NAME.put("LIGHT_BLUE_DYE", "dye_light_blue");
        LEGACY_MODEL_NAME.put("LIGHT_BLUE_TERRACOTTA", "light_blue_stained_hardened_clay");
        LEGACY_MODEL_NAME.put("LIGHT_GRAY_BANNER", "banner");
        LEGACY_MODEL_NAME.put("LIGHT_GRAY_BED", "bed");
        LEGACY_MODEL_NAME.put("LIGHT_GRAY_CARPET", "silver_carpet");
        LEGACY_MODEL_NAME.put("LIGHT_GRAY_CONCRETE", "silver_concrete");
        LEGACY_MODEL_NAME.put("LIGHT_GRAY_CONCRETE_POWDER", "silver_concrete_powder");
        LEGACY_MODEL_NAME.put("LIGHT_GRAY_DYE", "dye_silver");
        LEGACY_MODEL_NAME.put("LIGHT_GRAY_GLAZED_TERRACOTTA", "silver_glazed_terracotta");
        LEGACY_MODEL_NAME.put("LIGHT_GRAY_SHULKER_BOX", "silver_shulker_box");
        LEGACY_MODEL_NAME.put("LIGHT_GRAY_STAINED_GLASS", "silver_stained_glass");
        LEGACY_MODEL_NAME.put("LIGHT_GRAY_STAINED_GLASS_PANE", "silver_stained_glass_pane");
        LEGACY_MODEL_NAME.put("LIGHT_GRAY_TERRACOTTA", "silver_stained_hardened_clay");
        LEGACY_MODEL_NAME.put("LIGHT_GRAY_WOOL", "silver_wool");
        LEGACY_MODEL_NAME.put("LILAC", "syringa");
        LEGACY_MODEL_NAME.put("LILY_PAD", "waterlily");
        LEGACY_MODEL_NAME.put("LIME_BANNER", "banner");
        LEGACY_MODEL_NAME.put("LIME_BED", "bed");
        LEGACY_MODEL_NAME.put("LIME_DYE", "dye_lime");
        LEGACY_MODEL_NAME.put("LIME_TERRACOTTA", "lime_stained_hardened_clay");
        LEGACY_MODEL_NAME.put("LINGERING_POTION", "bottle_lingering");
        LEGACY_MODEL_NAME.put("LLAMA_SPAWN_EGG", "spawn_egg");
        LEGACY_MODEL_NAME.put("MAGENTA_BANNER", "banner");
        LEGACY_MODEL_NAME.put("MAGENTA_BED", "bed");
        LEGACY_MODEL_NAME.put("MAGENTA_DYE", "dye_magenta");
        LEGACY_MODEL_NAME.put("MAGENTA_TERRACOTTA", "magenta_stained_hardened_clay");
        LEGACY_MODEL_NAME.put("MAGMA_BLOCK", "magma");
        LEGACY_MODEL_NAME.put("MAGMA_CUBE_SPAWN_EGG", "spawn_egg");
        LEGACY_MODEL_NAME.put("MELON_SLICE", "melon");
        LEGACY_MODEL_NAME.put("MOOSHROOM_SPAWN_EGG", "spawn_egg");
        LEGACY_MODEL_NAME.put("MOSSY_STONE_BRICKS", "mossy_stonebrick");
        LEGACY_MODEL_NAME.put("MULE_SPAWN_EGG", "spawn_egg");
        LEGACY_MODEL_NAME.put("MUSHROOM_STEM", "brown_mushroom_block");
        LEGACY_MODEL_NAME.put("MUSIC_DISC_11", "record_11");
        LEGACY_MODEL_NAME.put("MUSIC_DISC_13", "record_13");
        LEGACY_MODEL_NAME.put("MUSIC_DISC_BLOCKS", "record_blocks");
        LEGACY_MODEL_NAME.put("MUSIC_DISC_CAT", "record_cat");
        LEGACY_MODEL_NAME.put("MUSIC_DISC_CHIRP", "record_chirp");
        LEGACY_MODEL_NAME.put("MUSIC_DISC_FAR", "record_far");
        LEGACY_MODEL_NAME.put("MUSIC_DISC_MALL", "record_mall");
        LEGACY_MODEL_NAME.put("MUSIC_DISC_MELLOHI", "record_mellohi");
        LEGACY_MODEL_NAME.put("MUSIC_DISC_STAL", "record_stal");
        LEGACY_MODEL_NAME.put("MUSIC_DISC_STRAD", "record_strad");
        LEGACY_MODEL_NAME.put("MUSIC_DISC_WAIT", "record_wait");
        LEGACY_MODEL_NAME.put("MUSIC_DISC_WARD", "record_ward");
        LEGACY_MODEL_NAME.put("NETHER_BRICKS", "nether_brick");
        LEGACY_MODEL_NAME.put("NETHER_QUARTZ_ORE", "quartz_ore");
        LEGACY_MODEL_NAME.put("NOTE_BLOCK", "noteblock");
        LEGACY_MODEL_NAME.put("OAK_BUTTON", "wooden_button");
        LEGACY_MODEL_NAME.put("OAK_PRESSURE_PLATE", "wooden_pressure_plate");
        LEGACY_MODEL_NAME.put("OAK_TRAPDOOR", "trapdoor");
        LEGACY_MODEL_NAME.put("OCELOT_SPAWN_EGG", "spawn_egg");
        LEGACY_MODEL_NAME.put("ORANGE_BANNER", "banner");
        LEGACY_MODEL_NAME.put("ORANGE_BED", "bed");
        LEGACY_MODEL_NAME.put("ORANGE_DYE", "dye_orange");
        LEGACY_MODEL_NAME.put("ORANGE_TERRACOTTA", "orange_stained_hardened_clay");
        LEGACY_MODEL_NAME.put("PARROT_SPAWN_EGG", "spawn_egg");
        LEGACY_MODEL_NAME.put("PEONY", "paeonia");
        LEGACY_MODEL_NAME.put("PETRIFIED_OAK_SLAB", "oak_slab");
        LEGACY_MODEL_NAME.put("PHANTOM_SPAWN_EGG", "spawn_egg");
        LEGACY_MODEL_NAME.put("PIG_SPAWN_EGG", "spawn_egg");
        LEGACY_MODEL_NAME.put("PINK_BANNER", "banner");
        LEGACY_MODEL_NAME.put("PINK_BED", "bed");
        LEGACY_MODEL_NAME.put("PINK_DYE", "dye_pink");
        LEGACY_MODEL_NAME.put("PINK_TERRACOTTA", "pink_stained_hardened_clay");
        LEGACY_MODEL_NAME.put("PLAYER_HEAD", "skull_char");
        LEGACY_MODEL_NAME.put("POLAR_BEAR_SPAWN_EGG", "spawn_egg");
        LEGACY_MODEL_NAME.put("POLISHED_ANDESITE", "andesite_smooth");
        LEGACY_MODEL_NAME.put("POLISHED_DIORITE", "diorite_smooth");
        LEGACY_MODEL_NAME.put("POLISHED_GRANITE", "granite_smooth");
        LEGACY_MODEL_NAME.put("POPPED_CHORUS_FRUIT", "chorus_fruit_popped");
        LEGACY_MODEL_NAME.put("POTATOES", "potato");
        LEGACY_MODEL_NAME.put("POTION", "bottle_drinkable");
        LEGACY_MODEL_NAME.put("POWERED_RAIL", "golden_rail");
        LEGACY_MODEL_NAME.put("PUFFERFISH_SPAWN_EGG", "spawn_egg");
        LEGACY_MODEL_NAME.put("PURPLE_BANNER", "banner");
        LEGACY_MODEL_NAME.put("PURPLE_BED", "bed");
        LEGACY_MODEL_NAME.put("PURPLE_DYE", "dye_purple");
        LEGACY_MODEL_NAME.put("PURPLE_TERRACOTTA", "purple_stained_hardened_clay");
        LEGACY_MODEL_NAME.put("QUARTZ_PILLAR", "quartz_column");
        LEGACY_MODEL_NAME.put("RABBIT_SPAWN_EGG", "spawn_egg");
        LEGACY_MODEL_NAME.put("RED_BANNER", "banner");
        LEGACY_MODEL_NAME.put("RED_BED", "bed");
        LEGACY_MODEL_NAME.put("RED_NETHER_BRICKS", "red_nether_brick");
        LEGACY_MODEL_NAME.put("RED_TERRACOTTA", "red_stained_hardened_clay");
        LEGACY_MODEL_NAME.put("ROSE_BUSH", "double_rose");
        LEGACY_MODEL_NAME.put("RED_DYE", "dye_red");
        LEGACY_MODEL_NAME.put("SALMON_SPAWN_EGG", "spawn_egg");
        LEGACY_MODEL_NAME.put("SHEEP_SPAWN_EGG", "spawn_egg");
        LEGACY_MODEL_NAME.put("SHULKER_SPAWN_EGG", "spawn_egg");
        LEGACY_MODEL_NAME.put("OAK_SIGN", "sign");
        LEGACY_MODEL_NAME.put("SILVERFISH_SPAWN_EGG", "spawn_egg");
        LEGACY_MODEL_NAME.put("SKELETON_HORSE_SPAWN_EGG", "spawn_egg");
        LEGACY_MODEL_NAME.put("SKELETON_SKULL", "skull_skeleton");
        LEGACY_MODEL_NAME.put("SKELETON_SPAWN_EGG", "spawn_egg");
        LEGACY_MODEL_NAME.put("SLIME_BLOCK", "slime");
        LEGACY_MODEL_NAME.put("SLIME_SPAWN_EGG", "spawn_egg");
        LEGACY_MODEL_NAME.put("SMOOTH_STONE", "stone_slab");
        LEGACY_MODEL_NAME.put("SNOW_BLOCK", "snow");
        LEGACY_MODEL_NAME.put("SPAWNER", "mob_spawner");
        LEGACY_MODEL_NAME.put("SPIDER_SPAWN_EGG", "spawn_egg");
        LEGACY_MODEL_NAME.put("SPLASH_POTION", "bottle_splash");
        LEGACY_MODEL_NAME.put("SQUID_SPAWN_EGG", "spawn_egg");
        LEGACY_MODEL_NAME.put("STONE_BRICKS", "stonebrick");
        LEGACY_MODEL_NAME.put("STRAY_SPAWN_EGG", "spawn_egg");
        LEGACY_MODEL_NAME.put("SUGAR_CANE", "reeds");
        LEGACY_MODEL_NAME.put("TERRACOTTA", "hardened_clay");
        LEGACY_MODEL_NAME.put("TOTEM_OF_UNDYING", "totem");
        LEGACY_MODEL_NAME.put("TROPICAL_FISH_SPAWN_EGG", "spawn_egg");
        LEGACY_MODEL_NAME.put("TURTLE_SPAWN_EGG", "spawn_egg");
        LEGACY_MODEL_NAME.put("VEX_SPAWN_EGG", "spawn_egg");
        LEGACY_MODEL_NAME.put("VILLAGER_SPAWN_EGG", "spawn_egg");
        LEGACY_MODEL_NAME.put("VINDICATOR_SPAWN_EGG", "spawn_egg");
        LEGACY_MODEL_NAME.put("WET_SPONGE", "sponge_wet");
        LEGACY_MODEL_NAME.put("WHITE_BANNER", "banner");
        LEGACY_MODEL_NAME.put("WHITE_BED", "bed");
        LEGACY_MODEL_NAME.put("WHITE_TERRACOTTA", "white_stained_hardened_clay");
        LEGACY_MODEL_NAME.put("WITCH_SPAWN_EGG", "spawn_egg");
        LEGACY_MODEL_NAME.put("WITHER_SKELETON_SKULL", "skull_wither");
        LEGACY_MODEL_NAME.put("WITHER_SKELETON_SPAWN_EGG", "spawn_egg");
        LEGACY_MODEL_NAME.put("WOLF_SPAWN_EGG", "spawn_egg");
        LEGACY_MODEL_NAME.put("YELLOW_BANNER", "banner");
        LEGACY_MODEL_NAME.put("YELLOW_BED", "bed");
        LEGACY_MODEL_NAME.put("YELLOW_TERRACOTTA", "yellow_stained_hardened_clay");
        LEGACY_MODEL_NAME.put("ZOMBIE_HEAD", "skull_zombie");
        LEGACY_MODEL_NAME.put("ZOMBIE_HORSE_SPAWN_EGG", "spawn_egg");
        LEGACY_MODEL_NAME.put("ZOMBIFIED_PIGLIN_SPAWN_EGG", "spawn_egg");
        LEGACY_MODEL_NAME.put("ZOMBIE_SPAWN_EGG", "spawn_egg");
        LEGACY_MODEL_NAME.put("ZOMBIE_VILLAGER_SPAWN_EGG", "spawn_egg");
    }

    public static String getNamespace(ICMaterial icMaterial) {
        if (InteractiveChat.version.isOlderThan(MCVersion.V1_14)) {
            return ResourceRegistry.DEFAULT_NAMESPACE;
        }
        Material material = icMaterial.parseMaterial();
        if (material == null) {
            return ResourceRegistry.DEFAULT_NAMESPACE;
        }
        return material.getKey().getNamespace();
    }

    public static String getItemModelKey(ICMaterial icMaterial) {
        if (InteractiveChat.version.isLegacy()) {
            String legacyKey = LEGACY_MODEL_NAME.get(icMaterial.name());
            if (legacyKey != null) {
                return legacyKey.toLowerCase();
            }
        }
        if (InteractiveChat.version.isOlderThan(MCVersion.V1_14)) {
            return icMaterial.name().toLowerCase();
        }
        Material material = icMaterial.parseMaterial();
        if (material == null) {
            return icMaterial.name().toLowerCase();
        }
        return material.getKey().getKey();
    }

    public static BufferedImage convertToModernSkinTexture(BufferedImage skin) {
        if (skin.getWidth() == skin.getHeight()) {
            return skin;
        }
        int scale = skin.getWidth() / 64;
        BufferedImage modernSkin = new BufferedImage(skin.getWidth(), skin.getWidth(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = modernSkin.createGraphics();
        g.drawImage(skin, 0, 0, null);

        BufferedImage arm1 = ImageUtils.flipHorizontal(ImageUtils.copyAndGetSubImage(skin, 0, 20 * scale, 4 * scale, 12 * scale));
        BufferedImage arm2 = ImageUtils.flipHorizontal(ImageUtils.copyAndGetSubImage(skin, 4 * scale, 20 * scale, 4 * scale, 12 * scale));
        BufferedImage arm3 = ImageUtils.flipHorizontal(ImageUtils.copyAndGetSubImage(skin, 8 * scale, 20 * scale, 4 * scale, 12 * scale));
        BufferedImage arm4 = ImageUtils.flipHorizontal(ImageUtils.copyAndGetSubImage(skin, 12 * scale, 20 * scale, 4 * scale, 12 * scale));
        BufferedImage arm5 = ImageUtils.flipHorizontal(ImageUtils.copyAndGetSubImage(skin, 4 * scale, 16 * scale, 4 * scale, 4 * scale));
        BufferedImage arm6 = ImageUtils.flipHorizontal(ImageUtils.copyAndGetSubImage(skin, 8 * scale, 16 * scale, 4 * scale, 4 * scale));

        g.drawImage(arm1, 16 * scale, 52 * scale, null);
        g.drawImage(arm2, 20 * scale, 52 * scale, null);
        g.drawImage(arm3, 24 * scale, 52 * scale, null);
        g.drawImage(arm4, 28 * scale, 52 * scale, null);
        g.drawImage(arm5, 20 * scale, 48 * scale, null);
        g.drawImage(arm6, 24 * scale, 48 * scale, null);

        BufferedImage leg1 = ImageUtils.flipHorizontal(ImageUtils.copyAndGetSubImage(skin, 40 * scale, 20 * scale, 4 * scale, 12 * scale));
        BufferedImage leg2 = ImageUtils.flipHorizontal(ImageUtils.copyAndGetSubImage(skin, 44 * scale, 20 * scale, 4 * scale, 12 * scale));
        BufferedImage leg3 = ImageUtils.flipHorizontal(ImageUtils.copyAndGetSubImage(skin, 48 * scale, 20 * scale, 4 * scale, 12 * scale));
        BufferedImage leg4 = ImageUtils.flipHorizontal(ImageUtils.copyAndGetSubImage(skin, 52 * scale, 20 * scale, 4 * scale, 12 * scale));
        BufferedImage leg5 = ImageUtils.flipHorizontal(ImageUtils.copyAndGetSubImage(skin, 44 * scale, 16 * scale, 4 * scale, 4 * scale));
        BufferedImage leg6 = ImageUtils.flipHorizontal(ImageUtils.copyAndGetSubImage(skin, 48 * scale, 16 * scale, 4 * scale, 4 * scale));

        g.drawImage(leg1, 32 * scale, 52 * scale, null);
        g.drawImage(leg2, 36 * scale, 52 * scale, null);
        g.drawImage(leg3, 40 * scale, 52 * scale, null);
        g.drawImage(leg4, 44 * scale, 52 * scale, null);
        g.drawImage(leg5, 36 * scale, 48 * scale, null);
        g.drawImage(leg6, 40 * scale, 48 * scale, null);

        g.dispose();
        return modernSkin;
    }

    public static boolean isRenderedUpsideDown(Component component) {
        return isRenderedUpsideDown(ChatColorUtils.stripColor(PlainTextComponentSerializer.plainText().serialize(component)));
    }

    public static boolean isRenderedUpsideDown(Component component, boolean hasCape) {
        return isRenderedUpsideDown(ChatColorUtils.stripColor(PlainTextComponentSerializer.plainText().serialize(component)), hasCape);
    }

    public static boolean isRenderedUpsideDown(String name) {
        return isRenderedUpsideDown(name, true);
    }

    public static boolean isRenderedUpsideDown(String name, boolean hasCape) {
        return ("Dinnerbone".equals(name) || "Grumm".equals(name)) && hasCape;
    }

    public static boolean shouldTriggerCullface(Face face, ModelFaceSide side) {
        Point3D[] points = face.getPoints();
        switch (side) {
            case UP:
                return Arrays.stream(points).allMatch(p -> MathUtils.equals(p.y, 16.0));
            case DOWN:
                return Arrays.stream(points).allMatch(p -> MathUtils.equals(p.y, 0.0));
            case NORTH:
                return Arrays.stream(points).allMatch(p -> MathUtils.equals(p.z, 0.0));
            case EAST:
                return Arrays.stream(points).allMatch(p -> MathUtils.equals(p.x, 16.0));
            case SOUTH:
                return Arrays.stream(points).allMatch(p -> MathUtils.equals(p.z, 16.0));
            case WEST:
                return Arrays.stream(points).allMatch(p -> MathUtils.equals(p.x, 0.0));
        }
        throw new IllegalArgumentException("Unknown ModelFaceSide \"" + side.name() + "\"");
    }

}
