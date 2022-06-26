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

package com.loohp.interactivechatdiscordsrvaddon.resources.mods.chime;

import com.google.common.collect.Range;
import com.loohp.interactivechat.libs.io.github.bananapuncher714.nbteditor.NBTEditor;
import com.loohp.interactivechat.libs.net.querz.nbt.tag.CompoundTag;
import com.loohp.interactivechat.libs.org.json.simple.JSONObject;
import com.loohp.interactivechat.objectholders.OfflineICPlayer;
import com.loohp.interactivechat.utils.ItemNBTUtils;
import com.loohp.interactivechat.utils.NBTParsingUtils;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.BiomePrecipitation;
import com.loohp.interactivechatdiscordsrvaddon.resources.models.ModelOverride;
import com.loohp.interactivechatdiscordsrvaddon.resources.mods.chime.ChimePredicateEnums.ItemInHand;
import com.loohp.interactivechatdiscordsrvaddon.resources.mods.chime.ChimePredicateEnums.TargetType;
import com.loohp.interactivechatdiscordsrvaddon.resources.mods.chime.ChimeUtils.HashPredicate;
import com.loohp.interactivechatdiscordsrvaddon.utils.WorldUtils;
import com.loohp.interactivechatdiscordsrvaddon.wrappers.DimensionManagerWrapper;
import org.bukkit.Bukkit;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.util.RayTraceResult;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

public class ChimeModelOverride extends ModelOverride {

    private Map<ChimeModelOverrideType, Object> chimePredicates;
    private String armorTexture;

    public ChimeModelOverride(Map<ModelOverrideType, Float> predicates, Map<ChimeModelOverrideType, Object> chimePredicates, String model, String armorTexture) {
        super(predicates, model);
        this.chimePredicates = chimePredicates;
        this.armorTexture = armorTexture;
    }

    public ChimeModelOverride(Map<ModelOverrideType, Float> predicates, Map<ChimeModelOverrideType, Object> chimePredicates, String model) {
        this(predicates, chimePredicates, model, null);
    }

    public Map<ChimeModelOverrideType, Object> getChimePredicates() {
        return chimePredicates;
    }

    public boolean hasArmorTexture() {
        return armorTexture != null;
    }

    public String getArmorTexture() {
        return armorTexture;
    }

    @Deprecated
    @Override
    public boolean test(Map<ModelOverrideType, Float> data) {
        if (chimePredicates.isEmpty()) {
            return super.test(data);
        }
        return false;
    }

    public boolean test(Map<ModelOverrideType, Float> data, OfflineICPlayer player, World world, LivingEntity entity, ItemStack itemStack, UnaryOperator<String> translateFunction) {
        if (!super.test(data)) {
            return false;
        }
        for (Entry<ChimeModelOverrideType, Object> entry : chimePredicates.entrySet()) {
            if (!entry.getKey().test(entry.getValue(), player, world, entity, itemStack, translateFunction)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        ChimeModelOverride that = (ChimeModelOverride) o;
        return Objects.equals(chimePredicates, that.chimePredicates) && Objects.equals(armorTexture, that.armorTexture);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), chimePredicates, armorTexture);
    }

    @SuppressWarnings("deprecation")
    public enum ChimeModelOverrideType {

