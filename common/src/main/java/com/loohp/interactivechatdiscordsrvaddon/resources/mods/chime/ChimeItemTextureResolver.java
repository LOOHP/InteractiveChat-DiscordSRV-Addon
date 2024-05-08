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

import com.loohp.interactivechat.objectholders.OfflineICPlayer;
import com.loohp.interactivechat.objectholders.ValuePairs;
import com.loohp.interactivechatdiscordsrvaddon.resources.CustomItemTextureRegistry.CustomItemTextureResolver;
import com.loohp.interactivechatdiscordsrvaddon.resources.models.BlockModel;
import com.loohp.interactivechatdiscordsrvaddon.resources.models.ModelOverride.ModelOverrideType;
import com.loohp.interactivechatdiscordsrvaddon.resources.mods.optifine.cit.EnchantmentProperties.OpenGLBlending;
import com.loohp.interactivechatdiscordsrvaddon.resources.textures.TextureResource;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.UnaryOperator;

public class ChimeItemTextureResolver implements CustomItemTextureResolver {

    private final ChimeManager chimeManager;

    public ChimeItemTextureResolver(ChimeManager chimeManager) {
        this.chimeManager = chimeManager;
    }

    public ChimeManager getChimeManager() {
        return chimeManager;
    }


    @Override
    public ValuePairs<BlockModel, Map<String, TextureResource>> getItemPostResolveFunction(ValuePairs<BlockModel, Map<String, TextureResource>> previousResult, String modelKey, EquipmentSlot heldSlot, ItemStack itemStack, boolean is1_8, Map<ModelOverrideType, Float> predicates, OfflineICPlayer player, World world, LivingEntity entity, UnaryOperator<String> translateFunction) {
        return new ValuePairs<>(chimeManager.resolveBlockModel(modelKey, is1_8, predicates, player, world, entity, itemStack, translateFunction), previousResult.getSecond());
    }

    @Override
    public Optional<TextureResource> getElytraOverrideTextures(EquipmentSlot heldSlot, ItemStack itemStack, UnaryOperator<String> translateFunction) {
        return Optional.empty();
    }

    @Override
    public List<ValuePairs<TextureResource, OpenGLBlending>> getEnchantmentGlintOverrideTextures(EquipmentSlot heldSlot, ItemStack itemStack, UnaryOperator<String> translateFunction) {
        return Collections.emptyList();
    }

    @Override
    public Optional<TextureResource> getArmorOverrideTextures(String layer, EquipmentSlot heldSlot, ItemStack itemStack, OfflineICPlayer player, World world, LivingEntity entity, UnaryOperator<String> translateFunction) {
        return Optional.ofNullable(chimeManager.getArmorOverrideTextures(layer, itemStack, player, world, entity, translateFunction));
    }

}
