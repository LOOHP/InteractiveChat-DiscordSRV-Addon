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
import com.loohp.interactivechatdiscordsrvaddon.wrappers.DimensionManagerWrapper;
import org.bukkit.World;
import org.bukkit.World.Environment;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class WorldUtils {

    private static Class<?> craftWorldClass;
    private static Method getHandleMethod;
    private static Class<?> worldServerClass;
    private static Method getWorldTypeKeyMethod;
    private static Method getMinecraftKeyMethod;

    static {
        try {
            craftWorldClass = NMSUtils.getNMSClass("org.bukkit.craftbukkit.%s.CraftWorld");
            getHandleMethod = craftWorldClass.getMethod("getHandle");
            worldServerClass = getHandleMethod.getReturnType();
            getWorldTypeKeyMethod = worldServerClass.getMethod("getTypeKey");
            getMinecraftKeyMethod = getWorldTypeKeyMethod.getReturnType().getMethod("a");
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
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
        if (InteractiveChat.version.isNewerOrEqualTo(MCVersion.V1_16)) {
            return new DimensionManagerWrapper(world).natural();
        } else {
            return world.getEnvironment().equals(Environment.NORMAL);
        }
    }

}

