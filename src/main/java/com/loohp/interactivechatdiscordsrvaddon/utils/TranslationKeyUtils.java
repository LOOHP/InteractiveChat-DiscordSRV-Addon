package com.loohp.interactivechatdiscordsrvaddon.utils;

import com.loohp.interactivechat.InteractiveChat;
import com.loohp.interactivechat.libs.io.github.bananapuncher714.nbteditor.NBTEditor;
import com.loohp.interactivechat.utils.MCVersion;
import com.loohp.interactivechat.utils.NMSUtils;
import com.loohp.interactivechatdiscordsrvaddon.wrappers.PatternTypeWrapper;
import org.apache.commons.lang.WordUtils;
import org.bukkit.DyeColor;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.TropicalFish.Pattern;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta.Generation;
import org.bukkit.potion.PotionEffectType;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TranslationKeyUtils {

    private static final Map<Integer, Integer> PREDEFINED_TROPICAL_FISH = new HashMap<>();

    private static Method bukkitEnchantmentGetIdMethod;
    private static Class<?> nmsEnchantmentClass;
    private static Method getEnchantmentByIdMethod;
    private static Method getEnchantmentKeyMethod;
    private static Class<?> nmsMobEffectListClass;
    private static Field nmsMobEffectByIdField;
    private static Method getEffectFromIdMethod;
    private static Method getEffectKeyMethod;
    private static Class<?> craftItemStackClass;
    private static Class<?> nmsItemStackClass;
    private static Method asNMSCopyMethod;
    private static Method nmsGetItemMethod;
    private static Class<?> nmsItemRecordClass;
    private static Field nmsItemRecordTranslationKeyField;
    private static Class<?> craftTropicalFishClass;
    private static Method getTropicalFishPatternMethod;
    private static Method getTropicalFishPatternColorMethod;
    private static Method getTropicalFishBodyColorMethod;

    static {
        if (InteractiveChat.version.isLegacy()) {
            try {
                bukkitEnchantmentGetIdMethod = Enchantment.class.getMethod("getId");
                nmsEnchantmentClass = NMSUtils.getNMSClass("net.minecraft.server.%s.Enchantment", "net.minecraft.world.item.enchantment.Enchantment");
                if (InteractiveChat.version.isOld()) {
                    getEnchantmentByIdMethod = nmsEnchantmentClass.getMethod("getById", int.class);
                } else {
                    getEnchantmentByIdMethod = nmsEnchantmentClass.getMethod("c", int.class);
                }
                getEnchantmentKeyMethod = nmsEnchantmentClass.getMethod("a");
                nmsMobEffectListClass = NMSUtils.getNMSClass("net.minecraft.server.%s.MobEffectList", "net.minecraft.world.effect.MobEffectList");
                if (InteractiveChat.version.isOld()) {
                    nmsMobEffectByIdField = nmsMobEffectListClass.getField("byId");
                } else {
                    getEffectFromIdMethod = nmsMobEffectListClass.getMethod("fromId", int.class);
                }
                getEffectKeyMethod = nmsMobEffectListClass.getMethod("a");

                nmsItemRecordClass = NMSUtils.getNMSClass("net.minecraft.server.%s.ItemRecord", "net.minecraft.world.item.ItemRecord");
                try {
                    nmsItemRecordTranslationKeyField = nmsItemRecordClass.getDeclaredField("c");
                } catch (NoSuchFieldException e) {
                    nmsItemRecordTranslationKeyField = nmsItemRecordClass.getDeclaredField("a");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                if (InteractiveChat.version.isOlderOrEqualTo(MCVersion.V1_17)) {
                    nmsMobEffectListClass = NMSUtils.getNMSClass("net.minecraft.server.%s.MobEffectList", "net.minecraft.world.effect.MobEffectList");
                    try {
                        getEffectFromIdMethod = nmsMobEffectListClass.getMethod("fromId", int.class);
                    } catch (Exception e) {
                        getEffectFromIdMethod = nmsMobEffectListClass.getMethod("byId", int.class);
                    }
                    getEffectKeyMethod = nmsMobEffectListClass.getMethod("c");
                }

                craftTropicalFishClass = NMSUtils.getNMSClass("org.bukkit.craftbukkit.%s.entity.CraftTropicalFish");
                getTropicalFishPatternMethod = craftTropicalFishClass.getMethod("getPattern", int.class);
                getTropicalFishPatternColorMethod = craftTropicalFishClass.getMethod("getPatternColor", int.class);
                getTropicalFishBodyColorMethod = craftTropicalFishClass.getMethod("getBodyColor", int.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            craftItemStackClass = NMSUtils.getNMSClass("org.bukkit.craftbukkit.%s.inventory.CraftItemStack");
            nmsItemStackClass = NMSUtils.getNMSClass("net.minecraft.server.%s.ItemStack", "net.minecraft.world.item.ItemStack");
            asNMSCopyMethod = craftItemStackClass.getMethod("asNMSCopy", ItemStack.class);
            try {
                nmsGetItemMethod = nmsItemStackClass.getMethod("getItem");
            } catch (Exception e) {
                nmsGetItemMethod = nmsItemStackClass.getMethod("c");
            }
        } catch (ClassNotFoundException | SecurityException | NoSuchMethodException e) {
            e.printStackTrace();
        }

        PREDEFINED_TROPICAL_FISH.put(117506305, 0);
        PREDEFINED_TROPICAL_FISH.put(117899265, 1);
        PREDEFINED_TROPICAL_FISH.put(185008129, 2);
        PREDEFINED_TROPICAL_FISH.put(117441793, 3);
        PREDEFINED_TROPICAL_FISH.put(118161664, 4);
        PREDEFINED_TROPICAL_FISH.put(65536, 5);
        PREDEFINED_TROPICAL_FISH.put(50726144, 6);
        PREDEFINED_TROPICAL_FISH.put(67764993, 7);
        PREDEFINED_TROPICAL_FISH.put(234882305, 8);
        PREDEFINED_TROPICAL_FISH.put(67110144, 9);
        PREDEFINED_TROPICAL_FISH.put(117441025, 10);
        PREDEFINED_TROPICAL_FISH.put(16778497, 11);
        PREDEFINED_TROPICAL_FISH.put(101253888, 12);
        PREDEFINED_TROPICAL_FISH.put(50660352, 13);
        PREDEFINED_TROPICAL_FISH.put(918529, 14);
        PREDEFINED_TROPICAL_FISH.put(235340288, 15);
        PREDEFINED_TROPICAL_FISH.put(918273, 16);
        PREDEFINED_TROPICAL_FISH.put(67108865, 17);
        PREDEFINED_TROPICAL_FISH.put(917504, 18);
        PREDEFINED_TROPICAL_FISH.put(459008, 19);
        PREDEFINED_TROPICAL_FISH.put(67699456, 20);
        PREDEFINED_TROPICAL_FISH.put(67371009, 21);
    }

    public static String getOldIncompatiblePack() {
        if (InteractiveChat.version.isLegacy()) {
            return "resourcePack.incompatible.old";
        } else {
            return "pack.incompatible.old";
        }
    }

    public static String getNewIncompatiblePack() {
        if (InteractiveChat.version.isLegacy()) {
            return "resourcePack.incompatible.new";
        } else {
            return "pack.incompatible.new";
        }
    }

    public static String getFilledMapId() {
        return "filled_map.id";
    }

    public static String getFilledMapScale() {
        return "filled_map.scale";
    }

    public static String getFilledMapLevel() {
        return "filled_map.level";
    }

    public static String getNoEffect() {
        return "effect.none";
    }

    @SuppressWarnings("deprecation")
    public static String getEffect(PotionEffectType type) {
        if (InteractiveChat.version.isNewerOrEqualTo(MCVersion.V1_18)) {
            NamespacedKey namespacedKey = type.getKey();
            return "effect." + namespacedKey.getNamespace() + "." + namespacedKey.getKey();
        } else if (!InteractiveChat.version.isLegacy()) {
            try {
                int id = type.getId();
                Object nmsMobEffectListObject = getEffectFromIdMethod.invoke(null, id);
                if (nmsMobEffectListObject != null) {
                    return getEffectKeyMethod.invoke(nmsMobEffectListObject).toString();
                } else {
                    return "";
                }
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                e.printStackTrace();
                return "";
            }
        } else {
            try {
                int id = type.getId();
                Object nmsMobEffectListObject;
                if (InteractiveChat.version.isOlderOrEqualTo(MCVersion.V1_8_4)) {
                    Object nmsMobEffectListArray = nmsMobEffectByIdField.get(null);
                    if (Array.getLength(nmsMobEffectListArray) > id) {
                        nmsMobEffectListObject = Array.get(nmsMobEffectListArray, id);
                    } else {
                        return "";
                    }
                } else {
                    nmsMobEffectListObject = getEffectFromIdMethod.invoke(null, id);
                }
                if (nmsMobEffectListObject != null) {
                    String str = getEffectKeyMethod.invoke(nmsMobEffectListObject).toString();
                    return "effect." + str.substring(str.indexOf(".") + 1);
                } else {
                    return "";
                }
            } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
                return "";
            }
        }
    }

    public static String getEffectLevel(int level) {
        return "potion.potency." + level;
    }

    public static String getEnchantment(Enchantment enchantment) {
        if (!InteractiveChat.version.isLegacy()) {
            NamespacedKey namespacedKey = enchantment.getKey();
            return "enchantment." + namespacedKey.getNamespace() + "." + namespacedKey.getKey();
        } else {
            try {
                Object nmsEnchantmentObject = getEnchantmentByIdMethod.invoke(null, bukkitEnchantmentGetIdMethod.invoke(enchantment));
                if (nmsEnchantmentObject != null) {
                    return getEnchantmentKeyMethod.invoke(nmsEnchantmentObject).toString();
                } else {
                    return "";
                }
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                e.printStackTrace();
                return "";
            }
        }
    }

    public static String getEnchantmentLevel(int level) {
        return "enchantment.level." + level;
    }

    public static String getDyeColor() {
        return "item.color";
    }

    public static String getUnbreakable() {
        return "item.unbreakable";
    }

    public static String getDurability() {
        return "item.durability";
    }

    public static String getCrossbowProjectile() {
        return "item.minecraft.crossbow.projectile";
    }

    public static String getCopyToClipboard() {
        return "chat.copy";
    }

    public static String getOpenUrl() {
        return "chat.link.open";
    }

    public static String getRocketFlightDuration() {
        if (InteractiveChat.version.isLegacy()) {
            return "item.fireworks.flight";
        } else {
            return "item.minecraft.firework_rocket.flight";
        }
    }

    public static String getLevelTranslation(int level) {
        if (level == 1) {
            return "container.enchant.level.one";
        } else {
            return "container.enchant.level.many";
        }
    }

    public static String getMusicDiscName(ItemStack disc) {
        if (InteractiveChat.version.isLegacy()) {
            try {
                Object nmsItemStackObject = asNMSCopyMethod.invoke(null, disc);
                Object nmsItemObject = nmsGetItemMethod.invoke(nmsItemStackObject);
                Object nmsItemRecordObject = nmsItemRecordClass.cast(nmsItemObject);
                nmsItemRecordTranslationKeyField.setAccessible(true);
                if (InteractiveChat.version.isOld()) {
                    return "item.record." + nmsItemRecordTranslationKeyField.get(nmsItemRecordObject).toString() + ".desc";
                } else {
                    return nmsItemRecordTranslationKeyField.get(nmsItemRecordObject).toString();
                }
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                e.printStackTrace();
                return "";
            }
        } else {
            NamespacedKey namespacedKey = disc.getType().getKey();
            return "item." + namespacedKey.getNamespace() + "." + namespacedKey.getKey() + ".desc";
        }
    }

    public static List<String> getTropicalFishBucketName(ItemStack bucket) {
        List<String> list = new ArrayList<>();
        if (!InteractiveChat.version.isLegacy() && NBTEditor.contains(bucket, "BucketVariantTag")) {
            int variance = NBTEditor.getInt(bucket, "BucketVariantTag");
            int prefefinedType = PREDEFINED_TROPICAL_FISH.getOrDefault(variance, -1);
            if (prefefinedType != -1) {
                list.add("entity.minecraft.tropical_fish.predefined." + prefefinedType);
            } else {
                try {
                    variance = validateAndFixTropicalFishVariant(variance);
                    Pattern pattern = (Pattern) getTropicalFishPatternMethod.invoke(null, variance);
                    DyeColor bodyColor = (DyeColor) getTropicalFishBodyColorMethod.invoke(null, variance);
                    DyeColor patternColor = (DyeColor) getTropicalFishPatternColorMethod.invoke(null, variance);
                    list.add("entity.minecraft.tropical_fish.type." + pattern.toString().toLowerCase());
                    list.add("color.minecraft." + bodyColor.toString().toLowerCase());
                    if (!bodyColor.equals(patternColor)) {
                        list.add("color.minecraft." + patternColor.toString().toLowerCase());
                    }
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
        return list;
    }

    private static int validateAndFixTropicalFishVariant(int data) {
        byte[] bytes = new byte[] {(byte) (data >> 24), (byte) (data >> 16), (byte) (data >> 8), (byte) data};
        if (bytes.length != 4) {
            return 0;
        }
        if (bytes[3] < 0 || bytes[3] > 1) {
            bytes[3] = 1;
        }
        if (bytes[2] < 0 || bytes[2] > 5) {
            bytes[2] = 5;
        }
        if (bytes[1] < 0 || bytes[1] > 15) {
            bytes[1] = 0;
        }
        if (bytes[0] < 0 || bytes[0] > 15) {
            bytes[0] = 0;
        }
        return ((bytes[0] & 0xFF) << 24) | ((bytes[1] & 0xFF) << 16) | ((bytes[2] & 0xFF) << 8) | (bytes[3] & 0xFF);
    }

    public static String getBannerPatternName(PatternTypeWrapper type, DyeColor color) {
        if (InteractiveChat.version.isLegacy()) {
            String colorName = WordUtils.capitalizeFully(color.name().toLowerCase().replace("_", " ")).replace(" ", "");
            colorName = colorName.substring(0, 1).toLowerCase() + colorName.substring(1);
            return "item.banner." + type.getAssetName() + "." + colorName;
        } else {
            return "block.minecraft.banner." + type.getAssetName() + "." + color.name().toLowerCase();
        }
    }

    public static String getAttributeKey(String attributeName) {
        return "attribute.name." + attributeName;
    }

    public static String getAttributeModifierKey(double amount, int operation) {
        if (amount > 0) {
            return "attribute.modifier.plus." + operation;
        } else if (amount < 0) {
            return "attribute.modifier.take." + operation;
        } else {
            return "attribute.modifier.equals." + operation;
        }
    }

    public static String getModifierSlotKey(EquipmentSlot slot) {
        switch (slot) {
            case HEAD:
                return "item.modifiers.head";
            case CHEST:
                return "item.modifiers.chest";
            case LEGS:
                return "item.modifiers.legs";
            case FEET:
                return "item.modifiers.feet";
            case HAND:
                return "item.modifiers.mainhand";
            case OFF_HAND:
                return "item.modifiers.offhand";
            default:
                return "item.modifiers." + slot.toString().toLowerCase();
        }
    }

    public static String getCanDestroy() {
        return "item.canBreak";
    }

    public static String getCanPlace() {
        return "item.canPlace";
    }

    public static String getBookAuthor() {
        return "book.byAuthor";
    }

    public static String getBookGeneration(Generation generation) {
        switch (generation) {
            case COPY_OF_ORIGINAL:
                return "book.generation.1";
            case COPY_OF_COPY:
                return "book.generation.2";
            case TATTERED:
                return "book.generation.3";
            case ORIGINAL:
            default:
                return "book.generation.0";
        }
    }

}
