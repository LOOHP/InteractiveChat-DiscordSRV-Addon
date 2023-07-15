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

import com.loohp.interactivechat.objectholders.ICMaterial;
import com.loohp.interactivechatdiscordsrvaddon.graphics.ImageUtils;

import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.IntSupplier;
import java.util.function.IntUnaryOperator;

public class TintUtils {

    public static final float DEFAULT_TEMPERATURE = 0.8F;
    public static final float DEFAULT_DOWNFALL = 0.4F;

    public static final float DEFAULT2_TEMPERATURE = 0.5F;
    public static final float DEFAULT2_DOWNFALL = 1.0F;

    public static final int FOLIAGE_SPRUCE_COLOR = 6396257;
    public static final int FOLIAGE_BIRCH_COLOR = 8431445;
    public static final int FOLIAGE_MANGROVE_COLOR = 9619016;
    public static final int FOLIAGE_DEFAULT_COLOR = 4764952;

    public static final int LILY_PAD_COLOR = 2129968;

    private static final Map<String, TintColorProvider> TINT_PROVIDERS = new HashMap<>();
    private static final Map<String, SpawnEggTintData> SPAWN_EGG_TINT_DATA = new HashMap<>();

    private static int[] GRASS_TINT_PIXELS = new int[65536];
    private static int[] FOLIAGE_TINT_PIXELS = new int[65536];

