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
import com.loohp.interactivechatdiscordsrvaddon.objectholders.LegacyDimensionManager;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.EquipmentSlotGroup;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.ItemDamageInfo;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.MoonPhase;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.PaintingVariant;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.ProfileProperty;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.TintColorProvider;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import net.minecraft.EnumChatFormat;
import net.minecraft.MinecraftVersion;
import net.minecraft.advancements.AdvancementDisplay;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.critereon.CriterionConditionBlock;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.IRegistry;
import net.minecraft.core.IRegistryCustom;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.component.predicates.DataComponentPredicate;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.server.packs.EnumResourcePackType;
import net.minecraft.server.players.ProfileResolver;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.damagesource.CombatTracker;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectList;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.projectile.EntityFishingHook;
import net.minecraft.world.item.AdventureModePredicate;
import net.minecraft.world.item.Instrument;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemCrossbow;
import net.minecraft.world.item.JukeboxPlayable;
import net.minecraft.world.item.JukeboxSong;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.item.component.BundleContents;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.component.Fireworks;
import net.minecraft.world.item.component.InstrumentComponent;
import net.minecraft.world.item.component.ItemAttributeModifiers.b;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.item.component.TypedEntityData;
import net.minecraft.world.item.enchantment.EnchantmentManager;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.item.equipment.trim.TrimMaterial;
import net.minecraft.world.item.equipment.trim.TrimPattern;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.DecoratedPotPatterns;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
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
import org.bukkit.craftbukkit.v1_21_R6.CraftEquipmentSlot;
import org.bukkit.craftbukkit.v1_21_R6.CraftWorld;
import org.bukkit.craftbukkit.v1_21_R6.advancement.CraftAdvancement;
import org.bukkit.craftbukkit.v1_21_R6.attribute.CraftAttributeInstance;
import org.bukkit.craftbukkit.v1_21_R6.block.banner.CraftPatternType;
import org.bukkit.craftbukkit.v1_21_R6.enchantments.CraftEnchantment;
import org.bukkit.craftbukkit.v1_21_R6.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_21_R6.entity.CraftEntityType;
import org.bukkit.craftbukkit.v1_21_R6.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_21_R6.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_21_R6.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_21_R6.inventory.trim.CraftTrimMaterial;
import org.bukkit.craftbukkit.v1_21_R6.inventory.trim.CraftTrimPattern;
import org.bukkit.craftbukkit.v1_21_R6.map.CraftMapCursor;
import org.bukkit.craftbukkit.v1_21_R6.potion.CraftPotionEffectType;
import org.bukkit.craftbukkit.v1_21_R6.potion.CraftPotionUtil;
import org.bukkit.craftbukkit.v1_21_R6.util.CraftChatMessage;
import org.bukkit.craftbukkit.v1_21_R6.util.CraftMagicNumbers;
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
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@SuppressWarnings("unused")
public class V1_21_10 extends NMSAddonWrapper {

    private final Field adventureModePredicatePredicatesField;
    private final Method bundleContentsGetWeightMethod;
    private final Field attributeBaseSentimentField;

