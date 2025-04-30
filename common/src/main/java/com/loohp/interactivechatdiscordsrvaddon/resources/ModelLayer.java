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

package com.loohp.interactivechatdiscordsrvaddon.resources;

import com.loohp.interactivechat.objectholders.ValuePairs;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.TintColorProvider;
import com.loohp.interactivechatdiscordsrvaddon.resources.models.BlockModel;
import com.loohp.interactivechatdiscordsrvaddon.resources.models.ModelOverride;
import com.loohp.interactivechatdiscordsrvaddon.resources.textures.TextureResource;

import java.util.Map;
import java.util.function.Function;

public class ModelLayer {

    private final String modelKey;
    private final Map<ModelOverride.ModelOverrideType, Float> predicates;
    private final Map<String, TextureResource> providedTextures;
    private final TintColorProvider tintColorProvider;
    private final Function<BlockModel, ValuePairs<BlockModel, Map<String, TextureResource>>> postResolveFunction;

    public ModelLayer(String modelKey, Map<ModelOverride.ModelOverrideType, Float> predicates, Map<String, TextureResource> providedTextures, TintColorProvider tintColorProvider, Function<BlockModel, ValuePairs<BlockModel, Map<String, TextureResource>>> postResolveFunction) {
        this.modelKey = modelKey;
        this.predicates = predicates;
        this.providedTextures = providedTextures;
        this.tintColorProvider = tintColorProvider;
        this.postResolveFunction = postResolveFunction;
    }

    public String getModelKey() {
        return modelKey;
    }

    public Map<ModelOverride.ModelOverrideType, Float> getPredicates() {
        return predicates;
    }

    public Map<String, TextureResource> getProvidedTextures() {
        return providedTextures;
    }

    public TintColorProvider getTintColorProvider() {
        return tintColorProvider;
    }

    public Function<BlockModel, ValuePairs<BlockModel, Map<String, TextureResource>>> getPostResolveFunction() {
        return postResolveFunction;
    }
}