    static {
        TintColorProvider grassTint = new TintIndexData(Collections.singletonList(() -> getGrassTintColor(DEFAULT_TEMPERATURE, DEFAULT_DOWNFALL)));
        TINT_PROVIDERS.put("GRASS_BLOCK", grassTint);
        TINT_PROVIDERS.put("GRASS", grassTint);
        TINT_PROVIDERS.put("LARGE_FERN", grassTint);
        TINT_PROVIDERS.put("POTTED_FERN", grassTint);
        TINT_PROVIDERS.put("SUGAR_CANE", grassTint);

        TintColorProvider grassTint2 = new TintIndexData(Collections.singletonList(() -> getGrassTintColor(DEFAULT2_TEMPERATURE, DEFAULT2_DOWNFALL)));
        TINT_PROVIDERS.put("TALL_GRASS", grassTint2);
        TINT_PROVIDERS.put("FERN", grassTint2);

        TintColorProvider foliageTint = new TintIndexData(Collections.singletonList(() -> getFoliageTintColor(DEFAULT_TEMPERATURE, DEFAULT_DOWNFALL)));
        TINT_PROVIDERS.put("OAK_LEAVES", foliageTint);
        TINT_PROVIDERS.put("JUNGLE_LEAVES", foliageTint);
        TINT_PROVIDERS.put("ACACIA_LEAVES", foliageTint);
        TINT_PROVIDERS.put("DARK_OAK_LEAVES", foliageTint);
        TINT_PROVIDERS.put("VINE", foliageTint);

        TINT_PROVIDERS.put("BIRCH_LEAVES", new TintIndexData(Collections.singletonList(() -> FOLIAGE_BIRCH_COLOR)));
        TINT_PROVIDERS.put("SPRUCE_LEAVES", new TintIndexData(Collections.singletonList(() -> FOLIAGE_SPRUCE_COLOR)));
        TINT_PROVIDERS.put("MANGROVE_LEAVES", new TintIndexData(Collections.singletonList(() -> FOLIAGE_MANGROVE_COLOR)));
        TINT_PROVIDERS.put("LILY_PAD", new TintIndexData(Collections.singletonList(() -> LILY_PAD_COLOR)));

        SPAWN_EGG_TINT_DATA.put("ALLAY_SPAWN_EGG", new SpawnEggTintData(56063, 44543));
        SPAWN_EGG_TINT_DATA.put("AXOLOTL_SPAWN_EGG", new SpawnEggTintData(16499171, 10890612));
        SPAWN_EGG_TINT_DATA.put("BAT_SPAWN_EGG", new SpawnEggTintData(4996656, 986895));
        SPAWN_EGG_TINT_DATA.put("BEE_SPAWN_EGG", new SpawnEggTintData(15582019, 4400155));
        SPAWN_EGG_TINT_DATA.put("BLAZE_SPAWN_EGG", new SpawnEggTintData(16167425, 16775294));
        SPAWN_EGG_TINT_DATA.put("CAT_SPAWN_EGG", new SpawnEggTintData(15714446, 9794134));
        SPAWN_EGG_TINT_DATA.put("CAMEL_SPAWN_EGG", new SpawnEggTintData(16565097, 13341495));
        SPAWN_EGG_TINT_DATA.put("CAVE_SPIDER_SPAWN_EGG", new SpawnEggTintData(803406, 11013646));
        SPAWN_EGG_TINT_DATA.put("CHICKEN_SPAWN_EGG", new SpawnEggTintData(10592673, 16711680));
        SPAWN_EGG_TINT_DATA.put("COD_SPAWN_EGG", new SpawnEggTintData(12691306, 15058059));
        SPAWN_EGG_TINT_DATA.put("COW_SPAWN_EGG", new SpawnEggTintData(4470310, 10592673));
        SPAWN_EGG_TINT_DATA.put("CREEPER_SPAWN_EGG", new SpawnEggTintData(894731, 0));
        SPAWN_EGG_TINT_DATA.put("DOLPHIN_SPAWN_EGG", new SpawnEggTintData(2243405, 16382457));
        SPAWN_EGG_TINT_DATA.put("DONKEY_SPAWN_EGG", new SpawnEggTintData(5457209, 8811878));
        SPAWN_EGG_TINT_DATA.put("DROWNED_SPAWN_EGG", new SpawnEggTintData(9433559, 7969893));
        SPAWN_EGG_TINT_DATA.put("ELDER_GUARDIAN_SPAWN_EGG", new SpawnEggTintData(13552826, 7632531));
        SPAWN_EGG_TINT_DATA.put("ENDER_DRAGON_SPAWN_EGG", new SpawnEggTintData(1842204, 14711290));
        SPAWN_EGG_TINT_DATA.put("ENDERMAN_SPAWN_EGG", new SpawnEggTintData(1447446, 0));
        SPAWN_EGG_TINT_DATA.put("ENDERMITE_SPAWN_EGG", new SpawnEggTintData(1447446, 7237230));
        SPAWN_EGG_TINT_DATA.put("EVOKER_SPAWN_EGG", new SpawnEggTintData(9804699, 1973274));
        SPAWN_EGG_TINT_DATA.put("FOX_SPAWN_EGG", new SpawnEggTintData(14005919, 13396256));
        SPAWN_EGG_TINT_DATA.put("FROG_SPAWN_EGG", new SpawnEggTintData(13661252, 16762748));
        SPAWN_EGG_TINT_DATA.put("GHAST_SPAWN_EGG", new SpawnEggTintData(16382457, 12369084));
        SPAWN_EGG_TINT_DATA.put("GLOW_SQUID_SPAWN_EGG", new SpawnEggTintData(611926, 8778172));
        SPAWN_EGG_TINT_DATA.put("GOAT_SPAWN_EGG", new SpawnEggTintData(10851452, 5589310));
        SPAWN_EGG_TINT_DATA.put("GUARDIAN_SPAWN_EGG", new SpawnEggTintData(5931634, 15826224));
        SPAWN_EGG_TINT_DATA.put("HOGLIN_SPAWN_EGG", new SpawnEggTintData(13004373, 6251620));
        SPAWN_EGG_TINT_DATA.put("HORSE_SPAWN_EGG", new SpawnEggTintData(12623485, 15656192));
        SPAWN_EGG_TINT_DATA.put("HUSK_SPAWN_EGG", new SpawnEggTintData(7958625, 15125652));
        SPAWN_EGG_TINT_DATA.put("IRON_GOLEM_SPAWN_EGG", new SpawnEggTintData(14405058, 7643954));
        SPAWN_EGG_TINT_DATA.put("LLAMA_SPAWN_EGG", new SpawnEggTintData(12623485, 10051392));
        SPAWN_EGG_TINT_DATA.put("MAGMA_CUBE_SPAWN_EGG", new SpawnEggTintData(3407872, 16579584));
        SPAWN_EGG_TINT_DATA.put("MOOSHROOM_SPAWN_EGG", new SpawnEggTintData(10489616, 12040119));
        SPAWN_EGG_TINT_DATA.put("MULE_SPAWN_EGG", new SpawnEggTintData(1769984, 5321501));
        SPAWN_EGG_TINT_DATA.put("OCELOT_SPAWN_EGG", new SpawnEggTintData(15720061, 5653556));
        SPAWN_EGG_TINT_DATA.put("PANDA_SPAWN_EGG", new SpawnEggTintData(15198183, 1776418));
        SPAWN_EGG_TINT_DATA.put("PARROT_SPAWN_EGG", new SpawnEggTintData(894731, 16711680));
        SPAWN_EGG_TINT_DATA.put("PHANTOM_SPAWN_EGG", new SpawnEggTintData(4411786, 8978176));
        SPAWN_EGG_TINT_DATA.put("PIG_SPAWN_EGG", new SpawnEggTintData(15771042, 14377823));
        SPAWN_EGG_TINT_DATA.put("PIGLIN_SPAWN_EGG", new SpawnEggTintData(10051392, 16380836));
        SPAWN_EGG_TINT_DATA.put("PIGLIN_BRUTE_SPAWN_EGG", new SpawnEggTintData(5843472, 16380836));
        SPAWN_EGG_TINT_DATA.put("PILLAGER_SPAWN_EGG", new SpawnEggTintData(5451574, 9804699));
        SPAWN_EGG_TINT_DATA.put("POLAR_BEAR_SPAWN_EGG", new SpawnEggTintData(15658718, 14014157));
        SPAWN_EGG_TINT_DATA.put("PUFFERFISH_SPAWN_EGG", new SpawnEggTintData(16167425, 3654642));
        SPAWN_EGG_TINT_DATA.put("RABBIT_SPAWN_EGG", new SpawnEggTintData(10051392, 7555121));
        SPAWN_EGG_TINT_DATA.put("RAVAGER_SPAWN_EGG", new SpawnEggTintData(7697520, 5984329));
        SPAWN_EGG_TINT_DATA.put("SALMON_SPAWN_EGG", new SpawnEggTintData(10489616, 951412));
        SPAWN_EGG_TINT_DATA.put("SHEEP_SPAWN_EGG", new SpawnEggTintData(15198183, 16758197));
        SPAWN_EGG_TINT_DATA.put("SHULKER_SPAWN_EGG", new SpawnEggTintData(9725844, 5060690));
        SPAWN_EGG_TINT_DATA.put("SILVERFISH_SPAWN_EGG", new SpawnEggTintData(7237230, 3158064));
        SPAWN_EGG_TINT_DATA.put("SKELETON_SPAWN_EGG", new SpawnEggTintData(12698049, 4802889));
        SPAWN_EGG_TINT_DATA.put("SKELETON_HORSE_SPAWN_EGG", new SpawnEggTintData(6842447, 15066584));
        SPAWN_EGG_TINT_DATA.put("SLIME_SPAWN_EGG", new SpawnEggTintData(5349438, 8306542));
        SPAWN_EGG_TINT_DATA.put("SNIFFER_SPAWN_EGG", new SpawnEggTintData(8855049, 2468720));
        SPAWN_EGG_TINT_DATA.put("SNOW_GOLEM_SPAWN_EGG", new SpawnEggTintData(14283506, 8496292));
        SPAWN_EGG_TINT_DATA.put("SPIDER_SPAWN_EGG", new SpawnEggTintData(3419431, 11013646));
        SPAWN_EGG_TINT_DATA.put("SQUID_SPAWN_EGG", new SpawnEggTintData(2243405, 7375001));
        SPAWN_EGG_TINT_DATA.put("STRAY_SPAWN_EGG", new SpawnEggTintData(6387319, 14543594));
        SPAWN_EGG_TINT_DATA.put("STRIDER_SPAWN_EGG", new SpawnEggTintData(10236982, 5065037));
        SPAWN_EGG_TINT_DATA.put("TADPOLE_SPAWN_EGG", new SpawnEggTintData(7164733, 1444352));
        SPAWN_EGG_TINT_DATA.put("TRADER_LLAMA_SPAWN_EGG", new SpawnEggTintData(15377456, 4547222));
        SPAWN_EGG_TINT_DATA.put("TROPICAL_FISH_SPAWN_EGG", new SpawnEggTintData(15690005, 16775663));
        SPAWN_EGG_TINT_DATA.put("TURTLE_SPAWN_EGG", new SpawnEggTintData(15198183, 44975));
        SPAWN_EGG_TINT_DATA.put("VEX_SPAWN_EGG", new SpawnEggTintData(8032420, 15265265));
        SPAWN_EGG_TINT_DATA.put("VILLAGER_SPAWN_EGG", new SpawnEggTintData(5651507, 12422002));
        SPAWN_EGG_TINT_DATA.put("VINDICATOR_SPAWN_EGG", new SpawnEggTintData(9804699, 2580065));
        SPAWN_EGG_TINT_DATA.put("WANDERING_TRADER_SPAWN_EGG", new SpawnEggTintData(4547222, 15377456));
        SPAWN_EGG_TINT_DATA.put("WARDEN_SPAWN_EGG", new SpawnEggTintData(1001033, 3790560));
        SPAWN_EGG_TINT_DATA.put("WITCH_SPAWN_EGG", new SpawnEggTintData(3407872, 5349438));
        SPAWN_EGG_TINT_DATA.put("WITHER_SPAWN_EGG", new SpawnEggTintData(1315860, 5075616));
        SPAWN_EGG_TINT_DATA.put("WITHER_SKELETON_SPAWN_EGG", new SpawnEggTintData(1315860, 4672845));
        SPAWN_EGG_TINT_DATA.put("WOLF_SPAWN_EGG", new SpawnEggTintData(14144467, 13545366));
        SPAWN_EGG_TINT_DATA.put("ZOGLIN_SPAWN_EGG", new SpawnEggTintData(13004373, 15132390));
        SPAWN_EGG_TINT_DATA.put("ZOMBIE_SPAWN_EGG", new SpawnEggTintData(44975, 7969893));
        SPAWN_EGG_TINT_DATA.put("ZOMBIE_HORSE_SPAWN_EGG", new SpawnEggTintData(3232308, 9945732));
        SPAWN_EGG_TINT_DATA.put("ZOMBIE_VILLAGER_SPAWN_EGG", new SpawnEggTintData(5651507, 7969893));
        SPAWN_EGG_TINT_DATA.put("ZOMBIFIED_PIGLIN_SPAWN_EGG", new SpawnEggTintData(15373203, 5009705));
    }

