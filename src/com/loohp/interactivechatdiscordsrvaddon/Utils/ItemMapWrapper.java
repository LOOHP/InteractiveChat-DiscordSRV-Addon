package com.loohp.interactivechatdiscordsrvaddon.Utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapView;

import com.loohp.interactivechat.InteractiveChat;
import com.loohp.interactivechat.Utils.MCVersion;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;

public class ItemMapWrapper {
	
	private static Method bukkitBukkitClassGetMapShortMethod;
	private static Method bukkitMapViewClassGetIdMethod;
	
	private static Class<?> nmsItemWorldMapClass;
	private static Constructor<?> nmsItemWorldMapClassContructor;
	private static Class<?> nmsWorldClass;
	private static Class<?> nmsItemStackClass;
	private static Method nmsItemWorldMapClassGetSavedMapMethod;
	private static Class<?> craftItemStackClass;
	private static Method craftItemStackClassAsNMSCopyMethod;
	private static Class<?> craftWorldClass;
	private static Method craftWorldClassGetHandleMethod;
	private static Class<?> nmsWorldMapClass;
	private static Field nmsWorldMapClassColorsField;
	private static Field nmsWorldMapClassDecorationsField;
	private static Class<?> nmsMapIconClass;
	private static Method nmsMapIconClassGetTypeMethod;
	private static boolean nmsMapIconClassGetTypeMethodReturnsByte;
	private static Method nmsMapIconClassGetXMethod;
	private static Method nmsMapIconClassGetYMethod;
	private static Method nmsMapIconClassGetRotationMethod;
	private static Method nmsMapIconClassGetNameMethod;
	private static Class<?> nmsIChatBaseComponentClass;
	private static Class<?> nmsChatSerializerSubclass;
	private static Method nmsChatSerializerSubclassAMethod;
	
	private static final Comparator<MapIcon> ICON_ORDER = Comparator.comparing(each -> each.getType().ordinal());
	
