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
import com.loohp.interactivechat.libs.net.kyori.adventure.text.format.TextColor;
import com.loohp.interactivechat.objectholders.ICMaterial;
import com.loohp.interactivechat.utils.InteractiveChatComponentSerializer;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.AdvancementData;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.AdvancementType;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.EquipmentSlotGroup;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.BiomePrecipitation;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.DimensionManager;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.PaintingVariant;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.TintColorProvider;
import com.mojang.authlib.GameProfile;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_12_R1.AdvancementDisplay;
import net.minecraft.server.v1_12_R1.AttributeBase;
import net.minecraft.server.v1_12_R1.Block;
import net.minecraft.server.v1_12_R1.CombatTracker;
import net.minecraft.server.v1_12_R1.EnchantmentManager;
import net.minecraft.server.v1_12_R1.EntityFishingHook;
import net.minecraft.server.v1_12_R1.EntityLiving;
import net.minecraft.server.v1_12_R1.EntityPlayer;
import net.minecraft.server.v1_12_R1.EntityTypes;
import net.minecraft.server.v1_12_R1.EnumBannerPatternType;
import net.minecraft.server.v1_12_R1.EnumItemSlot;
import net.minecraft.server.v1_12_R1.EnumMonsterType;
import net.minecraft.server.v1_12_R1.IChatBaseComponent;
import net.minecraft.server.v1_12_R1.Item;
import net.minecraft.server.v1_12_R1.ItemArmor;
import net.minecraft.server.v1_12_R1.ItemMonsterEgg;
import net.minecraft.server.v1_12_R1.ItemRecord;
import net.minecraft.server.v1_12_R1.Items;
import net.minecraft.server.v1_12_R1.MinecraftKey;
import net.minecraft.server.v1_12_R1.MinecraftServer;
import net.minecraft.server.v1_12_R1.MobEffect;
import net.minecraft.server.v1_12_R1.MobEffectList;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import net.minecraft.server.v1_12_R1.NBTTagList;
import net.minecraft.server.v1_12_R1.PotionUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.advancement.Advancement;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.banner.PatternType;
import org.bukkit.craftbukkit.v1_12_R1.CraftEquipmentSlot;
import org.bukkit.craftbukkit.v1_12_R1.CraftServer;
import org.bukkit.craftbukkit.v1_12_R1.advancement.CraftAdvancement;
import org.bukkit.craftbukkit.v1_12_R1.enchantments.CraftEnchantment;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_12_R1.potion.CraftPotionEffectType;
import org.bukkit.craftbukkit.v1_12_R1.potion.CraftPotionUtil;
import org.bukkit.craftbukkit.v1_12_R1.util.CraftMagicNumbers;
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
public class V1_12 extends NMSAddonWrapper {

    private final Field enumBannerPatternTypeKeyField;
    private final Field mobEffectListAttributeModifiersField;
    private final Field mobEffectListIsDebuffField;
    private final Field advancementDisplayItemStackField;
    private final Field itemRecordTranslationKeyField;
    private final Field entityTypesListField;
    private final Field craftMetaSkullProfileField;

