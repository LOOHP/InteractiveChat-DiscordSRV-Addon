/*
 * This file is part of InteractiveChatDiscordSrvAddon2.
 *
 * Copyright (C) 2020 - 2025. LoohpJames <jamesloohp@gmail.com>
 * Copyright (C) 2020 - 2025. Contributors
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

import java.util.ArrayList;
import java.util.List;

public class EnumUtils {

    public static <E extends Enum<E>> List<E> valuesBetween(Class<E> clazz, E start, E end) {
        return valuesBetween(clazz, start, end, true);
    }

    public static <E extends Enum<E>> List<E> valuesBetween(Class<E> clazz, E start, E end, boolean includeEnd) {
        List<E> includedValues = new ArrayList<>();
        if (start.equals(end)) {
            includedValues.add(start);
            return includedValues;
        }
        E[] values = clazz.getEnumConstants();
        boolean flag = false;
        for (E e : values) {
            if (flag) {
                if (e.equals(end)) {
                    if (includeEnd) {
                        includedValues.add(e);
                    }
                    return includedValues;
                } else {
                    includedValues.add(e);
                }
            } else {
                if (e.equals(start)) {
                    flag = true;
                    includedValues.add(e);
                }
            }
        }
        return includedValues;
    }

}
