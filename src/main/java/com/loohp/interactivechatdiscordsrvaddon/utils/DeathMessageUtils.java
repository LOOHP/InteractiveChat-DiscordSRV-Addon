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

import com.loohp.interactivechat.libs.net.kyori.adventure.text.Component;
import com.loohp.interactivechat.utils.ChatComponentType;
import com.loohp.interactivechat.utils.NMSUtils;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;

public class DeathMessageUtils {

    private static Class<?> craftPlayerClass;
    private static Class<?> nmsEntityPlayerClass;
    private static Class<?> nmsCombatTrackerClass;
    private static Class<?> nmsIChatBaseComponentClass;
    private static Method getNmsEntityPlayerMethod;
    private static Field nmsCombatTrackerField;
    private static Method getDeathMessageMethod;

    static {
        try {
            craftPlayerClass = NMSUtils.getNMSClass("org.bukkit.craftbukkit.%s.entity.CraftPlayer");
            nmsEntityPlayerClass = NMSUtils.getNMSClass("net.minecraft.server.%s.EntityPlayer", "net.minecraft.server.level.EntityPlayer");
            nmsCombatTrackerClass = NMSUtils.getNMSClass("net.minecraft.server.%s.CombatTracker", "net.minecraft.world.damagesource.CombatTracker");
            nmsIChatBaseComponentClass = NMSUtils.getNMSClass("net.minecraft.server.%s.IChatBaseComponent", "net.minecraft.network.chat.IChatBaseComponent");
            getNmsEntityPlayerMethod = craftPlayerClass.getMethod("getHandle");
            nmsCombatTrackerField = Arrays.stream(nmsEntityPlayerClass.getFields()).filter(each -> each.getType().equals(nmsCombatTrackerClass)).findFirst().get();
            getDeathMessageMethod = Arrays.stream(nmsCombatTrackerClass.getMethods()).filter(each -> each.getReturnType().equals(nmsIChatBaseComponentClass)).findFirst().get();
        } catch (ClassNotFoundException | SecurityException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public static Component getDeathMessage(Player player) {
        try {
            Object craftPlayerObject = craftPlayerClass.cast(player);
            Object nmsEntityPlayerObject = getNmsEntityPlayerMethod.invoke(craftPlayerObject);
            Object nmsCombatTrackerObject = nmsCombatTrackerField.get(nmsEntityPlayerObject);
            Object nmsIChatBaseComponentObject = getDeathMessageMethod.invoke(nmsCombatTrackerObject);
            return ChatComponentType.IChatBaseComponent.convertFrom(nmsIChatBaseComponentObject);
        } catch (Throwable e) {
            return Component.text("");
        }
    }

}