    public static void setGrassAndFoliageColorMap(int[] grassColorMap, int[] foliageColorMap) {
        GRASS_TINT_PIXELS = grassColorMap;
        FOLIAGE_TINT_PIXELS = foliageColorMap;
    }

    public static TintColorProvider getTintProvider(String material) {
        return TINT_PROVIDERS.getOrDefault(material.toUpperCase(), TintColorProvider.EMPTY_INSTANCE);
    }

    public static TintColorProvider getTintProvider(ICMaterial material) {
        return TINT_PROVIDERS.getOrDefault(material.name(), TintColorProvider.EMPTY_INSTANCE);
    }

    public static SpawnEggTintData getSpawnEggTint(String spawnEgg) {
        return SPAWN_EGG_TINT_DATA.get(spawnEgg.toUpperCase());
    }

    public static SpawnEggTintData getSpawnEggTint(ICMaterial spawnEgg) {
        return SPAWN_EGG_TINT_DATA.get(spawnEgg.name());
    }

    public static int getGrassTintColor(float temperature, float downfall) {
        double d0 = Math.max(0.0, Math.min(1.0, temperature));
        double d1 = Math.max(0.0, Math.min(1.0, downfall));
        d1 *= d0;
        int i = (int) ((1.0 - d0) * 255.0);
        int j = (int) ((1.0 - d1) * 255.0);
        int k = j << 8 | i;
        return k >= GRASS_TINT_PIXELS.length ? -65281 : GRASS_TINT_PIXELS[k];
    }

