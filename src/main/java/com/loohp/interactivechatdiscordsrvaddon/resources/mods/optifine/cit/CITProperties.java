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

package com.loohp.interactivechatdiscordsrvaddon.resources.mods.optifine.cit;

import com.loohp.interactivechat.InteractiveChat;
import com.loohp.interactivechat.libs.com.cryptomorin.xseries.XMaterial;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.Component;
import com.loohp.interactivechat.libs.net.querz.nbt.tag.ByteTag;
import com.loohp.interactivechat.libs.net.querz.nbt.tag.CompoundTag;
import com.loohp.interactivechat.libs.net.querz.nbt.tag.DoubleTag;
import com.loohp.interactivechat.libs.net.querz.nbt.tag.FloatTag;
import com.loohp.interactivechat.libs.net.querz.nbt.tag.IntTag;
import com.loohp.interactivechat.libs.net.querz.nbt.tag.ListTag;
import com.loohp.interactivechat.libs.net.querz.nbt.tag.LongTag;
import com.loohp.interactivechat.libs.net.querz.nbt.tag.ShortTag;
import com.loohp.interactivechat.libs.net.querz.nbt.tag.StringTag;
import com.loohp.interactivechat.libs.net.querz.nbt.tag.Tag;
import com.loohp.interactivechat.utils.InteractiveChatComponentSerializer;
import com.loohp.interactivechat.utils.ItemNBTUtils;
import com.loohp.interactivechat.utils.NBTParsingUtils;
import com.loohp.interactivechat.utils.XMaterialUtils;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.IntegerRange;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.PercentageOrIntegerRange;
import com.loohp.interactivechatdiscordsrvaddon.resources.ResourceLoadingException;
import com.loohp.interactivechatdiscordsrvaddon.resources.ResourcePackFile;
import com.loohp.interactivechatdiscordsrvaddon.resources.mods.optifine.OptifineUtils;
import com.loohp.interactivechatdiscordsrvaddon.resources.mods.optifine.cit.EnchantmentProperties.OpenGLBlending;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.function.UnaryOperator;

public abstract class CITProperties {

