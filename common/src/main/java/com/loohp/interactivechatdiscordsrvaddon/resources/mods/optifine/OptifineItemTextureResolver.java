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

package com.loohp.interactivechatdiscordsrvaddon.resources.mods.optifine;

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

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.UnaryOperator;

public class OptifineItemTextureResolver implements CustomItemTextureResolver {

    private final OptifineManager optifineManager;

    public OptifineItemTextureResolver(OptifineManager optifineManager) {
        this.optifineManager = optifineManager;
    }

    public OptifineManager getOptifineManager() {
        return optifineManager;
    }

    @Override
    public ValuePairs<BlockModel, Map<String, TextureResource>> getItemPostResolveFunction(ValuePairs<BlockModel, Map<String, TextureResource>> previousResult, String modelKey, EquipmentSlot heldSlot, ItemStack itemStack, boolean is1_8, Map<ModelOverrideType, Float> predicates, OfflineICPlayer player, World world, LivingEntity entity, UnaryOperator<String> translateFunction) {
        Map<String, TextureResource> map = previousResult.getSecond();
        ValuePairs<BlockModel, Map<String, TextureResource>> pair = optifineManager.getItemPostResolveFunction(heldSlot, itemStack, is1_8, predicates, translateFunction).apply(previousResult.getFirst());
        map.putAll(pair.getSecond());
        return new ValuePairs<>(pair.getFirst(), map);
    }

    @Override
    public Optional<TextureResource> getElytraOverrideTextures(EquipmentSlot heldSlot, ItemStack itemStack, UnaryOperator<String> translateFunction) {
        return Optional.ofNullable(optifineManager.getElytraOverrideTextures(heldSlot, itemStack, translateFunction));
    }

    @Override
    public List<ValuePairs<TextureResource, OpenGLBlending>> getEnchantmentGlintOverrideTextures(EquipmentSlot heldSlot, ItemStack itemStack, UnaryOperator<String> translateFunction) {
        return optifineManager.getEnchantmentGlintOverrideTextures(heldSlot, itemStack, translateFunction);
    }

    @Override
    public Optional<TextureResource> getArmorOverrideTextures(String layer, EquipmentSlot heldSlot, ItemStack itemStack, OfflineICPlayer player, World world, LivingEntity entity, UnaryOperator<String> translateFunction) {
        return Optional.ofNullable(optifineManager.getArmorOverrideTextures(layer, heldSlot, itemStack, translateFunction));
    }

}