    public static int getFoliageTintColor(float temperature, float downfall) {
        double d0 = Math.max(0.0, Math.min(1.0, temperature));
        double d1 = Math.max(0.0, Math.min(1.0, downfall));
        d1 *= d0;
        int i = (int) ((1.0 - d0) * 255.0);
        int j = (int) ((1.0 - d1) * 255.0);
        int k = j << 8 | i;
        return k >= FOLIAGE_TINT_PIXELS.length ? FOLIAGE_DEFAULT_COLOR : FOLIAGE_TINT_PIXELS[k];
    }

    public interface TintColorProvider {

        TintColorProvider EMPTY_INSTANCE = new TintColorProvider() {
            @Override
            public BufferedImage applyTint(BufferedImage image, int tintIndex) {
                return image;
            }

            @Override
            public int getTintColor(int tintIndex) {
                return -1;
            }
        };

        BufferedImage applyTint(BufferedImage image, int tintIndex);

        int getTintColor(int tintIndex);

    }

    public static class TintIndexData implements TintColorProvider {

        private final List<IntSupplier> data;

        public TintIndexData(List<IntSupplier> data) {
            this.data = data;
        }

        public BufferedImage applyTint(BufferedImage image, int tintIndex) {
            if (tintIndex >= 0 && tintIndex < data.size()) {
                IntSupplier colorSupplier = data.get(tintIndex);
                if (colorSupplier != null) {
                    int color = colorSupplier.getAsInt();
                    BufferedImage tintImage = ImageUtils.changeColorTo(ImageUtils.copyImage(image), color);
                    return ImageUtils.multiply(image, tintImage);
                }
            }
            return image;
        }

