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

package com.loohp.interactivechatdiscordsrvaddon.resources.mods.chime;

import com.google.common.collect.BoundType;
import com.google.common.collect.Range;
import com.loohp.interactivechat.libs.net.querz.nbt.tag.ByteTag;
import com.loohp.interactivechat.libs.net.querz.nbt.tag.CompoundTag;
import com.loohp.interactivechat.libs.net.querz.nbt.tag.IntTag;
import com.loohp.interactivechat.libs.net.querz.nbt.tag.ListTag;
import com.loohp.interactivechat.libs.net.querz.nbt.tag.LongTag;
import com.loohp.interactivechat.libs.net.querz.nbt.tag.NumberTag;
import com.loohp.interactivechat.libs.net.querz.nbt.tag.ShortTag;
import com.loohp.interactivechat.libs.net.querz.nbt.tag.StringTag;
import com.loohp.interactivechat.libs.net.querz.nbt.tag.Tag;
import com.loohp.interactivechat.libs.org.json.simple.JSONArray;
import com.loohp.interactivechat.libs.org.json.simple.JSONObject;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.BiomePrecipitation;
import com.loohp.interactivechatdiscordsrvaddon.resources.mods.chime.ChimeModelOverride.ChimeModelOverrideType;
import com.loohp.interactivechatdiscordsrvaddon.resources.mods.chime.ChimePredicateEnums.ItemInHand;
import com.loohp.interactivechatdiscordsrvaddon.resources.mods.chime.ChimePredicateEnums.TargetType;
import org.bukkit.inventory.EquipmentSlot;

import java.util.EnumMap;
import java.util.Map;

public class ChimeUtils {

    public static Map<ChimeModelOverrideType, Object> getAllPredicates(JSONObject predicateJson, String... preKeys) {
        Map<ChimeModelOverrideType, Object> map = new EnumMap<>(ChimeModelOverrideType.class);
        for (Object obj : predicateJson.keySet()) {
            String predicateTypeKey = obj.toString();
            Object value = predicateJson.get(predicateTypeKey);
            String[] keys = new String[preKeys.length + 1];
            System.arraycopy(preKeys, 0, keys, 0, preKeys.length);
            keys[keys.length - 1] = predicateTypeKey;
            ChimeModelOverrideType type = ChimeModelOverrideType.fromKeys(keys);
            if (type == null) {
                if (value instanceof JSONObject) {
                    map.putAll(getAllPredicates((JSONObject) value, keys));
                }
            } else {
                Class<?> valueType = type.getValueType();
                if (valueType.equals(String.class)) {
                    map.put(type, value.toString());
                } else if (valueType.equals(Range.class)) {
                    map.put(type, parseRange(Float.class, value.toString()));
                } else if (valueType.equals(HashPredicate.class)) {
                    map.put(type, HashPredicate.parseType((JSONObject) value));
                } else if (valueType.equals(BiomePrecipitation.class)) {
                    map.put(type, BiomePrecipitation.fromName(value.toString()));
                } else if (valueType.equals(ItemInHand.class)) {
                    map.put(type, ItemInHand.fromName(value.toString()));
                } else if (valueType.equals(EquipmentSlot.class)) {
                    map.put(type, EquipmentSlot.valueOf(value.toString().toUpperCase()));
                } else if (valueType.equals(TargetType.class)) {
                    map.put(type, TargetType.fromName(value.toString()));
                } else {
                    map.put(type, value);
                }
            }
        }
        return map;
    }

