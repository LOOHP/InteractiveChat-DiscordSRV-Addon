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

package com.loohp.interactivechatdiscordsrvaddon.nms;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.loohp.interactivechat.libs.com.google.gson.Gson;
import com.loohp.interactivechat.libs.com.google.gson.JsonElement;
import com.loohp.interactivechat.libs.com.google.gson.JsonObject;
import com.loohp.interactivechat.libs.net.kyori.adventure.key.Key;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.Component;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.format.NamedTextColor;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.format.TextColor;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.object.PlayerHeadObjectContents;
import com.loohp.interactivechat.libs.org.apache.commons.lang3.math.Fraction;
import com.loohp.interactivechat.nms.NMS;
import com.loohp.interactivechat.objectholders.ICMaterial;
import com.loohp.interactivechat.utils.InteractiveChatComponentSerializer;
import com.loohp.interactivechat.utils.NativeJsonConverter;
import com.loohp.interactivechat.utils.ReflectionUtils;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.AdvancementData;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.AdvancementType;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.AttributeBase;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.BiomePrecipitation;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.CustomModelData;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.EquipmentSlotGroup;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.ItemDamageInfo;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.LegacyDimensionManager;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.MoonPhase;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.PaintingVariant;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.ProfileProperty;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.TintColorProvider;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import net.minecraft.ChatFormatting;
import net.minecraft.DetectedVersion;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.advancements.criterion.BlockPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.component.predicates.DataComponentPredicate;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.players.ProfileResolver;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.damagesource.CombatTracker;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.AdventureModePredicate;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.Instrument;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.JukeboxPlayable;
import net.minecraft.world.item.JukeboxSong;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.item.component.BundleContents;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.component.Fireworks;
import net.minecraft.world.item.component.InstrumentComponent;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.item.component.TypedEntityData;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.item.equipment.trim.TrimMaterial;
import net.minecraft.world.item.equipment.trim.TrimPattern;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.DecoratedPotPatterns;
import net.minecraft.world.level.saveddata.maps.MapDecorationType;
import net.minecraft.world.level.storage.TagValueOutput;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.advancement.Advancement;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.banner.PatternType;
import org.bukkit.craftbukkit.CraftEquipmentSlot;
import org.bukkit.craftbukkit.CraftRegistry;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.advancement.CraftAdvancement;
import org.bukkit.craftbukkit.attribute.CraftAttributeInstance;
import org.bukkit.craftbukkit.block.banner.CraftPatternType;
import org.bukkit.craftbukkit.enchantments.CraftEnchantment;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.entity.CraftEntityType;
import org.bukkit.craftbukkit.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.inventory.trim.CraftTrimMaterial;
import org.bukkit.craftbukkit.inventory.trim.CraftTrimPattern;
import org.bukkit.craftbukkit.map.CraftMapCursor;
import org.bukkit.craftbukkit.potion.CraftPotionEffectType;
import org.bukkit.craftbukkit.potion.CraftPotionUtil;
import org.bukkit.craftbukkit.util.CraftChatMessage;
import org.bukkit.craftbukkit.util.CraftLocation;
import org.bukkit.craftbukkit.util.CraftMagicNumbers;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.map.MapCursor;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@SuppressWarnings("unused")
public class V26_1_1 extends NMSAddonWrapper {

    private final Field adventureModePredicatePredicatesField;
    private final Method bundleContentsGetWeightMethod;
    private final Field attributeBaseSentimentField;

