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
import com.loohp.interactivechatdiscordsrvaddon.objectholders.BiomePrecipitation;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.DimensionManager;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.PaintingVariant;
import com.mojang.authlib.GameProfile;
import com.mojang.bridge.game.PackType;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.EnumChatFormat;
import net.minecraft.MinecraftVersion;
import net.minecraft.advancements.AdvancementDisplay;
import net.minecraft.core.IRegistry;
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
import net.minecraft.world.entity.decoration.Paintings;
import net.minecraft.world.entity.projectile.EntityFishingHook;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemArmor;
import net.minecraft.world.item.alchemy.PotionUtil;
import net.minecraft.world.item.enchantment.EnchantmentManager;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.EnumBannerPatternType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.advancement.Advancement;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.banner.PatternType;
import org.bukkit.craftbukkit.v1_18_R1.CraftEquipmentSlot;
import org.bukkit.craftbukkit.v1_18_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_18_R1.advancement.CraftAdvancement;
import org.bukkit.craftbukkit.v1_18_R1.attribute.CraftAttributeInstance;
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_18_R1.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_18_R1.potion.CraftPotionEffectType;
import org.bukkit.craftbukkit.v1_18_R1.potion.CraftPotionUtil;
import org.bukkit.craftbukkit.v1_18_R1.util.CraftChatMessage;
import org.bukkit.craftbukkit.v1_18_R1.util.CraftMagicNumbers;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;
import java.util.OptionalLong;

@SuppressWarnings("unused")
public class V1_18 extends NMSAddonWrapper {

    private final Field dimensionManagerFixedTimeField;
    private final Field dimensionManagerInfiniburnField;
    private final Field dimensionManagerAmbientLightField;
    private final Field craftMetaSkullProfileField;