    public static boolean matchesJsonObject(JSONObject object, CompoundTag tag) {
        for (Object obj : object.keySet()) {
            String key = obj.toString();
            Object element = object.get(key);
            if (element == null) {
                if (tag.containsKey(key)) {
                    return false;
                }
            } else {
                if (!tag.containsKey(key) || !matchesJsonElement(element, tag.get(key))) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean matchesJsonArray(JSONArray array, ListTag<?> list) {
        outer:
        for (Object element : array) {
            for (Tag<?> tag : list) {
                if (matchesJsonElement(element, tag)) {
                    continue outer;
                }
            }
            return false;
        }
        return true;
    }

    public static boolean matchesJsonElement(Object element, Tag<?> tag) {
        if (element instanceof JSONObject) {
            return tag instanceof CompoundTag && matchesJsonObject((JSONObject) element, (CompoundTag) tag);
        } else if (element instanceof JSONArray) {
            return tag instanceof ListTag<?> && matchesJsonArray((JSONArray) element, (ListTag<?>) tag);
        } else {
            if (tag instanceof NumberTag<?>) {
                NumberTag<?> number = (NumberTag<?>) tag;
                boolean isInt = tag instanceof ByteTag || tag instanceof ShortTag || tag instanceof IntTag || tag instanceof LongTag;
                if (element instanceof Boolean) {
                    return isInt && (boolean) element == (number.asInt() == 1);
                } else if (element instanceof Number) {
                    if (isInt) {
                        return ((Number) element).longValue() == number.asLong();
                    } else {
                        return ((Number) element).doubleValue() == number.asDouble();
                    }
                } else if (element instanceof String) {
                    if (isInt) {
                        Range<Long> r = parseRange(Long.class, (String) element);
                        return r != null && r.contains(number.asLong());
                    } else {
                        Range<Double> r = parseRange(Double.class, (String) element);
                        return r != null && r.contains(number.asDouble());
                    }
                }
            } else if (tag instanceof StringTag) {
                if (element instanceof String) {
                    return element.equals(((StringTag) tag).getValue());
                }
            }
        }
        return false;
    }

    public static <T extends Comparable<T>> Range<T> parseRange(Class<T> clazz, String s) {
        try {
            if (s.startsWith("<=")) {
                return Range.upTo(parseNumber(clazz, s.substring(2)), BoundType.CLOSED);
            } else if (s.startsWith("<")) {
                return Range.upTo(parseNumber(clazz, s.substring(1)), BoundType.OPEN);
            } else if (s.startsWith(">=")) {
                return Range.downTo(parseNumber(clazz, s.substring(2)), BoundType.CLOSED);
            } else if (s.startsWith(">")) {
                return Range.downTo(parseNumber(clazz, s.substring(1)), BoundType.CLOSED);
            } else if (s.startsWith("[") || s.startsWith("(")) {
                String[] parts = s.split("\\.\\.");
                if (parts.length == 2) {
                    BoundType lt = parts[0].startsWith("[") ? BoundType.CLOSED : BoundType.OPEN;
                    BoundType rt = parts[1].endsWith("[") ? BoundType.CLOSED : BoundType.OPEN;
                    return Range.range(parseNumber(clazz, parts[0].substring(1)), lt, parseNumber(clazz, parts[1].substring(0, parts[1].length() - 1)), rt);
                }
            } else {
                String[] parts = s.split("\\.\\.");
                if (parts.length == 2) {
                    return Range.closed(parseNumber(clazz, parts[0]), parseNumber(clazz, parts[1]));
                }
            }
            return Range.singleton(parseNumber(clazz, s));
        } catch (Exception e) {
        }
        return null;
    }

    public static <T extends Comparable<T>> T parseNumber(Class<T> clazz, String s) {
        if (clazz == Double.class) {
            return (T) Double.valueOf(s);
        } else if (clazz == Float.class) {
            return (T) Float.valueOf(s);
        } else if (clazz == Long.class) {
            return (T) Long.valueOf(s);
        }
        throw new UnsupportedOperationException();
    }

    public static class HashPredicate {

        public String subTag;
        public int modulo;
        public Range<Float> value;

        public HashPredicate(String subTag, int modulo, Range<Float> value) {
            this.subTag = subTag;
            this.modulo = modulo;
            this.value = value;
        }

        public boolean matches(Tag<?> tag) {
            try {
                String[] tags = subTag.split("/");
                for (String t : tags) {
                    if (t.length() == 0) {
                        continue;
                    }
                    tag = ((CompoundTag) tag).get(t);
                }
                int i = tag.toString().hashCode();
                i = i % modulo;
                if (i < 0) {
                    i += modulo;
                }
                return value.contains((float) i);
            } catch (Exception e) {
            }
            return value.contains(-1f);
        }

        public static HashPredicate parseType(JSONObject object) {
            int modulo = ((Number) object.get("modulo")).intValue();
            Range<Float> value = parseRange(Float.class, (String) object.get("value"));
            String subTag = "";
            if (object.containsKey("tag")) {
                subTag = (String) object.get("tag");
            }
            return new HashPredicate(subTag, modulo, value);
        }

    }

}
