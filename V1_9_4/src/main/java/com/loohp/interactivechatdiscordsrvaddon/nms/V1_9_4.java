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
import com.loohp.interactivechat.libs.net.kyori.adventure.key.Key;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.Component;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.format.NamedTextColor;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.format.TextColor;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.object.PlayerHeadObjectContents;
import com.loohp.interactivechat.libs.org.apache.commons.lang3.math.Fraction;
import com.loohp.interactivechat.libs.org.apache.commons.text.WordUtils;
import com.loohp.interactivechat.nms.NMS;
import com.loohp.interactivechat.objectholders.ICMaterial;
import com.loohp.interactivechat.utils.InteractiveChatComponentSerializer;
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
import net.minecraft.server.v1_9_R2.Block;
import net.minecraft.server.v1_9_R2.CombatTracker;
import net.minecraft.server.v1_9_R2.EnchantmentManager;
import net.minecraft.server.v1_9_R2.EntityFishingHook;
import net.minecraft.server.v1_9_R2.EntityLiving;
import net.minecraft.server.v1_9_R2.EntityPlayer;
import net.minecraft.server.v1_9_R2.EntityTypes;
import net.minecraft.server.v1_9_R2.EnumItemSlot;
import net.minecraft.server.v1_9_R2.EnumMonsterType;
import net.minecraft.server.v1_9_R2.IChatBaseComponent;
import net.minecraft.server.v1_9_R2.Item;
import net.minecraft.server.v1_9_R2.ItemArmor;
import net.minecraft.server.v1_9_R2.ItemRecord;
import net.minecraft.server.v1_9_R2.MinecraftKey;
import net.minecraft.server.v1_9_R2.MinecraftServer;
import net.minecraft.server.v1_9_R2.MobEffect;
import net.minecraft.server.v1_9_R2.MobEffectList;
import net.minecraft.server.v1_9_R2.NBTTagCompound;
import net.minecraft.server.v1_9_R2.NBTTagList;
import net.minecraft.server.v1_9_R2.PotionUtil;
import net.minecraft.server.v1_9_R2.TileEntityBanner;
import org.bukkit.Achievement;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.banner.PatternType;
import org.bukkit.craftbukkit.v1_9_R2.CraftEquipmentSlot;
import org.bukkit.craftbukkit.v1_9_R2.CraftServer;
import org.bukkit.craftbukkit.v1_9_R2.CraftStatistic;
import org.bukkit.craftbukkit.v1_9_R2.enchantments.CraftEnchantment;
import org.bukkit.craftbukkit.v1_9_R2.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_9_R2.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_9_R2.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_9_R2.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_9_R2.potion.CraftPotionEffectType;
import org.bukkit.craftbukkit.v1_9_R2.potion.CraftPotionUtil;
import org.bukkit.craftbukkit.v1_9_R2.util.CraftMagicNumbers;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerAchievementAwardedEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.map.MapCursor;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;

@SuppressWarnings("unused")
public class V1_9_4 extends NMSAddonWrapper {

    private final Field enumBannerPatternTypeKeyField;
    private final Field mobEffectListAttributeModifiersField;
    private final Field mobEffectListIsDebuffField;
    private final Field itemRecordTranslationKeyField;
    private final Field craftMetaSkullProfileField;

