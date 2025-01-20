/*
 * This file is part of InteractiveChatDiscordSrvAddon.
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
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;

public class EquipmentModelDefinition {

    public static EquipmentModelDefinition fromJson(JSONObject rootJson) throws ParseException {
        Map<EquipmentLayerType, List<EquipmentLayer>> layers = new EnumMap<>(EquipmentLayerType.class);
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
        this.layers = layers;
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

    public enum EquipmentLayerType {

        HUMANOID("humanoid"),
        HUMANOID_LEGGINGS("humanoid_leggings"),
        WINGS("wings"),
        WOLF_BODY("wolf_body"),
        HORSE_BODY("horse_body"),
        LLAMA_BODY("llama_body"),
        IC_LEGACY("ic_legacy");

        private static final EquipmentLayerType[] VALUES = values();

        private final String name;

        EquipmentLayerType(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

        public static EquipmentLayerType fromName(String name) {
            for (EquipmentLayerType type : VALUES) {
                if (type.getName().equalsIgnoreCase(name)) {
                    return type;
                }
            }
            return null;
        }
    }

}