    public V1_18() {
        try {
            dimensionManagerFixedTimeField = net.minecraft.world.level.dimension.DimensionManager.class.getDeclaredField("w");
            dimensionManagerInfiniburnField = net.minecraft.world.level.dimension.DimensionManager.class.getDeclaredField("K");
            dimensionManagerAmbientLightField = net.minecraft.world.level.dimension.DimensionManager.class.getDeclaredField("M");
            craftMetaSkullProfileField = Class.forName("org.bukkit.craftbukkit.v1_18_R1.inventory.CraftMetaSkull").getDeclaredField("profile");
        } catch (NoSuchFieldException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("PatternValidation")
    @Override
    public Key getPatternTypeKey(PatternType patternType) {
        try {
            for (EnumBannerPatternType type : EnumBannerPatternType.values()) {
                if (type.b().equalsIgnoreCase(patternType.getIdentifier())) {
                    return Key.key(type.a());
                }
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    @SuppressWarnings("PatternValidation")
    @Override
    public DimensionManager getDimensionManager(World world) {
        WorldServer worldServer = ((CraftWorld) world).getHandle();
        net.minecraft.world.level.dimension.DimensionManager manager = worldServer.q_();
        return new DimensionManager() {
            @Override
            public boolean hasFixedTime() {
                return manager.o();
            }
            @Override
            public OptionalLong getFixedTime() {
                try {
                    dimensionManagerFixedTimeField.setAccessible(true);
                    return (OptionalLong) dimensionManagerFixedTimeField.get(manager);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
            @Override
            public float timeOfDay(long i) {
                return manager.a(i);
            }
            @Override
            public boolean hasSkyLight() {
                return manager.b();
            }
            @Override
            public boolean hasCeiling() {
                return manager.c();
            }
            @Override
            public boolean ultraWarm() {
                return manager.d();
            }
            @Override
            public boolean natural() {
                return manager.e();
            }
            @Override
            public double coordinateScale() {
                return manager.f();
            }
            @Override
            public boolean createDragonFight() {
                return manager.n();
            }
            @Override
            public boolean piglinSafe() {
                return manager.g();
            }
            @Override
            public boolean bedWorks() {
                return manager.h();
            }
            @Override
            public boolean respawnAnchorWorks() {
                return manager.i();
            }
            @Override
            public boolean hasRaids() {
                return manager.j();
            }
            @Override
            public int minY() {
                return manager.k();
            }
            @Override
            public int height() {
                return manager.l();
            }
            @Override
            public int logicalHeight() {
                return manager.m();
            }
            @SuppressWarnings("PatternValidation")
            @Override
            public Key infiniburn() {
                try {
                    MinecraftKey key = (MinecraftKey) dimensionManagerInfiniburnField.get(manager);
                    return Key.key(key.b(), key.a());
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
            @SuppressWarnings("PatternValidation")
            @Override
            public Key effectsLocation() {
                MinecraftKey key = manager.q();
                return Key.key(key.b(), key.a());
            }
            @Override
            public float ambientLight() {
                try {
                    dimensionManagerAmbientLightField.setAccessible(true);
                    return dimensionManagerAmbientLightField.getFloat(manager);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    @SuppressWarnings("PatternValidation")
    @Override
    public Key getNamespacedKey(World world) {
        WorldServer worldServer = ((CraftWorld) world).getHandle();
        MinecraftKey key = worldServer.aa().a();
        return Key.key(key.b(), key.a());
    }

    @Override
    public BiomePrecipitation getPrecipitation(Location location) {
        WorldServer worldServer = ((CraftWorld) location.getWorld()).getHandle();
        BiomeBase biomeBase = worldServer.a(location.getBlockX(), location.getBlockY(), location.getBlockZ());
        BiomeBase.Precipitation precipitation = biomeBase.c();
        return BiomePrecipitation.fromName(precipitation.name());
    }

    @Override
    public OptionalInt getTropicalFishBucketVariantTag(ItemStack bucket) {
        net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(bucket);
        NBTTagCompound nbt = nmsItemStack.s();
        if (nbt == null || !nbt.e("BucketVariantTag")) {
            return OptionalInt.empty();
        }
        return OptionalInt.of(nbt.h("BucketVariantTag"));
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
        EnumChatFormat chatFormat = mobEffectList.e().a();
        return ChatColor.getByChar(chatFormat.toString().charAt(1));
    }

    @Override
    public Map<String, AttributeModifier> getPotionAttributeModifiers(PotionEffect effect) {
        Map<String, AttributeModifier> attributes = new HashMap<>();
        MobEffect mobEffect = CraftPotionUtil.fromBukkit(effect);
        MobEffectList mobEffectList = mobEffect.a();
        Map<AttributeBase, net.minecraft.world.entity.ai.attributes.AttributeModifier> nmsMap = mobEffectList.g();
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
        if (nmsItemStack.r() && nmsItemStack.s().b("CanPlaceOn", 9)) {
            NBTTagList nbtTagList = nmsItemStack.s().c("CanPlaceOn", 8);
            if (!nbtTagList.isEmpty()) {
                for (int i = 0; i < nbtTagList.size(); i++) {
                    try {
                        MinecraftKey key = MinecraftKey.a(nbtTagList.j(i));
                        Block block = IRegistry.X.a(key);
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
        if (nmsItemStack.r() && nmsItemStack.s().b("CanDestroy", 9)) {
            NBTTagList nbtTagList = nmsItemStack.s().c("CanDestroy", 8);
            if (!nbtTagList.isEmpty()) {
                for (int i = 0; i < nbtTagList.size(); i++) {
                    try {
                        MinecraftKey key = MinecraftKey.a(nbtTagList.j(i));
                        Block block = IRegistry.X.a(key);
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
        if (nmsItemStack.r() && nmsItemStack.s().e("display")) {
            NBTTagCompound display = nmsItemStack.s().p("display");
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
    public Key getGoatHornInstrument(ItemStack itemStack) {
        try {
            net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
            if (nmsItemStack.r() && nmsItemStack.s().e("instrument")) {
                String instrument = nmsItemStack.s().l("instrument");
                return Key.key(instrument);
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
            if (nmsItemStack.r() && nmsItemStack.s().e("EntityTag")) {
                String variant = nmsItemStack.s().p("EntityTag").l("variant");
                MinecraftKey key = new MinecraftKey(variant);
                Paintings paintingVariant = IRegistry.ae.a(key);
                return new PaintingVariant(Key.key(key.b(), key.a()), paintingVariant.a() / 16, paintingVariant.b() / 16);
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
    public float getTrimMaterialIndex(Object trimMaterial) {
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
    public Map<EquipmentSlot, Multimap<String, AttributeModifier>> getItemAttributeModifiers(ItemStack itemStack) {
        net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        Map<EquipmentSlot, Multimap<String, AttributeModifier>> result = new EnumMap<>(EquipmentSlot.class);
        for (EnumItemSlot slot : EnumItemSlot.values()) {
            EquipmentSlot equipmentSlot = CraftEquipmentSlot.getSlot(slot);
            Multimap<AttributeBase, net.minecraft.world.entity.ai.attributes.AttributeModifier> nmsMap = nmsItemStack.a(slot);
            for (Map.Entry<AttributeBase, net.minecraft.world.entity.ai.attributes.AttributeModifier> entry : nmsMap.entries()) {
                Multimap<String, AttributeModifier> attributes = result.computeIfAbsent(equipmentSlot, k -> LinkedHashMultimap.create());
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
        CombatTracker combatTracker = entityPlayer.ej();
        return InteractiveChatComponentSerializer.gson().deserialize(CraftChatMessage.toJSON(combatTracker.b()));
    }

    @Override
    public Key getDecoratedPotSherdPatternName(ItemStack itemStack) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getMusicDiscNameTranslationKey(ItemStack disc) {
        NamespacedKey namespacedKey = disc.getType().getKey();
        return "item." + namespacedKey.getNamespace() + "." + namespacedKey.getKey() + ".desc";
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
        EntityFishingHook entityFishingHook = entityPlayer.co;
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
        return EnchantmentManager.a(nmsItemStack, entityLiving.eq());
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
}
