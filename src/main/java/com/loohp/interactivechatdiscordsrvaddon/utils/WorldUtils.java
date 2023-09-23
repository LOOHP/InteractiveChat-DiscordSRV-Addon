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

import com.loohp.interactivechat.InteractiveChat;
import com.loohp.interactivechat.utils.MCVersion;
import com.loohp.interactivechat.utils.NMSUtils;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.BiomePrecipitation;
import com.loohp.interactivechatdiscordsrvaddon.wrappers.DimensionManagerWrapper;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.World.Environment;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class WorldUtils {

    private static Class<?> craftWorldClass;
    private static Method getHandleMethod;
    private static Class<?> worldServerClass;
    private static Method getWorldTypeKeyMethod;
    private static Method getMinecraftKeyMethod;
    private static Method getBiomeAtMethod;
    private static Method holderGetMethod;
    private static Class<?> nmsBlockPositionClass;
    private static Constructor<?> nmsBlockPositionConstructor;
    private static Method getPrecipitationMethod;

    static {
        if (InteractiveChat.version.isNewerOrEqualTo(MCVersion.V1_16)) {
            try {
                craftWorldClass = NMSUtils.getNMSClass("org.bukkit.craftbukkit.%s.CraftWorld");
                getHandleMethod = craftWorldClass.getMethod("getHandle");
                worldServerClass = getHandleMethod.getReturnType();
                getWorldTypeKeyMethod = worldServerClass.getMethod("getTypeKey");
                getMinecraftKeyMethod = getWorldTypeKeyMethod.getReturnType().getMethod("a");
                getBiomeAtMethod = worldServerClass.getMethod("a", int.class, int.class, int.class);
                Class<?> biomeBaseClass;
                if (InteractiveChat.version.isNewerOrEqualTo(MCVersion.V1_18)) {
                    holderGetMethod = getBiomeAtMethod.getReturnType().getMethod("a");
                    biomeBaseClass = Class.forName("net.minecraft.world.level.biome.BiomeBase");
                } else {
                    biomeBaseClass = getBiomeAtMethod.getReturnType();
                }
                if (InteractiveChat.version.isNewerOrEqualTo(MCVersion.V1_19_4)) {
                    nmsBlockPositionClass = NMSUtils.getNMSClass("net.minecraft.server.%s.BlockPosition", "net.minecraft.core.BlockPosition");
                    nmsBlockPositionConstructor = nmsBlockPositionClass.getConstructor(int.class, int.class, int.class);
                    getPrecipitationMethod = biomeBaseClass.getMethod("a", nmsBlockPositionClass);
                } else {
                    if (InteractiveChat.version.isOlderThan(MCVersion.V1_17)) {
                        getPrecipitationMethod = biomeBaseClass.getMethod("d");
                    } else {
                        getPrecipitationMethod = biomeBaseClass.getMethod("c");
                    }
                }
            } catch (ReflectiveOperationException e) {
                e.printStackTrace();
            }
        }
    }

    public static String getNamespacedKey(World world) {
        if (InteractiveChat.version.isNewerOrEqualTo(MCVersion.V1_16)) {
            try {
                Object craftWorldObject = craftWorldClass.cast(world);
                Object nmsWorldServerObject = getHandleMethod.invoke(craftWorldObject);
                Object nmsResourceKeyObject = getWorldTypeKeyMethod.invoke(nmsWorldServerObject);
                return getMinecraftKeyMethod.invoke(nmsResourceKeyObject).toString();
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        } else {
            if (world.getEnvironment().equals(Environment.NORMAL)) {
                return "minecraft:overworld";
            } else if (world.getEnvironment().equals(Environment.NETHER)) {
                return "minecraft:the_nether";
            } else if (world.getEnvironment().equals(Environment.THE_END)) {
                return "minecraft:the_end";
            } else {
                return "minecraft:custom";
            }
        }
        return null;
    }

    public static boolean isNatural(World world) {
        if (InteractiveChat.version.isNewerOrEqualTo(MCVersion.V1_19)) {
            return world.isNatural();
        } else if (InteractiveChat.version.isNewerOrEqualTo(MCVersion.V1_16)) {
            return new DimensionManagerWrapper(world).natural();
        } else {
            return world.getEnvironment().equals(Environment.NORMAL);
        }
    }

    @SuppressWarnings("deprecation")
    public static BiomePrecipitation getPrecipitation(Location location) {
        if (InteractiveChat.version.isNewerOrEqualTo(MCVersion.V1_16)) {
            try {
                Object craftWorldObject = craftWorldClass.cast(location.getWorld());
                Object nmsWorldServerObject = getHandleMethod.invoke(craftWorldObject);
                Object biomeBaseObject = getBiomeAtMethod.invoke(nmsWorldServerObject, location.getBlockX(), location.getBlockY(), location.getBlockZ());
                if (InteractiveChat.version.isNewerOrEqualTo(MCVersion.V1_18)) {
                    biomeBaseObject = holderGetMethod.invoke(biomeBaseObject);
                }
                if (getPrecipitationMethod.getParameterCount() == 0) {
                    return BiomePrecipitation.fromName(((Enum<?>) getPrecipitationMethod.invoke(biomeBaseObject)).name());
                }
                Object nmsBlockPositionObject = nmsBlockPositionConstructor.newInstance(location.getBlockX(), location.getBlockY(), location.getBlockZ());
                return BiomePrecipitation.fromName(((Enum<?>) getPrecipitationMethod.invoke(biomeBaseObject, nmsBlockPositionObject)).name());
            } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
                e.printStackTrace();
            }
        } else {
            double temperature = location.getWorld().getTemperature(location.getBlockX(), location.getBlockZ());
            if (temperature > 0.95) {
                return BiomePrecipitation.NONE;
            } else if (temperature < 0.15) {
                return BiomePrecipitation.SNOW;
            } else {
                return BiomePrecipitation.RAIN;
            }
        }
        return null;
    }

}

