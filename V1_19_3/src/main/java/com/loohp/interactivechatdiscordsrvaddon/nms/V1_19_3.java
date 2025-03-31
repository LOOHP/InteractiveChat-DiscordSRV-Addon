/*
 * This file is part of InteractiveChatDiscordSrvAddon-V1_19_3.
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
import com.loohp.interactivechat.libs.net.kyori.adventure.key.Key;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.Component;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.format.TextColor;
import com.loohp.interactivechat.libs.org.apache.commons.lang3.math.Fraction;
import com.loohp.interactivechat.nms.NMS;
import com.loohp.interactivechat.objectholders.ICMaterial;
import com.loohp.interactivechat.utils.InteractiveChatComponentSerializer;
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
import com.mojang.bridge.game.PackType;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.EnumChatFormat;
import net.minecraft.MinecraftVersion;
import net.minecraft.advancements.AdvancementDisplay;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.damagesource.CombatTracker;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectList;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.entity.EnumMonsterType;
import net.minecraft.world.entity.ai.attributes.AttributeBase;
import net.minecraft.world.entity.projectile.EntityFishingHook;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.BundleItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemArmor;
import net.minecraft.world.item.ItemFireworks;
import net.minecraft.world.item.ItemMonsterEgg;
import net.minecraft.world.item.ItemRecord;
import net.minecraft.world.item.alchemy.PotionUtil;
import net.minecraft.world.item.enchantment.EnchantmentManager;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.EnumBannerPatternType;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.advancement.Advancement;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.banner.PatternType;
import org.bukkit.craftbukkit.v1_19_R2.CraftEquipmentSlot;
import org.bukkit.craftbukkit.v1_19_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R2.advancement.CraftAdvancement;
import org.bukkit.craftbukkit.v1_19_R2.attribute.CraftAttributeInstance;
import org.bukkit.craftbukkit.v1_19_R2.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_19_R2.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_19_R2.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_19_R2.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_19_R2.potion.CraftPotionEffectType;
import org.bukkit.craftbukkit.v1_19_R2.potion.CraftPotionUtil;
import org.bukkit.craftbukkit.v1_19_R2.util.CraftChatMessage;
import org.bukkit.craftbukkit.v1_19_R2.util.CraftMagicNumbers;
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
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.map.MapCursor;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;
import java.util.OptionalLong;

@SuppressWarnings("unused")
public class V1_19_3 extends NMSAddonWrapper {

    private final Field craftMetaSkullProfileField;
    private final Method bundleItemGetWeightMethod;

    public V1_19_3() {
        try {
            craftMetaSkullProfileField = Class.forName("org.bukkit.craftbukkit.v1_19_R2.inventory.CraftMetaSkull").getDeclaredField("profile");
            bundleItemGetWeightMethod = BundleItem.class.getDeclaredMethod("k", net.minecraft.world.item.ItemStack.class);
        } catch (NoSuchFieldException | ClassNotFoundException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Map<ICMaterial, TintColorProvider.SpawnEggTintData> getSpawnEggColorMap() {
        Map<ICMaterial, TintColorProvider.SpawnEggTintData> mapping = new LinkedHashMap<>();
        for (Item item : BuiltInRegistries.i) {
            if (item instanceof ItemMonsterEgg) {
                ItemMonsterEgg egg = (ItemMonsterEgg) item;
                ICMaterial icMaterial = ICMaterial.of(CraftMagicNumbers.getMaterial(item));
                mapping.put(icMaterial, new TintColorProvider.SpawnEggTintData(egg.a(0), egg.a(1)));
            }
        }
        return mapping;
    }

    @Override
    public Key getMapCursorTypeKey(MapCursor mapCursor) {
        throw new UnsupportedOperationException();
    }

    @SuppressWarnings("PatternValidation")
    @Override
    public Key getPatternTypeKey(PatternType patternType) {
        try {
            MinecraftKey key = BuiltInRegistries.ak.b(EnumBannerPatternType.a(patternType.getIdentifier()).a());
            return Key.key(key.b(), key.a());
        } catch (Exception ignored) {
        }
        return null;
    }

    @SuppressWarnings("PatternValidation")
    @Override
    public DimensionManager getDimensionManager(World world) {
        WorldServer worldServer = ((CraftWorld) world).getHandle();
        net.minecraft.world.level.dimension.DimensionManager manager = worldServer.r_();
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
                return worldServer.ac() == net.minecraft.world.level.World.g && worldServer.ab().a(BuiltinDimensionTypes.c);
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
        MinecraftKey key = worldServer.ac().a();
        return Key.key(key.b(), key.a());
    }

    @Override
    public BiomePrecipitation getPrecipitation(Location location) {
        WorldServer worldServer = ((CraftWorld) location.getWorld()).getHandle();
        BiomeBase biomeBase = worldServer.a(location.getBlockX(), location.getBlockY(), location.getBlockZ()).a();
        BiomeBase.Precipitation precipitation = biomeBase.c();
        return BiomePrecipitation.fromName(precipitation.name());
    }

    @Override
    public OptionalInt getTropicalFishBucketVariantTag(ItemStack bucket) {
        net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(bucket);
        NBTTagCompound nbt = nmsItemStack.u();
        if (nbt == null || !nbt.e("BucketVariantTag")) {
            return OptionalInt.empty();
        }
        return OptionalInt.of(nbt.h("BucketVariantTag"));
    }

    @Override
    public PotionType getBasePotionType(ItemStack potion) {
        return ((PotionMeta) potion.getItemMeta()).getBasePotionData().getType();
    }

    @Override
    public List<PotionEffect> getAllPotionEffects(ItemStack potion) {
        net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(potion);
        List<PotionEffect> effects = new ArrayList<>();
        for (MobEffect mobEffect : PotionUtil.a(nmsItemStack)) {
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
        MobEffectList mobEffectList = mobEffect.b();
        Map<AttributeBase, net.minecraft.world.entity.ai.attributes.AttributeModifier> nmsMap = mobEffectList.h();
        for (Map.Entry<AttributeBase, net.minecraft.world.entity.ai.attributes.AttributeModifier> entry : nmsMap.entrySet()) {
            String name = entry.getKey().c();
            net.minecraft.world.entity.ai.attributes.AttributeModifier nmsAttributeModifier = entry.getValue();
            AttributeModifier am = CraftAttributeInstance.convert(nmsAttributeModifier);
            double leveledAmount = mobEffectList.a(effect.getAmplifier(), nmsAttributeModifier);
            attributes.put(name, new AttributeModifier(am.getUniqueId(), am.getName(), leveledAmount, am.getOperation(), am.getSlot()));
        }
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
        net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        List<ICMaterial> materials = new ArrayList<>();
        if (nmsItemStack.t() && nmsItemStack.u().b("CanPlaceOn", 9)) {
            NBTTagList nbtTagList = nmsItemStack.u().c("CanPlaceOn", 8);
            if (!nbtTagList.isEmpty()) {
                for (int i = 0; i < nbtTagList.size(); i++) {
                    try {
                        MinecraftKey key = MinecraftKey.a(nbtTagList.j(i));
                        Block block = BuiltInRegistries.f.a(key);
                        materials.add(ICMaterial.of(CraftMagicNumbers.getMaterial(block)));
                    } catch (Exception ignore) {
                    }
                }
            }
        }
        return materials;
    }

    @Override
    public List<ICMaterial> getItemCanDestroyList(ItemStack itemStack) {
        net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        List<ICMaterial> materials = new ArrayList<>();
        if (nmsItemStack.t() && nmsItemStack.u().b("CanDestroy", 9)) {
            NBTTagList nbtTagList = nmsItemStack.u().c("CanDestroy", 8);
            if (!nbtTagList.isEmpty()) {
                for (int i = 0; i < nbtTagList.size(); i++) {
                    try {
                        MinecraftKey key = MinecraftKey.a(nbtTagList.j(i));
                        Block block = BuiltInRegistries.f.a(key);
                        materials.add(ICMaterial.of(CraftMagicNumbers.getMaterial(block)));
                    } catch (Exception ignore) {
                    }
                }
            }
        }
        return materials;
    }

    @Override
    public OptionalInt getLeatherArmorColor(ItemStack itemStack) {
        net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        if (nmsItemStack.t() && nmsItemStack.u().e("display")) {
            NBTTagCompound display = nmsItemStack.u().p("display");
            if (display.e("color")) {
                return OptionalInt.of(display.h("color"));
            }
        }
        return OptionalInt.empty();
    }

    @Override
    public boolean hasBlockEntityTag(ItemStack itemStack) {
        net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        return nmsItemStack.b("BlockEntityTag") != null;
    }

    @SuppressWarnings("PatternValidation")
    @Override
    public Component getInstrumentDescription(ItemStack itemStack) {
        try {
            net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
            if (nmsItemStack.t() && nmsItemStack.u().e("instrument")) {
                Key instrument = Key.key(nmsItemStack.u().l("instrument"));
                return Component.translatable("instrument." + instrument.namespace() + "." + instrument.value());
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    @SuppressWarnings("PatternValidation")
    @Override
    public PaintingVariant getPaintingVariant(ItemStack itemStack) {
        try {
            net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
            if (nmsItemStack.t() && nmsItemStack.u().e("EntityTag")) {
                String variant = nmsItemStack.u().p("EntityTag").l("variant");
                MinecraftKey key = new MinecraftKey(variant);
                net.minecraft.world.entity.decoration.PaintingVariant paintingVariant = BuiltInRegistries.m.a(key);
                return new PaintingVariant(Key.key(key.b(), key.a()), paintingVariant.a(), paintingVariant.b());
            }
        } catch (Exception ignored) {
        }
        return null;
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
        throw new UnsupportedOperationException();
    }

    @Override
    public TextColor getTrimMaterialColor(Object trimMaterial) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AdvancementData getAdvancementDataFromBukkitAdvancement(Object bukkitAdvancement) {
        net.minecraft.advancements.Advancement advancement = ((CraftAdvancement) bukkitAdvancement).getHandle();
        AdvancementDisplay display = advancement.c();
        if (display == null) {
            return null;
        }
        Component title = InteractiveChatComponentSerializer.gson().deserialize(CraftChatMessage.toJSON(display.a()));
        Component description = InteractiveChatComponentSerializer.gson().deserialize(CraftChatMessage.toJSON(display.b()));
        ItemStack item = CraftItemStack.asBukkitCopy(display.c());
        AdvancementType advancementType = AdvancementType.fromName(display.e().a());
        boolean isMinecraft = advancement.h().b().equals(Key.MINECRAFT_NAMESPACE);
        return new AdvancementData(title, description, item, advancementType, isMinecraft);
    }

    @Override
    public Advancement getBukkitAdvancementFromEvent(Event event) {
        return ((PlayerAdvancementDoneEvent) event).getAdvancement();
    }

    @Override
    public boolean matchArmorSlot(ItemStack armorItem, EquipmentSlot slot) {
        net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(armorItem);
        Item item = nmsItemStack.c();
        if (!(item instanceof ItemArmor)) {
            return false;
        }
        return CraftEquipmentSlot.getSlot(((ItemArmor) item).b()).equals(slot);
    }

    @SuppressWarnings("PatternValidation")
    @Override
    public Key getArmorMaterialKey(ItemStack armorItem) {
        net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(armorItem);
        Item item = nmsItemStack.c();
        if (!(item instanceof ItemArmor)) {
            return null;
        }
        ArmorMaterial armorMaterial = ((ItemArmor) item).d();
        return Key.key(armorMaterial.d());
    }

    @Override
    public Map<EquipmentSlotGroup, Multimap<String, AttributeModifier>> getItemAttributeModifiers(ItemStack itemStack) {
        net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        Map<EquipmentSlotGroup, Multimap<String, AttributeModifier>> result = new EnumMap<>(EquipmentSlotGroup.class);
        for (EnumItemSlot slot : EnumItemSlot.values()) {
            EquipmentSlotGroup equipmentSlotGroup = EquipmentSlotGroup.forEquipmentSlot(CraftEquipmentSlot.getSlot(slot));
            Multimap<AttributeBase, net.minecraft.world.entity.ai.attributes.AttributeModifier> nmsMap = nmsItemStack.a(slot);
            for (Map.Entry<AttributeBase, net.minecraft.world.entity.ai.attributes.AttributeModifier> entry : nmsMap.entries()) {
                Multimap<String, AttributeModifier> attributes = result.computeIfAbsent(equipmentSlotGroup, k -> LinkedHashMultimap.create());
                String name = entry.getKey().c();
                AttributeModifier attributeModifier = CraftAttributeInstance.convert(entry.getValue());
                attributes.put(name, attributeModifier);
            }
        }
        return result;
    }

    @Override
    public Component getDeathMessage(Player player) {
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
        CombatTracker combatTracker = entityPlayer.ex();
        return InteractiveChatComponentSerializer.gson().deserialize(CraftChatMessage.toJSON(combatTracker.b()));
    }

    @Override
    public Key getDecoratedPotSherdPatternName(ItemStack itemStack) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isJukeboxPlayable(ItemStack itemStack) {
        net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        return nmsItemStack.c() instanceof ItemRecord;
    }

    @Override
    public boolean shouldSongShowInToolTip(ItemStack disc) {
        return true;
    }

    @Override
    public Component getJukeboxSongDescription(ItemStack disc) {
        NamespacedKey namespacedKey = disc.getType().getKey();
        return Component.translatable("item." + namespacedKey.getNamespace() + "." + namespacedKey.getKey() + ".desc");
    }

    @Override
    public Component getEnchantmentDescription(Enchantment enchantment) {
        NamespacedKey namespacedKey = enchantment.getKey();
        return Component.translatable("enchantment." + namespacedKey.getNamespace() + "." + namespacedKey.getKey());
    }

    @Override
    public String getEffectTranslationKey(PotionEffectType type) {
        NamespacedKey namespacedKey = type.getKey();
        return "effect." + namespacedKey.getNamespace() + "." + namespacedKey.getKey();
    }

    @SuppressWarnings("deprecation")
    @Override
    public String getEntityTypeTranslationKey(EntityType type) {
        EntityTypes<?> entityTypes = EntityTypes.a(type.getName()).orElse(null);
        if (entityTypes == null) {
            return "";
        }
        return entityTypes.g();
    }

    @Override
    public FishHook getFishHook(Player player) {
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
        EntityFishingHook entityFishingHook = entityPlayer.cm;
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
        return MinecraftVersion.a().getPackVersion(PackType.RESOURCE);
    }

    @Override
    public float getEnchantmentDamageBonus(ItemStack itemStack, LivingEntity livingEntity) {
        net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        if (livingEntity == null) {
            return EnchantmentManager.a(nmsItemStack, EnumMonsterType.a);
        }
        EntityLiving entityLiving = ((CraftLivingEntity) livingEntity).getHandle();
        return EnchantmentManager.a(nmsItemStack, entityLiving.eE());
    }

    @Override
    public int getItemComponentsSize(ItemStack itemStack) {
        throw new UnsupportedOperationException();
    }

    @Override
    public GameProfile getPlayerHeadProfile(ItemStack playerHead) {
        try {
            craftMetaSkullProfileField.setAccessible(true);
            SkullMeta skullMeta = (SkullMeta) playerHead.getItemMeta();
            return (GameProfile) craftMetaSkullProfileField.get(skullMeta);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ItemFlag getHideAdditionalItemFlag() {
        return ItemFlag.HIDE_POTION_EFFECTS;
    }

    @Override
    public boolean shouldHideTooltip(ItemStack itemStack) {
        return false;
    }

    @Override
    public Key getAttributeModifierKey(Object attributeModifier) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ProfileProperty toProfileProperty(Property property) {
        return new ProfileProperty(property.getName(), property.getValue(), property.getSignature());
    }

    @Override
    public Fraction getWeightForBundle(ItemStack itemStack) {
        try {
            net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
            bundleItemGetWeightMethod.setAccessible(true);
            int weight = (int) bundleItemGetWeightMethod.invoke(null, nmsItemStack);
            return Fraction.getFraction(weight, 64);
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
    public boolean hasDataComponent(ItemStack itemStack, Key componentName, boolean ignoreDefault) {
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

    @Override
    public Key getItemModelResourceLocation(ItemStack itemStack) {
        return NMS.getInstance().getNMSItemStackNamespacedKey(itemStack);
    }

    @Override
    public Boolean getEnchantmentGlintOverride(ItemStack itemStack) {
        return null;
    }

    @Override
    public Key getCustomTooltipResourceLocation(ItemStack itemStack) {
        return null;
    }

    @Override
    public String getBannerPatternTranslationKey(PatternType type, DyeColor color) {
        Key typeKey = getPatternTypeKey(type);
        return "block.minecraft.banner." + typeKey.value() + "." + color.name().toLowerCase();
    }

    @Override
    public Component getTrimMaterialDescription(Object trimMaterial) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Component getTrimPatternDescription(Object trimPattern, Object trimMaterial) {
        throw new UnsupportedOperationException();
    }

    @Override
    public OptionalInt getFireworkFlightDuration(ItemStack itemStack) {
        net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        if (nmsItemStack.c() instanceof ItemFireworks) {
            NBTTagCompound nbt = nmsItemStack.b("Fireworks");
            if (nbt != null) {
                if (nbt.b("Flight", NBTBase.u)) {
                    return OptionalInt.of(nbt.f("Flight"));
                }
            }
        }
        return OptionalInt.empty();
    }

    @Override
    public boolean shouldShowOperatorBlockWarnings(ItemStack itemStack, Player player) {
        return false;
    }

}