    @SuppressWarnings("deprecation")
    public static CITProperties fromProperties(ResourcePackFile file, Properties properties) {
        String name = file.getName();
        if (name.contains(".")) {
            name = name.substring(0, name.lastIndexOf("."));
        }
        int weight = Integer.parseInt(properties.getProperty("weight", "0"));
        Set<XMaterial> items = new HashSet<>();
        String itemsStr = properties.getProperty("items");
        if (itemsStr == null) {
            itemsStr = properties.getProperty("matchItems");
        }
        if (itemsStr != null) {
            for (String section : itemsStr.split(" ")) {
                if (section.contains(":")) {
                    section = section.substring(section.indexOf(":") + 1);
                }
                Optional<XMaterial> optMaterial = XMaterial.matchXMaterial(section);
                if (optMaterial.isPresent()) {
                    items.add(optMaterial.get());
                } else {
                    XMaterial.matchXMaterial(name).ifPresent(items::add);
                }
            }
        }
        String stackSizeStr = properties.getProperty("stackSize");
        IntegerRange stackSize = new IntegerRange(Integer.MIN_VALUE, Integer.MAX_VALUE);
        if (stackSizeStr != null) {
            stackSize = new IntegerRange(stackSizeStr);
        }
        String damageStr = properties.getProperty("damage");
        PercentageOrIntegerRange damage = new PercentageOrIntegerRange(Integer.MIN_VALUE, Integer.MAX_VALUE, false);
        if (damageStr != null) {
            damage = new PercentageOrIntegerRange(damageStr);
        }
        String damageMaskStr = properties.getProperty("damageMask");
        int damageMask = Integer.MIN_VALUE;
        if (damageMaskStr != null) {
            damageMask = Integer.parseInt(damageMaskStr);
        }
        String handStr = properties.getProperty("hand");
        EquipmentSlot hand = null;
        if (handStr != null) {
            if (handStr.equals("main")) {
                hand = EquipmentSlot.HAND;
            } else if (handStr.equals("off")) {
                hand = EquipmentSlot.OFF_HAND;
            }
        }
        Map<Enchantment, IntegerRange> enchantments = new LinkedHashMap<>();
        String enchantmentsStr = properties.getProperty("enchantments");
        String enchantmentLevelsStr = properties.getProperty("enchantmentLevels");
        if (enchantmentsStr != null) {
            String[] levelsSection = enchantmentLevelsStr == null ? new String[0] : enchantmentLevelsStr.split(" ");
            int i = 0;
            for (String section : enchantmentsStr.split(" ")) {
                Enchantment enchantment;
                try {
                    enchantment = Enchantment.getByKey(org.bukkit.NamespacedKey.fromString(section));
                } catch (Throwable e) {
                    enchantment = Enchantment.getByName(section);
                }
                IntegerRange level = new IntegerRange(1, Integer.MAX_VALUE);
                if (i < levelsSection.length) {
                    level = new IntegerRange(levelsSection[i]);
                }
                enchantments.put(enchantment, level);
                i++;
            }
        }

        Map<String, CITStringMatcher> nbtMatch = new HashMap<>();
        for (Entry<Object, Object> entry : properties.entrySet()) {
            String key = (String) entry.getKey();
            if (key.startsWith("nbt")) {
                String path = key.substring(key.length() > 3 ? 4 : 3);
                String value = (String) entry.getValue();
                CITStringMatcher matcher;
                if (value.startsWith("regex:")) {
                    matcher = new CITStringMatcher.RegexMatcher(value.substring(6));
                } else if (value.startsWith("iregex:")) {
                    matcher = new CITStringMatcher.IRegexMatcher(value.substring(7));
                } else if (value.startsWith("pattern:")) {
                    matcher = new CITStringMatcher.PatternMatcher(value.substring(8));
                } else if (value.startsWith("ipattern:")) {
                    matcher = new CITStringMatcher.IPatternMatcher(value.substring(9));
                } else {
                    matcher = new CITStringMatcher.DirectMatcher(value);
                }
                nbtMatch.put(path, matcher);
            }
        }

        String type = properties.getProperty("type", "item");
        if (type.equalsIgnoreCase("item")) {
            Map<String, String> models = new HashMap<>();
            Map<String, String> textures = new HashMap<>();
            for (Entry<Object, Object> entry : properties.entrySet()) {
                String key = (String) entry.getKey();
                if (key.startsWith("texture")) {
                    String path = key.substring(key.length() > 7 ? 8 : 7);
                    String value = (String) entry.getValue();
                    if (!value.endsWith(".png")) {
                        value += ".png";
                    }
                    textures.put(path, value);
                } else if (key.startsWith("model")) {
                    String path = key.substring(key.length() > 5 ? 6 : 5);
                    String value = (String) entry.getValue();
                    if (!value.endsWith(".json")) {
                        value += ".json";
                    }
                    models.put(path, value);
                }
            }
            if (models.isEmpty() && textures.isEmpty()) {
                ResourcePackFile defModel = file.getParentFile().getChild(name + ".json");
                if (defModel.exists()) {
                    String defaultPath = defModel.getAbsolutePath();
                    defaultPath = defaultPath.substring(defaultPath.indexOf("assets"));
                    models.put("", defaultPath);
                } else {
                    ResourcePackFile defTexture = file.getParentFile().getChild(name + ".png");
                    if (defTexture.exists()) {
                        String defaultPath = defTexture.getAbsolutePath();
                        defaultPath = defaultPath.substring(defaultPath.indexOf("assets"));
                        textures.put("", defaultPath);
                    }
                }
            }
            return new ItemProperties(weight, items, stackSize, damage, damageMask, hand, enchantments, nbtMatch, models, textures);
        } else if (type.equalsIgnoreCase("armor")) {
            Map<String, String> textures = new HashMap<>();
            for (Entry<Object, Object> entry : properties.entrySet()) {
                String key = (String) entry.getKey();
                if (key.startsWith("texture")) {
                    String path = key.substring(key.length() > 7 ? 8 : 7);
                    String value = (String) entry.getValue();
                    if (!value.endsWith(".png")) {
                        value += ".png";
                    }
                    textures.put(path, value);
                }
            }
            return new ArmorProperties(weight, items, stackSize, damage, damageMask, hand, enchantments, nbtMatch, textures);
        } else if (type.equalsIgnoreCase("elytra")) {
            String texture = properties.getProperty("texture");
            if (texture == null) {
                ResourcePackFile defTexture = file.getParentFile().getChild(name + ".png");
                if (defTexture.exists()) {
                    String defaultPath = defTexture.getAbsolutePath();
                    defaultPath = defaultPath.substring(defaultPath.indexOf("assets"));
                    texture = defaultPath;
                }
            } else {
                if (!texture.endsWith(".png")) {
                    texture += ".png";
                }
            }
            return new ElytraProperties(weight, items, stackSize, damage, damageMask, hand, enchantments, nbtMatch, texture);
        } else if (type.equalsIgnoreCase("enchantment")) {
            String texture = properties.getProperty("texture");
            if (texture == null) {
                ResourcePackFile defTexture = file.getParentFile().getChild(name + ".png");
                if (defTexture.exists()) {
                    String defaultPath = defTexture.getAbsolutePath();
                    defaultPath = defaultPath.substring(defaultPath.indexOf("assets"));
                    texture = defaultPath;
                }
            } else {
                if (!texture.endsWith(".png")) {
                    texture += ".png";
                }
            }
            int layer = Integer.parseInt(properties.getProperty("layer", "0"));
            double speed = Double.parseDouble(properties.getProperty("speed", "1.0"));
            double rotation = Double.parseDouble(properties.getProperty("rotation", "0.0"));
            double duration = Double.parseDouble(properties.getProperty("duration", "0.0"));
            String blend = properties.getProperty("blend", "add");
            OpenGLBlending openGLBlending = OpenGLBlending.ADD;
            try {
                openGLBlending = OpenGLBlending.fromString(blend);
            } catch (Throwable e) {
                new ResourceLoadingException("Invalid blend mode \"" + blend + "\" in " + file.getAbsolutePath(), e).printStackTrace();
            }
            return new EnchantmentProperties(weight, items, stackSize, damage, damageMask, hand, enchantments, nbtMatch, layer, speed, rotation, duration, openGLBlending, texture);
        }
        throw new IllegalArgumentException("Invalid CIT property type \"" + type + "\"");
    }

