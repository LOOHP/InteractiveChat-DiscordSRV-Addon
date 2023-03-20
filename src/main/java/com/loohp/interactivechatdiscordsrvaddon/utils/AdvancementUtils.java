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
import com.loohp.interactivechat.utils.ItemStackUtils;
import com.loohp.interactivechat.utils.NMSUtils;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.AdvancementData;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.AdvancementType;
import org.bukkit.advancement.Advancement;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class AdvancementUtils {

    public static final String MINECRAFT_NAMESPACE = "minecraft";

    private static Class<?> craftAdvancementClass;
    private static Method craftAdvancementClassGetHandleMethod;
    private static Class<?> nmsAdvancementClass;
    private static Method nmsAdvancementClassGetDisplayMethod;
    private static Class<?> nmsAdvancementDisplayClass;
    private static Method nmsAdvancementDisplayClassGetTitleMethod;
    private static Method nmsAdvancementDisplayClassGetDescriptionMethod;
    private static Method nmsAdvancementDisplayClassGetTypeMethod;
    private static Field nmsAdvancementDisplayClassItemStackField;

    static {
        try {
            craftAdvancementClass = NMSUtils.getNMSClass("org.bukkit.craftbukkit.%s.advancement.CraftAdvancement");
            craftAdvancementClassGetHandleMethod = craftAdvancementClass.getMethod("getHandle");
            nmsAdvancementClass = NMSUtils.getNMSClass("net.minecraft.server.%s.Advancement", "net.minecraft.advancements.Advancement");
            nmsAdvancementDisplayClass = NMSUtils.getNMSClass("net.minecraft.server.%s.AdvancementDisplay", "net.minecraft.advancements.AdvancementDisplay");
            nmsAdvancementClassGetDisplayMethod = NMSUtils.reflectiveLookup(Method.class, () -> {
                Method method = nmsAdvancementClass.getMethod("c");
                if (!method.getReturnType().equals(nmsAdvancementDisplayClass)) {
                    throw new ReflectiveOperationException("method does not return the correct type");
                }
                return method;
            }, () -> {
                Method method = nmsAdvancementClass.getMethod("d");
                if (!method.getReturnType().equals(nmsAdvancementDisplayClass)) {
                    throw new ReflectiveOperationException("method does not return the correct type");
                }
                return method;
            });
            nmsAdvancementDisplayClassGetTitleMethod = nmsAdvancementDisplayClass.getMethod("a");
            nmsAdvancementDisplayClassGetDescriptionMethod = nmsAdvancementDisplayClass.getMethod("b");
            nmsAdvancementDisplayClassGetTypeMethod = nmsAdvancementDisplayClass.getMethod("e");
            nmsAdvancementDisplayClassItemStackField = nmsAdvancementDisplayClass.getDeclaredField("c");
        } catch (SecurityException | ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }

    public static AdvancementData getAdvancementData(Object advancementObject) {
        try {
            Advancement advancement = (Advancement) advancementObject;
            boolean isMinecraft = advancement.getKey().getNamespace().equals(MINECRAFT_NAMESPACE);
            Object craftAdvancement = craftAdvancementClass.cast(advancement);
            Object nmsAdvancement = craftAdvancementClassGetHandleMethod.invoke(craftAdvancement);
            Object nmsAdvancementDisplay = nmsAdvancementClassGetDisplayMethod.invoke(nmsAdvancement);
            Object nmsTitle = nmsAdvancementDisplayClassGetTitleMethod.invoke(nmsAdvancementDisplay);
            Component title = ChatComponentType.IChatBaseComponent.convertFrom(nmsTitle);
            Object nmsDescription = nmsAdvancementDisplayClassGetDescriptionMethod.invoke(nmsAdvancementDisplay);
            Component description = ChatComponentType.IChatBaseComponent.convertFrom(nmsDescription);
            Object nmsAdvancementType = nmsAdvancementDisplayClassGetTypeMethod.invoke(nmsAdvancementDisplay);
            AdvancementType advancementType = AdvancementType.fromHandle(nmsAdvancementType);
            nmsAdvancementDisplayClassItemStackField.setAccessible(true);
            Object nmsItemStack = nmsAdvancementDisplayClassItemStackField.get(nmsAdvancementDisplay);
            ItemStack itemStack = ItemStackUtils.toBukkitCopy(nmsItemStack);
            return new AdvancementData(title, description, itemStack, advancementType, isMinecraft);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object getAdvancementFromEvent(Object event) {
        return ((PlayerAdvancementDoneEvent) event).getAdvancement();
    }

}