        public int getTintColor(int tintIndex) {
            if (tintIndex >= 0 && tintIndex < data.size()) {
                IntSupplier colorSupplier = data.get(tintIndex);
                if (colorSupplier != null) {
                    return colorSupplier.getAsInt();
                }
            }
            return -1;
        }

    }

    public static class DyeTintProvider implements TintColorProvider {

        private final IntUnaryOperator colorSupplier;

        public DyeTintProvider(IntUnaryOperator colorSupplier) {
            this.colorSupplier = colorSupplier;
        }

        @Override
        public BufferedImage applyTint(BufferedImage image, int tintIndex) {
            if (tintIndex >= 0) {
                int color = colorSupplier.applyAsInt(tintIndex);
                BufferedImage tintImage = ImageUtils.changeColorTo(ImageUtils.copyImage(image), color);
                return ImageUtils.multiply(image, tintImage);
            }
            return image;
        }

        @Override
        public int getTintColor(int tintIndex) {
            if (tintIndex >= 0) {
                return colorSupplier.applyAsInt(tintIndex);
            }
            return -1;
        }

    }

    public static class SpawnEggTintData {

        private final int base;
        private final int overlay;

        public SpawnEggTintData(int base, int overlay) {
            this.base = base;
            this.overlay = overlay;
        }

        public int getColor(int tintIndex) {
            return tintIndex == 0 ? this.base : this.overlay;
        }

        public int getBase() {
            return base;
        }

        public int getOverlay() {
            return overlay;
        }

    }

}
