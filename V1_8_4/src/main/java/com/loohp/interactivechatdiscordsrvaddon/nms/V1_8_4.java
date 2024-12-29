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

import com.google.common.collect.Multimap;
import com.loohp.interactivechat.libs.net.kyori.adventure.key.Key;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.Component;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.format.NamedTextColor;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.format.TextColor;
import com.loohp.interactivechat.libs.org.apache.commons.lang3.math.Fraction;
import com.loohp.interactivechat.libs.org.apache.commons.text.WordUtils;
import com.loohp.interactivechat.nms.NMS;
import com.loohp.interactivechat.objectholders.ICMaterial;
import com.loohp.interactivechat.utils.InteractiveChatComponentSerializer;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.AdvancementData;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.AdvancementType;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.CustomModelData;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.EquipmentSlotGroup;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.BiomePrecipitation;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.DimensionManager;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.ItemDamageInfo;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.PaintingVariant;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.ProfileProperty;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.TintColorProvider;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_8_R3.Block;
import net.minecraft.server.v1_8_R3.CombatTracker;
import net.minecraft.server.v1_8_R3.EnchantmentManager;
import net.minecraft.server.v1_8_R3.EntityFishingHook;
import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.EntityTypes;
import net.minecraft.server.v1_8_R3.EnumMonsterType;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.Item;
import net.minecraft.server.v1_8_R3.ItemArmor;
import net.minecraft.server.v1_8_R3.ItemRecord;
import net.minecraft.server.v1_8_R3.MinecraftKey;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import net.minecraft.server.v1_8_R3.MobEffectList;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.NBTTagList;
import net.minecraft.server.v1_8_R3.TileEntityBanner;
import org.bukkit.Achievement;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.banner.PatternType;
import org.bukkit.craftbukkit.v1_8_R3.CraftEquipmentSlot;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.CraftStatistic;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_8_R3.potion.CraftPotionEffectType;
import org.bukkit.craftbukkit.v1_8_R3.util.CraftMagicNumbers;
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
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.map.MapCursor;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;

@SuppressWarnings("unused")
public class V1_8_4 extends NMSAddonWrapper {

    private final Field enumBannerPatternTypeKeyField;
    private final Field mobEffectListIsDebuffField;
    private final Field itemRecordTranslationKeyField;
    private final Field craftMetaSkullProfileField;

    public V1_8_4() {
        try {
            enumBannerPatternTypeKeyField = TileEntityBanner.EnumBannerPatternType.class.getDeclaredField("N");
            mobEffectListIsDebuffField = MobEffectList.class.getDeclaredField("K");
            itemRecordTranslationKeyField = ItemRecord.class.getDeclaredField("a");
            craftMetaSkullProfileField = Class.forName("org.bukkit.craftbukkit.v1_8_R3.inventory.CraftMetaSkull").getDeclaredField("profile");
        } catch (NoSuchFieldException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Map<ICMaterial, TintColorProvider.SpawnEggTintData> getSpawnEggColorMap() {
        Map<ICMaterial, TintColorProvider.SpawnEggTintData> mapping = new LinkedHashMap<>();
        for (EntityTypes.MonsterEggInfo eggInfo : EntityTypes.eggInfo.values()) {
            ItemStack itemStack = new ItemStack(Material.MONSTER_EGG, 1, (short) eggInfo.a);
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
        return Potion.fromItemStack(potion).getType();
    }

    @Override
    public List<PotionEffect> getAllPotionEffects(ItemStack potion) {
        List<PotionEffect> effects = new ArrayList<>(Potion.fromItemStack(potion).getEffects());
        ItemMeta itemMeta = potion.getItemMeta();
        if (itemMeta instanceof PotionMeta) {
            effects.addAll(((PotionMeta) itemMeta).getCustomEffects());
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
    public Map<String, ?> getPotionAttributeModifiers(PotionEffect effect) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isItemUnbreakable(ItemStack itemStack) {
        if (itemStack.getType().equals(Material.AIR)) {
            return false;
        }
        net.minecraft.server.v1_8_R3.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        return nmsItemStack.e();
    }

    @Override
    public List<ICMaterial> getItemCanPlaceOnList(ItemStack itemStack) {
        net.minecraft.server.v1_8_R3.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
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
        net.minecraft.server.v1_8_R3.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
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
        net.minecraft.server.v1_8_R3.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
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
        net.minecraft.server.v1_8_R3.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        return nmsItemStack.hasTag() && nmsItemStack.getTag().hasKey("BlockEntityTag");
    }

    @SuppressWarnings("PatternValidation")
    @Override
    public Component getInstrumentDescription(ItemStack itemStack) {
        try {
            net.minecraft.server.v1_8_R3.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
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
        net.minecraft.server.v1_8_R3.Entity nmsEntity = ((CraftEntity) entity).getHandle();
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
        net.minecraft.server.v1_8_R3.Achievement achievement = CraftStatistic.getNMSAchievement((Achievement) bukkitAdvancement);
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
        net.minecraft.server.v1_8_R3.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(armorItem);
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
    public Map<EquipmentSlotGroup, ? extends Multimap<String, ?>> getItemAttributeModifiers(ItemStack itemStack) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Component getDeathMessage(Player player) {
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
        CombatTracker combatTracker = entityPlayer.combatTracker;
        return InteractiveChatComponentSerializer.gson().deserialize(IChatBaseComponent.ChatSerializer.a(combatTracker.b()));
    }

    @Override
    public Key getDecoratedPotSherdPatternName(ItemStack itemStack) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isJukeboxPlayable(ItemStack itemStack) {
        net.minecraft.server.v1_8_R3.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
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
            net.minecraft.server.v1_8_R3.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(disc);
            ItemRecord itemRecord = (ItemRecord) nmsItemStack.getItem();
            return Component.translatable((String) itemRecordTranslationKeyField.get(itemRecord));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public Component getEnchantmentDescription(Enchantment enchantment) {
        return Component.translatable(net.minecraft.server.v1_8_R3.Enchantment.getById(enchantment.getId()).a());
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
        String name = EntityTypes.b(typeId);
        if (name == null) {
            return "";
        } else {
            return "entity." + name + ".name";
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
        return 1;
    }

    @Override
    public float getEnchantmentDamageBonus(ItemStack itemStack, LivingEntity livingEntity) {
        net.minecraft.server.v1_8_R3.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
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
        throw new UnsupportedOperationException();
    }

    @Override
    public CustomModelData getCustomModelData(ItemStack itemStack) {
        return null;
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
        return new ItemDamageInfo(itemStack.getDurability(), itemStack.getType().getMaxDurability());
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

}