    public V1_21_10() {
        try {
            adventureModePredicatePredicatesField = ReflectionUtils.findDeclaredField(AdventureModePredicate.class, List.class, "predicates", "g");
            bundleContentsGetWeightMethod = ReflectionUtils.findDeclaredMethod(BundleContents.class, new Class[] {net.minecraft.world.item.ItemStack.class}, "getWeight", "b");
            attributeBaseSentimentField = ReflectionUtils.findDeclaredField(net.minecraft.world.entity.ai.attributes.AttributeBase.class, net.minecraft.world.entity.ai.attributes.AttributeBase.a.class, "sentiment", "f");
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
        MapDecorationType nmsType = CraftMapCursor.CraftType.bukkitToMinecraft(mapCursor.getType());
        MinecraftKey key = nmsType.b();
        return Key.key(key.b(), key.a());
    }

    @SuppressWarnings({"PatternValidation", "deprecation"})
    @Override
    public Key getPatternTypeKey(PatternType patternType) {
        NamespacedKey key = patternType.getKey();
        return Key.key(key.getNamespace(), key.getKey());
    }

    @SuppressWarnings("PatternValidation")
    @Override
    public LegacyDimensionManager getLegacyDimensionManager(World world) {
        WorldServer worldServer = ((CraftWorld) world).getHandle();
        net.minecraft.world.level.dimension.DimensionManager manager = worldServer.H_();
        return new LegacyDimensionManager() {
            @Override
            public boolean hasFixedTime() {
                return manager.a();
            }
            @Override
            public OptionalLong getFixedTime() {
                return manager.f();
            }
            @Override
            public float timeOfDay(long i) {
                return manager.a(i);
            }
            @Override
            public boolean hasSkyLight() {
                return manager.g();
            }
            @Override
            public boolean hasCeiling() {
                return manager.h();
            }
            @Override
            public boolean ultraWarm() {
                return manager.i();
            }
            @Override
            public boolean natural() {
                return manager.j();
            }
            @Override
            public double coordinateScale() {
                return manager.k();
            }
            @Override
            public boolean createDragonFight() {
                return worldServer.al() == net.minecraft.world.level.World.j && worldServer.ak().a(BuiltinDimensionTypes.c);
            }
            @Override
            public boolean piglinSafe() {
                return manager.b();
            }
            @Override
            public boolean bedWorks() {
                return manager.l();
            }
            @Override
            public boolean respawnAnchorWorks() {
                return manager.m();
            }
            @Override
            public boolean hasRaids() {
                return manager.c();
            }
            @Override
            public int minY() {
                return manager.n();
            }
            @Override
            public int height() {
                return manager.o();
            }
            @Override
            public int logicalHeight() {
                return manager.p();
            }
            @SuppressWarnings("PatternValidation")
            @Override
            public Key infiniburn() {
                MinecraftKey key = manager.q().b();
                return Key.key(key.b(), key.a());
            }
            @SuppressWarnings("PatternValidation")
            @Override
            public Key effectsLocation() {
                MinecraftKey key = manager.r();
                return Key.key(key.b(), key.a());
            }
            @Override
            public float ambientLight() {
                return manager.s();
            }
        };
    }

    @SuppressWarnings("PatternValidation")
    @Override
    public Key getNamespacedKey(World world) {
        WorldServer worldServer = ((CraftWorld) world).getHandle();
        MinecraftKey key = worldServer.al().a();
        return Key.key(key.b(), key.a());
    }

    @Override
    public BiomePrecipitation getPrecipitation(Location location) {
        WorldServer worldServer = ((CraftWorld) location.getWorld()).getHandle();
        BiomeBase biomeBase = worldServer.a(location.getBlockX(), location.getBlockY(), location.getBlockZ()).a();
        BiomeBase.Precipitation precipitation = biomeBase.a(new BlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ()), location.getBlockY());
        return BiomePrecipitation.fromName(precipitation.name());
    }

    @Override
    public OptionalInt getTropicalFishBucketVariantTag(ItemStack bucket) {
        net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(bucket);
        TypedEntityData<EntityTypes<?>> typedEntityData = nmsItemStack.a(DataComponents.Y);
        if (!EntityTypes.bF.equals(typedEntityData.a())) {
            return OptionalInt.empty();
        }
        NBTTagCompound nbt = typedEntityData.c();
        if (nbt == null) {
            return OptionalInt.empty();
        }
        return nbt.e("BucketVariantTag").map(i -> OptionalInt.of(i)).orElse(OptionalInt.empty());
    }

    @Override
    public PotionType getBasePotionType(ItemStack potion) {
        return ((PotionMeta) potion.getItemMeta()).getBasePotionType();
    }

    @Override
    public List<PotionEffect> getAllPotionEffects(ItemStack potion) {
        net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(potion);
        PotionContents potionContents = nmsItemStack.a(DataComponents.R);
        List<PotionEffect> effects = new ArrayList<>();
        for (MobEffect mobEffect : potionContents.a()) {
            effects.add(CraftPotionUtil.toBukkit(mobEffect));
        }
        return effects;
    }

    @Override
    public TextColor getPotionEffectChatColor(PotionEffectType type) {
        MobEffectList mobEffectList = CraftPotionEffectType.bukkitToMinecraft(type);
        EnumChatFormat chatFormat = mobEffectList.h().a();
        return TextColor.color(chatFormat.f());
    }

