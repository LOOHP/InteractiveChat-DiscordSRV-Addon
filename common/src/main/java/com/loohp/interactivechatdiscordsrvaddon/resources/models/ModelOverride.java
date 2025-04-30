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

package com.loohp.interactivechatdiscordsrvaddon.resources.models;

import com.loohp.interactivechatdiscordsrvaddon.registry.ResourceRegistry;

import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

public class ModelOverride {

    private static float floatValueOr(Float value, float fallback) {
        return value == null ? fallback : value;
    }

    private final Map<ModelOverrideType, Float> predicates;
    private final String model;

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
            data = Collections.emptyMap();
        }
        for (Entry<ModelOverrideType, Float> entry : predicates.entrySet()) {
            if (entry.getValue() != null) {
                float value = floatValueOr(data.get(entry.getKey()), Float.NEGATIVE_INFINITY);
                float valueComparing = entry.getValue();
                if (value < valueComparing) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ModelOverride that = (ModelOverride) o;
        return Objects.equals(predicates, that.predicates) && Objects.equals(model, that.model);
    }

    @Override
    public int hashCode() {
        return Objects.hash(predicates, model);
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
        TRIM_TYPE,
        CUSTOM_MODEL_DATA;

        public static ModelOverrideType fromKey(String key) {
            for (ModelOverrideType type : values()) {
                if (key.equalsIgnoreCase(type.toString())) {
                    return type;
                }
            }
            return null;
        }

    }

}
