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

package com.loohp.interactivechatdiscordsrvaddon.resources.definitions.equipment;

import com.loohp.interactivechat.libs.org.json.simple.JSONArray;
import com.loohp.interactivechat.libs.org.json.simple.JSONObject;
import com.loohp.interactivechat.libs.org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.concurrent.ConcurrentHashMap;

public class EquipmentModelDefinition {

    public static EquipmentModelDefinition fromJson(JSONObject rootJson) throws ParseException {
        Map<EquipmentLayerType, List<EquipmentLayer>> layers = new HashMap<>();
        for (Object keyObj : rootJson.keySet()) {
            EquipmentLayerType equipmentLayerType = EquipmentLayerType.fromName((String) keyObj);
            List<EquipmentLayer> list = new ArrayList<>();
            for (Object layerObj : (JSONArray) rootJson.get(keyObj)) {
                JSONObject layerJson = (JSONObject) layerObj;
                String texture = (String) layerJson.get("texture");
                if (layerJson.containsKey("dyeable")) {
                    JSONObject dyeableJson = (JSONObject) layerJson.get("dyeable");
                    OptionalInt colorWhenUndyed;
                    if (dyeableJson.containsKey("color_when_undyed")) {
                        colorWhenUndyed = OptionalInt.of(((Number) dyeableJson.get("color_when_undyed")).intValue());
                    } else {
                        colorWhenUndyed = OptionalInt.empty();
                    }
                    list.add(new EquipmentLayer(texture, new EquipmentLayerDyeable(colorWhenUndyed)));
                } else {
                    list.add(new EquipmentLayer(texture));
                }
            }
            layers.put(equipmentLayerType, list);
        }
        return new EquipmentModelDefinition(layers);
    }

    private final Map<EquipmentLayerType, List<EquipmentLayer>> layers;

    public EquipmentModelDefinition(Map<EquipmentLayerType, List<EquipmentLayer>> layers) {
        this.layers = Collections.unmodifiableMap(layers);
    }

    public Map<EquipmentLayerType, List<EquipmentLayer>> getLayers() {
        return layers;
    }

    public List<EquipmentLayer> getLayers(EquipmentLayerType equipmentLayerType) {
        List<EquipmentLayer> list = layers.get(equipmentLayerType);
        return list == null ? Collections.emptyList() : list;
    }

    public static class EquipmentLayer {

        private final String texture;
        private final EquipmentLayerDyeable dyeable;

        public EquipmentLayer(String texture, EquipmentLayerDyeable dyeable) {
            this.texture = texture;
            this.dyeable = dyeable;
        }

        public EquipmentLayer(String texture) {
            this(texture, null);
        }

        public String getTexture() {
            return texture;
        }

        public boolean isDyeable() {
            return dyeable != null;
        }

        public EquipmentLayerDyeable getDyeable() {
            return dyeable;
        }
    }

    public static class EquipmentLayerDyeable {

        private final OptionalInt colorWhenUndyed;

        public EquipmentLayerDyeable(OptionalInt colorWhenUndyed) {
            this.colorWhenUndyed = colorWhenUndyed;
        }

        public EquipmentLayerDyeable(int colorWhenUndyed) {
            this(OptionalInt.of(colorWhenUndyed));
        }

        public OptionalInt getOptionalColorWhenUndyed() {
            return colorWhenUndyed;
        }

        public int getColorWhenUndyed(int defaultColor) {
            return colorWhenUndyed.orElse(defaultColor);
        }
    }

    public static class EquipmentLayerType {

        private static final Map<String, EquipmentLayerType> VALUES = new ConcurrentHashMap<>();

        public static final EquipmentLayerType HUMANOID = register("humanoid");
        public static final EquipmentLayerType HUMANOID_LEGGINGS = register("humanoid_leggings");
        public static final EquipmentLayerType WINGS = register("wings");
        public static final EquipmentLayerType WOLF_BODY = register("wolf_body");
        public static final EquipmentLayerType HORSE_BODY = register("horse_body");
        public static final EquipmentLayerType LLAMA_BODY = register("llama_body");
        public static final EquipmentLayerType CAMEL_SADDLE = register("camel_saddle");
        public static final EquipmentLayerType DONKEY_SADDLE = register("donkey_saddle");
        public static final EquipmentLayerType HORSE_SADDLE = register("horse_saddle");
        public static final EquipmentLayerType MULE_SADDLE = register("mule_saddle");
        public static final EquipmentLayerType PIG_SADDLE = register("pig_saddle");
        public static final EquipmentLayerType SKELETON_HORSE_SADDLE = register("skeleton_horse_saddle");
        public static final EquipmentLayerType STRIDER_SADDLE = register("strider_saddle");
        public static final EquipmentLayerType ZOMBIE_HORSE_SADDLE = register("zombie_horse_saddle");
        public static final EquipmentLayerType IC_LEGACY = register("ic_legacy");

        private static EquipmentLayerType register(String name) {
            EquipmentLayerType type = new EquipmentLayerType(name);
            VALUES.put(type.getName(), type);
            return type;
        }

        public static EquipmentLayerType fromName(String name) {
            EquipmentLayerType type = VALUES.get(name);
            return type == null ? register(name) : type;
        }

        public static Map<String, EquipmentLayerType> values() {
            return Collections.unmodifiableMap(VALUES);
        }

        private final String name;

        public EquipmentLayerType(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

        @Override
        public boolean equals(Object object) {
            if (object == null || getClass() != object.getClass()) return false;
            EquipmentLayerType that = (EquipmentLayerType) object;
            return Objects.equals(name, that.name);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(name);
        }
    }

}