    public V1_9_4() {
        try {
            enumBannerPatternTypeKeyField = TileEntityBanner.EnumBannerPatternType.class.getDeclaredField("N");
            mobEffectListAttributeModifiersField = MobEffectList.class.getDeclaredField("a");
            mobEffectListIsDebuffField = MobEffectList.class.getDeclaredField("c");
            itemRecordTranslationKeyField = ItemRecord.class.getDeclaredField("c");
            craftMetaSkullProfileField = Class.forName("org.bukkit.craftbukkit.v1_9_R2.inventory.CraftMetaSkull").getDeclaredField("profile");
        } catch (NoSuchFieldException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Map<ICMaterial, TintColorProvider.SpawnEggTintData> getSpawnEggColorMap() {
        Map<ICMaterial, TintColorProvider.SpawnEggTintData> mapping = new LinkedHashMap<>();
        for (EntityTypes.MonsterEggInfo eggInfo : EntityTypes.eggInfo.values()) {
            int id = EntityTypes.a(eggInfo.a);
            ItemStack itemStack = new ItemStack(Material.MONSTER_EGG, 1, (short) id);
            ICMaterial icMaterial = ICMaterial.from(itemStack);
            mapping.put(icMaterial, new TintColorProvider.SpawnEggTintData(eggInfo.b, eggInfo.c));
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
            enumBannerPatternTypeKeyField.setAccessible(true);
            for (TileEntityBanner.EnumBannerPatternType type : TileEntityBanner.EnumBannerPatternType.values()) {
                if (type.b().equalsIgnoreCase(patternType.getIdentifier())) {
                    String key = (String) enumBannerPatternTypeKeyField.get(type);
                    return Key.key(key);
                }
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    @Override
    public LegacyDimensionManager getLegacyDimensionManager(World world) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Key getNamespacedKey(World world) {
        if (world.getEnvironment().equals(World.Environment.NORMAL)) {
            return Key.key("minecraft", "overworld");
        } else if (world.getEnvironment().equals(World.Environment.NETHER)) {
            return Key.key("minecraft", "the_nether");
        } else if (world.getEnvironment().equals(World.Environment.THE_END)) {
            return Key.key("minecraft", "the_end");
        } else {
            return Key.key("minecraft", "custom");
        }
    }

    @Override
    public BiomePrecipitation getPrecipitation(Location location) {
        double temperature = location.getWorld().getTemperature(location.getBlockX(), location.getBlockZ());
        if (temperature > 0.95) {
            return BiomePrecipitation.NONE;
        } else if (temperature < 0.15) {
            return BiomePrecipitation.SNOW;
        } else {
            return BiomePrecipitation.RAIN;
        }
    }

    @Override
    public OptionalInt getTropicalFishBucketVariantTag(ItemStack bucket) {
        return OptionalInt.empty();
    }

    @Override
    public PotionType getBasePotionType(ItemStack potion) {
        return ((PotionMeta) potion.getItemMeta()).getBasePotionData().getType();
    }

    @Override
    public List<PotionEffect> getAllPotionEffects(ItemStack potion) {
        net.minecraft.server.v1_9_R2.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(potion);
        List<PotionEffect> effects = new ArrayList<>();
        for (MobEffect mobEffect : PotionUtil.getEffects(nmsItemStack)) {
            effects.add(CraftPotionUtil.toBukkit(mobEffect));
        }
        return effects;
    }

    @Override
    public TextColor getPotionEffectChatColor(PotionEffectType type) {
        try {
            mobEffectListIsDebuffField.setAccessible(true);
            MobEffectList mobEffectList = ((CraftPotionEffectType) type).getHandle();
            boolean isDebuff = mobEffectListIsDebuffField.getBoolean(mobEffectList);
            return isDebuff ? NamedTextColor.RED : NamedTextColor.BLUE;
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Map<AttributeBase, AttributeModifier> getPotionAttributeModifiers(PotionEffect effect) {
        try {
            mobEffectListAttributeModifiersField.setAccessible(true);
            Map<AttributeBase, AttributeModifier> attributes = new HashMap<>();
            MobEffect mobEffect = CraftPotionUtil.fromBukkit(effect);
            MobEffectList mobEffectList = mobEffect.getMobEffect();
            Map<net.minecraft.server.v1_9_R2.AttributeBase, net.minecraft.server.v1_9_R2.AttributeModifier> nmsMap = (Map<net.minecraft.server.v1_9_R2.AttributeBase, net.minecraft.server.v1_9_R2.AttributeModifier>) mobEffectListAttributeModifiersField.get(mobEffectList);
            for (Map.Entry<net.minecraft.server.v1_9_R2.AttributeBase, net.minecraft.server.v1_9_R2.AttributeModifier> entry : nmsMap.entrySet()) {
                net.minecraft.server.v1_9_R2.AttributeBase nmsAttributeBase = entry.getKey();
                AttributeBase attributeBase = new AttributeBase(nmsAttributeBase.getName(), nmsAttributeBase.c());
                net.minecraft.server.v1_9_R2.AttributeModifier nms = entry.getValue();
                AttributeModifier am = new AttributeModifier(nms.a(), nms.b(), nms.d(), AttributeModifier.Operation.values()[nms.c()]);
                double leveledAmount = mobEffectList.a(effect.getAmplifier(), nms);
                attributes.put(attributeBase, new AttributeModifier(am.getUniqueId(), am.getName(), leveledAmount, am.getOperation()));
            }
            return attributes;
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isItemUnbreakable(ItemStack itemStack) {
        if (itemStack.getType().equals(Material.AIR)) {
            return false;
        }
        net.minecraft.server.v1_9_R2.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        return nmsItemStack.e();
    }

    @Override
    public List<ICMaterial> getItemCanPlaceOnList(ItemStack itemStack) {
        net.minecraft.server.v1_9_R2.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        List<ICMaterial> materials = new ArrayList<>();
        if (nmsItemStack.hasTag() && nmsItemStack.getTag().hasKeyOfType("CanPlaceOn", 9)) {
            NBTTagList nbtTagList = nmsItemStack.getTag().getList("CanPlaceOn", 8);
            if (!nbtTagList.isEmpty()) {
                for (int i = 0; i < nbtTagList.size(); i++) {
                    try {
                        MinecraftKey key = new MinecraftKey(nbtTagList.getString(i));
                        Block block = Block.REGISTRY.get(key);
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
        net.minecraft.server.v1_9_R2.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        List<ICMaterial> materials = new ArrayList<>();
        if (nmsItemStack.hasTag() && nmsItemStack.getTag().hasKeyOfType("CanDestroy", 9)) {
            NBTTagList nbtTagList = nmsItemStack.getTag().getList("CanDestroy", 8);
            if (!nbtTagList.isEmpty()) {
                for (int i = 0; i < nbtTagList.size(); i++) {
                    try {
                        MinecraftKey key = new MinecraftKey(nbtTagList.getString(i));
                        Block block = Block.REGISTRY.get(key);
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
        net.minecraft.server.v1_9_R2.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        if (nmsItemStack.hasTag() && nmsItemStack.getTag().hasKey("display")) {
            NBTTagCompound display = nmsItemStack.getTag().getCompound("display");
            if (display.hasKey("color")) {
                return OptionalInt.of(display.getInt("color"));
            }
        }
        return OptionalInt.empty();
    }

    @Override
    public boolean hasBlockEntityTag(ItemStack itemStack) {
        net.minecraft.server.v1_9_R2.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        return nmsItemStack.hasTag() && nmsItemStack.getTag().hasKey("BlockEntityTag");
    }

    @SuppressWarnings("PatternValidation")
    @Override
    public Component getInstrumentDescription(ItemStack itemStack) {
        try {
            net.minecraft.server.v1_9_R2.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
            if (nmsItemStack.hasTag() && nmsItemStack.getTag().hasKey("instrument")) {
                Key instrument = Key.key(nmsItemStack.getTag().getString("instrument"));
                return Component.translatable("instrument." + instrument.namespace() + "." + instrument.value());
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    @Override
    public PaintingVariant getPaintingVariant(ItemStack itemStack) {
        return null;
    }

    @Override
    public String getEntityNBT(Entity entity) {
        net.minecraft.server.v1_9_R2.Entity nmsEntity = ((CraftEntity) entity).getHandle();
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
        net.minecraft.server.v1_9_R2.Achievement achievement = CraftStatistic.getNMSAchievement((Achievement) bukkitAdvancement);
        String name = achievement.name;
        Component title = Component.translatable(name).color(NamedTextColor.GREEN);
        Component description = Component.translatable(name + ".desc");
        ItemStack item = CraftItemStack.asBukkitCopy(achievement.d);
        AdvancementType advancementType = AdvancementType.LEGACY;
        return new AdvancementData(title, description, item, AdvancementType.LEGACY, true);
    }

    @Override
    public Achievement getBukkitAdvancementFromEvent(Event event) {
        return ((PlayerAchievementAwardedEvent) event).getAchievement();
    }

    @Override
    public boolean matchArmorSlot(ItemStack armorItem, EquipmentSlot slot) {
        net.minecraft.server.v1_9_R2.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(armorItem);
        Item item = nmsItemStack.getItem();
        if (!(item instanceof ItemArmor)) {
            return false;
        }
        return CraftEquipmentSlot.getSlot(((ItemArmor) item).c).equals(slot);
    }

    @Override
    public Key getArmorMaterialKey(ItemStack armorItem) {
        String armorItemMaterial = armorItem.getType().name();
        if (armorItemMaterial.contains("DIAMOND")) {
            return Key.key("minecraft", "diamond");
        } else if (armorItemMaterial.contains("GOLD")) {
            return Key.key("minecraft", "gold");
        } else if (armorItemMaterial.contains("IRON")) {
            return Key.key("minecraft", "iron");
        } else if (armorItemMaterial.contains("CHAIN")) {
            return Key.key("minecraft", "chainmail");
        } else {
            return Key.key("minecraft", "leather");
        }
    }

    @Override
    public Map<EquipmentSlotGroup, Multimap<AttributeBase, AttributeModifier>> getItemAttributeModifiers(ItemStack itemStack) {
        net.minecraft.server.v1_9_R2.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        Map<EquipmentSlotGroup, Multimap<AttributeBase, AttributeModifier>> result = new EnumMap<>(EquipmentSlotGroup.class);
        for (EnumItemSlot slot : EnumItemSlot.values()) {
            EquipmentSlotGroup equipmentSlotGroup = EquipmentSlotGroup.forEquipmentSlot(CraftEquipmentSlot.getSlot(slot));
            Multimap<String, net.minecraft.server.v1_9_R2.AttributeModifier> nmsMap = nmsItemStack.a(slot);
            for (Map.Entry<String, net.minecraft.server.v1_9_R2.AttributeModifier> entry : nmsMap.entries()) {
                Multimap<AttributeBase, AttributeModifier> attributes = result.computeIfAbsent(equipmentSlotGroup, k -> LinkedHashMultimap.create());
                AttributeBase attributeBase = new AttributeBase(entry.getKey());
                net.minecraft.server.v1_9_R2.AttributeModifier nms = entry.getValue();
                AttributeModifier attributeModifier = new AttributeModifier(nms.a(), nms.b(), nms.d(), AttributeModifier.Operation.values()[nms.c()]);
                attributes.put(attributeBase, attributeModifier);
            }
        }
        return result;
    }

    @Override
    public Component getDeathMessage(Player player) {
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
        CombatTracker combatTracker = entityPlayer.getCombatTracker();
        return InteractiveChatComponentSerializer.gson().deserialize(IChatBaseComponent.ChatSerializer.a(combatTracker.getDeathMessage()));
    }

    @Override
    public Key getDecoratedPotSherdPatternName(ItemStack itemStack) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isJukeboxPlayable(ItemStack itemStack) {
        net.minecraft.server.v1_9_R2.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        return nmsItemStack.getItem() instanceof ItemRecord;
    }

    @Override
    public boolean shouldSongShowInToolTip(ItemStack disc) {
        return true;
    }

    @Override
    public Component getJukeboxSongDescription(ItemStack disc) {
        try {
            itemRecordTranslationKeyField.setAccessible(true);
            net.minecraft.server.v1_9_R2.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(disc);
            ItemRecord itemRecord = (ItemRecord) nmsItemStack.getItem();
            return Component.translatable((String) itemRecordTranslationKeyField.get(itemRecord));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Component getEnchantmentDescription(Enchantment enchantment) {
        return Component.translatable(((CraftEnchantment) enchantment).getHandle().a());
    }

    @Override
    public String getEffectTranslationKey(PotionEffectType type) {
        String name = ((CraftPotionEffectType) type).getHandle().a();
        return "effect." + name.substring(name.indexOf(".") + 1);
    }

    @SuppressWarnings("deprecation")
    @Override
    public String getEntityTypeTranslationKey(EntityType type) {
        int typeId = type.getTypeId();
        if (typeId < 0) {
            return "";
        }
        Class<? extends net.minecraft.server.v1_9_R2.Entity> entityClass = EntityTypes.a(typeId);
        if (entityClass == null) {
            return "";
        } else {
            return "entity." + EntityTypes.getName(entityClass) + ".name";
        }
    }

    @Override
    public FishHook getFishHook(Player player) {
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
        EntityFishingHook entityFishingHook = entityPlayer.hookedFish;
        return entityFishingHook == null ? null : (FishHook) entityFishingHook.getBukkitEntity();
    }

    @Override
    public String getServerResourcePack() {
        MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
        return server.getResourcePack();
    }

    @Override
    public String getServerResourcePackHash() {
        MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
        return server.getResourcePackHash();
    }

    @Override
    public int getServerResourcePackMajorVersion() {
        return 2;
    }

    @Override
    public int getServerResourcePackMinorVersion() {
        return 0;
    }

    @Override
    public float getEnchantmentDamageBonus(ItemStack itemStack, LivingEntity livingEntity) {
        net.minecraft.server.v1_9_R2.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        if (livingEntity == null) {
            return EnchantmentManager.a(nmsItemStack, EnumMonsterType.UNDEFINED);
        }
        EntityLiving entityLiving = ((CraftLivingEntity) livingEntity).getHandle();
        return EnchantmentManager.a(nmsItemStack, entityLiving.getMonsterType());
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
    public GameProfile getPlayerHeadProfile(PlayerHeadObjectContents contents) {
        return null;
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
    public PropertyMap getGameProfilePropertyMap(GameProfile gameProfile) {
        return gameProfile.getProperties();
    }

    @Override
    public ProfileProperty toProfileProperty(Property property) {
        return new ProfileProperty(property.getName(), property.getValue(), property.getSignature());
    }

    @Override
    public Fraction getWeightForBundle(ItemStack itemStack) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CustomModelData getCustomModelData(ItemStack itemStack) {
        return null;
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
        return new ItemDamageInfo(itemStack.getDurability(), itemStack.getType().getMaxDurability());
    }

    @Override
    public float getItemCooldownProgress(Player player, ItemStack itemStack) {
        return 0.0F;
    }

    @Override
    public float getSkyAngle(Location location) {
        return 0F;
    }

    @Override
    public MoonPhase getMoonPhase(Location location) {
        return MoonPhase.FULL_MOON;
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
        String colorName = WordUtils.capitalizeFully(color.name().toLowerCase().replace("_", " ")).replace(" ", "");
        colorName = colorName.substring(0, 1).toLowerCase() + colorName.substring(1);
        return "item.banner." + typeKey.value() + "." + colorName;
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
        return OptionalInt.empty();
    }

    @Override
    public boolean shouldShowOperatorBlockWarnings(ItemStack itemStack, Player player) {
        return false;
    }

    @Override
    public Object getItemStackDataComponentValue(ItemStack itemStack, Key component) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object serializeDataComponent(Key component, String data) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean evaluateComponentPredicateOnItemStack(ItemStack itemStack, String predicateData, String data) {
        throw new UnsupportedOperationException();
    }

}