    public V1_12() {
        try {
            enumBannerPatternTypeKeyField = EnumBannerPatternType.class.getDeclaredField("N");
            mobEffectListAttributeModifiersField = MobEffectList.class.getDeclaredField("a");
            mobEffectListIsDebuffField = MobEffectList.class.getDeclaredField("c");
            advancementDisplayItemStackField = AdvancementDisplay.class.getDeclaredField("c");
            itemRecordTranslationKeyField = ItemRecord.class.getDeclaredField("c");
            entityTypesListField = EntityTypes.class.getDeclaredField("g");
            craftMetaSkullProfileField = Class.forName("org.bukkit.craftbukkit.v1_12_R1.inventory.CraftMetaSkull").getDeclaredField("profile");
        } catch (NoSuchFieldException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Map<ICMaterial, TintColorProvider.SpawnEggTintData> getSpawnEggColorMap() {
        Map<ICMaterial, TintColorProvider.SpawnEggTintData> mapping = new LinkedHashMap<>();
        for (EntityTypes.MonsterEggInfo eggInfo : EntityTypes.eggInfo.values()) {
            net.minecraft.server.v1_12_R1.ItemStack nmsItemStack = new net.minecraft.server.v1_12_R1.ItemStack(Items.SPAWN_EGG);
            ItemMonsterEgg.a(nmsItemStack, eggInfo.a);
            ICMaterial icMaterial = ICMaterial.from(CraftItemStack.asCraftMirror(nmsItemStack));
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
            for (EnumBannerPatternType type : EnumBannerPatternType.values()) {
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
    public DimensionManager getDimensionManager(World world) {
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
        net.minecraft.server.v1_12_R1.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(potion);
        List<PotionEffect> effects = new ArrayList<>();
        for (MobEffect mobEffect : PotionUtil.getEffects(nmsItemStack)) {
            effects.add(CraftPotionUtil.toBukkit(mobEffect));
        }
        return effects;
    }

    @Override
    public ChatColor getPotionEffectChatColor(PotionEffectType type) {
        try {
            mobEffectListIsDebuffField.setAccessible(true);
            MobEffectList mobEffectList = ((CraftPotionEffectType) type).getHandle();
            boolean isDebuff = mobEffectListIsDebuffField.getBoolean(mobEffectList);
            return isDebuff ? ChatColor.RED : ChatColor.BLUE;
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Map<String, AttributeModifier> getPotionAttributeModifiers(PotionEffect effect) {
        try {
            mobEffectListAttributeModifiersField.setAccessible(true);
            Map<String, AttributeModifier> attributes = new HashMap<>();
            MobEffect mobEffect = CraftPotionUtil.fromBukkit(effect);
            MobEffectList mobEffectList = mobEffect.getMobEffect();
            Map<AttributeBase, net.minecraft.server.v1_12_R1.AttributeModifier> nmsMap = (Map<AttributeBase, net.minecraft.server.v1_12_R1.AttributeModifier>) mobEffectListAttributeModifiersField.get(mobEffectList);
            for (Map.Entry<AttributeBase, net.minecraft.server.v1_12_R1.AttributeModifier> entry : nmsMap.entrySet()) {
                String name = entry.getKey().getName();
                net.minecraft.server.v1_12_R1.AttributeModifier nms = entry.getValue();
                AttributeModifier am = new AttributeModifier(nms.a(), nms.b(), nms.d(), AttributeModifier.Operation.values()[nms.c()]);
                double leveledAmount = mobEffectList.a(effect.getAmplifier(), nms);
                attributes.put(name, new AttributeModifier(am.getUniqueId(), am.getName(), leveledAmount, am.getOperation()));
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
        ItemMeta itemMeta = itemStack.getItemMeta();
        return itemMeta != null && itemMeta.isUnbreakable();
    }

    @Override
    public List<ICMaterial> getItemCanPlaceOnList(ItemStack itemStack) {
        net.minecraft.server.v1_12_R1.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
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
        net.minecraft.server.v1_12_R1.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
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
        net.minecraft.server.v1_12_R1.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
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
        net.minecraft.server.v1_12_R1.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        return nmsItemStack.hasTag() && nmsItemStack.getTag().hasKey("BlockEntityTag");
    }

    @SuppressWarnings("PatternValidation")
    @Override
    public Key getGoatHornInstrument(ItemStack itemStack) {
        try {
            net.minecraft.server.v1_12_R1.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
            if (nmsItemStack.hasTag() && nmsItemStack.getTag().hasKey("instrument")) {
                String instrument = nmsItemStack.getTag().getString("instrument");
                return Key.key(instrument);
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
        net.minecraft.server.v1_12_R1.Entity nmsEntity = ((CraftEntity) entity).getHandle();
        NBTTagCompound nbt = new NBTTagCompound();
        nmsEntity.save(nbt);
        return nbt.toString();
    }

    @Override
    public float getTrimMaterialIndex(Object trimMaterial) {
        throw new UnsupportedOperationException();
    }

    @Override
    public TextColor getTrimMaterialColor(Object trimMaterial) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AdvancementData getAdvancementDataFromBukkitAdvancement(Object bukkitAdvancement) {
        try {
            advancementDisplayItemStackField.setAccessible(true);
            net.minecraft.server.v1_12_R1.Advancement advancement = ((CraftAdvancement) bukkitAdvancement).getHandle();
            AdvancementDisplay display = advancement.c();
            if (display == null) {
                return null;
            }
            Component title = InteractiveChatComponentSerializer.gson().deserialize(IChatBaseComponent.ChatSerializer.a(display.a()));
            Component description = InteractiveChatComponentSerializer.gson().deserialize(IChatBaseComponent.ChatSerializer.a(display.b()));
            ItemStack item = CraftItemStack.asBukkitCopy((net.minecraft.server.v1_12_R1.ItemStack) advancementDisplayItemStackField.get(display));
            AdvancementType advancementType = AdvancementType.fromName(display.e().a());
            boolean isMinecraft = advancement.getName().b().equals(Key.MINECRAFT_NAMESPACE);
            return new AdvancementData(title, description, item, advancementType, isMinecraft);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Advancement getBukkitAdvancementFromEvent(Event event) {
        return ((PlayerAdvancementDoneEvent) event).getAdvancement();
    }

    @Override
    public boolean matchArmorSlot(ItemStack armorItem, EquipmentSlot slot) {
        net.minecraft.server.v1_12_R1.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(armorItem);
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
    public Map<EquipmentSlotGroup, Multimap<String, AttributeModifier>> getItemAttributeModifiers(ItemStack itemStack) {
        net.minecraft.server.v1_12_R1.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        Map<EquipmentSlotGroup, Multimap<String, AttributeModifier>> result = new EnumMap<>(EquipmentSlotGroup.class);
        for (EnumItemSlot slot : EnumItemSlot.values()) {
            EquipmentSlotGroup equipmentSlotGroup = EquipmentSlotGroup.forEquipmentSlot(CraftEquipmentSlot.getSlot(slot));
            Multimap<String, net.minecraft.server.v1_12_R1.AttributeModifier> nmsMap = nmsItemStack.a(slot);
            for (Map.Entry<String, net.minecraft.server.v1_12_R1.AttributeModifier> entry : nmsMap.entries()) {
                Multimap<String, AttributeModifier> attributes = result.computeIfAbsent(equipmentSlotGroup, k -> LinkedHashMultimap.create());
                String name = entry.getKey();
                net.minecraft.server.v1_12_R1.AttributeModifier nms = entry.getValue();
                AttributeModifier attributeModifier = new AttributeModifier(nms.a(), nms.b(), nms.d(), AttributeModifier.Operation.values()[nms.c()]);
                attributes.put(name, attributeModifier);
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
    public String getMusicDiscNameTranslationKey(ItemStack disc) {
        try {
            itemRecordTranslationKeyField.setAccessible(true);
            net.minecraft.server.v1_12_R1.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(disc);
            ItemRecord itemRecord = (ItemRecord) nmsItemStack.getItem();
            return (String) itemRecordTranslationKeyField.get(itemRecord);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getEnchantmentTranslationKey(Enchantment enchantment) {
        return ((CraftEnchantment) enchantment).getHandle().a();
    }

    @Override
    public String getEffectTranslationKey(PotionEffectType type) {
        String name = ((CraftPotionEffectType) type).getHandle().a();
        return "effect." + name.substring(name.indexOf(".") + 1);
    }

    @SuppressWarnings("deprecation")
    @Override
    public String getEntityTypeTranslationKey(EntityType type) {
        try {
            int typeId = type.getTypeId();
            if (typeId < 0) {
                return "";
            }
            entityTypesListField.setAccessible(true);
            List<String> names = (List<String>) entityTypesListField.get(null);
            if (typeId >= names.size()) {
                return "";
            } else {
                return "entity." + names.get(typeId) + ".name";
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
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
    public int getServerResourcePackVersion() {
        return 3;
    }

    @Override
    public float getEnchantmentDamageBonus(ItemStack itemStack, LivingEntity livingEntity) {
        net.minecraft.server.v1_12_R1.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
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
    public ItemFlag getHideAdditionalItemFlag() {
        return ItemFlag.HIDE_POTION_EFFECTS;
    }

}