    @Override
    public Map<AttributeBase, AttributeModifier> getPotionAttributeModifiers(PotionEffect effect) {
        attributeBaseSentimentField.setAccessible(true);
        Map<AttributeBase, AttributeModifier> attributes = new HashMap<>();
        MobEffect mobEffect = CraftPotionUtil.fromBukkit(effect);
        MobEffectList mobEffectList = mobEffect.c().a();
        mobEffectList.a(effect.getAmplifier(), (holder, nmsAttributeModifier) -> {
            try {
                net.minecraft.world.entity.ai.attributes.AttributeBase nmsAttributeBase = holder.a();
                net.minecraft.world.entity.ai.attributes.AttributeBase.a nmsSentiment = (net.minecraft.world.entity.ai.attributes.AttributeBase.a) attributeBaseSentimentField.get(nmsAttributeBase);
                AttributeBase.AttributeSentiment sentiment = AttributeBase.AttributeSentiment.fromNMS(nmsSentiment);
                AttributeBase attributeBase = new AttributeBase(nmsAttributeBase.c(), nmsAttributeBase.b(), sentiment);
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
            AdventureModePredicate adventureModePredicate = nmsItemStack.a(DataComponents.m);
            if (adventureModePredicate == null) {
                return Collections.emptyList();
            }
            adventureModePredicatePredicatesField.setAccessible(true);
            List<CriterionConditionBlock> predicate = (List<CriterionConditionBlock>) adventureModePredicatePredicatesField.get(adventureModePredicate);
            List<ICMaterial> materials = new ArrayList<>();
            for (CriterionConditionBlock block : predicate) {
                Optional<HolderSet<Block>> optSet = block.b();
                if (optSet.isPresent()) {
                    for (Holder<Block> set : optSet.get()) {
                        materials.add(ICMaterial.of(CraftMagicNumbers.getMaterial(set.a())));
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
            AdventureModePredicate adventureModePredicate = nmsItemStack.a(DataComponents.n);
            if (adventureModePredicate == null) {
                return Collections.emptyList();
            }
            adventureModePredicatePredicatesField.setAccessible(true);
            List<CriterionConditionBlock> predicate = (List<CriterionConditionBlock>) adventureModePredicatePredicatesField.get(adventureModePredicate);
            List<ICMaterial> materials = new ArrayList<>();
            for (CriterionConditionBlock block : predicate) {
                Optional<HolderSet<Block>> optSet = block.b();
                if (optSet.isPresent()) {
                    for (Holder<Block> set : optSet.get()) {
                        materials.add(ICMaterial.of(CraftMagicNumbers.getMaterial(set.a())));
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
        DyedItemColor dyedItemColor = nmsItemStack.a(DataComponents.K);
        return dyedItemColor == null ? OptionalInt.empty() : OptionalInt.of(dyedItemColor.a());
    }

    @Override
    public boolean hasBlockEntityTag(ItemStack itemStack) {
        net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        return nmsItemStack.a(DataComponents.aa) != null;
    }

    @Override
    public Component getInstrumentDescription(ItemStack itemStack) {
        net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        InstrumentComponent component = nmsItemStack.a(DataComponents.ab);
        if (component == null) {
            return null;
        }
        Optional<Holder<Instrument>> optHolder = component.a(((CraftWorld) Bukkit.getWorlds().get(0)).getHandle().L_());
        if (optHolder == null && !optHolder.isPresent()) {
            return null;
        }
        IChatBaseComponent description = optHolder.get().a().d();
        return InteractiveChatComponentSerializer.gson().deserialize(CraftChatMessage.toJSON(description));
    }

    @SuppressWarnings("PatternValidation")
    @Override
    public PaintingVariant getPaintingVariant(ItemStack itemStack) {
        net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        TypedEntityData<EntityTypes<?>> typedEntityData = nmsItemStack.a(DataComponents.Y);
        if (typedEntityData == null || !EntityTypes.aP.equals(typedEntityData.a())) {
            return null;
        }
        NBTTagCompound nbt = typedEntityData.c();
        if (nbt == null || !nbt.b("variant")) {
            return null;
        }
        Optional<String> optVariant = nbt.i("variant");
        if (optVariant != null && !optVariant.isPresent()) {
            return null;
        }
        MinecraftKey key = MinecraftKey.a(optVariant.get());
        IRegistryCustom customRegistry = ((CraftWorld) Bukkit.getWorlds().get(0)).getHandle().L_();
        IRegistry<net.minecraft.world.entity.decoration.PaintingVariant> paintingRegistry = customRegistry.f(Registries.bh);
        net.minecraft.world.entity.decoration.PaintingVariant paintingVariant = paintingRegistry.a(key);
        if (paintingVariant == null) {
            return null;
        }
        Optional<Component> title = paintingVariant.e().map(c -> InteractiveChatComponentSerializer.gson().deserialize(CraftChatMessage.toJSON(c)));
        Optional<Component> author = paintingVariant.f().map(c -> InteractiveChatComponentSerializer.gson().deserialize(CraftChatMessage.toJSON(c)));
        return new PaintingVariant(Key.key(key.b(), key.a()), paintingVariant.b(), paintingVariant.c(), title, author);
    }

    @Override
    public String getEntityNBT(Entity entity) {
        net.minecraft.world.entity.Entity nmsEntity = ((CraftEntity) entity).getHandle();
        TagValueOutput output = TagValueOutput.a(ProblemReporter.a);
        nmsEntity.c(output);
        return output.b().toString();
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
        TextColor textColor = InteractiveChatComponentSerializer.gson().deserialize(CraftChatMessage.toJSON(nmsTrimMaterial.b())).color();
        return textColor == null ? NamedTextColor.GRAY : textColor;
    }

    @Override
    public AdvancementData getAdvancementDataFromBukkitAdvancement(Object bukkitAdvancement) {
        AdvancementHolder holder = ((CraftAdvancement) bukkitAdvancement).getHandle();
        Optional<AdvancementDisplay> optAdvancementDisplay = holder.b().c();
        if (!optAdvancementDisplay.isPresent()) {
            return null;
        }
        AdvancementDisplay display = optAdvancementDisplay.get();
        Component title = InteractiveChatComponentSerializer.gson().deserialize(CraftChatMessage.toJSON(display.a()));
        Component description = InteractiveChatComponentSerializer.gson().deserialize(CraftChatMessage.toJSON(display.b()));
        ItemStack item = CraftItemStack.asBukkitCopy(display.c());
        AdvancementType advancementType = AdvancementType.fromName(display.e().c());
        boolean isMinecraft = holder.a().b().equals(Key.MINECRAFT_NAMESPACE);
        return new AdvancementData(title, description, item, advancementType, isMinecraft);
    }

    @Override
    public Advancement getBukkitAdvancementFromEvent(Event event) {
        return ((PlayerAdvancementDoneEvent) event).getAdvancement();
    }

    @Override
    public boolean matchArmorSlot(ItemStack armorItem, EquipmentSlot slot) {
        net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(armorItem);
        Equippable equippable = nmsItemStack.a(DataComponents.D);
        if (equippable == null) {
            return false;
        }
        if (equippable.f().map(a -> a.a().anyMatch(s -> s.a().equals(EntityTypes.bS))).orElse(true)) {
            return CraftEquipmentSlot.getSlot(equippable.b()).equals(slot);
        }
        return false;
    }

    @SuppressWarnings("PatternValidation")
    @Override
    public Key getArmorMaterialKey(ItemStack armorItem) {
        net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(armorItem);
        Equippable equippable = nmsItemStack.a(DataComponents.D);
        if (equippable == null) {
            return null;
        }
        return equippable.d().map(key -> Key.key(key.a().b(), key.a().a())).orElse(null);
    }

    @Override
    public Map<EquipmentSlotGroup, Multimap<AttributeBase, AttributeModifier>> getItemAttributeModifiers(ItemStack itemStack) {
        attributeBaseSentimentField.setAccessible(true);
        net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        Map<EquipmentSlotGroup, Multimap<AttributeBase, AttributeModifier>> result = new EnumMap<>(EquipmentSlotGroup.class);
        for (net.minecraft.world.entity.EquipmentSlotGroup slotGroup : net.minecraft.world.entity.EquipmentSlotGroup.values()) {
            EquipmentSlotGroup equipmentSlotGroup = EquipmentSlotGroup.fromName(slotGroup.c());
            nmsItemStack.a(slotGroup, (holder, nmsAttributeModifier, itemAttributeModifiersB) -> {
                try {
                    Multimap<AttributeBase, AttributeModifier> attributes = result.computeIfAbsent(equipmentSlotGroup, k -> LinkedHashMultimap.create());
                    net.minecraft.world.entity.ai.attributes.AttributeBase nmsAttributeBase = holder.a();
                    net.minecraft.world.entity.ai.attributes.AttributeBase.a nmsSentiment = (net.minecraft.world.entity.ai.attributes.AttributeBase.a) attributeBaseSentimentField.get(nmsAttributeBase);
                    AttributeBase.AttributeSentiment sentiment = AttributeBase.AttributeSentiment.fromNMS(nmsSentiment);
                    boolean hidden = itemAttributeModifiersB == b.b();
                    AttributeBase attributeBase = new AttributeBase(nmsAttributeBase.c(), nmsAttributeBase.b(), sentiment, hidden);
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
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
        CombatTracker combatTracker = entityPlayer.fh();
        return InteractiveChatComponentSerializer.gson().deserialize(CraftChatMessage.toJSON(combatTracker.a()));
    }

    @SuppressWarnings("PatternValidation")
    @Override
    public Key getDecoratedPotSherdPatternName(ItemStack itemStack) {
        net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        Item item = nmsItemStack.h();
        MinecraftKey key = DecoratedPotPatterns.a(item).a();
        return Key.key(key.b(), key.a());
    }

    @Override
    public boolean isJukeboxPlayable(ItemStack itemStack) {
        net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        JukeboxPlayable jukeboxPlayable = nmsItemStack.a(DataComponents.ae);
        return jukeboxPlayable != null;
    }

    @Override
    public boolean shouldSongShowInToolTip(ItemStack disc) {
        net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(disc);
        JukeboxPlayable jukeboxPlayable = nmsItemStack.a(DataComponents.ae);
        if (jukeboxPlayable == null) {
            return false;
        }
        IRegistryCustom registryAccess = ((CraftWorld) Bukkit.getWorlds().get(0)).getHandle().L_();
        Optional<Holder<JukeboxSong>> optJukeboxSong = jukeboxPlayable.a().a(registryAccess);
        return optJukeboxSong != null && optJukeboxSong.isPresent();
    }

    @Override
    public Component getJukeboxSongDescription(ItemStack disc) {
        net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(disc);
        JukeboxPlayable jukeboxPlayable = nmsItemStack.a(DataComponents.ae);
        if (jukeboxPlayable == null) {
            return null;
        }
        IRegistryCustom registryAccess = ((CraftWorld) Bukkit.getWorlds().get(0)).getHandle().L_();
        Optional<Holder<JukeboxSong>> optJukeboxSong = jukeboxPlayable.a().a(registryAccess);
        return optJukeboxSong.map(h -> InteractiveChatComponentSerializer.gson().deserialize(CraftChatMessage.toJSON(h.a().c()))).orElse(null);
    }

    @Override
    public Component getEnchantmentDescription(Enchantment enchantment) {
        IChatBaseComponent description = CraftEnchantment.bukkitToMinecraft(enchantment).f();
        return InteractiveChatComponentSerializer.gson().deserialize(CraftChatMessage.toJSON(description));
    }

    @SuppressWarnings("deprecation")
    @Override
    public String getEffectTranslationKey(PotionEffectType type) {
        NamespacedKey namespacedKey = type.getKey();
        return "effect." + namespacedKey.getNamespace() + "." + namespacedKey.getKey();
    }

    @Override
    public String getEntityTypeTranslationKey(EntityType type) {
        return CraftEntityType.bukkitToMinecraft(type).g();
    }

    @Override
    public FishHook getFishHook(Player player) {
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
        EntityFishingHook entityFishingHook = entityPlayer.cw;
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
        return MinecraftVersion.a().a(EnumResourcePackType.a).b();
    }

    @Override
    public int getServerResourcePackMinorVersion() {
        return MinecraftVersion.a().a(EnumResourcePackType.a).c();
    }

    @Override
    public float getEnchantmentDamageBonus(ItemStack itemStack, LivingEntity livingEntity) {
        net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        EntityLiving entityLiving = ((CraftLivingEntity) livingEntity).getHandle();
        return EnchantmentManager.a(nmsItemStack, entityLiving);
    }

    @Override
    public int getItemComponentsSize(ItemStack itemStack) {
        net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        return nmsItemStack.a().d();
    }

    @Override
    public GameProfile getPlayerHeadProfile(ItemStack playerHead) {
        try {
            net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(playerHead);
            ResolvableProfile resolvableProfile = nmsItemStack.a(DataComponents.ak);
            if (resolvableProfile == null) {
                return null;
            }
            @SuppressWarnings("resource")
            ProfileResolver resolver = ((CraftWorld) Bukkit.getWorlds().get(0)).getHandle().q().av().g();
            return resolvableProfile.a(resolver).get();
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
                resolvableProfile = ResolvableProfile.a(gameProfile);
            } else if (contents.name() != null) {
                resolvableProfile = ResolvableProfile.a(contents.name());
            } else if (contents.id() != null) {
                resolvableProfile = ResolvableProfile.a(contents.id());
            } else {
                return null;
            }
            @SuppressWarnings("resource")
            ProfileResolver resolver = ((CraftWorld) Bukkit.getWorlds().get(0)).getHandle().q().av().g();
            return resolvableProfile.a(resolver).get();
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
    public PropertyMap getGameProfilePropertyMap(GameProfile gameProfile) {
        return gameProfile.properties();
    }

    @Override
    public ProfileProperty toProfileProperty(Property property) {
        return new ProfileProperty(property.name(), property.value(), property.signature());
    }

    @Override
    public Fraction getWeightForBundle(ItemStack itemStack) {
        try {
            net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
            bundleContentsGetWeightMethod.setAccessible(true);
            org.apache.commons.lang3.math.Fraction weight = (org.apache.commons.lang3.math.Fraction) bundleContentsGetWeightMethod.invoke(null, nmsItemStack);
            return Fraction.getFraction(weight.getNumerator(), weight.getDenominator());
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public CustomModelData getCustomModelData(ItemStack itemStack) {
        net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        net.minecraft.world.item.component.CustomModelData customModelData = nmsItemStack.a(DataComponents.p);
        if (customModelData == null) {
            return null;
        }
        return new CustomModelData(customModelData.a(), customModelData.b(), customModelData.c(), customModelData.d());
    }

    @Override
    public boolean hasDataComponent(ItemStack itemStack, Key componentName, boolean ignoreDefault) {
        net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        Optional<DataComponentType<?>> optType = BuiltInRegistries.an.b(MinecraftKey.a(componentName.namespace(), componentName.value()));
        if (!optType.isPresent()) {
            return false;
        }
        DataComponentType<?> dataComponentType = optType.get();
        return ignoreDefault ? nmsItemStack.d(dataComponentType) : nmsItemStack.c(dataComponentType);
    }

    @Override
    public String getBlockStateProperty(ItemStack itemStack, String property) {
        net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        BlockItemStateProperties blockStateComponent = nmsItemStack.a(DataComponents.aq);
        if (blockStateComponent == null) {
            return null;
        }
        return blockStateComponent.b().get(property);
    }

    @Override
    public ItemDamageInfo getItemDamageInfo(ItemStack itemStack) {
        net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        int damage = nmsItemStack.o();
        int maxDamage = nmsItemStack.p();
        return new ItemDamageInfo(damage, maxDamage);
    }

    @Override
    public float getItemCooldownProgress(Player player, ItemStack itemStack) {
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
        net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        return entityPlayer.gW().a(nmsItemStack, 0.0F);
    }

    @Override
    public float getSkyAngle(Location location) {
        return ((CraftWorld) location.getWorld()).getHandle().f(1.0F);
    }

    @Override
    public MoonPhase getMoonPhase(Location location) {
        return MoonPhase.fromIndex(((CraftWorld) location.getWorld()).getHandle().at());
    }

    @Override
    public int getCrossbowPullTime(ItemStack itemStack, LivingEntity livingEntity) {
        net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        EntityLiving entityLiving = ((CraftLivingEntity) livingEntity).getHandle();
        return ItemCrossbow.b(nmsItemStack, entityLiving);
    }

    @Override
    public int getItemUseTimeLeft(LivingEntity livingEntity) {
        EntityLiving entityLiving = ((CraftLivingEntity) livingEntity).getHandle();
        return entityLiving.fR();
    }

    @Override
    public int getTicksUsedSoFar(ItemStack itemStack, LivingEntity livingEntity) {
        net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        EntityLiving entityLiving = ((CraftLivingEntity) livingEntity).getHandle();
        return nmsItemStack.a(entityLiving) - getItemUseTimeLeft(livingEntity);
    }

    @SuppressWarnings("PatternValidation")
    @Override
    public Key getItemModelResourceLocation(ItemStack itemStack) {
        net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        MinecraftKey itemModel = nmsItemStack.a(DataComponents.i);
        if (itemModel == null) {
            return NMS.getInstance().getNMSItemStackNamespacedKey(itemStack);
        }
        return Key.key(itemModel.b(), itemModel.a());
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
        String translationKey = CraftPatternType.bukkitToMinecraft(type).b();
        return translationKey + "." + color.name().toLowerCase();
    }

    @Override
    public Component getTrimMaterialDescription(Object trimMaterial) {
        TrimMaterial material = CraftTrimMaterial.bukkitToMinecraft((org.bukkit.inventory.meta.trim.TrimMaterial) trimMaterial);
        IChatBaseComponent description = material.b();
        return InteractiveChatComponentSerializer.gson().deserialize(CraftChatMessage.toJSON(description));
    }

    @Override
    public Component getTrimPatternDescription(Object trimPattern, Object trimMaterial) {
        TrimPattern pattern = CraftTrimPattern.bukkitToMinecraft((org.bukkit.inventory.meta.trim.TrimPattern) trimPattern);
        IChatBaseComponent description;
        if (trimMaterial == null) {
            description = pattern.b();
        } else {
            TrimMaterial material = CraftTrimMaterial.bukkitToMinecraft((org.bukkit.inventory.meta.trim.TrimMaterial) trimMaterial);
            description = pattern.a(Holder.a(material));
        }
        return InteractiveChatComponentSerializer.gson().deserialize(CraftChatMessage.toJSON(description));
    }

    @Override
    public OptionalInt getFireworkFlightDuration(ItemStack itemStack) {
        net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        Fireworks fireworks = nmsItemStack.a(DataComponents.aj);
        if (fireworks == null) {
            return OptionalInt.empty();
        }
        return OptionalInt.of(fireworks.a());
    }

    @Override
    public boolean shouldShowOperatorBlockWarnings(ItemStack itemStack, Player player) {
        EntityPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
        net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        return nmsItemStack.h().a(nmsItemStack, nmsPlayer);
    }

    @Override
    public Object getItemStackDataComponentValue(ItemStack itemStack, Key component) {
        net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        DataComponentType<?> componentType = BuiltInRegistries.an.a(MinecraftKey.a(component.namespace(), component.value()));
        if (componentType == null) {
            return false;
        }
        return nmsItemStack.a(componentType);
    }

    @Override
    public Object serializeDataComponent(Key component, String data) {
        DataComponentType<?> componentType = BuiltInRegistries.an.a(MinecraftKey.a(component.namespace(), component.value()));
        if (componentType == null) {
            return null;
        }
        IRegistryCustom registryAccess = ((CraftWorld)Bukkit.getWorlds().get(0)).getHandle().L_();
        JsonElement jsonElement = new Gson().fromJson(data, JsonElement.class);
        Object nativeJsonElement = NativeJsonConverter.toNative(jsonElement);
        return componentType.c().decode(registryAccess.a((DynamicOps<Object>) (DynamicOps<?>) JsonOps.INSTANCE), nativeJsonElement).result().map(r -> r.getFirst()).orElse(null);
    }

    @Override
    public boolean evaluateComponentPredicateOnItemStack(ItemStack itemStack, String predicateData, String data) {
        IRegistryCustom registryAccess = ((CraftWorld)Bukkit.getWorlds().get(0)).getHandle().L_();
        JsonElement jsonElement = new Gson().fromJson(predicateData, JsonElement.class);
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("predicate", jsonElement);
        Object nativeJsonObject = NativeJsonConverter.toNative(jsonObject);
        DataComponentPredicate.a<?> predicate = DataComponentPredicate.a("predicate").codec().decode(registryAccess.a((DynamicOps<Object>) (DynamicOps<?>) JsonOps.INSTANCE), nativeJsonObject).result().map(r -> r.getFirst()).orElse(null);
        if (predicate == null) {
            return false;
        }
        net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        return predicate.b().a(nmsItemStack);
    }

}
