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

package com.loohp.interactivechatdiscordsrvaddon.utils;

import com.loohp.interactivechat.objectholders.OfflineICPlayer;
import com.loohp.interactivechat.objectholders.ValuePairs;
import com.loohp.interactivechatdiscordsrvaddon.resources.ResourceManager;
import com.loohp.interactivechatdiscordsrvaddon.resources.models.BlockModel;
import com.loohp.interactivechatdiscordsrvaddon.resources.models.ModelOverride.ModelOverrideType;
import com.loohp.interactivechatdiscordsrvaddon.resources.textures.TextureResource;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class CustomItemTextureUtils {

    public static Optional<Function<BlockModel, ValuePairs<BlockModel, Map<String, TextureResource>>>> getItemPostResolveFunction(ResourceManager resourceManager, String modelKey, EquipmentSlot heldSlot, ItemStack itemStack, boolean is1_8, Map<ModelOverrideType, Float> predicates, OfflineICPlayer player, World world, LivingEntity entity) {
        Function<BlockModel, ValuePairs<BlockModel, Map<String, TextureResource>>> function = blockModel -> new ValuePairs<>(blockModel, new HashMap<>());
        boolean noFunction = true;
        if (resourceManager.hasChimeManager()) {
            noFunction = false;
            function = function.andThen(result -> {
                return new ValuePairs<>(resourceManager.getChimeManager().resolveBlockModel(modelKey, is1_8, predicates, player, world, entity, itemStack), result.getSecond());
            });
        }
        if (resourceManager.hasOptifineManager()) {
            noFunction = false;
            function = function.andThen(result -> {
                Map<String, TextureResource> map = result.getSecond();
                ValuePairs<BlockModel, Map<String, TextureResource>> pair = resourceManager.getOptifineManager().getItemPostResolveFunction(heldSlot, itemStack, is1_8, predicates).apply(result.getFirst());
                map.putAll(pair.getSecond());
                return new ValuePairs<>(pair.getFirst(), map);
            });
        }
        return noFunction ? Optional.empty() : Optional.of(function);
    }

    public static Optional<TextureResource> getElytraOverrideTextures(ResourceManager resourceManager, EquipmentSlot heldSlot, ItemStack itemStack) {
        if (resourceManager.hasOptifineManager()) {
            return Optional.ofNullable(resourceManager.getOptifineManager().getElytraOverrideTextures(heldSlot, itemStack));
        }
        return Optional.empty();
    }

    public static Optional<TextureResource> getEnchantmentGlintOverrideTextures(ResourceManager resourceManager, EquipmentSlot heldSlot, ItemStack itemStack) {
        if (resourceManager.hasOptifineManager()) {
            return Optional.ofNullable(resourceManager.getOptifineManager().getEnchantmentGlintOverrideTextures(heldSlot, itemStack));
        }
        return Optional.empty();
    }

    public static Optional<TextureResource> getArmorOverrideTextures(ResourceManager resourceManager, String layer, EquipmentSlot heldSlot, ItemStack itemStack, OfflineICPlayer player, World world, LivingEntity entity) {
        if (resourceManager.hasChimeManager()) {
            TextureResource textureResource = resourceManager.getChimeManager().getArmorOverrideTextures(layer, itemStack, player, world, entity);
            if (textureResource != null) {
                return Optional.of(textureResource);
            }
        }
        if (resourceManager.hasOptifineManager()) {
            TextureResource textureResource = resourceManager.getOptifineManager().getArmorOverrideTextures(layer, heldSlot, itemStack);
            if (textureResource != null) {
                return Optional.of(textureResource);
            }
        }
        return Optional.empty();
    }

}
