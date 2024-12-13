/*
 * This file is part of InteractiveChatDiscordSrvAddon.
 *
 * Copyright (C) 2024. LoohpJames <jamesloohp@gmail.com>
 * Copyright (C) 2024. Contributors
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
import com.loohp.interactivechat.libs.net.kyori.adventure.key.Key;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.Component;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.format.NamedTextColor;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.format.TextColor;
import com.loohp.interactivechat.libs.org.apache.commons.lang3.math.Fraction;
import com.loohp.interactivechat.nms.NMS;
import com.loohp.interactivechat.objectholders.ICMaterial;
import com.loohp.interactivechat.utils.InteractiveChatComponentSerializer;
import com.loohp.interactivechat.utils.ReflectionUtils;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.AdvancementData;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.AdvancementType;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.BiomePrecipitation;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.CustomModelData;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.DimensionManager;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.EquipmentSlotGroup;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.ItemDamageInfo;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.PaintingVariant;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.ProfileProperty;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.TintColorProvider;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.EnumChatFormat;
import net.minecraft.MinecraftVersion;
import net.minecraft.advancements.AdvancementDisplay;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.critereon.CriterionConditionBlock;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.IRegistryCustom;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.server.packs.EnumResourcePackType;
import net.minecraft.world.damagesource.CombatTracker;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectList;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.animal.EntityTropicalFish;
import net.minecraft.world.entity.animal.EntityTropicalFish.d;
import net.minecraft.world.entity.decoration.EntityPainting;
import net.minecraft.world.entity.projectile.EntityFishingHook;
import net.minecraft.world.item.AdventureModePredicate;
import net.minecraft.world.item.Instrument;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemMonsterEgg;
import net.minecraft.world.item.JukeboxPlayable;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.component.BundleContents;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.item.enchantment.EnchantmentManager;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.item.equipment.trim.TrimMaterial;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.DecoratedPotPatterns;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.advancement.Advancement;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.banner.PatternType;
import org.bukkit.craftbukkit.v1_21_R2.CraftEquipmentSlot;
import org.bukkit.craftbukkit.v1_21_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_21_R2.advancement.CraftAdvancement;
import org.bukkit.craftbukkit.v1_21_R2.attribute.CraftAttributeInstance;
import org.bukkit.craftbukkit.v1_21_R2.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_21_R2.entity.CraftEntityType;
import org.bukkit.craftbukkit.v1_21_R2.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_21_R2.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_21_R2.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_21_R2.inventory.trim.CraftTrimMaterial;
import org.bukkit.craftbukkit.v1_21_R2.potion.CraftPotionEffectType;
import org.bukkit.craftbukkit.v1_21_R2.potion.CraftPotionUtil;
import org.bukkit.craftbukkit.v1_21_R2.util.CraftChatMessage;
import org.bukkit.craftbukkit.v1_21_R2.util.CraftMagicNumbers;
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
import org.bukkit.inventory.meta.Damageable;
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
import java.util.concurrent.ExecutionException;

@SuppressWarnings("unused")
public class V1_21_2 extends NMSAddonWrapper {

    private final Field adventureModePredicatePredicatesField;
    private final Method bundleContentsGetWeightMethod;

    public V1_21_2() {
        try {
            adventureModePredicatePredicatesField = ReflectionUtils.findDeclaredField(AdventureModePredicate.class, List.class, "predicates", "h");
            bundleContentsGetWeightMethod = ReflectionUtils.findDeclaredMethod(BundleContents.class, new Class[] {net.minecraft.world.item.ItemStack.class}, "getWeight", "b");
        } catch (NoSuchFieldException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Map<ICMaterial, TintColorProvider.SpawnEggTintData> getSpawnEggColorMap() {
        Map<ICMaterial, TintColorProvider.SpawnEggTintData> mapping = new LinkedHashMap<>();
        for (Item item : BuiltInRegistries.g) {
            if (item instanceof ItemMonsterEgg) {
                ItemMonsterEgg egg = (ItemMonsterEgg) item;
                ICMaterial icMaterial = ICMaterial.of(CraftMagicNumbers.getMaterial(item));
                mapping.put(icMaterial, new TintColorProvider.SpawnEggTintData(egg.a(0), egg.a(1)));
            }
        }
        return mapping;
    }

    @SuppressWarnings("PatternValidation")
    @Override
    public Key getMapCursorTypeKey(MapCursor mapCursor) {
        NamespacedKey key = mapCursor.getType().getKey();
        return Key.key(key.getNamespace(), key.getKey());
    }

    @SuppressWarnings("PatternValidation")
    @Override
    public Key getPatternTypeKey(PatternType patternType) {
        NamespacedKey key = patternType.getKey();
        return Key.key(key.getNamespace(), key.getKey());
    }

    @SuppressWarnings("PatternValidation")
    @Override
    public DimensionManager getDimensionManager(World world) {
        WorldServer worldServer = ((CraftWorld) world).getHandle();
        net.minecraft.world.level.dimension.DimensionManager manager = worldServer.G_();
        return new DimensionManager() {
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
                return worldServer.ah() == net.minecraft.world.level.World.j && worldServer.ag().a(BuiltinDimensionTypes.c);
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
        MinecraftKey key = worldServer.ah().a();
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
        CustomData customData = nmsItemStack.a(DataComponents.X);
        if (customData.b()) {
            return OptionalInt.empty();
        }
        Optional<EntityTropicalFish.d> optional = customData.a(d.a.fieldOf("BucketVariantTag")).result();
        return optional.map(f -> OptionalInt.of(EntityTropicalFish.b.indexOf(f))).orElse(OptionalInt.empty());
    }

    @Override
    public PotionType getBasePotionType(ItemStack potion) {
        return ((PotionMeta) potion.getItemMeta()).getBasePotionType();
    }

    @Override
    public List<PotionEffect> getAllPotionEffects(ItemStack potion) {
        net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(potion);
        PotionContents potionContents = nmsItemStack.a(DataComponents.Q);
        List<PotionEffect> effects = new ArrayList<>();
        for (MobEffect mobEffect : potionContents.a()) {
            effects.add(CraftPotionUtil.toBukkit(mobEffect));
        }
        return effects;
    }

    @Override
    public ChatColor getPotionEffectChatColor(PotionEffectType type) {
        MobEffectList mobEffectList = ((CraftPotionEffectType) type).getHandle();
        EnumChatFormat chatFormat = mobEffectList.f().a();
        return ChatColor.getByChar(chatFormat.toString().charAt(1));
    }

    @Override
    public Map<String, AttributeModifier> getPotionAttributeModifiers(PotionEffect effect) {
        Map<String, AttributeModifier> attributes = new HashMap<>();
        MobEffect mobEffect = CraftPotionUtil.fromBukkit(effect);
        MobEffectList mobEffectList = mobEffect.c().a();
        mobEffectList.a(effect.getAmplifier(), (holder, nmsAttributeModifier) -> {
            String name = holder.a().c();
            AttributeModifier attributeModifier = CraftAttributeInstance.convert(nmsAttributeModifier);
            attributes.put(name, attributeModifier);
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
        DyedItemColor dyedItemColor = nmsItemStack.a(DataComponents.J);
        return dyedItemColor == null ? OptionalInt.empty() : OptionalInt.of(dyedItemColor.a());
    }

    @Override
    public boolean hasBlockEntityTag(ItemStack itemStack) {
        net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        return nmsItemStack.a(DataComponents.Y) != null;
    }

    @SuppressWarnings("PatternValidation")
    @Override
    public Key getGoatHornInstrument(ItemStack itemStack) {
        net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        Holder<Instrument> holder = nmsItemStack.a(DataComponents.Z);
        if (holder == null) {
            return null;
        }
        return holder.e().map(r -> Key.key(r.b().b(), r.b().a())).orElse(null);
    }

    @SuppressWarnings("PatternValidation")
    @Override
    public PaintingVariant getPaintingVariant(ItemStack itemStack) {
        net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        CustomData customData = nmsItemStack.a(DataComponents.W);
        if (customData == null) {
            return null;
        }
        Optional<Holder<net.minecraft.world.entity.decoration.PaintingVariant>> optional = customData.a(EntityPainting.d).result();
        if (!optional.isPresent()) {
            return null;
        }
        net.minecraft.world.entity.decoration.PaintingVariant paintingVariant = optional.get().a();
        MinecraftKey key = paintingVariant.d();
        return new PaintingVariant(Key.key(key.b(), key.a()), paintingVariant.a() / 16, paintingVariant.b() / 16);
    }

    @Override
    public String getEntityNBT(Entity entity) {
        net.minecraft.world.entity.Entity nmsEntity = ((CraftEntity) entity).getHandle();
        NBTTagCompound nbt = new NBTTagCompound();
        nmsEntity.e(nbt);
        return nbt.toString();
    }

    @Override
    public float getLegacyTrimMaterialIndex(Object trimMaterial) {
        if (trimMaterial == null) {
            return 0.0F;
        }
        TrimMaterial nmsTrimMaterial = ((CraftTrimMaterial) trimMaterial).getHandle();
        return nmsTrimMaterial.c();
    }

    @Override
    public TextColor getTrimMaterialColor(Object trimMaterial) {
        if (trimMaterial == null) {
            return NamedTextColor.GRAY;
        }
        TrimMaterial nmsTrimMaterial = ((CraftTrimMaterial) trimMaterial).getHandle();
        TextColor textColor = InteractiveChatComponentSerializer.gson().deserialize(CraftChatMessage.toJSON(nmsTrimMaterial.e())).color();
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
        if (equippable.e().map(a -> a.a().anyMatch(s -> s.a().equals(EntityTypes.bS))).orElse(true)) {
            return CraftEquipmentSlot.getSlot(equippable.a()).equals(slot);
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
        return equippable.c().map(key -> Key.key(key.b(), key.a())).orElse(null);
    }

    @Override
    public Map<EquipmentSlotGroup, Multimap<String, AttributeModifier>> getItemAttributeModifiers(ItemStack itemStack) {
        net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        Map<EquipmentSlotGroup, Multimap<String, AttributeModifier>> result = new EnumMap<>(EquipmentSlotGroup.class);
        for (net.minecraft.world.entity.EquipmentSlotGroup slotGroup : net.minecraft.world.entity.EquipmentSlotGroup.values()) {
            EquipmentSlotGroup equipmentSlotGroup = EquipmentSlotGroup.fromName(slotGroup.c());
            nmsItemStack.a(slotGroup, (holder, nmsAttributeModifier) -> {
                Multimap<String, AttributeModifier> attributes = result.computeIfAbsent(equipmentSlotGroup, k -> LinkedHashMultimap.create());
                String name = holder.a().c();
                AttributeModifier attributeModifier = CraftAttributeInstance.convert(nmsAttributeModifier);
                attributes.put(name, attributeModifier);
            });
        }
        return result;
    }

    @Override
    public Component getDeathMessage(Player player) {
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
        CombatTracker combatTracker = entityPlayer.eQ();
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
        JukeboxPlayable jukeboxPlayable = nmsItemStack.a(DataComponents.ab);
        return jukeboxPlayable != null;
    }

    @Override
    public boolean shouldSongShowInToolTip(ItemStack disc) {
        net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(disc);
        JukeboxPlayable jukeboxPlayable = nmsItemStack.a(DataComponents.ab);
        if (jukeboxPlayable == null) {
            return false;
        }
        return jukeboxPlayable.b();
    }

    @Override
    public Component getMusicDiscNameTranslationKey(ItemStack disc) {
        net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(disc);
        JukeboxPlayable jukeboxPlayable = nmsItemStack.a(DataComponents.ab);
        if (jukeboxPlayable == null) {
            return null;
        }
        IRegistryCustom registryAccess = ((CraftWorld) Bukkit.getWorlds().get(0)).getHandle().K_();
        return jukeboxPlayable.a().a(registryAccess).map(h -> InteractiveChatComponentSerializer.gson().deserialize(CraftChatMessage.toJSON(h.a().c()))).orElse(null);
    }

    @Override
    public String getEnchantmentTranslationKey(Enchantment enchantment) {
        NamespacedKey namespacedKey = enchantment.getKey();
        return "enchantment." + namespacedKey.getNamespace() + "." + namespacedKey.getKey();
    }

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
        EntityFishingHook entityFishingHook = entityPlayer.cv;
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
    public int getServerResourcePackVersion() {
        return MinecraftVersion.a().a(EnumResourcePackType.a);
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
            ResolvableProfile resolvableProfile = nmsItemStack.a(DataComponents.ag);
            if (resolvableProfile == null) {
                return null;
            }
            return resolvableProfile.a().get().f();
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
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null || !itemMeta.hasCustomModelData()) {
            return null;
        }
        return new CustomModelData(itemMeta.getCustomModelData());
    }

    @Override
    public boolean hasDataComponent(ItemStack itemStack, String componentName, boolean ignoreDefault) {
        return false;
    }

    @Override
    public String getBlockStateProperty(ItemStack itemStack, String property) {
        return null;
    }

    @Override
    public ItemDamageInfo getItemDamageInfo(ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta instanceof Damageable) {
            return new ItemDamageInfo(((Damageable) itemMeta).getDamage(), itemStack.getType().getMaxDurability());
        }
        return new ItemDamageInfo(0, itemStack.getType().getMaxDurability());
    }

    @Override
    public float getItemCooldownProgress(Player player, ItemStack itemStack) {
        return 0.0F;
    }

    @Override
    public float getSkyAngle(World world) {
        return 0F;
    }

    @Override
    public int getMoonPhase(World world) {
        return 0;
    }

    @Override
    public int getCrossbowPullTime(ItemStack itemStack, LivingEntity livingEntity) {
        return 0;
    }

    @Override
    public int getItemUseTimeLeft(LivingEntity livingEntity) {
        return 0;
    }

    @Override
    public int getTicksUsedSoFar(ItemStack itemStack, LivingEntity livingEntity) {
        return 0;
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

}