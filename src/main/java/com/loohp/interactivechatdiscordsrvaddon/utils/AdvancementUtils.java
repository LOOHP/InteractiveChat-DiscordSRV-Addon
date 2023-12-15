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
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.Optional;

public class AdvancementUtils {

    public static final String MINECRAFT_NAMESPACE = "minecraft";

    private static Class<?> craftAdvancementClass;
    private static Method craftAdvancementClassGetHandleMethod;
    private static Class<?> nmsAdvancementHolderClass;
    private static Method nmsAdvancementHolderGetAdvancementMethod;
    private static Class<?> nmsAdvancementClass;
    private static Field nmsAdvancementClassGetDisplayField;
    private static Class<?> nmsAdvancementDisplayClass;
    private static Method nmsAdvancementDisplayClassGetTitleMethod;
    private static Method nmsAdvancementDisplayClassGetDescriptionMethod;
    private static Method nmsAdvancementDisplayClassGetTypeMethod;
    private static Class<?> nmsItemStackClass;
    private static Field nmsAdvancementDisplayClassItemStackField;

    static {
        try {
            craftAdvancementClass = NMSUtils.getNMSClass("org.bukkit.craftbukkit.%s.advancement.CraftAdvancement");
            craftAdvancementClassGetHandleMethod = craftAdvancementClass.getMethod("getHandle");
            nmsAdvancementClass = NMSUtils.getNMSClass("net.minecraft.server.%s.Advancement", "net.minecraft.advancements.Advancement");
            if (!craftAdvancementClassGetHandleMethod.getReturnType().equals(nmsAdvancementClass)) {
                nmsAdvancementHolderClass = craftAdvancementClassGetHandleMethod.getReturnType();
                nmsAdvancementHolderGetAdvancementMethod = Arrays.stream(nmsAdvancementHolderClass.getMethods()).filter(m -> m.getReturnType().equals(nmsAdvancementClass)).findFirst().get();
            }
            nmsAdvancementDisplayClass = NMSUtils.getNMSClass("net.minecraft.server.%s.AdvancementDisplay", "net.minecraft.advancements.AdvancementDisplay");
            nmsAdvancementClassGetDisplayField = Arrays.stream(nmsAdvancementClass.getDeclaredFields()).filter(f -> f.getType().equals(nmsAdvancementDisplayClass)).findFirst().orElseGet(() -> {
                return Arrays.stream(nmsAdvancementClass.getDeclaredFields()).filter(f -> f.getType().equals(Optional.class) && ((ParameterizedType) f.getGenericType()).getActualTypeArguments()[0].equals(nmsAdvancementDisplayClass)).findFirst().get();
            });
            nmsAdvancementDisplayClassGetTitleMethod = nmsAdvancementDisplayClass.getMethod("a");
            nmsAdvancementDisplayClassGetDescriptionMethod = nmsAdvancementDisplayClass.getMethod("b");
            nmsAdvancementDisplayClassGetTypeMethod = nmsAdvancementDisplayClass.getMethod("e");
            nmsItemStackClass = NMSUtils.getNMSClass("net.minecraft.server.%s.ItemStack", "net.minecraft.world.item.ItemStack");
            nmsAdvancementDisplayClassItemStackField = Arrays.stream(nmsAdvancementDisplayClass.getDeclaredFields()).filter(e -> e.getType().equals(nmsItemStackClass)).findFirst().get();
        } catch (SecurityException | ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }

    public static AdvancementData getAdvancementData(Object advancementObject) {
        try {
            Advancement advancement = (Advancement) advancementObject;
            boolean isMinecraft = advancement.getKey().getNamespace().equals(MINECRAFT_NAMESPACE);
            Object craftAdvancement = craftAdvancementClass.cast(advancement);
            Object nmsAdvancementOrHolder = craftAdvancementClassGetHandleMethod.invoke(craftAdvancement);
            Object nmsAdvancement;
            if (nmsAdvancementClass.isInstance(nmsAdvancementOrHolder)) {
                nmsAdvancement = nmsAdvancementOrHolder;
            } else {
                nmsAdvancement = nmsAdvancementHolderGetAdvancementMethod.invoke(nmsAdvancementOrHolder);
            }
            nmsAdvancementClassGetDisplayField.setAccessible(true);
            Object nmsAdvancementDisplay = nmsAdvancementClassGetDisplayField.get(nmsAdvancement);
            if (nmsAdvancementDisplay == null || (nmsAdvancementDisplay instanceof Optional && (nmsAdvancementDisplay = ((Optional<?>) nmsAdvancementDisplay).orElse(null)) == null)) {
                return null;
            }
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