    public V26_1_1() {
        try {
            adventureModePredicatePredicatesField = ReflectionUtils.findDeclaredField(AdventureModePredicate.class, List.class, "predicates");
            bundleContentsGetWeightMethod = ReflectionUtils.findDeclaredMethod(BundleContents.class, new Class[] {ItemInstance.class}, "getWeight");
            attributeBaseSentimentField = ReflectionUtils.findDeclaredField(net.minecraft.world.entity.ai.attributes.Attribute.class, Attribute.Sentiment.class, "sentiment");
        } catch (NoSuchFieldException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Map<ICMaterial, TintColorProvider.SpawnEggTintData> getSpawnEggColorMap() {
        return Collections.emptyMap(); // Spawn Eggs no longer uses tinting
    }

    @SuppressWarnings("PatternValidation")
    @Override
    public Key getMapCursorTypeKey(MapCursor mapCursor) {
        MapDecorationType nmsType = CraftMapCursor.CraftType.bukkitToMinecraftHolder(mapCursor.getType()).value();
        Identifier key = nmsType.assetId();
        return Key.key(key.getNamespace(), key.getPath());
    }

    @SuppressWarnings({"PatternValidation", "deprecation"})
    @Override
    public Key getPatternTypeKey(PatternType patternType) {
        NamespacedKey key = patternType.getKey();
        return Key.key(key.getNamespace(), key.getKey());
    }

    @Override
    public LegacyDimensionManager getLegacyDimensionManager(World world) {
        throw new UnsupportedOperationException();
    }

    @SuppressWarnings("PatternValidation")
    @Override
    public Key getNamespacedKey(World world) {
        ServerLevel worldServer = ((CraftWorld) world).getHandle();
        Identifier key = worldServer.getTypeKey().identifier();
        return Key.key(key.getNamespace(), key.getPath());
    }

    @Override
    public BiomePrecipitation getPrecipitation(Location location) {
        ServerLevel worldServer = ((CraftWorld) location.getWorld()).getHandle();
        BlockPos blockPos = new BlockPos(location.getBlockX(), location.getBlockY(), location.getBlockZ());
        Biome biomeBase = worldServer.getBiome(blockPos).value();
        Biome.Precipitation precipitation = biomeBase.getPrecipitationAt(blockPos, location.getBlockY());
        return BiomePrecipitation.fromName(precipitation.name());
    }

    @Override
    public OptionalInt getTropicalFishBucketVariantTag(ItemStack bucket) {
        net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(bucket);
        TypedEntityData<net.minecraft.world.entity.EntityType<?>> typedEntityData = nmsItemStack.get(DataComponents.ENTITY_DATA);
        if (!net.minecraft.world.entity.EntityType.TROPICAL_FISH.equals(typedEntityData.type())) {
            return OptionalInt.empty();
        }
        CompoundTag nbt = typedEntityData.copyTagWithoutId();
        return nbt.getInt("BucketVariantTag").map(i -> OptionalInt.of(i)).orElse(OptionalInt.empty());
    }

    @Override
    public PotionType getBasePotionType(ItemStack potion) {
        return ((PotionMeta) potion.getItemMeta()).getBasePotionType();
    }

    @Override
    public List<PotionEffect> getAllPotionEffects(ItemStack potion) {
        net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(potion);
        PotionContents potionContents = nmsItemStack.get(DataComponents.POTION_CONTENTS);
        List<PotionEffect> effects = new ArrayList<>();
        for (MobEffectInstance mobEffect : potionContents.getAllEffects()) {
            effects.add(CraftPotionUtil.toBukkit(mobEffect));
        }
        return effects;
    }

    @Override
    public TextColor getPotionEffectChatColor(PotionEffectType type) {
        MobEffect mobEffectList = CraftPotionEffectType.bukkitToMinecraftHolder(type).value();
        ChatFormatting chatFormat = mobEffectList.getCategory().getTooltipFormatting();
        return TextColor.color(chatFormat.getColor());
    }

    @Override
    public Map<AttributeBase, AttributeModifier> getPotionAttributeModifiers(PotionEffect effect) {
        attributeBaseSentimentField.setAccessible(true);
        Map<AttributeBase, AttributeModifier> attributes = new HashMap<>();
        MobEffectInstance mobEffect = CraftPotionUtil.fromBukkit(effect);
        MobEffect mobEffectList = mobEffect.getEffect().value();
        mobEffectList.createModifiers(effect.getAmplifier(), (holder, nmsAttributeModifier) -> {
            try {
                net.minecraft.world.entity.ai.attributes.Attribute nmsAttributeBase = holder.value();
                Attribute.Sentiment nmsSentiment = (Attribute.Sentiment) attributeBaseSentimentField.get(nmsAttributeBase);
                AttributeBase.AttributeSentiment sentiment = AttributeBase.AttributeSentiment.fromNMS(nmsSentiment);
                AttributeBase attributeBase = new AttributeBase(nmsAttributeBase.getDescriptionId(), nmsAttributeBase.isClientSyncable(), sentiment);
                AttributeModifier attributeModifier = CraftAttributeInstance.convert(nmsAttributeModifier);
                attributes.put(attributeBase, attributeModifier);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });
        return attributes;
    }

    @Override
    public boolean isItemUnbreakable(ItemStack itemStack) {
        if (itemStack.getType().isAir()) {
            return false;
        }
        ItemMeta itemMeta = itemStack.getItemMeta();
        return itemMeta != null && itemMeta.isUnbreakable();
    }

    @Override
    public List<ICMaterial> getItemCanPlaceOnList(ItemStack itemStack) {
        try {
            net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
            AdventureModePredicate adventureModePredicate = nmsItemStack.get(DataComponents.CAN_PLACE_ON);
            if (adventureModePredicate == null) {
                return Collections.emptyList();
            }
            adventureModePredicatePredicatesField.setAccessible(true);
            List<BlockPredicate> predicate = (List<BlockPredicate>) adventureModePredicatePredicatesField.get(adventureModePredicate);
            List<ICMaterial> materials = new ArrayList<>();
            for (BlockPredicate block : predicate) {
                Optional<HolderSet<Block>> optSet = block.blocks();
                if (optSet.isPresent()) {
                    for (Holder<Block> set : optSet.get()) {
                        materials.add(ICMaterial.of(CraftMagicNumbers.getMaterial(set.value())));
                    }
                }
            }
            return materials;
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ICMaterial> getItemCanDestroyList(ItemStack itemStack) {
        try {
            net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
            AdventureModePredicate adventureModePredicate = nmsItemStack.get(DataComponents.CAN_BREAK);
            if (adventureModePredicate == null) {
                return Collections.emptyList();
            }
            adventureModePredicatePredicatesField.setAccessible(true);
            List<BlockPredicate> predicate = (List<BlockPredicate>) adventureModePredicatePredicatesField.get(adventureModePredicate);
            List<ICMaterial> materials = new ArrayList<>();
            for (BlockPredicate block : predicate) {
                Optional<HolderSet<Block>> optSet = block.blocks();
                if (optSet.isPresent()) {
                    for (Holder<Block> set : optSet.get()) {
                        materials.add(ICMaterial.of(CraftMagicNumbers.getMaterial(set.value())));
                    }
                }
            }
            return materials;
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public OptionalInt getLeatherArmorColor(ItemStack itemStack) {
        net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        DyedItemColor dyedItemColor = nmsItemStack.get(DataComponents.DYED_COLOR);
        return dyedItemColor == null ? OptionalInt.empty() : OptionalInt.of(dyedItemColor.rgb());
    }

    @Override
    public boolean hasBlockEntityTag(ItemStack itemStack) {
        net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        TypedEntityData<BlockEntityType<?>> data = nmsItemStack.get(DataComponents.BLOCK_ENTITY_DATA);
        return data != null;
    }

    @Override
    public Component getInstrumentDescription(ItemStack itemStack) {
        net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        InstrumentComponent component = nmsItemStack.get(DataComponents.INSTRUMENT);
        if (component == null) {
            return null;
        }
        Holder<Instrument> optHolder = component.instrument();
        net.minecraft.network.chat.Component description = optHolder.value().description();
        return InteractiveChatComponentSerializer.gson().deserialize(CraftChatMessage.toJSON(description));
    }

    @SuppressWarnings("PatternValidation")
    @Override
    public PaintingVariant getPaintingVariant(ItemStack itemStack) {
        net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        Holder<net.minecraft.world.entity.decoration.painting.PaintingVariant> paintingVariantHolder = nmsItemStack.get(DataComponents.PAINTING_VARIANT);
        if (paintingVariantHolder == null) {
            return null;
        }
        net.minecraft.world.entity.decoration.painting.PaintingVariant paintingVariant = paintingVariantHolder.value();
        Identifier key = paintingVariantHolder.unwrapKey()
                .map(k -> k.identifier())
                .orElseGet(() -> CraftRegistry.getMinecraftRegistry().lookupOrThrow(Registries.PAINTING_VARIANT).getKey(paintingVariant));
        Optional<Component> title = paintingVariant.title().map(c -> InteractiveChatComponentSerializer.gson().deserialize(CraftChatMessage.toJSON(c)));
        Optional<Component> author = paintingVariant.author().map(c -> InteractiveChatComponentSerializer.gson().deserialize(CraftChatMessage.toJSON(c)));
        return new PaintingVariant(Key.key(key.getNamespace(), key.getPath()), paintingVariant.width(), paintingVariant.height(), title, author);
    }

    @Override
    public String getEntityNBT(Entity entity) {
        net.minecraft.world.entity.Entity nmsEntity = ((CraftEntity) entity).getHandle();
        TagValueOutput output = TagValueOutput.createWithoutContext(ProblemReporter.DISCARDING);
        nmsEntity.save(output);
        return output.buildResult().toString();
    }

    @Override
    public float getLegacyTrimMaterialIndex(Object trimMaterial) {
        throw new UnsupportedOperationException();
    }

    @Override
    public TextColor getTrimMaterialColor(Object trimMaterial) {
        if (trimMaterial == null) {
            return NamedTextColor.GRAY;
        }
        TrimMaterial nmsTrimMaterial = ((CraftTrimMaterial) trimMaterial).getHandle();
        TextColor textColor = InteractiveChatComponentSerializer.gson().deserialize(CraftChatMessage.toJSON(nmsTrimMaterial.description())).color();
        return textColor == null ? NamedTextColor.GRAY : textColor;
    }

    @Override
    public AdvancementData getAdvancementDataFromBukkitAdvancement(Object bukkitAdvancement) {
        AdvancementHolder holder = ((CraftAdvancement) bukkitAdvancement).getHandle();
        Optional<DisplayInfo> optAdvancementDisplay = holder.value().display();
        if (!optAdvancementDisplay.isPresent()) {
            return null;
        }
        DisplayInfo display = optAdvancementDisplay.get();
        Component title = InteractiveChatComponentSerializer.gson().deserialize(CraftChatMessage.toJSON(display.getTitle()));
        Component description = InteractiveChatComponentSerializer.gson().deserialize(CraftChatMessage.toJSON(display.getDescription()));
        ItemStack item = CraftItemStack.asBukkitCopy(display.getIcon().create());
        AdvancementType advancementType = AdvancementType.fromName(display.getType().getSerializedName());
        boolean isMinecraft = holder.id().getNamespace().equals(Key.MINECRAFT_NAMESPACE);
        return new AdvancementData(title, description, item, advancementType, isMinecraft);
    }

    @Override
    public Advancement getBukkitAdvancementFromEvent(Event event) {
        return ((PlayerAdvancementDoneEvent) event).getAdvancement();
    }

    @Override
    public boolean matchArmorSlot(ItemStack armorItem, EquipmentSlot slot) {
        net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(armorItem);
        Equippable equippable = nmsItemStack.get(DataComponents.EQUIPPABLE);
        if (equippable == null) {
            return false;
        }
        if (equippable.allowedEntities().map(a -> a.stream().anyMatch(s -> s.value().equals(net.minecraft.world.entity.EntityType.PLAYER))).orElse(true)) {
            return CraftEquipmentSlot.getSlot(equippable.slot()).equals(slot);
        }
        return false;
    }

    @SuppressWarnings("PatternValidation")
    @Override
    public Key getArmorMaterialKey(ItemStack armorItem) {
        net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(armorItem);
        Equippable equippable = nmsItemStack.get(DataComponents.EQUIPPABLE);
        if (equippable == null) {
            return null;
        }
        return equippable.assetId().map(key -> Key.key(key.identifier().getNamespace(), key.identifier().getPath())).orElse(null);
    }

    @Override
    public Map<EquipmentSlotGroup, Multimap<AttributeBase, AttributeModifier>> getItemAttributeModifiers(ItemStack itemStack) {
        attributeBaseSentimentField.setAccessible(true);
        net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        Map<EquipmentSlotGroup, Multimap<AttributeBase, AttributeModifier>> result = new EnumMap<>(EquipmentSlotGroup.class);
        for (net.minecraft.world.entity.EquipmentSlotGroup slotGroup : net.minecraft.world.entity.EquipmentSlotGroup.values()) {
            EquipmentSlotGroup equipmentSlotGroup = EquipmentSlotGroup.fromName(slotGroup.getSerializedName());
            nmsItemStack.forEachModifier(slotGroup, (holder, nmsAttributeModifier, itemAttributeModifiersB) -> {
                try {
                    Multimap<AttributeBase, AttributeModifier> attributes = result.computeIfAbsent(equipmentSlotGroup, k -> LinkedHashMultimap.create());
                    net.minecraft.world.entity.ai.attributes.Attribute nmsAttributeBase = holder.value();
                    net.minecraft.world.entity.ai.attributes.Attribute.Sentiment nmsSentiment = (Attribute.Sentiment) attributeBaseSentimentField.get(nmsAttributeBase);
                    AttributeBase.AttributeSentiment sentiment = AttributeBase.AttributeSentiment.fromNMS(nmsSentiment);
                    boolean hidden = itemAttributeModifiersB == ItemAttributeModifiers.Display.hidden();
                    AttributeBase attributeBase = new AttributeBase(nmsAttributeBase.getDescriptionId(), nmsAttributeBase.isClientSyncable(), sentiment, hidden);
                    AttributeModifier attributeModifier = CraftAttributeInstance.convert(nmsAttributeModifier);
                    attributes.put(attributeBase, attributeModifier);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        return result;
    }

    @Override
    public Component getDeathMessage(Player player) {
        ServerPlayer entityPlayer = ((CraftPlayer) player).getHandle();
        CombatTracker combatTracker = entityPlayer.getCombatTracker();
        return InteractiveChatComponentSerializer.gson().deserialize(CraftChatMessage.toJSON(combatTracker.getDeathMessage()));
    }

    @SuppressWarnings("PatternValidation")
    @Override
    public Key getDecoratedPotSherdPatternName(ItemStack itemStack) {
        net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        Item item = nmsItemStack.getItem();
        Identifier key = DecoratedPotPatterns.getPatternFromItem(item).identifier();
        return Key.key(key.getNamespace(), key.getPath());
    }
    
    @Override
    public Component getSulfurCubeContentBlockDescription(ItemStack itemStack) {
        return null;
    }

    @Override
    public boolean isJukeboxPlayable(ItemStack itemStack) {
        net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        JukeboxPlayable jukeboxPlayable = nmsItemStack.get(DataComponents.JUKEBOX_PLAYABLE);
        return jukeboxPlayable != null;
    }

    @Override
    public boolean shouldSongShowInToolTip(ItemStack disc) {
        return isJukeboxPlayable(disc);
    }

    @Override
    public Component getJukeboxSongDescription(ItemStack disc) {
        net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(disc);
        JukeboxPlayable jukeboxPlayable = nmsItemStack.get(DataComponents.JUKEBOX_PLAYABLE);
        if (jukeboxPlayable == null) {
            return null;
        }
        JukeboxSong jukeboxSong = jukeboxPlayable.song().value();
        return InteractiveChatComponentSerializer.gson().deserialize(CraftChatMessage.toJSON(jukeboxSong.description()));
    }

    @Override
    public Component getEnchantmentDescription(Enchantment enchantment) {
        net.minecraft.network.chat.Component description = CraftEnchantment.bukkitToMinecraftHolder(enchantment).value().description();
        return InteractiveChatComponentSerializer.gson().deserialize(CraftChatMessage.toJSON(description));
    }

    @Override
    public List<Enchantment> getEnchantmentOrderForTooltip(Collection<Enchantment> enchantments) {
        Optional<HolderSet.Named<net.minecraft.world.item.enchantment.Enchantment>> optList = CraftRegistry.getMinecraftRegistry().lookupOrThrow(Registries.ENCHANTMENT).get(EnchantmentTags.TOOLTIP_ORDER);
        if (!optList.isPresent()) {
            return new ArrayList<>(enchantments);
        }
        HolderSet.Named<net.minecraft.world.item.enchantment.Enchantment> list = optList.get();
        List<Enchantment> result = new ArrayList<>();
        for (Holder<net.minecraft.world.item.enchantment.Enchantment> registryEntry : list) {
            Enchantment enchantment = CraftEnchantment.minecraftHolderToBukkit(registryEntry);
            if (enchantments.contains(enchantment)) {
                result.add(enchantment);
            }
        }
        for (Enchantment enchantment : enchantments) {
            if (!result.contains(enchantment)) {
                result.add(enchantment);
            }
        }
        return result;
    }

    @SuppressWarnings("deprecation")
    @Override
    public String getEffectTranslationKey(PotionEffectType type) {
        NamespacedKey namespacedKey = type.getKey();
        return "effect." + namespacedKey.getNamespace() + "." + namespacedKey.getKey();
    }

    @Override
    public String getEntityTypeTranslationKey(EntityType type) {
        return CraftEntityType.bukkitToMinecraftHolder(type).getRegisteredName();
    }

    @Override
    public FishHook getFishHook(Player player) {
        ServerPlayer entityPlayer = ((CraftPlayer) player).getHandle();
        FishingHook entityFishingHook = entityPlayer.fishing;
        return entityFishingHook == null ? null : (FishHook) entityFishingHook.getBukkitEntity();
    }

    @Override
    public String getServerResourcePack() {
        return Bukkit.getResourcePack();
    }

    @Override
    public String getServerResourcePackHash() {
        return Bukkit.getResourcePackHash();
    }

    @Override
    public int getServerResourcePackMajorVersion() {
        return DetectedVersion.tryDetectVersion().packVersion(PackType.CLIENT_RESOURCES).major();
    }

    @Override
    public int getServerResourcePackMinorVersion() {
        return DetectedVersion.tryDetectVersion().packVersion(PackType.CLIENT_RESOURCES).minor();
    }

    @Override
    public float getLegacyEnchantmentDamageBonus(ItemStack itemStack, LivingEntity livingEntity) {
        return 0F;
    }

    @Override
    public int getItemComponentsSize(ItemStack itemStack) { // love gorgor
        net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        return nmsItemStack.getComponents().size();
    }

    @Override
    public GameProfile getPlayerHeadProfile(ItemStack playerHead) {
        try {
            net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(playerHead);
            ResolvableProfile resolvableProfile = nmsItemStack.get(DataComponents.PROFILE);
            if (resolvableProfile == null) {
                return null;
            }
            ProfileResolver resolver = ((CraftWorld) Bukkit.getWorlds().get(0)).getHandle().getServer().services().profileResolver();
            return resolvableProfile.resolveProfile(resolver).get();
        } catch (InterruptedException | ExecutionException e) {
            return null;
        }
    }

    @Override
    public GameProfile getPlayerHeadProfile(PlayerHeadObjectContents contents) {
        try {
            ResolvableProfile resolvableProfile;
            if (!contents.profileProperties().isEmpty() || (contents.name() != null && contents.id() != null)) {
                Map<String, Property> properties = new LinkedHashMap<>();
                for (PlayerHeadObjectContents.ProfileProperty profileProperties : contents.profileProperties()) {
                    properties.put(profileProperties.name(), new Property(profileProperties.name(), profileProperties.value(), profileProperties.signature()));
                }
                PropertyMap propertyMap = new PropertyMap(Multimaps.forMap(properties));
                GameProfile gameProfile = new GameProfile(contents.id() == null ? new UUID(0, 0) : contents.id(), contents.name() == null ? "" : contents.name(), propertyMap);
                resolvableProfile = ResolvableProfile.createResolved(gameProfile);
            } else if (contents.name() != null) {
                resolvableProfile = ResolvableProfile.createUnresolved(contents.name());
            } else if (contents.id() != null) {
                resolvableProfile = ResolvableProfile.createUnresolved(contents.id());
            } else {
                return null;
            }
            ProfileResolver resolver = ((CraftWorld) Bukkit.getWorlds().get(0)).getHandle().getServer().services().profileResolver();
            return resolvableProfile.resolveProfile(resolver).get();
        } catch (InterruptedException | ExecutionException e) {
            return null;
        }
    }

    @Override
    public ItemFlag getHideAdditionalItemFlag() {
        return ItemFlag.HIDE_ADDITIONAL_TOOLTIP;
    }

    @Override
    public boolean shouldHideTooltip(ItemStack itemStack) {
        if (!itemStack.hasItemMeta()) {
            return false;
        }
        return itemStack.getItemMeta().isHideTooltip();
    }

    @SuppressWarnings("PatternValidation")
    @Override
    public Key getAttributeModifierKey(Object attributeModifier) {
        NamespacedKey namespacedKey = ((AttributeModifier) attributeModifier).getKey();
        return Key.key(namespacedKey.getNamespace(), namespacedKey.getKey());
    }

    @Override
    public ProfileProperty toProfileProperty(Property property) {
        return new ProfileProperty(property.name(), property.value(), property.signature());
    }

    @SuppressWarnings("unchecked")
    @Override
    public Fraction getWeightForBundle(ItemStack itemStack) {
        try {
            net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
            bundleContentsGetWeightMethod.setAccessible(true);
            DataResult<org.apache.commons.lang3.math.Fraction> dataResult = (DataResult<org.apache.commons.lang3.math.Fraction>) bundleContentsGetWeightMethod.invoke(null, nmsItemStack);
            org.apache.commons.lang3.math.Fraction weight = dataResult.getOrThrow();
            return Fraction.getFraction(weight.getNumerator(), weight.getDenominator());
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public CustomModelData getCustomModelData(ItemStack itemStack) {
        net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        net.minecraft.world.item.component.CustomModelData customModelData = nmsItemStack.get(DataComponents.CUSTOM_MODEL_DATA);
        if (customModelData == null) {
            return null;
        }
        return new CustomModelData(customModelData.floats(), customModelData.flags(), customModelData.strings(), customModelData.colors());
    }

    @Override
    public boolean hasDataComponent(ItemStack itemStack, Key componentName, boolean ignoreDefault) {
        net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        Optional<DataComponentType<?>> optType = BuiltInRegistries.DATA_COMPONENT_TYPE.getOptional(Identifier.fromNamespaceAndPath(componentName.namespace(), componentName.value()));
        if (!optType.isPresent()) {
            return false;
        }
        DataComponentType<?> dataComponentType = optType.get();
        return ignoreDefault ? nmsItemStack.hasNonDefault(dataComponentType) : nmsItemStack.has(dataComponentType);
    }

    @Override
    public String getBlockStateProperty(ItemStack itemStack, String property) {
        net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        BlockItemStateProperties blockStateComponent = nmsItemStack.get(DataComponents.BLOCK_STATE);
        if (blockStateComponent == null) {
            return null;
        }
        return blockStateComponent.properties().get(property);
    }

    @Override
    public ItemDamageInfo getItemDamageInfo(ItemStack itemStack) {
        net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        int damage = nmsItemStack.getDamageValue();
        int maxDamage = nmsItemStack.getMaxDamage();
        return new ItemDamageInfo(damage, maxDamage);
    }

    @Override
    public float getItemCooldownProgress(Player player, ItemStack itemStack) {
        ServerPlayer entityPlayer = ((CraftPlayer) player).getHandle();
        net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        return entityPlayer.getCooldowns().getCooldownPercent(nmsItemStack, 0.0F);
    }

    @Override
    public float getSkyAngle(Location location) {
        BlockPos blockPosition = CraftLocation.toBlockPosition(location);
        return ((CraftWorld) location.getWorld()).getHandle().environmentAttributes().getValue(EnvironmentAttributes.SUN_ANGLE, blockPosition) / 360.0F;
    }

    @Override
    public MoonPhase getMoonPhase(Location location) {
        BlockPos blockPosition = CraftLocation.toBlockPosition(location);
        net.minecraft.world.level.MoonPhase moonphase = ((CraftWorld) location.getWorld()).getHandle().environmentAttributes().getValue(EnvironmentAttributes.MOON_PHASE, blockPosition);
        return MoonPhase.fromIndex(moonphase.index());
    }

    @Override
    public int getCrossbowPullTime(ItemStack itemStack, LivingEntity livingEntity) {
        net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        net.minecraft.world.entity.LivingEntity entityLiving = ((CraftLivingEntity) livingEntity).getHandle();
        return CrossbowItem.getChargeDuration(nmsItemStack, entityLiving);
    }

    @Override
    public int getItemUseTimeLeft(LivingEntity livingEntity) {
        net.minecraft.world.entity.LivingEntity entityLiving = ((CraftLivingEntity) livingEntity).getHandle();
        return entityLiving.getUseItemRemainingTicks();
    }

    @Override
    public int getTicksUsedSoFar(ItemStack itemStack, LivingEntity livingEntity) {
        net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        net.minecraft.world.entity.LivingEntity entityLiving = ((CraftLivingEntity) livingEntity).getHandle();
        return nmsItemStack.getUseDuration(entityLiving) - getItemUseTimeLeft(livingEntity);
    }

    @SuppressWarnings("PatternValidation")
    @Override
    public Key getItemModelResourceLocation(ItemStack itemStack) {
        net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        Identifier itemModel = nmsItemStack.get(DataComponents.ITEM_MODEL);
        if (itemModel == null) {
            return NMS.getInstance().getNMSItemStackNamespacedKey(itemStack);
        }
        return Key.key(itemModel.getNamespace(), itemModel.getPath());
    }

    @Override
    public Boolean getEnchantmentGlintOverride(ItemStack itemStack) {
        if (!itemStack.hasItemMeta()) {
            return null;
        }
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (!itemMeta.hasEnchantmentGlintOverride()) {
            return null;
        }
        return itemMeta.getEnchantmentGlintOverride();
    }

    @SuppressWarnings("PatternValidation")
    @Override
    public Key getCustomTooltipResourceLocation(ItemStack itemStack) {
        if (!itemStack.hasItemMeta()) {
            return null;
        }
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (!itemMeta.hasTooltipStyle()) {
            return null;
        }
        NamespacedKey namespacedKey = itemMeta.getTooltipStyle();
        return Key.key(namespacedKey.getNamespace(), namespacedKey.getKey());
    }

    @Override
    public String getBannerPatternTranslationKey(PatternType type, DyeColor color) {
        String translationKey = CraftPatternType.bukkitToMinecraftHolder(type).value().translationKey();
        return translationKey + "." + color.name().toLowerCase();
    }

    @Override
    public Component getTrimMaterialDescription(Object trimMaterial) {
        TrimMaterial material = CraftTrimMaterial.bukkitToMinecraftHolder((org.bukkit.inventory.meta.trim.TrimMaterial) trimMaterial).value();
        net.minecraft.network.chat.Component description = material.description();
        return InteractiveChatComponentSerializer.gson().deserialize(CraftChatMessage.toJSON(description));
    }

    @Override
    public Component getTrimPatternDescription(Object trimPattern, Object trimMaterial) {
        TrimPattern pattern = CraftTrimPattern.bukkitToMinecraftHolder((org.bukkit.inventory.meta.trim.TrimPattern) trimPattern).value();
        net.minecraft.network.chat.Component description;
        if (trimMaterial == null) {
            description = pattern.description();
        } else {
            TrimMaterial material = CraftTrimMaterial.bukkitToMinecraftHolder((org.bukkit.inventory.meta.trim.TrimMaterial) trimMaterial).value();
            description = pattern.copyWithStyle(Holder.direct(material));
        }
        return InteractiveChatComponentSerializer.gson().deserialize(CraftChatMessage.toJSON(description));
    }

    @Override
    public OptionalInt getFireworkFlightDuration(ItemStack itemStack) {
        net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        Fireworks fireworks = nmsItemStack.get(DataComponents.FIREWORKS);
        if (fireworks == null) {
            return OptionalInt.empty();
        }
        return OptionalInt.of(fireworks.flightDuration());
    }

    @Override
    public boolean shouldShowOperatorBlockWarnings(ItemStack itemStack, Player player) {
        ServerPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
        net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        return nmsItemStack.getItem().shouldPrintOpWarning(nmsItemStack, nmsPlayer);
    }

    @Override
    public Object getItemStackDataComponentValue(ItemStack itemStack, Key component) {
        net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        DataComponentType<?> componentType = BuiltInRegistries.DATA_COMPONENT_TYPE.getValue(Identifier.fromNamespaceAndPath(component.namespace(), component.value()));
        if (componentType == null) {
            return false;
        }
        return nmsItemStack.get(componentType);
    }

    @Override
    public Object serializeDataComponent(Key component, String data) {
        DataComponentType<?> componentType = BuiltInRegistries.DATA_COMPONENT_TYPE.getValue(Identifier.fromNamespaceAndPath(component.namespace(), component.value()));
        if (componentType == null) {
            return null;
        }
        JsonElement jsonElement = new Gson().fromJson(data, JsonElement.class);
        Object nativeJsonElement = NativeJsonConverter.toNative(jsonElement);
        return componentType.codec().decode(CraftRegistry.getMinecraftRegistry().createSerializationContext((DynamicOps<Object>) (DynamicOps<?>) JsonOps.INSTANCE), nativeJsonElement).result().map(r -> r.getFirst()).orElse(null);
    }

    @Override
    public boolean evaluateComponentPredicateOnItemStack(ItemStack itemStack, String predicateData, String data) {
        JsonElement jsonElement = new Gson().fromJson(predicateData, JsonElement.class);
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("predicate", jsonElement);
        Object nativeJsonObject = NativeJsonConverter.toNative(jsonObject);
        DataComponentPredicate.Single<?> predicate = DataComponentPredicate.singleCodec("predicate").codec().decode(CraftRegistry.getMinecraftRegistry().createSerializationContext((DynamicOps<Object>) (DynamicOps<?>) JsonOps.INSTANCE), nativeJsonObject).result().map(r -> r.getFirst()).orElse(null);
        if (predicate == null) {
            return false;
        }
        net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        return predicate.predicate().matches(nmsItemStack);
    }

    @Override
    public UUID getGameProfileId(GameProfile gameProfile) {
        return gameProfile.id();
    }

    @Override
    public String getGameProfileName(GameProfile gameProfile) {
        return gameProfile.name();
    }

    @Override
    public PropertyMap getGameProfileProperty(GameProfile gameProfile) {
        return gameProfile.properties();
    }

}