        COUNT("count", Range.class, (value, player, world, livingEntity, itemStack, translateFunction) -> {
            return value.contains(itemStack.getAmount());
        }),
        DURABILITY("durability", Range.class, (value, player, world, livingEntity, itemStack, translateFunction) -> {
            if (itemStack.hasItemMeta() && itemStack.getItemMeta() instanceof Damageable) {
                return value.contains(((Damageable) itemStack.getItemMeta()).getDamage());
            } else {
                return value.contains(0);
            }
        }),
        NBT("nbt", JSONObject.class, (value, player, world, livingEntity, itemStack, translateFunction) -> {
            String nbt = ItemNBTUtils.getNMSItemStackJson(itemStack);
            CompoundTag compoundTag = (CompoundTag) NBTParsingUtils.fromSNBT(nbt);
            if (compoundTag.containsKey("tag")) {
                return ChimeUtils.matchesJsonObject(value, compoundTag.getCompoundTag("tag"));
            } else {
                return false;
            }
        }),
        NAME("name", String.class, (value, player, world, livingEntity, itemStack, translateFunction) -> {
            String name = ChimeUtils.getItemDisplayName(itemStack, translateFunction);
            if (value.startsWith("/") && value.endsWith("/")) {
                return Pattern.matches(value.substring(1, value.length() - 1), name);
            } else {
                return value.equals(name);
            }
        }),
        HASH("hash", HashPredicate.class, (value, player, world, livingEntity, itemStack, translateFunction) -> {
            String nbt = ItemNBTUtils.getNMSItemStackJson(itemStack);
            CompoundTag compoundTag = (CompoundTag) NBTParsingUtils.fromSNBT(nbt);
            if (compoundTag.containsKey("tag")) {
                return value.matches(compoundTag.getCompoundTag("tag"));
            } else {
                return false;
            }
        }),
        DIMENSION_ID("dimension/id", String.class, (value, player, world, livingEntity, itemStack, translateFunction) -> {
            return world != null && WorldUtils.getNamespacedKey(world).equals(value);
        }),
        DIMENSION_HAS_SKY_LIGHT("dimension/has_sky_light", boolean.class, (value, player, world, livingEntity, itemStack, translateFunction) -> {
            return world != null && new DimensionManagerWrapper(world).hasSkyLight() == value;
        }),
        DIMENSION_HAS_CEILING("dimension/has_ceiling", boolean.class, (value, player, world, livingEntity, itemStack, translateFunction) -> {
            return world != null && new DimensionManagerWrapper(world).hasCeiling() == value;
        }),
        DIMENSION_ULTRAWARM("dimension/ultrawarm", boolean.class, (value, player, world, livingEntity, itemStack, translateFunction) -> {
            return world != null && new DimensionManagerWrapper(world).ultraWarm() == value;
        }),
        DIMENSION_NATURAL("dimension/natural", boolean.class, (value, player, world, livingEntity, itemStack, translateFunction) -> {
            return world != null && new DimensionManagerWrapper(world).natural() == value;
        }),
        DIMENSION_HAS_ENDER_DRAGON_FIGHT("dimension/has_ender_dragon_fight", boolean.class, (value, player, world, livingEntity, itemStack, translateFunction) -> {
            return world != null && new DimensionManagerWrapper(world).createDragonFight() == value;
        }),
        DIMENSION_PIGLIN_SAFE("dimension/piglin_safe", boolean.class, (value, player, world, livingEntity, itemStack, translateFunction) -> {
            return world != null && new DimensionManagerWrapper(world).piglinSafe() == value;
        }),
        DIMENSION_BED_WORKS("dimension/bed_works", boolean.class, (value, player, world, livingEntity, itemStack, translateFunction) -> {
            return world != null && new DimensionManagerWrapper(world).bedWorks() == value;
        }),
        DIMENSION_RESPAWN_ANCHOR_WORKS("dimension/respawn_anchor_works", boolean.class, (value, player, world, livingEntity, itemStack, translateFunction) -> {
            return world != null && new DimensionManagerWrapper(world).respawnAnchorWorks() == value;
        }),
        DIMENSION_HAS_RAIDS("dimension/has_raids", boolean.class, (value, player, world, livingEntity, itemStack, translateFunction) -> {
            return world != null && new DimensionManagerWrapper(world).hasRaids() == value;
        }),
        WORLD_RAINING("world/raining", boolean.class, (value, player, world, livingEntity, itemStack, translateFunction) -> {
            return world != null && world.hasStorm() == value;
        }),
        WORLD_THUNDERING("world/thundering", boolean.class, (value, player, world, livingEntity, itemStack, translateFunction) -> {
            return world != null && world.isThundering() == value;
        }),
        WORLD_BIOME_ID("world/biome/id", String.class, (value, player, world, livingEntity, itemStack, translateFunction) -> {
            return world != null && livingEntity != null && world.getBiome(livingEntity.getLocation()).getKey().toString().equals(value);
        }),
        WORLD_BIOME_PRECIPITATION("world/biome/precipitation", BiomePrecipitation.class, (value, player, world, livingEntity, itemStack, translateFunction) -> {
            if (world == null || livingEntity == null) {
                return false;
            }
            return WorldUtils.getPrecipitation(livingEntity.getLocation()).equals(value);
        }),
        WORLD_BIOME_TEMPERATURE("world/biome/temperature", Range.class, (value, player, world, livingEntity, itemStack, translateFunction) -> {
            if (world == null || livingEntity == null) {
                return false;
            }
            Location location = livingEntity.getLocation();
            try {
                return value.contains(world.getTemperature(location.getBlockX(), location.getBlockY(), location.getBlockZ()));
            } catch (Throwable e) {
                return value.contains(world.getTemperature(location.getBlockX(), location.getBlockZ()));
            }
        }),
        WORLD_BIOME_DOWNFALL("world/biome/downfall", Range.class, (value, player, world, livingEntity, itemStack, translateFunction) -> {
            if (world == null || livingEntity == null) {
                return false;
            }
            Location location = livingEntity.getLocation();
            try {
                return value.contains(world.getHumidity(location.getBlockX(), location.getBlockY(), location.getBlockZ()));
            } catch (Throwable e) {
                return value.contains(world.getHumidity(location.getBlockX(), location.getBlockZ()));
            }
        }),
        ENTITY_NBT("entity/nbt", JSONObject.class, (value, player, world, livingEntity, itemStack, translateFunction) -> {
            if (livingEntity == null) {
                return false;
            }
            String nbt = NBTEditor.getNBTCompound(livingEntity).toJson();
            CompoundTag compoundTag = (CompoundTag) NBTParsingUtils.fromSNBT(nbt);
            return ChimeUtils.matchesJsonObject(value, compoundTag);
        }),
        ENTITY_X("entity/x", Range.class, (value, player, world, livingEntity, itemStack, translateFunction) -> {
            return livingEntity != null && value.contains(livingEntity.getLocation().getX());
        }),
        ENTITY_Y("entity/y", Range.class, (value, player, world, livingEntity, itemStack, translateFunction) -> {
            return livingEntity != null && value.contains(livingEntity.getLocation().getY());
        }),
        ENTITY_Z("entity/z", Range.class, (value, player, world, livingEntity, itemStack, translateFunction) -> {
            return livingEntity != null && value.contains(livingEntity.getLocation().getZ());
        }),
        ENTITY_LIGHT("entity/light", Range.class, (value, player, world, livingEntity, itemStack, translateFunction) -> {
            return livingEntity != null && value.contains(livingEntity.getLocation().getBlock().getLightLevel());
        }),
        ENTITY_BLOCK_LIGHT("entity/block_light", Range.class, (value, player, world, livingEntity, itemStack, translateFunction) -> {
            return livingEntity != null && value.contains(livingEntity.getLocation().getBlock().getLightFromBlocks());
        }),
        ENTITY_SKY_LIGHT("entity/sky_light", Range.class, (value, player, world, livingEntity, itemStack, translateFunction) -> {
            return livingEntity != null && value.contains(livingEntity.getLocation().getBlock().getLightFromSky());
        }),
        ENTITY_CAN_SEE_SKY("entity/can_see_sky", boolean.class, (value, player, world, livingEntity, itemStack, translateFunction) -> {
            if (livingEntity == null) {
                return false;
            }
            Location location = livingEntity.getEyeLocation();
            return (livingEntity.getWorld().getHighestBlockYAt(location) <= location.getY()) == value;
        }),
        ENTITY_HAND("entity/hand", ItemInHand.class, (value, player, world, livingEntity, itemStack, translateFunction) -> {
            if (livingEntity == null) {
                if (player == null) {
                    return false;
                }
                boolean mainhand = Objects.equals(player.getEquipment().getItemInMainHand(), itemStack);
                boolean offhand = Objects.equals(player.getEquipment().getItemInOffHand(), itemStack);
                switch (value) {
                    case MAIN:
                        return mainhand;
                    case OFF:
                        return offhand;
                    case EITHER:
                        return mainhand || offhand;
                    case NEITHER:
                        return !mainhand && !offhand;
                }
            } else {
                boolean mainhand = Objects.equals(livingEntity.getEquipment().getItemInMainHand(), itemStack);
                boolean offhand = Objects.equals(livingEntity.getEquipment().getItemInOffHand(), itemStack);
                switch (value) {
                    case MAIN:
                        return mainhand;
                    case OFF:
                        return offhand;
                    case EITHER:
                        return mainhand || offhand;
                    case NEITHER:
                        return !mainhand && !offhand;
                }
            }
            return false;
        }),
        ENTITY_SLOT("entity/slot", EquipmentSlot.class, (value, player, world, livingEntity, itemStack, translateFunction) -> {
            if (livingEntity == null) {
                if (player == null) {
                    return false;
                }
                switch (value) {
                    case HEAD:
                        return Objects.equals(player.getEquipment().getHelmet(), itemStack);
                    case CHEST:
                        return Objects.equals(player.getEquipment().getChestplate(), itemStack);
                    case LEGS:
                        return Objects.equals(player.getEquipment().getLeggings(), itemStack);
                    case FEET:
                        return Objects.equals(player.getEquipment().getBoots(), itemStack);
                }
            } else {
                switch (value) {
                    case HEAD:
                        return Objects.equals(livingEntity.getEquipment().getHelmet(), itemStack);
                    case CHEST:
                        return Objects.equals(livingEntity.getEquipment().getChestplate(), itemStack);
                    case LEGS:
                        return Objects.equals(livingEntity.getEquipment().getLeggings(), itemStack);
                    case FEET:
                        return Objects.equals(livingEntity.getEquipment().getBoots(), itemStack);
                }
            }
            return false;
        }),
        ENTITY_TARGET("entity/target", TargetType.class, (value, player, world, livingEntity, itemStack, translateFunction) -> {
            if (!(livingEntity instanceof Player)) {
                return false;
            }
            RayTraceResult result = livingEntity.rayTraceBlocks(5.0, FluidCollisionMode.NEVER);
            switch (value) {
                case BLOCK:
                    return result.getHitBlock() != null;
                case ENTITY:
                    return result.getHitEntity() != null;
                case MISS:
                    return result.getHitEntity() == null && result.getHitBlock() == null;
            }
            return false;
        }),
        ENTITY_TARGET_BLOCK_ID("entity/target_block/id", String.class, (value, player, world, livingEntity, itemStack, translateFunction) -> {
            if (livingEntity == null) {
                return false;
            }
            if (!value.contains(":")) {
                value = "minecraft:" + value;
            }
            RayTraceResult result = livingEntity.rayTraceBlocks(5.0, FluidCollisionMode.NEVER);
            if (result.getHitBlock() == null) {
                return value.equals("minecraft:air");
            }
            Material material = result.getHitBlock().getType();
            if (value.startsWith("#")) {
                value = value.substring(1);
                for (org.bukkit.Tag<Material> tag : Bukkit.getTags(org.bukkit.Tag.REGISTRY_BLOCKS, Material.class)) {
                    if (value.equals(tag.getKey().toString())) {
                        return tag.isTagged(material);
                    }
                }
                return false;
            } else {
                return value.equals(material.getKey().toString());
            }
        }),
        ENTITY_TARGET_BLOCK_CAN_MINE("entity/target_block/can_mine", boolean.class, (value, player, world, livingEntity, itemStack, translateFunction) -> {
            if (livingEntity == null) {
                return false;
            }
            RayTraceResult result = livingEntity.rayTraceBlocks(5.0, FluidCollisionMode.NEVER);
            if (result.getHitBlock() == null) {
                return false;
            }
            return result.getHitBlock().isPreferredTool(itemStack);
        }),
        ENTITY_TARGET_ENTITY_ID("entity/target_entity/id", String.class, (value, player, world, livingEntity, itemStack, translateFunction) -> {
            if (livingEntity == null) {
                return false;
            }
            if (!value.contains(":")) {
                value = "minecraft:" + value;
            }
            RayTraceResult result = livingEntity.rayTraceBlocks(5.0, FluidCollisionMode.NEVER);
            if (result.getHitEntity() == null) {
                return false;
            }
            return result.getHitEntity().getType().getKey().toString().equals(value);
        }),
        ENTITY_TARGET_ENTITY_NBT("entity/target_entity/nbt", JSONObject.class, (value, player, world, livingEntity, itemStack, translateFunction) -> {
            if (livingEntity == null) {
                return false;
            }
            RayTraceResult result = livingEntity.rayTraceBlocks(5.0, FluidCollisionMode.NEVER);
            if (result.getHitEntity() == null) {
                return false;
            }
            String nbt = NBTEditor.getNBTCompound(result.getHitEntity()).toJson();
            CompoundTag compoundTag = (CompoundTag) NBTParsingUtils.fromSNBT(nbt);
            return ChimeUtils.matchesJsonObject(value, compoundTag);
        });

