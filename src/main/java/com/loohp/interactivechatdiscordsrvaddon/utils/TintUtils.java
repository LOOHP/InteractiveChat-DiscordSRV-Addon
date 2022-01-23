package com.loohp.interactivechatdiscordsrvaddon.utils;

import com.loohp.blockmodelrenderer.utils.ColorUtils;
import com.loohp.interactivechat.libs.com.cryptomorin.xseries.XMaterial;
import com.loohp.interactivechatdiscordsrvaddon.graphics.ImageUtils;

import java.awt.Color;
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

        SPAWN_EGG_TINT_DATA.put(XMaterial.AXOLOTL_SPAWN_EGG, new SpawnEggTintData(new Color(16499171), new Color(10890612)));
        SPAWN_EGG_TINT_DATA.put(XMaterial.BAT_SPAWN_EGG, new SpawnEggTintData(new Color(4996656), new Color(986895)));
        SPAWN_EGG_TINT_DATA.put(XMaterial.BEE_SPAWN_EGG, new SpawnEggTintData(new Color(15582019), new Color(4400155)));
        SPAWN_EGG_TINT_DATA.put(XMaterial.BLAZE_SPAWN_EGG, new SpawnEggTintData(new Color(16167425), new Color(16775294)));
        SPAWN_EGG_TINT_DATA.put(XMaterial.CAT_SPAWN_EGG, new SpawnEggTintData(new Color(15714446), new Color(9794134)));
        SPAWN_EGG_TINT_DATA.put(XMaterial.CAVE_SPIDER_SPAWN_EGG, new SpawnEggTintData(new Color(803406), new Color(11013646)));
        SPAWN_EGG_TINT_DATA.put(XMaterial.CHICKEN_SPAWN_EGG, new SpawnEggTintData(new Color(10592673), new Color(16711680)));
        SPAWN_EGG_TINT_DATA.put(XMaterial.COD_SPAWN_EGG, new SpawnEggTintData(new Color(12691306), new Color(15058059)));
        SPAWN_EGG_TINT_DATA.put(XMaterial.COW_SPAWN_EGG, new SpawnEggTintData(new Color(4470310), new Color(10592673)));
        SPAWN_EGG_TINT_DATA.put(XMaterial.CREEPER_SPAWN_EGG, new SpawnEggTintData(new Color(894731), new Color(0)));
        SPAWN_EGG_TINT_DATA.put(XMaterial.DOLPHIN_SPAWN_EGG, new SpawnEggTintData(new Color(2243405), new Color(16382457)));
        SPAWN_EGG_TINT_DATA.put(XMaterial.DONKEY_SPAWN_EGG, new SpawnEggTintData(new Color(5457209), new Color(8811878)));
        SPAWN_EGG_TINT_DATA.put(XMaterial.DROWNED_SPAWN_EGG, new SpawnEggTintData(new Color(9433559), new Color(7969893)));
        SPAWN_EGG_TINT_DATA.put(XMaterial.ELDER_GUARDIAN_SPAWN_EGG, new SpawnEggTintData(new Color(13552826), new Color(7632531)));
        SPAWN_EGG_TINT_DATA.put(XMaterial.ENDERMAN_SPAWN_EGG, new SpawnEggTintData(new Color(1447446), new Color(0)));
        SPAWN_EGG_TINT_DATA.put(XMaterial.ENDERMITE_SPAWN_EGG, new SpawnEggTintData(new Color(1447446), new Color(7237230)));
        SPAWN_EGG_TINT_DATA.put(XMaterial.EVOKER_SPAWN_EGG, new SpawnEggTintData(new Color(9804699), new Color(1973274)));
        SPAWN_EGG_TINT_DATA.put(XMaterial.FOX_SPAWN_EGG, new SpawnEggTintData(new Color(14005919), new Color(13396256)));
        SPAWN_EGG_TINT_DATA.put(XMaterial.GHAST_SPAWN_EGG, new SpawnEggTintData(new Color(16382457), new Color(12369084)));
        SPAWN_EGG_TINT_DATA.put(XMaterial.GLOW_SQUID_SPAWN_EGG, new SpawnEggTintData(new Color(611926), new Color(8778172)));
        SPAWN_EGG_TINT_DATA.put(XMaterial.GOAT_SPAWN_EGG, new SpawnEggTintData(new Color(10851452), new Color(5589310)));
        SPAWN_EGG_TINT_DATA.put(XMaterial.GUARDIAN_SPAWN_EGG, new SpawnEggTintData(new Color(5931634), new Color(15826224)));
        SPAWN_EGG_TINT_DATA.put(XMaterial.HOGLIN_SPAWN_EGG, new SpawnEggTintData(new Color(13004373), new Color(6251620)));
        SPAWN_EGG_TINT_DATA.put(XMaterial.HORSE_SPAWN_EGG, new SpawnEggTintData(new Color(12623485), new Color(15656192)));
        SPAWN_EGG_TINT_DATA.put(XMaterial.HUSK_SPAWN_EGG, new SpawnEggTintData(new Color(7958625), new Color(15125652)));
        SPAWN_EGG_TINT_DATA.put(XMaterial.LLAMA_SPAWN_EGG, new SpawnEggTintData(new Color(12623485), new Color(10051392)));
        SPAWN_EGG_TINT_DATA.put(XMaterial.MAGMA_CUBE_SPAWN_EGG, new SpawnEggTintData(new Color(3407872), new Color(16579584)));
        SPAWN_EGG_TINT_DATA.put(XMaterial.MOOSHROOM_SPAWN_EGG, new SpawnEggTintData(new Color(10489616), new Color(12040119)));
        SPAWN_EGG_TINT_DATA.put(XMaterial.MULE_SPAWN_EGG, new SpawnEggTintData(new Color(1769984), new Color(5321501)));
        SPAWN_EGG_TINT_DATA.put(XMaterial.OCELOT_SPAWN_EGG, new SpawnEggTintData(new Color(15720061), new Color(5653556)));
        SPAWN_EGG_TINT_DATA.put(XMaterial.PANDA_SPAWN_EGG, new SpawnEggTintData(new Color(15198183), new Color(1776418)));
        SPAWN_EGG_TINT_DATA.put(XMaterial.PARROT_SPAWN_EGG, new SpawnEggTintData(new Color(894731), new Color(16711680)));
        SPAWN_EGG_TINT_DATA.put(XMaterial.PHANTOM_SPAWN_EGG, new SpawnEggTintData(new Color(4411786), new Color(8978176)));
        SPAWN_EGG_TINT_DATA.put(XMaterial.PIG_SPAWN_EGG, new SpawnEggTintData(new Color(15771042), new Color(14377823)));
        SPAWN_EGG_TINT_DATA.put(XMaterial.PIGLIN_SPAWN_EGG, new SpawnEggTintData(new Color(10051392), new Color(16380836)));
        SPAWN_EGG_TINT_DATA.put(XMaterial.PIGLIN_BRUTE_SPAWN_EGG, new SpawnEggTintData(new Color(5843472), new Color(16380836)));
        SPAWN_EGG_TINT_DATA.put(XMaterial.PILLAGER_SPAWN_EGG, new SpawnEggTintData(new Color(5451574), new Color(9804699)));
        SPAWN_EGG_TINT_DATA.put(XMaterial.POLAR_BEAR_SPAWN_EGG, new SpawnEggTintData(new Color(15921906), new Color(9803152)));
        SPAWN_EGG_TINT_DATA.put(XMaterial.PUFFERFISH_SPAWN_EGG, new SpawnEggTintData(new Color(16167425), new Color(3654642)));
        SPAWN_EGG_TINT_DATA.put(XMaterial.RABBIT_SPAWN_EGG, new SpawnEggTintData(new Color(10051392), new Color(7555121)));
        SPAWN_EGG_TINT_DATA.put(XMaterial.RAVAGER_SPAWN_EGG, new SpawnEggTintData(new Color(7697520), new Color(5984329)));
        SPAWN_EGG_TINT_DATA.put(XMaterial.SALMON_SPAWN_EGG, new SpawnEggTintData(new Color(10489616), new Color(951412)));
        SPAWN_EGG_TINT_DATA.put(XMaterial.SHEEP_SPAWN_EGG, new SpawnEggTintData(new Color(15198183), new Color(16758197)));
        SPAWN_EGG_TINT_DATA.put(XMaterial.SHULKER_SPAWN_EGG, new SpawnEggTintData(new Color(9725844), new Color(5060690)));
        SPAWN_EGG_TINT_DATA.put(XMaterial.SILVERFISH_SPAWN_EGG, new SpawnEggTintData(new Color(7237230), new Color(3158064)));
        SPAWN_EGG_TINT_DATA.put(XMaterial.SKELETON_SPAWN_EGG, new SpawnEggTintData(new Color(12698049), new Color(4802889)));
        SPAWN_EGG_TINT_DATA.put(XMaterial.SKELETON_HORSE_SPAWN_EGG, new SpawnEggTintData(new Color(6842447), new Color(15066584)));
        SPAWN_EGG_TINT_DATA.put(XMaterial.SLIME_SPAWN_EGG, new SpawnEggTintData(new Color(5349438), new Color(8306542)));
        SPAWN_EGG_TINT_DATA.put(XMaterial.SPIDER_SPAWN_EGG, new SpawnEggTintData(new Color(3419431), new Color(11013646)));
        SPAWN_EGG_TINT_DATA.put(XMaterial.SQUID_SPAWN_EGG, new SpawnEggTintData(new Color(2243405), new Color(7375001)));
        SPAWN_EGG_TINT_DATA.put(XMaterial.STRAY_SPAWN_EGG, new SpawnEggTintData(new Color(6387319), new Color(14543594)));
        SPAWN_EGG_TINT_DATA.put(XMaterial.STRIDER_SPAWN_EGG, new SpawnEggTintData(new Color(10236982), new Color(5065037)));
        SPAWN_EGG_TINT_DATA.put(XMaterial.TRADER_LLAMA_SPAWN_EGG, new SpawnEggTintData(new Color(15377456), new Color(4547222)));
        SPAWN_EGG_TINT_DATA.put(XMaterial.TROPICAL_FISH_SPAWN_EGG, new SpawnEggTintData(new Color(15690005), new Color(16775663)));
        SPAWN_EGG_TINT_DATA.put(XMaterial.TURTLE_SPAWN_EGG, new SpawnEggTintData(new Color(15198183), new Color(44975)));
        SPAWN_EGG_TINT_DATA.put(XMaterial.VEX_SPAWN_EGG, new SpawnEggTintData(new Color(8032420), new Color(15265265)));
        SPAWN_EGG_TINT_DATA.put(XMaterial.VILLAGER_SPAWN_EGG, new SpawnEggTintData(new Color(5651507), new Color(12422002)));
        SPAWN_EGG_TINT_DATA.put(XMaterial.VINDICATOR_SPAWN_EGG, new SpawnEggTintData(new Color(9804699), new Color(2580065)));
        SPAWN_EGG_TINT_DATA.put(XMaterial.WANDERING_TRADER_SPAWN_EGG, new SpawnEggTintData(new Color(4547222), new Color(15377456)));
        SPAWN_EGG_TINT_DATA.put(XMaterial.WITCH_SPAWN_EGG, new SpawnEggTintData(new Color(3407872), new Color(5349438)));
        SPAWN_EGG_TINT_DATA.put(XMaterial.WITHER_SKELETON_SPAWN_EGG, new SpawnEggTintData(new Color(1315860), new Color(4672845)));
        SPAWN_EGG_TINT_DATA.put(XMaterial.WOLF_SPAWN_EGG, new SpawnEggTintData(new Color(14144467), new Color(13545366)));
        SPAWN_EGG_TINT_DATA.put(XMaterial.ZOGLIN_SPAWN_EGG, new SpawnEggTintData(new Color(13004373), new Color(15132390)));
        SPAWN_EGG_TINT_DATA.put(XMaterial.ZOMBIE_SPAWN_EGG, new SpawnEggTintData(new Color(44975), new Color(7969893)));
        SPAWN_EGG_TINT_DATA.put(XMaterial.ZOMBIE_HORSE_SPAWN_EGG, new SpawnEggTintData(new Color(3232308), new Color(9945732)));
        SPAWN_EGG_TINT_DATA.put(XMaterial.ZOMBIE_VILLAGER_SPAWN_EGG, new SpawnEggTintData(new Color(5651507), new Color(7969893)));
        SPAWN_EGG_TINT_DATA.put(XMaterial.ZOMBIFIED_PIGLIN_SPAWN_EGG, new SpawnEggTintData(new Color(15373203), new Color(5009705)));
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

        private Color base;
        private Color overlay;

        public SpawnEggTintData(Color base, Color overlay) {
            this.base = base;
            this.overlay = overlay;
        }

        public Color getBase() {
            return base;
        }

        public Color getOverlay() {
            return overlay;
        }

    }

}