	static {
		try {
			try {
				bukkitBukkitClassGetMapShortMethod = Bukkit.class.getMethod("getMap", short.class);
			} catch (NoSuchMethodException e1) {
				bukkitBukkitClassGetMapShortMethod = null;
			}
			try {
				bukkitMapViewClassGetIdMethod = MapView.class.getMethod("getId");
			} catch (NoSuchMethodException e1) {
				bukkitMapViewClassGetIdMethod = null;
			}
			
			nmsItemWorldMapClass = getNMSClass("net.minecraft.server.", "ItemWorldMap");
			try {
				nmsItemWorldMapClassContructor = nmsItemWorldMapClass.getDeclaredConstructor();
			} catch (NoSuchMethodException e1) {
				nmsItemWorldMapClassContructor = null;
			}
			nmsWorldClass = getNMSClass("net.minecraft.server.", "World");
			nmsItemStackClass = getNMSClass("net.minecraft.server.", "ItemStack");
			nmsItemWorldMapClassGetSavedMapMethod = nmsItemWorldMapClass.getMethod("getSavedMap", nmsItemStackClass, nmsWorldClass);
			craftItemStackClass = getNMSClass("org.bukkit.craftbukkit.", "inventory.CraftItemStack");
			craftItemStackClassAsNMSCopyMethod = craftItemStackClass.getMethod("asNMSCopy", ItemStack.class);
			craftWorldClass = getNMSClass("org.bukkit.craftbukkit.", "CraftWorld");
			craftWorldClassGetHandleMethod = craftWorldClass.getMethod("getHandle");
			nmsWorldMapClass = getNMSClass("net.minecraft.server.", "WorldMap");
			nmsWorldMapClassColorsField = nmsWorldMapClass.getField("colors");
			nmsWorldMapClassDecorationsField = nmsWorldMapClass.getField("decorations");
			nmsMapIconClass = getNMSClass("net.minecraft.server.", "MapIcon");
			nmsMapIconClassGetTypeMethod = nmsMapIconClass.getMethod("getType");
			nmsMapIconClassGetTypeMethodReturnsByte = nmsMapIconClassGetTypeMethod.getReturnType().equals(byte.class);
			nmsMapIconClassGetXMethod = nmsMapIconClass.getMethod("getX");
			nmsMapIconClassGetYMethod = nmsMapIconClass.getMethod("getY");
			nmsMapIconClassGetRotationMethod = nmsMapIconClass.getMethod("getRotation");
			try {
				nmsMapIconClassGetNameMethod = nmsMapIconClass.getMethod("getName");
			} catch (NoSuchMethodException e1) {
				nmsMapIconClassGetNameMethod = null;
			}
			nmsIChatBaseComponentClass = getNMSClass("net.minecraft.server.", "IChatBaseComponent");
			nmsChatSerializerSubclass = Arrays.asList(nmsIChatBaseComponentClass.getDeclaredClasses()).stream().filter(each -> each.getSimpleName().equals("ChatSerializer")).findFirst().get();
			nmsChatSerializerSubclassAMethod = nmsChatSerializerSubclass.getMethod("a", nmsIChatBaseComponentClass);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static Class<?> getNMSClass(String prefix, String nmsClassString) throws ClassNotFoundException {
        String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3] + ".";
        String name = prefix + version + nmsClassString;
        return Class.forName(name);
    }
	
	public static boolean isFilledMap(ItemStack itemStack) {
		return (itemStack.getItemMeta() != null) && (itemStack.getItemMeta() instanceof MapMeta);
	}
	
	private ItemStack itemStack;
	private byte[] colors;
	private List<MapIcon> icons;
	
	public ItemMapWrapper(ItemStack itemStack) throws Exception {
		this.itemStack = itemStack;
		this.icons = new ArrayList<>();
		update();
	}
	
	@SuppressWarnings("deprecation")
	public void update() throws Exception {
		if (!isFilledMap(itemStack)) {
			throw new IllegalArgumentException("Provided item is not a filled map");
		}
		MapMeta map = (MapMeta) itemStack.getItemMeta();
		MapView mapView;
		if (InteractiveChat.version.isPost1_14() || InteractiveChat.version.equals(MCVersion.V1_13_1)) {
			mapView = map.getMapView();
		} else if (InteractiveChat.version.equals(MCVersion.V1_13)) {
			mapView = (MapView) bukkitBukkitClassGetMapShortMethod.invoke(null, bukkitMapViewClassGetIdMethod.invoke(map));
		} else {
			mapView = (MapView) bukkitBukkitClassGetMapShortMethod.invoke(null, itemStack.getDurability());
		}
		Object nmsItemStackObject = craftItemStackClassAsNMSCopyMethod.invoke(null, itemStack);
		Object nmsWorldServerObject = craftWorldClassGetHandleMethod.invoke(craftWorldClass.cast(mapView.getWorld()));
		Object worldMapObject;
		if (!InteractiveChat.version.isLegacy() || InteractiveChat.version.equals(MCVersion.V1_12) || InteractiveChat.version.equals(MCVersion.V1_11)) {
			worldMapObject = nmsItemWorldMapClassGetSavedMapMethod.invoke(null, nmsItemStackObject, nmsWorldServerObject);
		} else {
			nmsItemWorldMapClassContructor.setAccessible(true);
			worldMapObject = nmsItemWorldMapClassGetSavedMapMethod.invoke(nmsItemWorldMapClassContructor.newInstance(), nmsItemStackObject, nmsWorldServerObject);
			nmsItemWorldMapClassContructor.setAccessible(false);
		}
		colors = (byte[]) nmsWorldMapClassColorsField.get(worldMapObject);
		Collection<?> nmsMapIconsCollection = ((Map<?, ?>) nmsWorldMapClassDecorationsField.get(worldMapObject)).values();
		icons.clear();
		for (Object nmsMapIconObject : nmsMapIconsCollection) {
			MapIcon.Type type;
			if (nmsMapIconClassGetTypeMethodReturnsByte) {
				type = MapIcon.Type.getByValue((byte) nmsMapIconClassGetTypeMethod.invoke(nmsMapIconObject));
			} else {
				type = MapIcon.Type.valueOf(((Enum<?>) nmsMapIconClassGetTypeMethod.invoke(nmsMapIconObject)).name());
			}
			byte x = (byte) nmsMapIconClassGetXMethod.invoke(nmsMapIconObject);
			byte y = (byte) nmsMapIconClassGetYMethod.invoke(nmsMapIconObject);
			byte rotation = (byte) nmsMapIconClassGetRotationMethod.invoke(nmsMapIconObject);
			Object ichatbasecomponentObject = nmsMapIconClassGetNameMethod == null ? null : nmsMapIconClassGetNameMethod.invoke(nmsMapIconObject);
			BaseComponent name = ichatbasecomponentObject == null ? null : ComponentSerializer.parse((String) nmsChatSerializerSubclassAMethod.invoke(null, ichatbasecomponentObject))[0];
			icons.add(new MapIcon(type, x, y, rotation, name));
		}
		icons = icons.stream().sorted(ICON_ORDER).collect(Collectors.toList());
	}
	
	public byte[] getColors() {
		return colors;
	}
	
	public List<MapIcon> getMapIcons() {
		return Collections.unmodifiableList(icons);
	}

	public static class MapIcon {

	    private final MapIcon.Type type;
	    private final byte x;
	    private final byte y;
	    private final byte rotation;
	    private final BaseComponent name;

	    public MapIcon(MapIcon.Type mapicon_type, byte b0, byte b1, byte b2, BaseComponent name) {
	        this.type = mapicon_type;
	        this.x = b0;
	        this.y = b1;
	        this.rotation = b2;
	        this.name = name;
	    }

	    public MapIcon.Type getType() {
	        return this.type;
	    }

	    public byte getX() {
	        return this.x;
	    }

	    public byte getY() {
	        return this.y;
	    }

	    public byte getRotation() {
	        return this.rotation;
	    }

	    public BaseComponent getName() {
	        return this.name;
	    }

	    public boolean equals(Object object) {
	        if (this == object) {
	            return true;
	        } else if (!(object instanceof MapIcon)) {
	            return false;
	        } else {
	            MapIcon mapicon = (MapIcon) object;

	            return this.type != mapicon.type ? false : (this.rotation != mapicon.rotation ? false : (this.x != mapicon.x ? false : (this.y != mapicon.y ? false : Objects.equals(this.name, mapicon.name))));
	        }
	    }

	    public int hashCode() {
	        int i = 31 + this.x;

	        i = 31 * i + this.y;
	        i = 31 * i + this.rotation;
	        i = 31 * i + Objects.hashCode(this.name);
	        return i;
	    }

	    public static enum Type {
	        PLAYER(), FRAME(), RED_MARKER(), BLUE_MARKER(), TARGET_X(), TARGET_POINT(), PLAYER_OFF_MAP(), PLAYER_OFF_LIMITS(), MANSION(), MONUMENT(), BANNER_WHITE(), BANNER_ORANGE(), BANNER_MAGENTA(), BANNER_LIGHT_BLUE(), BANNER_YELLOW(), BANNER_LIME(), BANNER_PINK(), BANNER_GRAY(), BANNER_LIGHT_GRAY(), BANNER_CYAN(), BANNER_PURPLE(), BANNER_BLUE(), BANNER_BROWN(), BANNER_GREEN(), BANNER_RED(), BANNER_BLACK(), RED_X();
	    	
	    	public static Type getByValue(int i) {
	    		return values()[i];
	    	}
	    }
	}


}
