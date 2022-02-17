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
import com.loohp.interactivechat.libs.net.kyori.adventure.text.format.NamedTextColor;
import com.loohp.interactivechat.utils.ItemStackUtils;
import com.loohp.interactivechat.utils.NMSUtils;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.AdvancementData;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.AdvancementType;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class AchievementUtils {

    private static Class<?> bukkitPlayerAchievementAwardedEventClass;
    private static Method bukkitPlayerAchievementAwardedEventClassGetAchievementMethod;
    private static Class<?> bukkitAchievementClass;
    private static Class<?> craftStatisticClass;
    private static Method craftStatisticClassGetNMSAchievementMethod;
    private static Class<?> nmsAdvancementClass;
    private static Field nmsAdvancementClassNameField;
    private static Field nmsAdvancementClassItemStackField;

    static {
        try {
            bukkitPlayerAchievementAwardedEventClass = Class.forName("org.bukkit.event.player.PlayerAchievementAwardedEvent");
            bukkitPlayerAchievementAwardedEventClassGetAchievementMethod = bukkitPlayerAchievementAwardedEventClass.getMethod("getAchievement");
            bukkitAchievementClass = Class.forName("org.bukkit.Achievement");
            craftStatisticClass = NMSUtils.getNMSClass("org.bukkit.craftbukkit.%s.CraftStatistic");
            craftStatisticClassGetNMSAchievementMethod = craftStatisticClass.getMethod("getNMSAchievement", bukkitAchievementClass);
            nmsAdvancementClass = NMSUtils.getNMSClass("net.minecraft.server.%s.Achievement");
            nmsAdvancementClassNameField = nmsAdvancementClass.getDeclaredField("name");
            nmsAdvancementClassItemStackField = nmsAdvancementClass.getDeclaredField("d");
        } catch (ClassNotFoundException | NoSuchMethodException | SecurityException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    public static AdvancementData getAdvancementData(Object achievementObject) {
        try {
            Object nmsAchievement = craftStatisticClassGetNMSAchievementMethod.invoke(null, achievementObject);
            nmsAdvancementClassNameField.setAccessible(true);
            String name = nmsAdvancementClassNameField.get(nmsAchievement).toString();
            Component title = Component.translatable(name).color(NamedTextColor.GREEN);
            Component description = Component.translatable(name + ".desc");
            AdvancementType advancementType = AdvancementType.LEGACY;
            nmsAdvancementClassItemStackField.setAccessible(true);
            Object nmsItemStack = nmsAdvancementClassItemStackField.get(nmsAchievement);
            ItemStack itemStack = ItemStackUtils.toBukkitCopy(nmsItemStack);
            return new AdvancementData(title, description, itemStack, advancementType, true);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object getAdvancementFromEvent(Object event) {
        try {
            Object bukkitEvent = bukkitPlayerAchievementAwardedEventClass.cast(event);
            return bukkitPlayerAchievementAwardedEventClassGetAchievementMethod.invoke(bukkitEvent);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

}
