package com.loohp.interactivechatdiscordsrvaddon.utils;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.TropicalFish.Pattern;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import com.loohp.interactivechat.InteractiveChat;
import com.loohp.interactivechat.utils.MCVersion;
import com.loohp.interactivechat.utils.NBTUtils;

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
				nmsEnchantmentClass = getNMSClass("net.minecraft.server.", "Enchantment");
				if (InteractiveChat.version.isOld()) {
					getEnchantmentByIdMethod = nmsEnchantmentClass.getMethod("getById", int.class);
				} else {
					getEnchantmentByIdMethod = nmsEnchantmentClass.getMethod("c", int.class);
				}
				getEnchantmentKeyMethod = nmsEnchantmentClass.getMethod("a");
				nmsMobEffectListClass = getNMSClass("net.minecraft.server.", "MobEffectList");
				if (InteractiveChat.version.isOld()) {
					nmsMobEffectByIdField = nmsMobEffectListClass.getField("byId");
				} else {
					getEffectFromIdMethod = nmsMobEffectListClass.getMethod("fromId", int.class);
				}
				getEffectKeyMethod = nmsMobEffectListClass.getMethod("a");
				
				nmsItemRecordClass = getNMSClass("net.minecraft.server.", "ItemRecord");
				nmsItemRecordTranslationKeyField = nmsItemRecordClass.getDeclaredField("c");
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			try {
				nmsMobEffectListClass = getNMSClass("net.minecraft.server.", "MobEffectList");
				getEffectFromIdMethod = nmsMobEffectListClass.getMethod("fromId", int.class);
				getEffectKeyMethod = nmsMobEffectListClass.getMethod("c");
				
				craftTropicalFishClass = getNMSClass("org.bukkit.craftbukkit.", "entity.CraftTropicalFish");
				getTropicalFishPatternMethod = craftTropicalFishClass.getMethod("getPattern", int.class);
				getTropicalFishPatternColorMethod = craftTropicalFishClass.getMethod("getPatternColor", int.class);
				getTropicalFishBodyColorMethod = craftTropicalFishClass.getMethod("getBodyColor", int.class);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		try {
			craftItemStackClass = getNMSClass("org.bukkit.craftbukkit.", "inventory.CraftItemStack");
			nmsItemStackClass = getNMSClass("net.minecraft.server.", "ItemStack");
			asNMSCopyMethod = craftItemStackClass.getMethod("asNMSCopy", ItemStack.class);
			nmsGetItemMethod = nmsItemStackClass.getMethod("getItem");
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
	
	private static Class<?> getNMSClass(String prefix, String nmsClassString) throws ClassNotFoundException {	
        String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3] + ".";
        String name = prefix + version + nmsClassString;
        return Class.forName(name);
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
		if (!InteractiveChat.version.isLegacy()) {
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

	public static String getEnchantment(Enchantment enchantment) {
		if (!InteractiveChat.version.isLegacy()) {
			return "enchantment." + enchantment.getKey().getNamespace() + "." + enchantment.getKey().getKey();
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
				return nmsItemRecordTranslationKeyField.get(nmsItemRecordObject).toString();
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
		if (!InteractiveChat.version.isLegacy() && NBTUtils.contains(bucket, "BucketVariantTag")) {
			int variance = NBTUtils.getInt(bucket, "BucketVariantTag");
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

}