    protected int weight;
    protected Set<XMaterial> items;
    protected IntegerRange stackSize;
    protected PercentageOrIntegerRange damage;
    protected int damageMask;
    protected EquipmentSlot hand;
    protected Map<Enchantment, IntegerRange> enchantments;
    protected Map<String, CITStringMatcher> nbtMatch;

    public CITProperties(int weight, Set<XMaterial> items, IntegerRange stackSize, PercentageOrIntegerRange damage, int damageMask, EquipmentSlot hand, Map<Enchantment, IntegerRange> enchantments, Map<String, CITStringMatcher> nbtMatch) {
        this.weight = weight;
        this.items = items;
        this.stackSize = stackSize;
        this.damage = damage;
        this.damageMask = damageMask;
        this.hand = hand;
        this.enchantments = enchantments;
        this.nbtMatch = nbtMatch;
    }

    public int getWeight() {
        return weight;
    }

    public Set<XMaterial> getItems() {
        return items;
    }

    public IntegerRange getStackSize() {
        return stackSize;
    }

    public PercentageOrIntegerRange getDamage() {
        return damage;
    }

    public int getDamageMask() {
        return damageMask;
    }

    public EquipmentSlot getHand() {
        return hand;
    }

    public Map<Enchantment, IntegerRange> getEnchantments() {
        return enchantments;
    }

    public Map<String, CITStringMatcher> getNbtMatch() {
        return nbtMatch;
    }