        private String key;
        private String[] sectionedKeys;
        private Class<?> valueType;
        private ChimeOverridePredicate<Object> predicate;

        <T> ChimeModelOverrideType(String key, Class<T> valueType, ChimeOverridePredicate<T> predicate) {
            this.key = key;
            this.sectionedKeys = key.split("/");
            this.valueType = valueType;
            this.predicate = (ChimeOverridePredicate<Object>) predicate;
        }

        public String getKey() {
            return key;
        }

        public Class<?> getValueType() {
            return valueType;
        }

        public ChimeOverridePredicate<Object> getPredicate() {
            return predicate;
        }

        public boolean test(Object value, OfflineICPlayer player, World world, LivingEntity entity, ItemStack itemStack, UnaryOperator<String> translateFunction) {
            try {
                return predicate.test(value, player, world, entity, itemStack, translateFunction);
            } catch (Throwable e) {
                return false;
            }
        }

        public static ChimeModelOverrideType fromKeys(String... keys) {
            for (ChimeModelOverrideType type : values()) {
                if (keysMatch(type.sectionedKeys, keys)) {
                    return type;
                }
            }
            return null;
        }

        private static boolean keysMatch(String[] source, String[] target) {
            if (source.length != target.length) {
                return false;
            }
            for (int i = 0; i < source.length; i++) {
                if (!source[i].equalsIgnoreCase(target[i])) {
                    return false;
                }
            }
            return true;
        }

        public static ChimeModelOverrideType fromKey(String key) {
            String[] sections = key.split("/");
            return fromKeys(sections);
        }

    }

    @FunctionalInterface
    public interface ChimeOverridePredicate<T> {

        boolean test(T t, OfflineICPlayer player, World world, LivingEntity entity, ItemStack itemStack, UnaryOperator<String> translateFunction) throws Throwable;

    }

}
