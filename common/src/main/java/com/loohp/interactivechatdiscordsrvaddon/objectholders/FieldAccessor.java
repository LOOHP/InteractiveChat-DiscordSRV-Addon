/*
 * This file is part of InteractiveChatDiscordSrvAddon2.
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

package com.loohp.interactivechatdiscordsrvaddon.objectholders;

import java.lang.reflect.Field;
import java.util.function.Function;

public class FieldAccessor<T> {

    public static <T> FieldAccessor<T> fromField(Field field, Function<Object, T> mapper, T defaultValue) {
        return new FieldAccessor<>(field, mapper, defaultValue);
    }

    public static <T> FieldAccessor<T> fromField(Field field, Function<Object, T> mapper) {
        return new FieldAccessor<>(field, mapper, null);
    }

    public static <T> FieldAccessor<T> fromField(Field field, T defaultValue) {
        return new FieldAccessor<>(field, null, defaultValue);
    }

    public static <T> FieldAccessor<T> fromField(Field field) {
        return new FieldAccessor<>(field, null, null);
    }

    public static <T> FieldAccessor<T> ofValue(T defaultValue) {
        return new FieldAccessor<>(null, null, defaultValue);
    }

    private final Field field;
    private final Function<Object, T> mapper;
    private final T defaultValue;

    private FieldAccessor(Field field, Function<Object, T> mapper, T defaultValue) {
        this.field = field;
        this.mapper = mapper;
        this.defaultValue = defaultValue;
    }

    public T get(Object object) throws IllegalAccessException {
        field.setAccessible(true);
        Object obj = field.get(object);
        if (mapper != null) {
            return mapper.apply(obj);
        }
        return (T) obj;
    }

    public T getOrDefault(Object object) {
        if (field == null) {
            return defaultValue;
        }
        try {
            field.setAccessible(true);
            Object obj = field.get(object);
            if (mapper != null) {
                return mapper.apply(obj);
            }
            return (T) obj;
        } catch (Throwable e) {
            return defaultValue;
        }
    }

    public Field getField() {
        return field;
    }

    public Function<Object, T> getMapper() {
        return mapper;
    }

    public T getDefaultValue() {
        return defaultValue;
    }

}
