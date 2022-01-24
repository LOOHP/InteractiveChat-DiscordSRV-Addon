package com.loohp.interactivechatdiscordsrvaddon.utils;

import com.loohp.interactivechat.libs.com.cryptomorin.xseries.XMaterial;
import com.loohp.interactivechatdiscordsrvaddon.graphics.ImageUtils;

import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.IntSupplier;
import java.util.stream.IntStream;

public class TintUtils {

    public static final float DEFAULT_TEMPERATURE = 0.8F;
    public static final float DEFAULT_DOWNFALL = 0.4F;

    public static final int FOLIAGE_EVERGREEN_COLOR = 6396257;
    public static final int FOLIAGE_BIRCH_COLOR = 8431445;
    public static final int FOLIAGE_DEFAULT_COLOR = 4764952;

    public static final int LILY_PAD_COLOR = 2129968;

    private static final Map<XMaterial, TintIndexData> TINT_DATA = new HashMap<>();
    private static final Map<XMaterial, SpawnEggTintData> SPAWN_EGG_TINT_DATA = new HashMap<>();

    private static int[] GRASS_TINT_PIXELS = new int[65536];
    private static int[] FOLIAGE_TINT_PIXELS = new int[65536];

    static {
        TintIndexData grassTint = new TintIndexData(Collections.singletonList(() -> getGrassTintColor(DEFAULT_TEMPERATURE, DEFAULT_DOWNFALL)));
        TINT_DATA.put(XMaterial.GRASS_BLOCK, grassTint);
        TINT_DATA.put(XMaterial.GRASS, grassTint);
        TINT_DATA.put(XMaterial.TALL_GRASS, grassTint);
        TINT_DATA.put(XMaterial.FERN, grassTint);
        TINT_DATA.put(XMaterial.LARGE_FERN, grassTint);
        TINT_DATA.put(XMaterial.POTTED_FERN, grassTint);
        TINT_DATA.put(XMaterial.SUGAR_CANE, grassTint);

        TintIndexData foliageTint = new TintIndexData(Collections.singletonList(() -> getFoliageTintColor(DEFAULT_TEMPERATURE, DEFAULT_DOWNFALL)));
        TINT_DATA.put(XMaterial.OAK_LEAVES, foliageTint);
        TINT_DATA.put(XMaterial.JUNGLE_LEAVES, foliageTint);
        TINT_DATA.put(XMaterial.ACACIA_LEAVES, foliageTint);
        TINT_DATA.put(XMaterial.DARK_OAK_LEAVES, foliageTint);
        TINT_DATA.put(XMaterial.VINE, foliageTint);

        TINT_DATA.put(XMaterial.BIRCH_LEAVES, new TintIndexData(Collections.singletonList(() -> FOLIAGE_BIRCH_COLOR)));
        TINT_DATA.put(XMaterial.SPRUCE_LEAVES, new TintIndexData(Collections.singletonList(() -> FOLIAGE_EVERGREEN_COLOR)));
        TINT_DATA.put(XMaterial.LILY_PAD, new TintIndexData(Collections.singletonList(() -> LILY_PAD_COLOR)));

        SPAWN_EGG_TINT_DATA.put(XMaterial.AXOLOTL_SPAWN_EGG, new SpawnEggTintData(16499171, 10890612));
        SPAWN_EGG_TINT_DATA.put(XMaterial.BAT_SPAWN_EGG, new SpawnEggTintData(4996656, 986895));
        SPAWN_EGG_TINT_DATA.put(XMaterial.BEE_SPAWN_EGG, new SpawnEggTintData(15582019, 4400155));
        SPAWN_EGG_TINT_DATA.put(XMaterial.BLAZE_SPAWN_EGG, new SpawnEggTintData(16167425, 16775294));
        SPAWN_EGG_TINT_DATA.put(XMaterial.CAT_SPAWN_EGG, new SpawnEggTintData(15714446, 9794134));
        SPAWN_EGG_TINT_DATA.put(XMaterial.CAVE_SPIDER_SPAWN_EGG, new SpawnEggTintData(803406, 11013646));
        SPAWN_EGG_TINT_DATA.put(XMaterial.CHICKEN_SPAWN_EGG, new SpawnEggTintData(10592673, 16711680));
        SPAWN_EGG_TINT_DATA.put(XMaterial.COD_SPAWN_EGG, new SpawnEggTintData(12691306, 15058059));
        SPAWN_EGG_TINT_DATA.put(XMaterial.COW_SPAWN_EGG, new SpawnEggTintData(4470310, 10592673));
        SPAWN_EGG_TINT_DATA.put(XMaterial.CREEPER_SPAWN_EGG, new SpawnEggTintData(894731, 0));
        SPAWN_EGG_TINT_DATA.put(XMaterial.DOLPHIN_SPAWN_EGG, new SpawnEggTintData(2243405, 16382457));
        SPAWN_EGG_TINT_DATA.put(XMaterial.DONKEY_SPAWN_EGG, new SpawnEggTintData(5457209, 8811878));
        SPAWN_EGG_TINT_DATA.put(XMaterial.DROWNED_SPAWN_EGG, new SpawnEggTintData(9433559, 7969893));
        SPAWN_EGG_TINT_DATA.put(XMaterial.ELDER_GUARDIAN_SPAWN_EGG, new SpawnEggTintData(13552826, 7632531));
        SPAWN_EGG_TINT_DATA.put(XMaterial.ENDERMAN_SPAWN_EGG, new SpawnEggTintData(1447446, 0));
        SPAWN_EGG_TINT_DATA.put(XMaterial.ENDERMITE_SPAWN_EGG, new SpawnEggTintData(1447446, 7237230));
        SPAWN_EGG_TINT_DATA.put(XMaterial.EVOKER_SPAWN_EGG, new SpawnEggTintData(9804699, 1973274));
        SPAWN_EGG_TINT_DATA.put(XMaterial.FOX_SPAWN_EGG, new SpawnEggTintData(14005919, 13396256));
        SPAWN_EGG_TINT_DATA.put(XMaterial.GHAST_SPAWN_EGG, new SpawnEggTintData(16382457, 12369084));
        SPAWN_EGG_TINT_DATA.put(XMaterial.GLOW_SQUID_SPAWN_EGG, new SpawnEggTintData(611926, 8778172));
        SPAWN_EGG_TINT_DATA.put(XMaterial.GOAT_SPAWN_EGG, new SpawnEggTintData(10851452, 5589310));
        SPAWN_EGG_TINT_DATA.put(XMaterial.GUARDIAN_SPAWN_EGG, new SpawnEggTintData(5931634, 15826224));
        SPAWN_EGG_TINT_DATA.put(XMaterial.HOGLIN_SPAWN_EGG, new SpawnEggTintData(13004373, 6251620));
        SPAWN_EGG_TINT_DATA.put(XMaterial.HORSE_SPAWN_EGG, new SpawnEggTintData(12623485, 15656192));
        SPAWN_EGG_TINT_DATA.put(XMaterial.HUSK_SPAWN_EGG, new SpawnEggTintData(7958625, 15125652));
        SPAWN_EGG_TINT_DATA.put(XMaterial.LLAMA_SPAWN_EGG, new SpawnEggTintData(12623485, 10051392));
        SPAWN_EGG_TINT_DATA.put(XMaterial.MAGMA_CUBE_SPAWN_EGG, new SpawnEggTintData(3407872, 16579584));
        SPAWN_EGG_TINT_DATA.put(XMaterial.MOOSHROOM_SPAWN_EGG, new SpawnEggTintData(10489616, 12040119));
        SPAWN_EGG_TINT_DATA.put(XMaterial.MULE_SPAWN_EGG, new SpawnEggTintData(1769984, 5321501));
        SPAWN_EGG_TINT_DATA.put(XMaterial.OCELOT_SPAWN_EGG, new SpawnEggTintData(15720061, 5653556));
        SPAWN_EGG_TINT_DATA.put(XMaterial.PANDA_SPAWN_EGG, new SpawnEggTintData(15198183, 1776418));
        SPAWN_EGG_TINT_DATA.put(XMaterial.PARROT_SPAWN_EGG, new SpawnEggTintData(894731, 16711680));
        SPAWN_EGG_TINT_DATA.put(XMaterial.PHANTOM_SPAWN_EGG, new SpawnEggTintData(4411786, 8978176));
        SPAWN_EGG_TINT_DATA.put(XMaterial.PIG_SPAWN_EGG, new SpawnEggTintData(15771042, 14377823));
        SPAWN_EGG_TINT_DATA.put(XMaterial.PIGLIN_SPAWN_EGG, new SpawnEggTintData(10051392, 16380836));
        SPAWN_EGG_TINT_DATA.put(XMaterial.PIGLIN_BRUTE_SPAWN_EGG, new SpawnEggTintData(5843472, 16380836));
        SPAWN_EGG_TINT_DATA.put(XMaterial.PILLAGER_SPAWN_EGG, new SpawnEggTintData(5451574, 9804699));
        SPAWN_EGG_TINT_DATA.put(XMaterial.POLAR_BEAR_SPAWN_EGG, new SpawnEggTintData(15921906, 9803152));
        SPAWN_EGG_TINT_DATA.put(XMaterial.PUFFERFISH_SPAWN_EGG, new SpawnEggTintData(16167425, 3654642));
        SPAWN_EGG_TINT_DATA.put(XMaterial.RABBIT_SPAWN_EGG, new SpawnEggTintData(10051392, 7555121));
        SPAWN_EGG_TINT_DATA.put(XMaterial.RAVAGER_SPAWN_EGG, new SpawnEggTintData(7697520, 5984329));
        SPAWN_EGG_TINT_DATA.put(XMaterial.SALMON_SPAWN_EGG, new SpawnEggTintData(10489616, 951412));
        SPAWN_EGG_TINT_DATA.put(XMaterial.SHEEP_SPAWN_EGG, new SpawnEggTintData(15198183, 16758197));
        SPAWN_EGG_TINT_DATA.put(XMaterial.SHULKER_SPAWN_EGG, new SpawnEggTintData(9725844, 5060690));
        SPAWN_EGG_TINT_DATA.put(XMaterial.SILVERFISH_SPAWN_EGG, new SpawnEggTintData(7237230, 3158064));
        SPAWN_EGG_TINT_DATA.put(XMaterial.SKELETON_SPAWN_EGG, new SpawnEggTintData(12698049, 4802889));
        SPAWN_EGG_TINT_DATA.put(XMaterial.SKELETON_HORSE_SPAWN_EGG, new SpawnEggTintData(6842447, 15066584));
        SPAWN_EGG_TINT_DATA.put(XMaterial.SLIME_SPAWN_EGG, new SpawnEggTintData(5349438, 8306542));
        SPAWN_EGG_TINT_DATA.put(XMaterial.SPIDER_SPAWN_EGG, new SpawnEggTintData(3419431, 11013646));
        SPAWN_EGG_TINT_DATA.put(XMaterial.SQUID_SPAWN_EGG, new SpawnEggTintData(2243405, 7375001));
        SPAWN_EGG_TINT_DATA.put(XMaterial.STRAY_SPAWN_EGG, new SpawnEggTintData(6387319, 14543594));
        SPAWN_EGG_TINT_DATA.put(XMaterial.STRIDER_SPAWN_EGG, new SpawnEggTintData(10236982, 5065037));
        SPAWN_EGG_TINT_DATA.put(XMaterial.TRADER_LLAMA_SPAWN_EGG, new SpawnEggTintData(15377456, 4547222));
        SPAWN_EGG_TINT_DATA.put(XMaterial.TROPICAL_FISH_SPAWN_EGG, new SpawnEggTintData(15690005, 16775663));
        SPAWN_EGG_TINT_DATA.put(XMaterial.TURTLE_SPAWN_EGG, new SpawnEggTintData(15198183, 44975));
        SPAWN_EGG_TINT_DATA.put(XMaterial.VEX_SPAWN_EGG, new SpawnEggTintData(8032420, 15265265));
        SPAWN_EGG_TINT_DATA.put(XMaterial.VILLAGER_SPAWN_EGG, new SpawnEggTintData(5651507, 12422002));
        SPAWN_EGG_TINT_DATA.put(XMaterial.VINDICATOR_SPAWN_EGG, new SpawnEggTintData(9804699, 2580065));
        SPAWN_EGG_TINT_DATA.put(XMaterial.WANDERING_TRADER_SPAWN_EGG, new SpawnEggTintData(4547222, 15377456));
        SPAWN_EGG_TINT_DATA.put(XMaterial.WITCH_SPAWN_EGG, new SpawnEggTintData(3407872, 5349438));
        SPAWN_EGG_TINT_DATA.put(XMaterial.WITHER_SKELETON_SPAWN_EGG, new SpawnEggTintData(1315860, 4672845));
        SPAWN_EGG_TINT_DATA.put(XMaterial.WOLF_SPAWN_EGG, new SpawnEggTintData(14144467, 13545366));
        SPAWN_EGG_TINT_DATA.put(XMaterial.ZOGLIN_SPAWN_EGG, new SpawnEggTintData(13004373, 15132390));
        SPAWN_EGG_TINT_DATA.put(XMaterial.ZOMBIE_SPAWN_EGG, new SpawnEggTintData(44975, 7969893));
        SPAWN_EGG_TINT_DATA.put(XMaterial.ZOMBIE_HORSE_SPAWN_EGG, new SpawnEggTintData(3232308, 9945732));
        SPAWN_EGG_TINT_DATA.put(XMaterial.ZOMBIE_VILLAGER_SPAWN_EGG, new SpawnEggTintData(5651507, 7969893));
        SPAWN_EGG_TINT_DATA.put(XMaterial.ZOMBIFIED_PIGLIN_SPAWN_EGG, new SpawnEggTintData(15373203, 5009705));
    }

    public static void setGrassAndFoliageColorMap(int[] grassColorMap, int[] foliageColorMap) {
        GRASS_TINT_PIXELS = grassColorMap;
        FOLIAGE_TINT_PIXELS = foliageColorMap;
    }

    public static TintIndexData getTintData(XMaterial material) {
        return TINT_DATA.getOrDefault(material, TintIndexData.EMPTY_INSTANCE);
    }

    public static SpawnEggTintData getSpawnEggTint(XMaterial spawnEgg) {
        return SPAWN_EGG_TINT_DATA.get(spawnEgg);
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

    public static class TintIndexData {

        public static final TintIndexData EMPTY_INSTANCE = new TintIndexData(Collections.emptyList());

        private List<IntSupplier> data;

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
            return 0xFFFFFF;
        }

        public int[] getAvailableTintIndexes() {
            return IntStream.range(0, data.size()).filter(i -> data.get(i) != null).toArray();
        }

    }

    public static class SpawnEggTintData {

        private int base;
        private int overlay;

        public SpawnEggTintData(int base, int overlay) {
            this.base = base;
            this.overlay = overlay;
        }

        public int getBase() {
            return base;
        }

        public int getOverlay() {
            return overlay;
        }

    }

}
