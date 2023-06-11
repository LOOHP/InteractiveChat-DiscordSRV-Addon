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

package com.loohp.interactivechatdiscordsrvaddon.resources;

import com.loohp.interactivechat.objectholders.OfflineICPlayer;
import com.loohp.interactivechat.objectholders.ValuePairs;
import com.loohp.interactivechatdiscordsrvaddon.resources.ResourceManager.ResourceRegistrySupplier;
import com.loohp.interactivechatdiscordsrvaddon.resources.models.BlockModel;
import com.loohp.interactivechatdiscordsrvaddon.resources.models.ModelOverride.ModelOverrideType;
import com.loohp.interactivechatdiscordsrvaddon.resources.mods.optifine.cit.EnchantmentProperties.OpenGLBlending;
import com.loohp.interactivechatdiscordsrvaddon.resources.textures.TextureResource;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class CustomItemTextureRegistry implements IResourceRegistry {

    public static final String IDENTIFIER = "CustomItemTextureRegistry";

    public static ResourceRegistrySupplier<CustomItemTextureRegistry> getDefaultSupplier() {
        return manager -> new CustomItemTextureRegistry();
    }

    private final List<CustomItemTextureResolver> resolvers;

    public CustomItemTextureRegistry() {
        this.resolvers = new CopyOnWriteArrayList<>();
    }

    @Override
    public String getRegistryIdentifier() {
        return IDENTIFIER;
    }

    public void appendResolver(CustomItemTextureResolver resolver) {
        resolvers.add(resolver);
    }

    public void prependResolver(CustomItemTextureResolver resolver) {
        resolvers.add(0, resolver);
    }

    public void removeResolver(CustomItemTextureResolver resolver) {
        resolvers.remove(resolver);
    }

    public Optional<Function<BlockModel, ValuePairs<BlockModel, Map<String, TextureResource>>>> getItemPostResolveFunction(String modelKey, EquipmentSlot heldSlot, ItemStack itemStack, boolean is1_8, Map<ModelOverrideType, Float> predicates, OfflineICPlayer player, World world, LivingEntity entity, UnaryOperator<String> translateFunction) {
        return resolvers.stream()
                .map(each -> (Function<ValuePairs<BlockModel, Map<String, TextureResource>>, ValuePairs<BlockModel, Map<String, TextureResource>>>) result -> each.getItemPostResolveFunction(result, modelKey, heldSlot, itemStack, is1_8, predicates, player, world, entity, translateFunction))
                .reduce(Function::andThen)
                .map(each -> ((Function<BlockModel, ValuePairs<BlockModel, Map<String, TextureResource>>>) blockModel -> new ValuePairs<>(blockModel, new HashMap<>())).andThen(each));
    }

    public Optional<TextureResource> getElytraOverrideTextures(EquipmentSlot heldSlot, ItemStack itemStack, UnaryOperator<String> translateFunction) {
        return resolvers.stream().map(each -> each.getElytraOverrideTextures(heldSlot, itemStack, translateFunction)).filter(each -> each.isPresent()).findFirst().flatMap(each -> each);
    }

    public List<ValuePairs<TextureResource, OpenGLBlending>> getEnchantmentGlintOverrideTextures(EquipmentSlot heldSlot, ItemStack itemStack, Supplier<List<ValuePairs<TextureResource, OpenGLBlending>>> ifEmpty, UnaryOperator<String> translateFunction) {
        return resolvers.stream().map(each -> each.getEnchantmentGlintOverrideTextures(heldSlot, itemStack, translateFunction)).filter(each -> !each.isEmpty()).findFirst().orElseGet(ifEmpty);
    }

    public Optional<TextureResource> getArmorOverrideTextures(String layer, EquipmentSlot heldSlot, ItemStack itemStack, OfflineICPlayer player, World world, LivingEntity entity, UnaryOperator<String> translateFunction) {
        return resolvers.stream().map(each -> each.getArmorOverrideTextures(layer, heldSlot, itemStack, player, world, entity, translateFunction)).filter(each -> each.isPresent()).findFirst().flatMap(each -> each);
    }

    public interface CustomItemTextureResolver {

        ValuePairs<BlockModel, Map<String, TextureResource>> getItemPostResolveFunction(ValuePairs<BlockModel, Map<String, TextureResource>> previousResult, String modelKey, EquipmentSlot heldSlot, ItemStack itemStack, boolean is1_8, Map<ModelOverrideType, Float> predicates, OfflineICPlayer player, World world, LivingEntity entity, UnaryOperator<String> translateFunction);

        Optional<TextureResource> getElytraOverrideTextures(EquipmentSlot heldSlot, ItemStack itemStack, UnaryOperator<String> translateFunction);

        List<ValuePairs<TextureResource, OpenGLBlending>> getEnchantmentGlintOverrideTextures(EquipmentSlot heldSlot, ItemStack itemStack, UnaryOperator<String> translateFunction);

        Optional<TextureResource> getArmorOverrideTextures(String layer, EquipmentSlot heldSlot, ItemStack itemStack, OfflineICPlayer player, World world, LivingEntity entity, UnaryOperator<String> translateFunction);

    }

}
