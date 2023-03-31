/*
 * This file is part of InteractiveChatDiscordSrvAddon.
 *
 * Copyright (C) 2023. LoohpJames <jamesloohp@gmail.com>
 * Copyright (C) 2023. Contributors
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

package com.loohp.interactivechatdiscordsrvaddon.hook;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

public class ItemsAdderHook {

    private static Class<?> itemsAdderCoreClass;
    private static Method itemsAdderCoreGetInstMethod;
    private static Class<?> itemsAdderResourcePackDataClass;
    private static Field itemsAdderCoreResourcePackDataField;
    private static Class<?> itemsAdderResourcePackHostClass;
    private static Field itemsAdderResourcePackDataGetHostField;
    private static Method itemsAdderResourcePackHostGetURLMethod;

    static {
        try {
            itemsAdderCoreClass = Class.forName("ia.m.I");
            itemsAdderCoreGetInstMethod = Arrays.stream(itemsAdderCoreClass.getMethods()).filter(m -> m.getParameterCount() == 0 && m.getReturnType().equals(itemsAdderCoreClass)).findFirst().get();
            itemsAdderResourcePackDataClass = Class.forName("ia.m.iF");
            itemsAdderCoreResourcePackDataField = Arrays.stream(itemsAdderCoreClass.getFields()).filter(f -> f.getType().equals(itemsAdderResourcePackDataClass)).findFirst().get();
            itemsAdderResourcePackHostClass = Class.forName("ia.m.jg");
            itemsAdderResourcePackDataGetHostField = Arrays.stream(itemsAdderResourcePackDataClass.getFields()).filter(f -> f.getType().equals(itemsAdderResourcePackHostClass)).findFirst().get();
            itemsAdderResourcePackHostGetURLMethod = Arrays.stream(itemsAdderResourcePackHostClass.getDeclaredMethods()).filter(m -> m.getParameterCount() == 1 && m.getParameterTypes()[0].equals(boolean.class) && m.getReturnType().equals(String.class)).findFirst().get();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getItemsAdderResourcePackURL() {
        try {
            Object core = itemsAdderCoreGetInstMethod.invoke(null);
            Object data = itemsAdderCoreResourcePackDataField.get(core);
            Object host = itemsAdderResourcePackDataGetHostField.get(data);
            itemsAdderResourcePackHostGetURLMethod.setAccessible(true);
            return (String) itemsAdderResourcePackHostGetURLMethod.invoke(host, true);
        } catch (IllegalAccessException | InvocationTargetException | ClassCastException e) {
            e.printStackTrace();
        }
        return null;
    }

}
