package com.loohp.interactivechatdiscordsrvaddon.utils;

import com.loohp.interactivechat.libs.com.cryptomorin.xseries.XMaterial;
import com.loohp.interactivechat.utils.ColorUtils;
import com.loohp.interactivechatdiscordsrvaddon.graphics.ImageUtils;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TintUtils {

    private static final List<TintIndexData> TINT_DATA = new ArrayList<>();
    private static final Map<XMaterial, SpawnEggTintData> SPAWN_EGG_TINT_DATA = new HashMap<>();

    static {
        TINT_DATA.add(new TintIndexData((84.0 / 256.0) / (169.0 / 256.0), (124.0 / 256.0) / (169.0 / 256.0), (70.0 / 256.0) / (169.0 / 256.0)));

        SPAWN_EGG_TINT_DATA.put(XMaterial.AXOLOTL_SPAWN_EGG, new SpawnEggTintData(ColorUtils.hex2Rgb("#fcc5e8"), ColorUtils.hex2Rgb("#9c2b6c")));
        SPAWN_EGG_TINT_DATA.put(XMaterial.BAT_SPAWN_EGG, new SpawnEggTintData(ColorUtils.hex2Rgb("#4c3e30"), ColorUtils.hex2Rgb("#0e0e0e")));
        SPAWN_EGG_TINT_DATA.put(XMaterial.BEE_SPAWN_EGG, new SpawnEggTintData(ColorUtils.hex2Rgb("#edc343"), ColorUtils.hex2Rgb("#3d2118")));
        SPAWN_EGG_TINT_DATA.put(XMaterial.BLAZE_SPAWN_EGG, new SpawnEggTintData(ColorUtils.hex2Rgb("#f6b201"), ColorUtils.hex2Rgb("#eae373")));
        SPAWN_EGG_TINT_DATA.put(XMaterial.CAT_SPAWN_EGG, new SpawnEggTintData(ColorUtils.hex2Rgb("#efc88e"), ColorUtils.hex2Rgb("#89694e")));
        SPAWN_EGG_TINT_DATA.put(XMaterial.CAVE_SPIDER_SPAWN_EGG, new SpawnEggTintData(ColorUtils.hex2Rgb("#0c424e"), ColorUtils.hex2Rgb("#9b0d0d")));
        SPAWN_EGG_TINT_DATA.put(XMaterial.CHICKEN_SPAWN_EGG, new SpawnEggTintData(ColorUtils.hex2Rgb("#a1a1a1"), ColorUtils.hex2Rgb("#ea0000")));
        SPAWN_EGG_TINT_DATA.put(XMaterial.COD_SPAWN_EGG, new SpawnEggTintData(ColorUtils.hex2Rgb("#c1a76a"), ColorUtils.hex2Rgb("#d2b480")));
        SPAWN_EGG_TINT_DATA.put(XMaterial.COW_SPAWN_EGG, new SpawnEggTintData(ColorUtils.hex2Rgb("#443626"), ColorUtils.hex2Rgb("#949494")));
        SPAWN_EGG_TINT_DATA.put(XMaterial.CREEPER_SPAWN_EGG, new SpawnEggTintData(ColorUtils.hex2Rgb("#0da70b"), ColorUtils.hex2Rgb("#000000")));
        SPAWN_EGG_TINT_DATA.put(XMaterial.DOLPHIN_SPAWN_EGG, new SpawnEggTintData(ColorUtils.hex2Rgb("#223b4d"), ColorUtils.hex2Rgb("#e4e4e4")));
        SPAWN_EGG_TINT_DATA.put(XMaterial.DONKEY_SPAWN_EGG, new SpawnEggTintData(ColorUtils.hex2Rgb("#534539"), ColorUtils.hex2Rgb("#7b6b5d")));
        SPAWN_EGG_TINT_DATA.put(XMaterial.DROWNED_SPAWN_EGG, new SpawnEggTintData(ColorUtils.hex2Rgb("#8ff0d7"), ColorUtils.hex2Rgb("#6f8f5c")));
        SPAWN_EGG_TINT_DATA.put(XMaterial.ELDER_GUARDIAN_SPAWN_EGG, new SpawnEggTintData(ColorUtils.hex2Rgb("#ceccba"), ColorUtils.hex2Rgb("#6a6c87")));
        SPAWN_EGG_TINT_DATA.put(XMaterial.ENDERMAN_SPAWN_EGG, new SpawnEggTintData(ColorUtils.hex2Rgb("#161616"), ColorUtils.hex2Rgb("#000000")));
        SPAWN_EGG_TINT_DATA.put(XMaterial.ENDERMITE_SPAWN_EGG, new SpawnEggTintData(ColorUtils.hex2Rgb("#161616"), ColorUtils.hex2Rgb("#666666")));
        SPAWN_EGG_TINT_DATA.put(XMaterial.EVOKER_SPAWN_EGG, new SpawnEggTintData(ColorUtils.hex2Rgb("#959a9a"), ColorUtils.hex2Rgb("#1c1918")));
        SPAWN_EGG_TINT_DATA.put(XMaterial.FOX_SPAWN_EGG, new SpawnEggTintData(ColorUtils.hex2Rgb("#d5b69f"), ColorUtils.hex2Rgb("#bb611d")));
        SPAWN_EGG_TINT_DATA.put(XMaterial.GHAST_SPAWN_EGG, new SpawnEggTintData(ColorUtils.hex2Rgb("#f9f9f9"), ColorUtils.hex2Rgb("#adadad")));
        SPAWN_EGG_TINT_DATA.put(XMaterial.GLOW_SQUID_SPAWN_EGG, new SpawnEggTintData(ColorUtils.hex2Rgb("#095858"), ColorUtils.hex2Rgb("#7de2b1")));
        SPAWN_EGG_TINT_DATA.put(XMaterial.GOAT_SPAWN_EGG, new SpawnEggTintData(ColorUtils.hex2Rgb("#c0ac90"), ColorUtils.hex2Rgb("#7a6959")));
        SPAWN_EGG_TINT_DATA.put(XMaterial.GUARDIAN_SPAWN_EGG, new SpawnEggTintData(ColorUtils.hex2Rgb("#5a8272"), ColorUtils.hex2Rgb("#de722c")));
        SPAWN_EGG_TINT_DATA.put(XMaterial.HOGLIN_SPAWN_EGG, new SpawnEggTintData(ColorUtils.hex2Rgb("#ca7057"), ColorUtils.hex2Rgb("#595d5d")));
        SPAWN_EGG_TINT_DATA.put(XMaterial.HORSE_SPAWN_EGG, new SpawnEggTintData(ColorUtils.hex2Rgb("#c09e7d"), ColorUtils.hex2Rgb("#dad200")));
        SPAWN_EGG_TINT_DATA.put(XMaterial.HUSK_SPAWN_EGG, new SpawnEggTintData(ColorUtils.hex2Rgb("#797061"), ColorUtils.hex2Rgb("#d3bb88")));
        SPAWN_EGG_TINT_DATA.put(XMaterial.LLAMA_SPAWN_EGG, new SpawnEggTintData(ColorUtils.hex2Rgb("#c09e7d"), ColorUtils.hex2Rgb("#8d583b")));
        SPAWN_EGG_TINT_DATA.put(XMaterial.MAGMA_CUBE_SPAWN_EGG, new SpawnEggTintData(ColorUtils.hex2Rgb("#340000"), ColorUtils.hex2Rgb("#e8e800")));
        SPAWN_EGG_TINT_DATA.put(XMaterial.MOOSHROOM_SPAWN_EGG, new SpawnEggTintData(ColorUtils.hex2Rgb("#a00f10"), ColorUtils.hex2Rgb("#a8a8a8")));
        SPAWN_EGG_TINT_DATA.put(XMaterial.MULE_SPAWN_EGG, new SpawnEggTintData(ColorUtils.hex2Rgb("#1b0200"), ColorUtils.hex2Rgb("#4a2f1b")));
        SPAWN_EGG_TINT_DATA.put(XMaterial.OCELOT_SPAWN_EGG, new SpawnEggTintData(ColorUtils.hex2Rgb("#eede7d"), ColorUtils.hex2Rgb("#4e3e2f")));
        SPAWN_EGG_TINT_DATA.put(XMaterial.PANDA_SPAWN_EGG, new SpawnEggTintData(ColorUtils.hex2Rgb("#e7e7e7"), ColorUtils.hex2Rgb("#18181f")));
        SPAWN_EGG_TINT_DATA.put(XMaterial.PARROT_SPAWN_EGG, new SpawnEggTintData(ColorUtils.hex2Rgb("#0da70b"), ColorUtils.hex2Rgb("#ea0000")));
        SPAWN_EGG_TINT_DATA.put(XMaterial.PHANTOM_SPAWN_EGG, new SpawnEggTintData(ColorUtils.hex2Rgb("#43518a"), ColorUtils.hex2Rgb("#7dea00")));
        SPAWN_EGG_TINT_DATA.put(XMaterial.PIG_SPAWN_EGG, new SpawnEggTintData(ColorUtils.hex2Rgb("#efa5a2"), ColorUtils.hex2Rgb("#c95b58")));
        SPAWN_EGG_TINT_DATA.put(XMaterial.PIGLIN_SPAWN_EGG, new SpawnEggTintData(ColorUtils.hex2Rgb("#9b6041"), ColorUtils.hex2Rgb("#e8e399")));
        SPAWN_EGG_TINT_DATA.put(XMaterial.PIGLIN_BRUTE_SPAWN_EGG, new SpawnEggTintData(ColorUtils.hex2Rgb("#592910"), ColorUtils.hex2Rgb("#e4df97")));
        SPAWN_EGG_TINT_DATA.put(XMaterial.PILLAGER_SPAWN_EGG, new SpawnEggTintData(ColorUtils.hex2Rgb("#532f36"), ColorUtils.hex2Rgb("#898e8e")));
        SPAWN_EGG_TINT_DATA.put(XMaterial.POLAR_BEAR_SPAWN_EGG, new SpawnEggTintData(ColorUtils.hex2Rgb("#f2f2f2"), ColorUtils.hex2Rgb("#898985")));
        SPAWN_EGG_TINT_DATA.put(XMaterial.PUFFERFISH_SPAWN_EGG, new SpawnEggTintData(ColorUtils.hex2Rgb("#f6b201"), ColorUtils.hex2Rgb("#33b3df")));
        SPAWN_EGG_TINT_DATA.put(XMaterial.RABBIT_SPAWN_EGG, new SpawnEggTintData(ColorUtils.hex2Rgb("#995f40"), ColorUtils.hex2Rgb("#6a422d")));
        SPAWN_EGG_TINT_DATA.put(XMaterial.RAVAGER_SPAWN_EGG, new SpawnEggTintData(ColorUtils.hex2Rgb("#757470"), ColorUtils.hex2Rgb("#534a43")));
        SPAWN_EGG_TINT_DATA.put(XMaterial.SALMON_SPAWN_EGG, new SpawnEggTintData(ColorUtils.hex2Rgb("#a00f10"), ColorUtils.hex2Rgb("#0d796a")));
        SPAWN_EGG_TINT_DATA.put(XMaterial.SHEEP_SPAWN_EGG, new SpawnEggTintData(ColorUtils.hex2Rgb("#e7e7e7"), ColorUtils.hex2Rgb("#eaa6a6")));
        SPAWN_EGG_TINT_DATA.put(XMaterial.SHULKER_SPAWN_EGG, new SpawnEggTintData(ColorUtils.hex2Rgb("#946794"), ColorUtils.hex2Rgb("#46344b")));
        SPAWN_EGG_TINT_DATA.put(XMaterial.SILVERFISH_SPAWN_EGG, new SpawnEggTintData(ColorUtils.hex2Rgb("#6e6e6e"), ColorUtils.hex2Rgb("#2c2c2c")));
        SPAWN_EGG_TINT_DATA.put(XMaterial.SKELETON_SPAWN_EGG, new SpawnEggTintData(ColorUtils.hex2Rgb("#c1c1c1"), ColorUtils.hex2Rgb("#434343")));
        SPAWN_EGG_TINT_DATA.put(XMaterial.SKELETON_HORSE_SPAWN_EGG, new SpawnEggTintData(ColorUtils.hex2Rgb("#68684f"), ColorUtils.hex2Rgb("#d2d2c6")));
        SPAWN_EGG_TINT_DATA.put(XMaterial.SLIME_SPAWN_EGG, new SpawnEggTintData(ColorUtils.hex2Rgb("#51a03e"), ColorUtils.hex2Rgb("#73af66")));
        SPAWN_EGG_TINT_DATA.put(XMaterial.SPIDER_SPAWN_EGG, new SpawnEggTintData(ColorUtils.hex2Rgb("#342d27"), ColorUtils.hex2Rgb("#9b0d0d")));
        SPAWN_EGG_TINT_DATA.put(XMaterial.SQUID_SPAWN_EGG, new SpawnEggTintData(ColorUtils.hex2Rgb("#223b4d"), ColorUtils.hex2Rgb("#677d8d")));
        SPAWN_EGG_TINT_DATA.put(XMaterial.STRAY_SPAWN_EGG, new SpawnEggTintData(ColorUtils.hex2Rgb("#617677"), ColorUtils.hex2Rgb("#cbd7d7")));
        SPAWN_EGG_TINT_DATA.put(XMaterial.STRIDER_SPAWN_EGG, new SpawnEggTintData(ColorUtils.hex2Rgb("#9b3436"), ColorUtils.hex2Rgb("#464346")));
        SPAWN_EGG_TINT_DATA.put(XMaterial.TRADER_LLAMA_SPAWN_EGG, new SpawnEggTintData(ColorUtils.hex2Rgb("#eaa430"), ColorUtils.hex2Rgb("#3f5a89")));
        SPAWN_EGG_TINT_DATA.put(XMaterial.TROPICAL_FISH_SPAWN_EGG, new SpawnEggTintData(ColorUtils.hex2Rgb("#ef6915"), ColorUtils.hex2Rgb("#eae4db")));
        SPAWN_EGG_TINT_DATA.put(XMaterial.TURTLE_SPAWN_EGG, new SpawnEggTintData(ColorUtils.hex2Rgb("#e7e7e7"), ColorUtils.hex2Rgb("#00a0a0")));
        SPAWN_EGG_TINT_DATA.put(XMaterial.VEX_SPAWN_EGG, new SpawnEggTintData(ColorUtils.hex2Rgb("#7a90a4"), ColorUtils.hex2Rgb("#d5dade")));
        SPAWN_EGG_TINT_DATA.put(XMaterial.VILLAGER_SPAWN_EGG, new SpawnEggTintData(ColorUtils.hex2Rgb("#563c33"), ColorUtils.hex2Rgb("#ad8069")));
        SPAWN_EGG_TINT_DATA.put(XMaterial.VINDICATOR_SPAWN_EGG, new SpawnEggTintData(ColorUtils.hex2Rgb("#959a9a"), ColorUtils.hex2Rgb("#245759")));
        SPAWN_EGG_TINT_DATA.put(XMaterial.WANDERING_TRADER_SPAWN_EGG, new SpawnEggTintData(ColorUtils.hex2Rgb("#456296"), ColorUtils.hex2Rgb("#d7972c")));
        SPAWN_EGG_TINT_DATA.put(XMaterial.WITCH_SPAWN_EGG, new SpawnEggTintData(ColorUtils.hex2Rgb("#340000"), ColorUtils.hex2Rgb("#4a9339")));
        SPAWN_EGG_TINT_DATA.put(XMaterial.WITHER_SKELETON_SPAWN_EGG, new SpawnEggTintData(ColorUtils.hex2Rgb("#141414"), ColorUtils.hex2Rgb("#414646")));
        SPAWN_EGG_TINT_DATA.put(XMaterial.WOLF_SPAWN_EGG, new SpawnEggTintData(ColorUtils.hex2Rgb("#d7d2d2"), ColorUtils.hex2Rgb("#bda089")));
        SPAWN_EGG_TINT_DATA.put(XMaterial.ZOGLIN_SPAWN_EGG, new SpawnEggTintData(ColorUtils.hex2Rgb("#ca7057"), ColorUtils.hex2Rgb("#d8d8d8")));
        SPAWN_EGG_TINT_DATA.put(XMaterial.ZOMBIE_SPAWN_EGG, new SpawnEggTintData(ColorUtils.hex2Rgb("#00afaf"), ColorUtils.hex2Rgb("#6f8f5c")));
        SPAWN_EGG_TINT_DATA.put(XMaterial.ZOMBIE_HORSE_SPAWN_EGG, new SpawnEggTintData(ColorUtils.hex2Rgb("#315234"), ColorUtils.hex2Rgb("#8ab279")));
        SPAWN_EGG_TINT_DATA.put(XMaterial.ZOMBIE_VILLAGER_SPAWN_EGG, new SpawnEggTintData(ColorUtils.hex2Rgb("#563c33"), ColorUtils.hex2Rgb("#6f8f5c")));
        SPAWN_EGG_TINT_DATA.put(XMaterial.ZOMBIFIED_PIGLIN_SPAWN_EGG, new SpawnEggTintData(ColorUtils.hex2Rgb("#ea9393"), ColorUtils.hex2Rgb("#456826")));
    }

    public static BufferedImage applyTint(BufferedImage image, int tintIndex) {
        if (tintIndex >= 0 && tintIndex < TINT_DATA.size()) {
            TintIndexData data = TINT_DATA.get(tintIndex);
            if (data != null) {
                return ImageUtils.multiply(image, data.getX(), data.getY(), data.getZ());
            }
        }
        return image;
    }

    public static SpawnEggTintData getSpawnEggTint(XMaterial spawnEgg) {
        return SPAWN_EGG_TINT_DATA.get(spawnEgg);
    }

    public static class TintIndexData {

        private double x;
        private double y;
        private double z;

        public TintIndexData(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }

        public double getZ() {
            return z;
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
