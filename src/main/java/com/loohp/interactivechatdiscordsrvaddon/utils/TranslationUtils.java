package com.loohp.interactivechatdiscordsrvaddon.utils;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.potion.PotionEffectType;

import com.loohp.interactivechat.InteractiveChat;
import com.loohp.interactivechat.utils.MCVersion;

public class TranslationUtils {
	
	private static Method bukkitEnchantmentGetIdMethod;
	private static Class<?> nmsEnchantmentClass;
	private static Method getEnchantmentByIdMethod;
	private static Method getEnchantmentKeyMethod;
	private static Class<?> nmsMobEffectListClass;
	private static Field nmsMobEffectByIdField;
	private static Method getEffectFromIdMethod;
	private static Method getEffectKeyMethod;
	
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
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			try {
				nmsMobEffectListClass = getNMSClass("net.minecraft.server.", "MobEffectList");
				getEffectFromIdMethod = nmsMobEffectListClass.getMethod("fromId", int.class);
				getEffectKeyMethod = nmsMobEffectListClass.getMethod("c");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
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

}
