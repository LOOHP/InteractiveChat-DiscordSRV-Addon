package com.loohp.interactivechatdiscordsrvaddon.utils;

import com.loohp.interactivechat.InteractiveChat;
import com.loohp.interactivechat.utils.MCVersion;
import com.loohp.interactivechat.utils.NMSUtils;
import org.bukkit.Bukkit;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ResourcePackUtils {

    private static Class<?> craftServerClass;
    private static Method craftServerGetServerMethod;
    private static Method nmsGetResourcePackMethod;
    private static Class<?> nmsMinecraftVersionClass;
    private static Constructor<?> nmsMinecraftVersionConstructor;
    private static Method nmsMinecraftVersionGetPackVersionMethod;
    private static Object mojangPackTypeResourceEnumObject;

    private static Object nmsMinecraftVersionObject;

    static {
        try {
            craftServerClass = NMSUtils.getNMSClass("org.bukkit.craftbukkit.%s.CraftServer");
            craftServerGetServerMethod = craftServerClass.getMethod("getServer");
            try {
                nmsGetResourcePackMethod = craftServerGetServerMethod.getReturnType().getMethod("getResourcePack");
            } catch (Exception e) {
                nmsGetResourcePackMethod = craftServerGetServerMethod.getReturnType().getMethod("T");
            }
            try {
                nmsMinecraftVersionClass = NMSUtils.getNMSClass("net.minecraft.server.%s.MinecraftVersion", "net.minecraft.MinecraftVersion");
                nmsMinecraftVersionConstructor = nmsMinecraftVersionClass.getDeclaredConstructor();
                nmsMinecraftVersionConstructor.setAccessible(true);
                nmsMinecraftVersionObject = nmsMinecraftVersionConstructor.newInstance();
                try {
                    nmsMinecraftVersionGetPackVersionMethod = nmsMinecraftVersionClass.getMethod("getPackVersion");
                } catch (Exception e) {
                    nmsMinecraftVersionGetPackVersionMethod = nmsMinecraftVersionClass.getMethod("getPackVersion", com.mojang.bridge.game.PackType.class);
                    mojangPackTypeResourceEnumObject = com.mojang.bridge.game.PackType.RESOURCE;
                }
            } catch (Exception e) {
            }
        } catch (ClassNotFoundException | NoSuchMethodException | SecurityException e) {
            e.printStackTrace();
        }
    }

    public static String getServerResourcePack() {
        try {
            Object craftServerObject = craftServerClass.cast(Bukkit.getServer());
            Object nmsMinecraftServerObject = craftServerGetServerMethod.invoke(craftServerObject);
            Object resourcePackStringObject = nmsGetResourcePackMethod.invoke(nmsMinecraftServerObject);
            return resourcePackStringObject == null ? "" : resourcePackStringObject.toString();
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static int getServerResourcePackVersion() {
        try {
            if (InteractiveChat.version.isNewerOrEqualTo(MCVersion.V1_15)) {
                if (mojangPackTypeResourceEnumObject == null) {
                    return (int) nmsMinecraftVersionGetPackVersionMethod.invoke(nmsMinecraftVersionObject);
                } else {
                    return (int) nmsMinecraftVersionGetPackVersionMethod.invoke(nmsMinecraftVersionObject, mojangPackTypeResourceEnumObject);
                }
            } else if (InteractiveChat.version.isNewerOrEqualTo(MCVersion.V1_13)) {
                return 4;
            } else if (InteractiveChat.version.isNewerOrEqualTo(MCVersion.V1_11)) {
                return 3;
            } else if (InteractiveChat.version.isNewerOrEqualTo(MCVersion.V1_9)) {
                return 2;
            } else {
                return 1;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

}