    @SuppressWarnings("deprecation")
    public boolean test(EquipmentSlot heldSlot, ItemStack itemStack, UnaryOperator<String> translateFunction) {
        if (itemStack == null || itemStack.getType().equals(Material.AIR)) {
            return false;
        }
        if (!items.contains(XMaterialUtils.matchXMaterial(itemStack))) {
            return false;
        }
        if (!stackSize.test(itemStack.getAmount())) {
            return false;
        }
        int maxDurability = itemStack.getType().getMaxDurability();
        int damage = 0;
        if (InteractiveChat.version.isLegacy()) {
            damage = itemStack.getDurability();
        } else {
            if (itemStack.hasItemMeta() && itemStack.getItemMeta() instanceof Damageable) {
                damage = ((Damageable) itemStack.getItemMeta()).getDamage();
            }
        }
        if (!this.damage.test(damage & damageMask, maxDurability)) {
            return false;
        }
        if (hand != null && !heldSlot.equals(hand)) {
            return false;
        }
        if (!enchantments.isEmpty() && enchantments.entrySet().stream().noneMatch(entry -> entry.getValue().test(itemStack.getEnchantmentLevel(entry.getKey())))) {
            return false;
        }
        try {
            String nbt = ItemNBTUtils.getNMSItemStackJson(itemStack);
            CompoundTag tag = (CompoundTag) NBTParsingUtils.fromSNBT(nbt);
            if (tag.containsKey("tag")) {
                tag = tag.getCompoundTag("tag");
                for (Entry<String, CITStringMatcher> entry : nbtMatch.entrySet()) {
                    String key = entry.getKey();
                    CITStringMatcher nbtValueMatcher = entry.getValue();
                    String[] paths = key.split("\\.");
                    Tag<?> subTag = tag;
                    try {
                        for (String path : paths) {
                            if (subTag instanceof CompoundTag) {
                                subTag = ((CompoundTag) subTag).get(path);
                            } else if (subTag instanceof ListTag<?>) {
                                subTag = ((ListTag<?>) subTag).get(Integer.parseInt(path));
                            }
                        }
                    } catch (Throwable e) {
                        return false;
                    }
                    if (subTag instanceof StringTag) {
                        String rawStringValue = ((StringTag) subTag).getValue();
                        String jsonResultValue;
                        try {
                            Component component = InteractiveChatComponentSerializer.gson().deserialize(rawStringValue);
                            jsonResultValue = OptifineUtils.componentToString(component, translateFunction);
                        } catch (Throwable ignore) {
                            jsonResultValue = null;
                        }
                        if (!nbtValueMatcher.matches(rawStringValue) && (jsonResultValue == null || !nbtValueMatcher.matches(jsonResultValue))) {
                            return false;
                        }
                    } else if (subTag instanceof IntTag) {
                        if (Integer.parseInt(nbtValueMatcher.value()) != ((IntTag) subTag).asInt()) {
                            return false;
                        }
                    } else if (subTag instanceof LongTag) {
                        if (Long.parseLong(nbtValueMatcher.value()) != ((LongTag) subTag).asLong()) {
                            return false;
                        }
                    } else if (subTag instanceof ByteTag) {
                        if (Byte.parseByte(nbtValueMatcher.value()) != ((ByteTag) subTag).asByte()) {
                            return false;
                        }
                    } else if (subTag instanceof ShortTag) {
                        if (Short.parseShort(nbtValueMatcher.value()) != ((ShortTag) subTag).asShort()) {
                            return false;
                        }
                    } else if (subTag instanceof FloatTag) {
                        if (Float.parseFloat(nbtValueMatcher.value()) != ((FloatTag) subTag).asFloat()) {
                            return false;
                        }
                    } else if (subTag instanceof DoubleTag) {
                        if (Double.parseDouble(nbtValueMatcher.value()) != ((DoubleTag) subTag).asDouble()) {
                            return false;
                        }
                    } else {
                        return false;
                    }
                }
            } else {
                if (!nbtMatch.isEmpty()) {
                    return false;
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public abstract String getOverrideAsset(String path, String extension);

}
