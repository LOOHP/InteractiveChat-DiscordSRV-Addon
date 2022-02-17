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

package com.loohp.interactivechatdiscordsrvaddon.resources.models;

import com.loohp.interactivechatdiscordsrvaddon.registry.ResourceRegistry;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Map.Entry;

public class ModelOverride {

    private Map<ModelOverrideType, Float> predicates;
    private String model;

    public ModelOverride(Map<ModelOverrideType, Float> predicates, String model) {
        this.predicates = Collections.unmodifiableMap(predicates);
        this.model = model;
    }

    public Map<ModelOverrideType, Float> getPredicates() {
        return predicates;
    }

    public String getRawModel() {
        return model;
    }

    public String getModel() {
        return model == null ? null : (model.contains(":") ? model : ResourceRegistry.DEFAULT_NAMESPACE + ":" + model);
    }

    public boolean test(Map<ModelOverrideType, Float> data) {
        if (data == null) {
            return false;
        }
        boolean result = true;
        Map<ModelOverrideType, Float> dataCopy = new EnumMap<>(ModelOverrideType.class);
        dataCopy.putAll(data);
        for (Entry<ModelOverrideType, Float> entry : predicates.entrySet()) {
            Float value = dataCopy.remove(entry.getKey());
            if (value == null) {
                value = 0F;
            }
            float valueComparing = entry.getValue();
            if (value < valueComparing) {
                result = false;
                break;
            }
        }
        return result && dataCopy.isEmpty();
    }

    public enum ModelOverrideType {

        ANGLE,
        BLOCKING,
        BROKEN,
        CAST,
        COOLDOWN,
        DAMAGE,
        DAMAGED,
        FILLED,
        LEFTHANDED,
        PULL,
        PULLING,
        CHARGED,
        FIREWORK,
        THROWING,
        TIME,
        LEVEL,
        CUSTOM_MODEL_DATA;

        public static ModelOverrideType fromKey(String key) {
            for (ModelOverrideType type : values()) {
                if (key.toUpperCase().equals(type.toString())) {
                    return type;
                }
            }
            return null;
        }

    }

}
