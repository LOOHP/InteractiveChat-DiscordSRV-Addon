/*
 * This file is part of InteractiveChatDiscordSrvAddon.
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

package com.loohp.interactivechatdiscordsrvaddon.graphics;

import com.loohp.blockmodelrenderer.blending.BlendingModes;
import com.loohp.blockmodelrenderer.utils.ColorUtils;
import com.loohp.interactivechat.InteractiveChat;
import com.loohp.interactivechat.libs.com.cryptomorin.xseries.XMaterial;
import com.loohp.interactivechat.libs.net.kyori.adventure.key.Key;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.Component;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.format.NamedTextColor;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.format.TextColor;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import com.loohp.interactivechat.libs.org.apache.commons.lang3.math.Fraction;
import com.loohp.interactivechat.libs.org.json.simple.JSONArray;
import com.loohp.interactivechat.libs.org.json.simple.JSONObject;
import com.loohp.interactivechat.libs.org.json.simple.parser.JSONParser;
import com.loohp.interactivechat.objectholders.ICMaterial;
import com.loohp.interactivechat.objectholders.ICPlayer;
import com.loohp.interactivechat.objectholders.OfflineICPlayer;
import com.loohp.interactivechat.objectholders.ValuePairs;
import com.loohp.interactivechat.objectholders.ValueTrios;
import com.loohp.interactivechat.utils.ComponentStyling;
import com.loohp.interactivechat.utils.CustomStringUtils;
import com.loohp.interactivechat.utils.FilledMapUtils;
import com.loohp.interactivechat.utils.HTTPRequestUtils;
import com.loohp.interactivechat.utils.HashUtils;
import com.loohp.interactivechat.utils.InteractiveChatComponentSerializer;
import com.loohp.interactivechat.utils.ItemNBTUtils;
import com.loohp.interactivechat.utils.MCVersion;
import com.loohp.interactivechat.utils.SkinUtils;
import com.loohp.interactivechatdiscordsrvaddon.InteractiveChatDiscordSrvAddon;
import com.loohp.interactivechatdiscordsrvaddon.debug.Debug;
import com.loohp.interactivechatdiscordsrvaddon.nms.NMSAddon;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.AdvancementType;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.CustomModelData;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.PaintingVariant;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.SteppedIntegerRange;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.TintColorProvider;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.ToolTipComponent;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.ToolTipComponent.ToolTipType;
import com.loohp.interactivechatdiscordsrvaddon.registry.ResourceRegistry;
import com.loohp.interactivechatdiscordsrvaddon.resources.CacheObject;
import com.loohp.interactivechatdiscordsrvaddon.resources.CustomItemTextureRegistry;
import com.loohp.interactivechatdiscordsrvaddon.resources.ICacheManager;
import com.loohp.interactivechatdiscordsrvaddon.resources.ModelLayer;
import com.loohp.interactivechatdiscordsrvaddon.resources.ModelRenderer;
import com.loohp.interactivechatdiscordsrvaddon.resources.ModelRenderer.PlayerModelItem;
import com.loohp.interactivechatdiscordsrvaddon.resources.ModelRenderer.PlayerModelItemPosition;
import com.loohp.interactivechatdiscordsrvaddon.resources.ModelRenderer.RawEnchantmentGlintData;
import com.loohp.interactivechatdiscordsrvaddon.resources.ModelRenderer.RenderResult;
import com.loohp.interactivechatdiscordsrvaddon.resources.ResourceManager;
import com.loohp.interactivechatdiscordsrvaddon.resources.definitions.equipment.EquipmentModelDefinition;
import com.loohp.interactivechatdiscordsrvaddon.resources.fonts.MinecraftFont.FontRenderResult;
import com.loohp.interactivechatdiscordsrvaddon.resources.languages.SpecificTranslateFunction;
import com.loohp.interactivechatdiscordsrvaddon.resources.models.ModelDisplay.ModelDisplayPosition;
import com.loohp.interactivechatdiscordsrvaddon.resources.mods.optifine.cit.EnchantmentProperties.OpenGLBlending;
import com.loohp.interactivechatdiscordsrvaddon.resources.textures.EnchantmentGlintType;
import com.loohp.interactivechatdiscordsrvaddon.resources.textures.GeneratedTextureResource;
import com.loohp.interactivechatdiscordsrvaddon.resources.textures.TextureManager;
import com.loohp.interactivechatdiscordsrvaddon.resources.textures.TextureMeta;
import com.loohp.interactivechatdiscordsrvaddon.resources.textures.TextureProperties;
import com.loohp.interactivechatdiscordsrvaddon.resources.textures.TextureResource;
import com.loohp.interactivechatdiscordsrvaddon.utils.AnimatedTextureUtils;
import com.loohp.interactivechatdiscordsrvaddon.utils.ArmorUtils;
import com.loohp.interactivechatdiscordsrvaddon.utils.BundleUtils;
import com.loohp.interactivechatdiscordsrvaddon.utils.ComponentStringUtils;
import com.loohp.interactivechatdiscordsrvaddon.utils.ComponentStringUtils.CharacterLengthProviderData;
import com.loohp.interactivechatdiscordsrvaddon.utils.ContainerTitlePrintingFunction;
import com.loohp.interactivechatdiscordsrvaddon.utils.ItemRenderUtils;
import com.loohp.interactivechatdiscordsrvaddon.utils.ItemRenderUtils.ItemStackProcessResult;
import com.loohp.interactivechatdiscordsrvaddon.utils.KeyUtils;
import com.loohp.interactivechatdiscordsrvaddon.utils.ModelUtils;
import com.loohp.interactivechatdiscordsrvaddon.utils.TranslationKeyUtils;
import com.loohp.interactivechatdiscordsrvaddon.wrappers.ItemMapWrapper;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.inventory.meta.BundleMeta;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.map.MapCursor;
import org.bukkit.map.MapPalette;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings("deprecation")
public class ImageGeneration {

    public static final int MAP_ICON_PER_ROLE = InteractiveChat.version.isLegacy() ? 4 : 16;
    public static final int MAP_SIZE = 1120;
    public static final int SPACING = 36;
    public static final int DEFAULT_ITEM_RENDER_SIZE = 32;
    public static final double ITEM_AMOUNT_TEXT_DARKEN_FACTOR = 75.0 / 255.0;
    public static final Color ENCHANTMENT_GLINT_LEGACY_COLOR = new Color(164, 84, 255);
    public static final String PLAYER_CAPE_CACHE_KEY = "PlayerCapeTexture";
    public static final String PLAYER_SKIN_CACHE_KEY = "PlayerSkinTexture";
    public static final String INVENTORY_CACHE_KEY = "Inventory";
    public static final String PLAYER_INVENTORY_CACHE_KEY = "PlayerInventory";
    public static final int TABLIST_SINGLE_COLUMN_LIMIT = 20;
    public static final int TABLIST_PLAYER_DISPLAY_LIMIT = 80;
    public static final int TABLIST_INTERNAL_HEIGHT = 146;
    public static final Color TABLIST_BACKGROUND = new Color(68, 68, 68);
    public static final Color TABLIST_PLAYER_BACKGROUND = new Color(107, 107, 107);
    public static final TextColor INVENTORY_DEFAULT_FONT_COLOR = TextColor.color(4210752);
    public static final int BOOK_LINE_LIMIT = 230;
    public static final int BOOK_MAX_LINES = 14;
    public static final Color TOOLTIP_BACKGROUND_COLOR = new Color(-267386864, true);
    public static final Color TOOLTIP_OUTLINE_TOP_COLOR = new Color(1347420415, true);
    public static final Color TOOLTIP_OUTLINE_BOTTOM_COLOR = new Color(1344798847, true);
    public static final String OPTIFINE_CAPE_URL = "https://optifine.net/capes/%s.png";
    public static final String PLAYER_INFO_URL = "https://sessionserver.mojang.com/session/minecraft/profile/%s";
    public static final Color BUNDLE_WEIGHT_OUTLINE_COLOR = new Color(55, 55, 55);
    public static final Color BUNDLE_WEIGHT_COLOR = new Color(85, 85, 255);
    public static final Color BUNDLE_WEIGHT_FULL_COLOR = new Color(255, 85, 85);

    private static Supplier<ResourceManager> resourceManager = () -> InteractiveChatDiscordSrvAddon.plugin.getResourceManager();
    private static Supplier<MCVersion> version = () -> InteractiveChat.version;
    private static Supplier<String> language = () -> InteractiveChatDiscordSrvAddon.plugin.language;
    private static Supplier<SpecificTranslateFunction> translateFunction = () -> resourceManager.get().getLanguageManager().getTranslateFunction().ofLanguage(language.get());

    public static BufferedImage getMissingImage(int width, int length) {
        return TextureManager.getMissingImage(width, length);
    }

    public static BufferedImage getRawEnchantedImage(TextureResource tintResource, BufferedImage source, int tick) {
        BufferedImage tintOriginal = tintResource.getTexture();
        if (version.get().isOlderOrEqualTo(MCVersion.V1_14)) {
            BufferedImage tinted = ImageUtils.changeColorTo(ImageUtils.copyImage(tintOriginal), ENCHANTMENT_GLINT_LEGACY_COLOR);
            tintOriginal = ImageUtils.multiply(tintOriginal, tinted);
        }
        if (tintResource.hasTextureMeta()) {
            TextureMeta meta = tintResource.getTextureMeta();
            if (meta.hasProperties()) {
                TextureProperties properties = meta.getProperties();
                if (properties.isBlur()) {
                    tintOriginal = ImageUtils.applyGaussianBlur(tintOriginal);
                }
            }
            if (meta.hasAnimation()) {
                tintOriginal = AnimatedTextureUtils.getCurrentAnimationFrame(tintOriginal, meta.getAnimation(), 0);
            }
        }

        BufferedImage tintImage = new BufferedImage(source.getWidth(), source.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g3 = tintImage.createGraphics();
        g3.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        if (tintImage.getHeight() < tintImage.getWidth()) {
            tintOriginal = ImageUtils.resizeImageFillWidth(tintOriginal, tintImage.getWidth() * 4);
        } else {
            tintOriginal = ImageUtils.resizeImageFillHeight(tintOriginal, tintImage.getHeight() * 4);
        }
        g3.drawImage(tintOriginal, 0, 0, null);
        g3.dispose();

        return AnimatedTextureUtils.getEnchantedImageFrame(tintImage, tick, 1.0, 1.0);
    }

    public static BufferedImage getEnchantedImage(List<ValuePairs<TextureResource, OpenGLBlending>> tintResources, BufferedImage source, int tick) {
        for (ValuePairs<TextureResource, OpenGLBlending> tintResource : tintResources) {
            source = getEnchantedImage(tintResource.getFirst(), source, BlendingUtils.convert(tintResource.getSecond()), tick);
        }
        return source;
    }

    public static BufferedImage getEnchantedImage(TextureResource tintResource, BufferedImage source, BlendingModes blendingModes, int tick) {
        BufferedImage overlay = getRawEnchantedImage(tintResource, source, tick);
        return ImageUtils.transformRGB(source, (x, y, colorValue) -> {
            return ColorUtils.composite(overlay.getRGB(x, y), colorValue, blendingModes);
        });
    }

    public static List<ValuePairs<TextureResource, OpenGLBlending>> getDefaultEnchantmentTint(EnchantmentGlintType type) {
        return Collections.singletonList(new ValuePairs<>(resourceManager.get().getTextureManager().getTexture(type.getResourceLocation()), OpenGLBlending.GLINT));
    }

    public static BufferedImage getAdvancementIcon(ItemStack item, AdvancementType advancementType, boolean completed, OfflineICPlayer player) throws IOException {
        BufferedImage frame = ImageUtils.resizeImageAbs(getAdvancementFrame(advancementType, completed), 52, 52);
        BufferedImage itemImage = getSingleRawItemImage(item, player);
        Graphics2D g = frame.createGraphics();
        g.drawImage(itemImage, 10, 10, null);
        g.dispose();
        return frame;
    }

    public static BufferedImage getItemStackImage(ItemStack item, OfflineICPlayer player) throws IOException {
        return getItemStackImage(item, player, false);
    }

    public static BufferedImage getItemStackImage(ItemStack item, OfflineICPlayer player, int renderSize) throws IOException {
        return getItemStackImage(item, player, false, renderSize);
    }

    public static BufferedImage getItemStackImage(ItemStack item, OfflineICPlayer player, boolean alternateAir) throws IOException {
        return getItemStackImage(item, player, alternateAir, DEFAULT_ITEM_RENDER_SIZE);
    }

    public static BufferedImage getItemStackImage(ItemStack item, OfflineICPlayer player, boolean alternateAir, int renderSize) throws IOException {
        InteractiveChatDiscordSrvAddon.plugin.imageCounter.incrementAndGet();
        Debug.debug("ImageGeneration creating item stack image " + item);

        double scale = (double) renderSize / DEFAULT_ITEM_RENDER_SIZE;

        BufferedImage background = new BufferedImage((int) Math.round(36 * scale), (int) Math.round(36 * scale), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = background.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);


        BufferedImage itemImage;
        if (item == null || item.getType().equals(Material.AIR)) {
            if (alternateAir) {
                itemImage = resourceManager.get().getTextureManager().getTexture(ResourceRegistry.IC_BLOCK_TEXTURE_LOCATION + "air_alternate").getTexture(32, 32);
            } else {
                g.dispose();
                return background;
            }
        } else {
            itemImage = getSingleRawItemImage(item, player, renderSize);
        }

        g.drawImage(itemImage, 0, 0, null);
        g.dispose();

        return background;
    }

    public static BufferedImage getInventoryImage(Inventory inventory, OfflineICPlayer player) throws Exception {
        return getInventoryImage(inventory, null, player);
    }

    public static BufferedImage getInventoryImage(Inventory inventory, Component title, OfflineICPlayer player) throws Exception {
        InteractiveChatDiscordSrvAddon.plugin.imageCounter.incrementAndGet();
        InteractiveChatDiscordSrvAddon.plugin.inventoryImageCounter.incrementAndGet();
        Debug.debug("ImageGeneration creating inventory image of " + player.getName());

        String key = INVENTORY_CACHE_KEY + HashUtils.createSha1(title == null ? "Inventory" : InteractiveChatComponentSerializer.gson().serialize(title), inventory);
        if (!inventory.contains(XMaterial.COMPASS.parseMaterial()) && !inventory.contains(XMaterial.CLOCK.parseMaterial()) && Arrays.stream(inventory.getContents()).noneMatch(each -> each != null && !CustomModelData.isEmpty(NMSAddon.getInstance().getCustomModelData(each)))) {
            CacheObject<?> cache = resourceManager.get().getResourceRegistry(ICacheManager.IDENTIFIER, ICacheManager.class).getCache(key);
            if (cache != null) {
                return ImageUtils.copyImage((BufferedImage) cache.getObject());
            }
        }

        int rows = inventory.getSize() / 9;
        GenericContainerBackgroundResult result = getGenericContainerBackground(rows, (image, x, y, fontSize, defaultTextColor) -> {
            Component defaultColorTitle = title == null ? Component.translatable(TranslationKeyUtils.getDefaultContainerTitle()).color(defaultTextColor) : title.colorIfAbsent(defaultTextColor);
            return ImageUtils.printComponentShadowless(resourceManager.get(), image, defaultColorTitle, InteractiveChatDiscordSrvAddon.plugin.language, version.get().isLegacyRGB(), x, y, fontSize).getImage();
        });
        BufferedImage background = result.getBackgroundImage();

        BufferedImage target = new BufferedImage(background.getWidth(), background.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = target.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g.drawImage(background, 0, 0, null);

        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack item = inventory.getItem(i);
            if (item == null || item.getType().equals(Material.AIR)) {
                continue;
            }

            BufferedImage itemImage = getSingleRawItemImage(item, player);

            if (itemImage != null) {
                g.drawImage(itemImage, result.getExpandedX() + 18 + (SPACING * (i % 9)), result.getExpandedY() + 38 + (SPACING * (i / 9)), null);
            }
        }
        g.dispose();

        resourceManager.get().getResourceRegistry(ICacheManager.IDENTIFIER, ICacheManager.class).putCache(key, target);

        return target;
    }

    public static BufferedImage getPlayerInventoryImage(Inventory inventory, OfflineICPlayer player) throws Exception {
        EntityEquipment equipment = player.getEquipment();
        ItemStack rightHand = player.isRightHanded() ? player.getMainHandItem() : player.getOffHandItem();
        ItemStack leftHand = player.isRightHanded() ? player.getOffHandItem() : player.getMainHandItem();
        return getPlayerInventoryImage(inventory, rightHand, leftHand, equipment.getHelmet(), equipment.getChestplate(), equipment.getLeggings(), equipment.getBoots(), player);
    }

    public static BufferedImage getPlayerInventoryImage(Inventory inventory, ItemStack puppetRightHand, ItemStack puppetLeftHand, ItemStack puppetHelmet, ItemStack puppetChestplate, ItemStack puppetLeggings, ItemStack puppetBoots, OfflineICPlayer player) throws Exception {
        InteractiveChatDiscordSrvAddon.plugin.imageCounter.incrementAndGet();
        InteractiveChatDiscordSrvAddon.plugin.inventoryImageCounter.incrementAndGet();
        Debug.debug("ImageGeneration creating player inventory image of " + player.getName());

        BufferedImage background = resourceManager.get().getTextureManager().getTexture(ResourceRegistry.IC_GUI_TEXTURE_LOCATION + "player_inventory").getTexture(356, 336);

        Object playerInventoryData = player.getProperty("player_inventory");
        if (playerInventoryData != null && playerInventoryData instanceof BufferedImage) {
            BufferedImage playerBackground = ImageUtils.copyImage((BufferedImage) playerInventoryData);
            Object mask = player.getProperty("player_inventory_mask");
            if (mask == null) {
                background = playerBackground;
            } else {
                background = ImageUtils.combineWithBinMask(background, playerBackground, (byte[]) mask);
            }
        }

        String key = PLAYER_INVENTORY_CACHE_KEY + HashUtils.createSha1(player.isRightHanded(), player.getSelectedSlot(), 0, player.getUniqueId().toString(), inventory) + ImageUtils.hash(background);
        if (!inventory.contains(XMaterial.COMPASS.parseMaterial()) && !inventory.contains(XMaterial.CLOCK.parseMaterial()) && Arrays.stream(inventory.getContents()).noneMatch(each -> each != null && !CustomModelData.isEmpty(NMSAddon.getInstance().getCustomModelData(each)))) {
            CacheObject<?> cache = resourceManager.get().getResourceRegistry(ICacheManager.IDENTIFIER, ICacheManager.class).getCache(key);
            if (cache != null) {
                return ImageUtils.copyImage((BufferedImage) cache.getObject());
            }
        }

        BufferedImage target = new BufferedImage(background.getWidth(), background.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = target.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g.drawImage(background, 0, 0, null);

        int i = 0;
        //hotbar
        for (; i < 9; i++) {
            ItemStack item = inventory.getItem(i);
            if (item == null || item.getType().equals(Material.AIR)) {
                continue;
            }

            BufferedImage itemImage = getSingleRawItemImage(item, player);

            if (itemImage != null) {
                g.drawImage(itemImage, 18 + (SPACING * (i % 9)), 286 + (SPACING * (i / 9)), null);
            }
        }

        //inv
        for (; i < 36; i++) {
            ItemStack item = inventory.getItem(i);
            if (item == null || item.getType().equals(Material.AIR)) {
                continue;
            }

            BufferedImage itemImage = getSingleRawItemImage(item, player);

            if (itemImage != null) {
                g.drawImage(itemImage, 18 + (SPACING * (i % 9)), 170 + (SPACING * ((i - 9) / 9)), null);
            }
        }

        //boots
        ItemStack boots = inventory.getItem(i);
        if (boots == null || boots.getType().equals(Material.AIR)) {
            String resourceLocation = ResourceRegistry.RESOURCE_PACK_VERSION < 46 ? ResourceRegistry.ITEM_TEXTURE_LOCATION + "empty_armor_slot_boots" : ResourceRegistry.DEFAULT_SPRITE_LOCATION + "container/slot/boots";
            g.drawImage(resourceManager.get().getTextureManager().getTexture(resourceLocation).getTexture(32, 32), 18, 126 - (SPACING * (i - 36)), 32, 32, null);
        } else {
            BufferedImage itemImage = getSingleRawItemImage(boots, player);
            if (itemImage != null) {
                g.drawImage(itemImage, 18, 126 - (SPACING * (i - 36)), null);
            }
        }
        i++;

        //leggings
        ItemStack leggings = inventory.getItem(i);
        if (leggings == null || leggings.getType().equals(Material.AIR)) {
            String resourceLocation = ResourceRegistry.RESOURCE_PACK_VERSION < 46 ? ResourceRegistry.ITEM_TEXTURE_LOCATION + "empty_armor_slot_leggings" : ResourceRegistry.DEFAULT_SPRITE_LOCATION + "container/slot/leggings";
            g.drawImage(resourceManager.get().getTextureManager().getTexture(resourceLocation).getTexture(32, 32), 18, 126 - (SPACING * (i - 36)), 32, 32, null);
        } else {
            BufferedImage itemImage = getSingleRawItemImage(leggings, player);
            if (itemImage != null) {
                g.drawImage(itemImage, 18, 126 - (SPACING * (i - 36)), null);
            }
        }
        i++;

        //chestplate
        ItemStack chestplate = inventory.getItem(i);
        if (chestplate == null || chestplate.getType().equals(Material.AIR)) {
            String resourceLocation = ResourceRegistry.RESOURCE_PACK_VERSION < 46 ? ResourceRegistry.ITEM_TEXTURE_LOCATION + "empty_armor_slot_chestplate" : ResourceRegistry.DEFAULT_SPRITE_LOCATION + "container/slot/chestplate";
            g.drawImage(resourceManager.get().getTextureManager().getTexture(resourceLocation).getTexture(32, 32), 18, 126 - (SPACING * (i - 36)), 32, 32, null);
        } else {
            BufferedImage itemImage = getSingleRawItemImage(chestplate, player);
            if (itemImage != null) {
                g.drawImage(itemImage, 18, 126 - (SPACING * (i - 36)), null);
            }
        }
        i++;

        //helmet
        ItemStack helmet = inventory.getItem(i);
        if (helmet == null || helmet.getType().equals(Material.AIR)) {
            String resourceLocation = ResourceRegistry.RESOURCE_PACK_VERSION < 46 ? ResourceRegistry.ITEM_TEXTURE_LOCATION + "empty_armor_slot_helmet" : ResourceRegistry.DEFAULT_SPRITE_LOCATION + "container/slot/helmet";
            g.drawImage(resourceManager.get().getTextureManager().getTexture(resourceLocation).getTexture(32, 32), 18, 126 - (SPACING * (i - 36)), 32, 32, null);
        } else {
            BufferedImage itemImage = getSingleRawItemImage(helmet, player);
            if (itemImage != null) {
                g.drawImage(itemImage, 18, 126 - (SPACING * (i - 36)), null);
            }
        }
        i++;

        //offhand
        if (!version.get().isOld()) {
            ItemStack offhand = inventory.getItem(i);
            if (offhand == null || offhand.getType().equals(Material.AIR)) {
                String resourceLocation = ResourceRegistry.RESOURCE_PACK_VERSION < 46 ? ResourceRegistry.ITEM_TEXTURE_LOCATION + "empty_armor_slot_shield" : ResourceRegistry.DEFAULT_SPRITE_LOCATION + "container/slot/shield";
                g.drawImage(resourceManager.get().getTextureManager().getTexture(resourceLocation).getTexture(32, 32), 162, 126, 32, 32, null);
            } else {
                BufferedImage itemImage = getSingleRawItemImage(offhand, player);
                if (itemImage != null) {
                    g.drawImage(itemImage, 162, 126, null);
                }
            }
        }

        //puppet
        BufferedImage puppet = getFullBodyImage(player, puppetRightHand, puppetLeftHand, puppetHelmet, puppetChestplate, puppetLeggings, puppetBoots);
        g.setClip(54, 18, 98, 140);
        g.drawImage(puppet, 45, -10, null);

        g.dispose();

        resourceManager.get().getResourceRegistry(ICacheManager.IDENTIFIER, ICacheManager.class).putCache(key, target);

        return target;
    }

    private static BufferedImage getFullBodyImage(OfflineICPlayer player, ItemStack rightHand, ItemStack leftHand, ItemStack helmet, ItemStack chestplate, ItemStack leggings, ItemStack boots) throws IOException {
        InteractiveChatDiscordSrvAddon.plugin.imageCounter.incrementAndGet();
        Debug.debug("ImageGeneration creating puppet image of " + player.getName());

        World world;
        LivingEntity livingEntity;
        if (player.isOnline() && player.getPlayer().isLocal()) {
            livingEntity = player.getPlayer().getLocalPlayer();
            world = livingEntity.getWorld();
        } else {
            livingEntity = null;
            world = null;
        }

        BufferedImage skin = null;
        boolean slim = false;
        BufferedImage cape;
        try {
            JSONObject json;
            ICPlayer icPlayer = player.getPlayer();
            if (icPlayer != null && icPlayer.isLocal()) {
                json = (JSONObject) new JSONParser().parse(SkinUtils.getSkinJsonFromProfile(((ICPlayer) player).getLocalPlayer()));
            } else {
                json = (JSONObject) new JSONParser().parse(new String(Base64.getDecoder().decode(((JSONObject) ((JSONArray) HTTPRequestUtils.getJSONResponse(PLAYER_INFO_URL.replace("%s", player.getUniqueId().toString())).get("properties")).get(0)).get("value").toString())));
            }
            if (json == null) {
                cape = null;
            } else {
                try {
                    if (((JSONObject) json.get("textures")).containsKey("CAPE")) {
                        String url = (String) ((JSONObject) ((JSONObject) json.get("textures")).get("CAPE")).get("url");
                        CacheObject<?> cache = resourceManager.get().getResourceRegistry(ICacheManager.IDENTIFIER, ICacheManager.class).getCache(player.getUniqueId().toString() + url + PLAYER_CAPE_CACHE_KEY);
                        if (cache == null) {
                            cape = ImageUtils.downloadImage(url);
                            resourceManager.get().getResourceRegistry(ICacheManager.IDENTIFIER, ICacheManager.class).putCache(player.getUniqueId().toString() + url + PLAYER_CAPE_CACHE_KEY, cape);
                        } else {
                            cape = (BufferedImage) cache.getObject();
                        }
                    } else {
                        String url = OPTIFINE_CAPE_URL.replaceAll("%s", CustomStringUtils.escapeReplaceAllMetaCharacters(player.getName()));
                        CacheObject<?> cache = resourceManager.get().getResourceRegistry(ICacheManager.IDENTIFIER, ICacheManager.class).getCache(player.getUniqueId().toString() + url + PLAYER_CAPE_CACHE_KEY);
                        if (cache == null) {
                            try {
                                cape = ImageUtils.downloadImage(url);
                                resourceManager.get().getResourceRegistry(ICacheManager.IDENTIFIER, ICacheManager.class).putCache(player.getUniqueId().toString() + url + PLAYER_CAPE_CACHE_KEY, cape);
                            } catch (Throwable ignore) {
                                cape = null;
                            }
                        } else {
                            cape = (BufferedImage) cache.getObject();
                        }
                    }
                } catch (Throwable e) {
                    cape = null;
                }

                try {
                    String value = (String) ((JSONObject) ((JSONObject) json.get("textures")).get("SKIN")).get("url");
                    if (((JSONObject) ((JSONObject) json.get("textures")).get("SKIN")).containsKey("metadata")) {
                        slim = ((JSONObject) ((JSONObject) ((JSONObject) json.get("textures")).get("SKIN")).get("metadata")).get("model").toString().equals("slim");
                    }
                    CacheObject<?> cache = resourceManager.get().getResourceRegistry(ICacheManager.IDENTIFIER, ICacheManager.class).getCache(player.getUniqueId().toString() + value + PLAYER_SKIN_CACHE_KEY);
                    if (cache == null) {
                        skin = ImageUtils.downloadImage(value);
                        resourceManager.get().getResourceRegistry(ICacheManager.IDENTIFIER, ICacheManager.class).putCache(player.getUniqueId().toString() + value + PLAYER_SKIN_CACHE_KEY, skin);
                    } else {
                        skin = (BufferedImage) cache.getObject();
                    }
                    skin = ImageUtils.copyImage(skin);
                } catch (Throwable e1) {
                }
            }
        } catch (Exception e) {
            cape = null;
        }

        if (skin == null) {
            if (slim) {
                skin = resourceManager.get().getTextureManager().getTexture(ResourceRegistry.DEFAULT_SLIM_SKIN_LOCATION).getTexture();
            } else {
                skin = resourceManager.get().getTextureManager().getTexture(ResourceRegistry.DEFAULT_WIDE_SKIN_LOCATION).getTexture();
            }
        }

        BufferedImage elytraImage = null;
        BufferedImage image = new BufferedImage(556, 748, BufferedImage.TYPE_INT_ARGB);
        Map<String, TextureResource> providedTextures = new HashMap<>();
        Map<PlayerModelItemPosition, PlayerModelItem> modelItems = new HashMap<>();

        providedTextures.put(ResourceRegistry.SKIN_FULL_TEXTURE_PLACEHOLDER, new GeneratedTextureResource(resourceManager.get(), ModelUtils.convertToModernSkinTexture(skin)));

        ArmorUtils.ArmorTextureResult leggingsArmorResult = ArmorUtils.getArmorTexture(resourceManager.get(), leggings, EquipmentSlot.LEGS);
        if (leggingsArmorResult.hasArmorTexture()) {
            BufferedImage leggingsImage = null;
            for (EquipmentModelDefinition.EquipmentLayer layer : leggingsArmorResult.getLayers()) {
                Key key = KeyUtils.toKey(layer.getTexture());
                BufferedImage layerImage = resourceManager.get().getResourceRegistry(CustomItemTextureRegistry.IDENTIFIER, CustomItemTextureRegistry.class).getArmorOverrideTextures(key.value(), EquipmentSlot.LEGS, leggings, player, world, livingEntity, translateFunction.get()).map(t -> t.getTexture()).orElse(resourceManager.get().getTextureManager().getOptionalTexture(key.namespace() + ":" + ResourceRegistry.ARMOR_LEGGINGS_TEXTURE_LOCATION + key.value()).map(t -> t.getTexture()).orElse(null));
                if (layerImage != null) {
                    if (layer.isDyeable()) {
                        BufferedImage colorOverlay = ImageUtils.changeColorTo(ImageUtils.copyImage(layerImage), leggingsArmorResult.getDyeableColor(layer.getDyeable().getColorWhenUndyed(ResourceRegistry.DEFAULT_DYE_COLOR)));
                        layerImage = ImageUtils.multiply(layerImage, colorOverlay);
                    }
                    if (leggingsImage == null) {
                        leggingsImage = layerImage;
                    } else {
                        Graphics2D g2 = leggingsImage.createGraphics();
                        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
                        g2.drawImage(layerImage, 0, 0, null);
                        g2.dispose();
                    }
                }
            }
            if (leggingsImage != null) {
                ItemMeta meta = leggings.getItemMeta();
                if (InteractiveChat.version.isNewerOrEqualTo(MCVersion.V1_20) && meta instanceof ArmorMeta) {
                    ArmorTrim armorTrim = ((ArmorMeta) meta).getTrim();
                    if (armorTrim != null) {
                        String material = armorTrim.getMaterial().getKey().getKey();
                        String pattern = armorTrim.getPattern().getKey().getKey();
                        BufferedImage trim = resourceManager.get().getTextureManager().getTexture(ResourceRegistry.ARMOR_TRIM_LEGGINGS_LOCATION.replaceFirst("%s", pattern).replaceFirst("%s", material)).getTexture();
                        Graphics2D g2 = leggingsImage.createGraphics();
                        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
                        g2.drawImage(trim, 0, 0, leggingsImage.getWidth(), leggingsImage.getHeight(), null);
                        g2.dispose();
                    }
                }
                if (!leggings.getEnchantments().isEmpty()) {
                    leggingsImage = getEnchantedImage(resourceManager.get().getResourceRegistry(CustomItemTextureRegistry.IDENTIFIER, CustomItemTextureRegistry.class).getEnchantmentGlintOverrideTextures(EquipmentSlot.LEGS, leggings, () -> getDefaultEnchantmentTint(EnchantmentGlintType.ENTITY), translateFunction.get()), leggingsImage, 0);
                }
                providedTextures.put(ResourceRegistry.LEGGINGS_TEXTURE_PLACEHOLDER, new GeneratedTextureResource(resourceManager.get(), leggingsImage));
            }
        }

        ArmorUtils.ArmorTextureResult bootsArmorResult = ArmorUtils.getArmorTexture(resourceManager.get(), boots, EquipmentSlot.FEET);
        if (bootsArmorResult.hasArmorTexture()) {
            BufferedImage bootsImage = null;
            for (EquipmentModelDefinition.EquipmentLayer layer : bootsArmorResult.getLayers()) {
                Key key = KeyUtils.toKey(layer.getTexture());
                BufferedImage layerImage = resourceManager.get().getResourceRegistry(CustomItemTextureRegistry.IDENTIFIER, CustomItemTextureRegistry.class).getArmorOverrideTextures(key.value(), EquipmentSlot.FEET, leggings, player, world, livingEntity, translateFunction.get()).map(t -> t.getTexture()).orElse(resourceManager.get().getTextureManager().getOptionalTexture(key.namespace() + ":" + ResourceRegistry.ARMOR_TEXTURE_LOCATION + key.value()).map(t -> t.getTexture()).orElse(null));
                if (layerImage != null) {
                    if (layer.isDyeable()) {
                        BufferedImage colorOverlay = ImageUtils.changeColorTo(ImageUtils.copyImage(layerImage), bootsArmorResult.getDyeableColor(layer.getDyeable().getColorWhenUndyed(ResourceRegistry.DEFAULT_DYE_COLOR)));
                        layerImage = ImageUtils.multiply(layerImage, colorOverlay);
                    }
                    if (bootsImage == null) {
                        bootsImage = layerImage;
                    } else {
                        Graphics2D g2 = bootsImage.createGraphics();
                        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
                        g2.drawImage(layerImage, 0, 0, null);
                        g2.dispose();
                    }
                }
            }
            if (bootsImage != null) {
                ItemMeta meta = boots.getItemMeta();
                if (InteractiveChat.version.isNewerOrEqualTo(MCVersion.V1_20) && meta instanceof ArmorMeta) {
                    ArmorTrim armorTrim = ((ArmorMeta) meta).getTrim();
                    if (armorTrim != null) {
                        String material = armorTrim.getMaterial().getKey().getKey();
                        String pattern = armorTrim.getPattern().getKey().getKey();
                        BufferedImage trim = resourceManager.get().getTextureManager().getTexture(ResourceRegistry.ARMOR_TRIM_LOCATION.replaceFirst("%s", pattern).replaceFirst("%s", material)).getTexture();
                        Graphics2D g2 = bootsImage.createGraphics();
                        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
                        g2.drawImage(trim, 0, 0, bootsImage.getWidth(), bootsImage.getHeight(), null);
                        g2.dispose();
                    }
                }
                if (!boots.getEnchantments().isEmpty()) {
                    bootsImage = getEnchantedImage(resourceManager.get().getResourceRegistry(CustomItemTextureRegistry.IDENTIFIER, CustomItemTextureRegistry.class).getEnchantmentGlintOverrideTextures(EquipmentSlot.FEET, boots, () -> getDefaultEnchantmentTint(EnchantmentGlintType.ENTITY), translateFunction.get()), bootsImage, 0);
                }
                providedTextures.put(ResourceRegistry.BOOTS_TEXTURE_PLACEHOLDER, new GeneratedTextureResource(resourceManager.get(), bootsImage));
            }
        }

        ArmorUtils.ArmorTextureResult chestplateArmorResult;
        if (chestplate != null && ICMaterial.from(chestplate).isMaterial(XMaterial.ELYTRA)) {
            BufferedImage chestplateImage = new BufferedImage(150, 150, BufferedImage.TYPE_INT_ARGB);
            BufferedImage wing = null;
            wing = resourceManager.get().getResourceRegistry(CustomItemTextureRegistry.IDENTIFIER, CustomItemTextureRegistry.class).getElytraOverrideTextures(EquipmentSlot.CHEST, chestplate, translateFunction.get()).map(t -> t.getTexture()).orElse(cape == null ? resourceManager.get().getTextureManager().getTexture(ResourceRegistry.ENTITY_TEXTURE_LOCATION + "elytra").getTexture() : cape);
            if (wing.getWidth() % 64 != 0 || wing.getHeight() % 32 != 0) {
                int w = 0;
                int h = 0;
                while (w < wing.getWidth()) {
                    w += 64;
                    h += 32;
                }
                BufferedImage resize = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g3 = resize.createGraphics();
                g3.drawImage(wing, 0, 0, null);
                g3.dispose();
                wing = resize;
            }
            int scale = wing.getWidth() / 64;
            wing = ImageUtils.copyAndGetSubImage(wing, 34 * scale, 2 * scale, 12 * scale, 20 * scale);
            wing = ImageUtils.multiply(ImageUtils.resizeImage(wing, Math.pow(scale, -1) * 3.75), 0.7);
            BufferedImage leftWing = ImageUtils.rotateImageByDegrees(wing, 23.41);
            Graphics2D g3 = chestplateImage.createGraphics();
            g3.drawImage(leftWing, 0, 0, null);
            wing = ImageUtils.flipHorizontal(wing);
            BufferedImage rightWing = ImageUtils.rotateImageByDegrees(wing, 360.0 - 23.41);
            g3.drawImage(rightWing, 26, 0, null);
            g3.dispose();

            if (!chestplate.getEnchantments().isEmpty()) {
                chestplateImage = getEnchantedImage(resourceManager.get().getResourceRegistry(CustomItemTextureRegistry.IDENTIFIER, CustomItemTextureRegistry.class).getEnchantmentGlintOverrideTextures(EquipmentSlot.CHEST, chestplate, () -> getDefaultEnchantmentTint(EnchantmentGlintType.ENTITY), translateFunction.get()), chestplateImage, 0);
            }
            elytraImage = chestplateImage;
        } else if ((chestplateArmorResult = ArmorUtils.getArmorTexture(resourceManager.get(), chestplate, EquipmentSlot.CHEST)).hasArmorTexture()) {
            BufferedImage chestplateImage = null;
            for (EquipmentModelDefinition.EquipmentLayer layer : chestplateArmorResult.getLayers()) {
                Key key = KeyUtils.toKey(layer.getTexture());
                BufferedImage layerImage = resourceManager.get().getResourceRegistry(CustomItemTextureRegistry.IDENTIFIER, CustomItemTextureRegistry.class).getArmorOverrideTextures(key.value(), EquipmentSlot.CHEST, leggings, player, world, livingEntity, translateFunction.get()).map(t -> t.getTexture()).orElse(resourceManager.get().getTextureManager().getOptionalTexture(key.namespace() + ":" + ResourceRegistry.ARMOR_TEXTURE_LOCATION + key.value()).map(t -> t.getTexture()).orElse(null));
                if (layerImage != null) {
                    if (layer.isDyeable()) {
                        BufferedImage colorOverlay = ImageUtils.changeColorTo(ImageUtils.copyImage(layerImage), chestplateArmorResult.getDyeableColor(layer.getDyeable().getColorWhenUndyed(ResourceRegistry.DEFAULT_DYE_COLOR)));
                        layerImage = ImageUtils.multiply(layerImage, colorOverlay);
                    }
                    if (chestplateImage == null) {
                        chestplateImage = layerImage;
                    } else {
                        Graphics2D g2 = chestplateImage.createGraphics();
                        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
                        g2.drawImage(layerImage, 0, 0, null);
                        g2.dispose();
                    }
                }
            }
            if (chestplateImage != null) {
                ItemMeta meta = chestplate.getItemMeta();
                if (InteractiveChat.version.isNewerOrEqualTo(MCVersion.V1_20) && meta instanceof ArmorMeta) {
                    ArmorTrim armorTrim = ((ArmorMeta) meta).getTrim();
                    if (armorTrim != null) {
                        String material = armorTrim.getMaterial().getKey().getKey();
                        String pattern = armorTrim.getPattern().getKey().getKey();
                        BufferedImage trim = resourceManager.get().getTextureManager().getTexture(ResourceRegistry.ARMOR_TRIM_LOCATION.replaceFirst("%s", pattern).replaceFirst("%s", material)).getTexture();
                        Graphics2D g2 = chestplateImage.createGraphics();
                        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
                        g2.drawImage(trim, 0, 0, chestplateImage.getWidth(), chestplateImage.getHeight(), null);
                        g2.dispose();
                    }
                }
                if (!chestplate.getEnchantments().isEmpty()) {
                    chestplateImage = getEnchantedImage(resourceManager.get().getResourceRegistry(CustomItemTextureRegistry.IDENTIFIER, CustomItemTextureRegistry.class).getEnchantmentGlintOverrideTextures(EquipmentSlot.CHEST, chestplate, () -> getDefaultEnchantmentTint(EnchantmentGlintType.ENTITY), translateFunction.get()), chestplateImage, 0);
                }

                providedTextures.put(ResourceRegistry.CHESTPLATE_TEXTURE_PLACEHOLDER, new GeneratedTextureResource(resourceManager.get(), chestplateImage));
            }
        }

        ArmorUtils.ArmorTextureResult helmetArmorResult = ArmorUtils.getArmorTexture(resourceManager.get(), helmet, EquipmentSlot.HEAD);
        if (ArmorUtils.isPlayerArmor(helmet) && helmetArmorResult.hasArmorTexture()) {
            BufferedImage helmetImage = null;
            for (EquipmentModelDefinition.EquipmentLayer layer : helmetArmorResult.getLayers()) {
                Key key = KeyUtils.toKey(layer.getTexture());
                BufferedImage layerImage = resourceManager.get().getResourceRegistry(CustomItemTextureRegistry.IDENTIFIER, CustomItemTextureRegistry.class).getArmorOverrideTextures(key.value(), EquipmentSlot.HEAD, leggings, player, world, livingEntity, translateFunction.get()).map(t -> t.getTexture()).orElse(resourceManager.get().getTextureManager().getOptionalTexture(key.namespace() + ":" + ResourceRegistry.ARMOR_TEXTURE_LOCATION + key.value()).map(t -> t.getTexture()).orElse(null));
                if (layerImage != null) {
                    if (layer.isDyeable()) {
                        BufferedImage colorOverlay = ImageUtils.changeColorTo(ImageUtils.copyImage(layerImage), helmetArmorResult.getDyeableColor(layer.getDyeable().getColorWhenUndyed(ResourceRegistry.DEFAULT_DYE_COLOR)));
                        layerImage = ImageUtils.multiply(layerImage, colorOverlay);
                    }
                    if (helmetImage == null) {
                        helmetImage = layerImage;
                    } else {
                        Graphics2D g2 = helmetImage.createGraphics();
                        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
                        g2.drawImage(layerImage, 0, 0, null);
                        g2.dispose();
                    }
                }
            }
            if (helmetImage != null) {
                ItemMeta meta = helmet.getItemMeta();
                if (InteractiveChat.version.isNewerOrEqualTo(MCVersion.V1_20) && meta instanceof ArmorMeta) {
                    ArmorTrim armorTrim = ((ArmorMeta) meta).getTrim();
                    if (armorTrim != null) {
                        String material = armorTrim.getMaterial().getKey().getKey();
                        String pattern = armorTrim.getPattern().getKey().getKey();
                        BufferedImage trim = resourceManager.get().getTextureManager().getTexture(ResourceRegistry.ARMOR_TRIM_LOCATION.replaceFirst("%s", pattern).replaceFirst("%s", material)).getTexture();
                        Graphics2D g2 = helmetImage.createGraphics();
                        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
                        g2.drawImage(trim, 0, 0, helmetImage.getWidth(), helmetImage.getHeight(), null);
                        g2.dispose();
                    }
                }
                if (!helmet.getEnchantments().isEmpty()) {
                    helmetImage = getEnchantedImage(resourceManager.get().getResourceRegistry(CustomItemTextureRegistry.IDENTIFIER, CustomItemTextureRegistry.class).getEnchantmentGlintOverrideTextures(EquipmentSlot.HEAD, helmet, () -> getDefaultEnchantmentTint(EnchantmentGlintType.ENTITY), translateFunction.get()), helmetImage, 0);
                }
                providedTextures.put(ResourceRegistry.HELMET_TEXTURE_PLACEHOLDER, new GeneratedTextureResource(resourceManager.get(), helmetImage));
            }
        } else if (helmet != null && !helmet.getType().equals(Material.AIR)) {
            ItemStackProcessResult itemProcessResult = ItemRenderUtils.processItemForRendering(resourceManager.get(), player, helmet, EquipmentSlot.HEAD, ModelDisplayPosition.HEAD, version.get().isOld(), language.get());
            boolean enchanted = itemProcessResult.requiresEnchantmentGlint();
            List<ModelLayer> modelLayers = itemProcessResult.getModelLayers();
            List<ValuePairs<TextureResource, OpenGLBlending>> enchantmentGlintResource = resourceManager.get().getResourceRegistry(CustomItemTextureRegistry.IDENTIFIER, CustomItemTextureRegistry.class).getEnchantmentGlintOverrideTextures(EquipmentSlot.HEAD, helmet, () -> getDefaultEnchantmentTint(EnchantmentGlintType.ITEM), translateFunction.get());
            Function<ModelRenderer.RawEnchantmentGlintParameters, BufferedImage> enchantmentGlintFunction = parameters -> getEnchantedImage(enchantmentGlintResource, parameters.getImage(), parameters.getTick());
            Function<ModelRenderer.RawEnchantmentGlintParameters, RawEnchantmentGlintData> rawEnchantmentGlintFunction = parameters -> new RawEnchantmentGlintData(enchantmentGlintResource.stream().map(each -> getRawEnchantedImage(each.getFirst(), parameters.getImage(), parameters.getTick())).collect(Collectors.toList()), enchantmentGlintResource.stream().map(each -> each.getSecond()).collect(Collectors.toList()));
            modelItems.put(PlayerModelItemPosition.HELMET, new PlayerModelItem(PlayerModelItemPosition.HELMET, modelLayers, modelLayers.stream().map(e -> resourceManager.get().getResourceRegistry(CustomItemTextureRegistry.IDENTIFIER, CustomItemTextureRegistry.class).getItemPostResolveFunction(e.getModelKey(), EquipmentSlot.HEAD, helmet, version.get().isOld(), e.getPredicates(), player, world, livingEntity, translateFunction.get()).orElse(null)).collect(Collectors.toList()), enchanted, enchantmentGlintFunction, rawEnchantmentGlintFunction));
        }

        if (InteractiveChatDiscordSrvAddon.plugin.renderHandHeldItems) {
            if (rightHand != null && !rightHand.getType().equals(Material.AIR)) {
                EquipmentSlot slot = player.isRightHanded() ? EquipmentSlot.HAND : EquipmentSlot.valueOf("OFF_HAND");
                ItemStackProcessResult itemProcessResult = ItemRenderUtils.processItemForRendering(resourceManager.get(), player, rightHand, slot, ModelDisplayPosition.THIRDPERSON_RIGHTHAND, version.get().isOld(), language.get());
                boolean enchanted = itemProcessResult.requiresEnchantmentGlint();
                List<ModelLayer> modelLayers = itemProcessResult.getModelLayers();
                List<ValuePairs<TextureResource, OpenGLBlending>> enchantmentGlintResource = resourceManager.get().getResourceRegistry(CustomItemTextureRegistry.IDENTIFIER, CustomItemTextureRegistry.class).getEnchantmentGlintOverrideTextures(slot, rightHand, () -> getDefaultEnchantmentTint(EnchantmentGlintType.ITEM), translateFunction.get());
                Function<ModelRenderer.RawEnchantmentGlintParameters, BufferedImage> enchantmentGlintFunction = parameters -> getEnchantedImage(enchantmentGlintResource, parameters.getImage(), parameters.getTick());
                Function<ModelRenderer.RawEnchantmentGlintParameters, RawEnchantmentGlintData> rawEnchantmentGlintFunction = parameters -> new RawEnchantmentGlintData(enchantmentGlintResource.stream().map(each -> getRawEnchantedImage(each.getFirst(), parameters.getImage(), parameters.getTick())).collect(Collectors.toList()), enchantmentGlintResource.stream().map(each -> each.getSecond()).collect(Collectors.toList()));
                modelItems.put(PlayerModelItemPosition.RIGHT_HAND, new PlayerModelItem(PlayerModelItemPosition.RIGHT_HAND, modelLayers, modelLayers.stream().map(e -> resourceManager.get().getResourceRegistry(CustomItemTextureRegistry.IDENTIFIER, CustomItemTextureRegistry.class).getItemPostResolveFunction(e.getModelKey(), slot, rightHand, version.get().isOld(), e.getPredicates(), player, world, livingEntity, translateFunction.get()).orElse(null)).collect(Collectors.toList()), enchanted, enchantmentGlintFunction, rawEnchantmentGlintFunction));
            }
            if (leftHand != null && !leftHand.getType().equals(Material.AIR)) {
                EquipmentSlot slot = player.isRightHanded() ? EquipmentSlot.valueOf("OFF_HAND") : EquipmentSlot.HAND;
                ItemStackProcessResult itemProcessResult = ItemRenderUtils.processItemForRendering(resourceManager.get(), player, leftHand, slot, ModelDisplayPosition.THIRDPERSON_LEFTHAND, version.get().isOld(), language.get());
                boolean enchanted = itemProcessResult.requiresEnchantmentGlint();
                List<ModelLayer> modelLayers = itemProcessResult.getModelLayers();
                List<ValuePairs<TextureResource, OpenGLBlending>> enchantmentGlintResource = resourceManager.get().getResourceRegistry(CustomItemTextureRegistry.IDENTIFIER, CustomItemTextureRegistry.class).getEnchantmentGlintOverrideTextures(slot, leftHand, () -> getDefaultEnchantmentTint(EnchantmentGlintType.ITEM), translateFunction.get());
                Function<ModelRenderer.RawEnchantmentGlintParameters, BufferedImage> enchantmentGlintFunction = parameters -> getEnchantedImage(enchantmentGlintResource, parameters.getImage(), parameters.getTick());
                Function<ModelRenderer.RawEnchantmentGlintParameters, RawEnchantmentGlintData> rawEnchantmentGlintFunction = parameters -> new RawEnchantmentGlintData(enchantmentGlintResource.stream().map(each -> getRawEnchantedImage(each.getFirst(), parameters.getImage(), parameters.getTick())).collect(Collectors.toList()), enchantmentGlintResource.stream().map(each -> each.getSecond()).collect(Collectors.toList()));
                modelItems.put(PlayerModelItemPosition.LEFT_HAND, new PlayerModelItem(PlayerModelItemPosition.LEFT_HAND, modelLayers, modelLayers.stream().map(e -> resourceManager.get().getResourceRegistry(CustomItemTextureRegistry.IDENTIFIER, CustomItemTextureRegistry.class).getItemPostResolveFunction(e.getModelKey(), slot, leftHand, version.get().isOld(), e.getPredicates(), player, world, livingEntity, translateFunction.get()).orElse(null)).collect(Collectors.toList()), enchanted, enchantmentGlintFunction, rawEnchantmentGlintFunction));
            }
        }

        boolean upsideDown = ModelUtils.isRenderedUpsideDown(player.getName(), cape != null);

        RenderResult renderResult = InteractiveChatDiscordSrvAddon.plugin.modelRenderer.renderPlayer(image.getWidth(), image.getHeight(), ModelRenderer.SINGLE_RENDER, resourceManager.get(), version.get().isOld(), slim, providedTextures, TintColorProvider.EMPTY_INSTANCE, modelItems);
        Graphics2D g = image.createGraphics();
        BufferedImage resizedImage = ImageUtils.resizeImageAbs(renderResult.getImage(0), 117, 159);
        if (upsideDown) {
            resizedImage = ImageUtils.rotateImageByDegrees(resizedImage, 180);
        }
        g.drawImage(resizedImage, -1, 12, null);
        g.dispose();

        if (elytraImage != null) {
            BufferedImage resizedElytraImage = ImageUtils.resizeImage(elytraImage, 0.9);
            if (upsideDown) {
                resizedElytraImage = ImageUtils.rotateImageByDegrees(resizedElytraImage, 180);
            }
            ImageUtils.drawTransparent(image, resizedElytraImage, 14, 75);
        }

        return image;
    }

    private static BufferedImage getSingleRawItemImage(ItemStack item, OfflineICPlayer player) throws IOException {
        return getSingleRawItemImage(item, player, DEFAULT_ITEM_RENDER_SIZE);
    }

    private static BufferedImage getSingleRawItemImage(ItemStack item, OfflineICPlayer player, int size) throws IOException {
        return getRawItemImage(item, player, size, ModelRenderer.SINGLE_RENDER).get(0);
    }

    private static List<BufferedImage> getRawItemImage(ItemStack item, OfflineICPlayer player) throws IOException {
        return getRawItemImage(item, player, DEFAULT_ITEM_RENDER_SIZE, ModelRenderer.SINGLE_RENDER);
    }

    private static List<BufferedImage> getRawItemImage(ItemStack item, OfflineICPlayer player, int size, SteppedIntegerRange animationSpec) throws IOException {
        InteractiveChatDiscordSrvAddon.plugin.imageCounter.incrementAndGet();
        Debug.debug("ImageGeneration creating raw item stack image " + (item == null ? "null" : ItemNBTUtils.getNMSItemStackJson(item)));

        double scale = (double) size / DEFAULT_ITEM_RENDER_SIZE;

        ICMaterial icMaterial = ICMaterial.from(item);
        int amount = item.getAmount();
        String key = ModelUtils.getItemModelKey(icMaterial);
        ItemStackProcessResult processResult = ItemRenderUtils.processItemForRendering(resourceManager.get(), player, item, null, ModelDisplayPosition.GUI, version.get().isOld(), language.get());
        boolean requiresEnchantmentGlint = processResult.requiresEnchantmentGlint();
        List<ModelLayer> modelLayers = processResult.getModelLayers();

        Debug.debug("ImageGeneration rendering with model key " + modelLayers);

        BufferedImage[] itemImages;
        RenderResult renderResult = InteractiveChatDiscordSrvAddon.plugin.modelRenderer.render(size, size, animationSpec, resourceManager.get(), version.get().isOld(), ModelDisplayPosition.GUI, modelLayers, requiresEnchantmentGlint, processResult.getEnchantmentGlintFunction(), processResult.getRawEnchantmentGlintFunction());
        if (renderResult.isSuccessful()) {
            itemImages = renderResult.getImages();
        } else {
            Debug.debug("ImageGeneration creating missing Image for material " + icMaterial);
            itemImages = new BufferedImage[] {TextureManager.getMissingImage(size, size)};
        }

        for (int i = 0; i < itemImages.length; i++) {
            BufferedImage itemImage = itemImages[i];
            if (item.getType().getMaxDurability() > 0) {
                int maxDur = item.getType().getMaxDurability();
                int durability = maxDur - (version.get().isLegacy() ? item.getDurability() : ((Damageable) item.getItemMeta()).getDamage());
                double percentage = Math.max(0.0, Math.min(1.0, ((double) durability / (double) maxDur)));
                if (percentage < 1) {
                    int hue = (int) (125 * percentage);
                    int length = (int) Math.round(26 * scale * percentage);
                    Color color = Color.getHSBColor((float) hue / 360, 1, 1);

                    Graphics2D g4 = itemImage.createGraphics();
                    g4.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
                    g4.setColor(Color.BLACK);
                    g4.fillPolygon(new int[] {(int) Math.round(4 * scale), (int) Math.round(30 * scale), (int) Math.round(30 * scale), (int) Math.round(4 * scale)}, new int[] {(int) Math.round(26 * scale), (int) Math.round(26 * scale), (int) Math.round(30 * scale), (int) Math.round(30 * scale)}, 4);
                    g4.setColor(color);
                    g4.fillPolygon(new int[] {(int) Math.round(4 * scale), (int) Math.round(4 * scale + length), (int) Math.round(4 * scale + length), (int) Math.round(4 * scale)}, new int[] {(int) Math.round(26 * scale), (int) Math.round(26 * scale), (int) Math.round(28 * scale), (int) Math.round(28 * scale)}, 4);
                    g4.dispose();
                }
            }
            if (icMaterial.isMaterial(XMaterial.BUNDLE)) {
                @SuppressWarnings("UnstableApiUsage")
                Fraction fraction = BundleUtils.getWeight(((BundleMeta) item.getItemMeta()).getItems());
                double fullness = Math.max(0.0, Math.min(1.0, fraction.doubleValue()));
                int length = (int) Math.ceil(26 * scale * fullness);

                Graphics2D g4 = itemImage.createGraphics();
                g4.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
                g4.setColor(Color.BLACK);
                g4.fillPolygon(new int[] {(int) Math.round(4 * scale), (int) Math.round(30 * scale), (int) Math.round(30 * scale), (int) Math.round(4 * scale)}, new int[] {(int) Math.round(26 * scale), (int) Math.round(26 * scale), (int) Math.round(30 * scale), (int) Math.round(30 * scale)}, 4);
                g4.setColor(fraction.compareTo(Fraction.ONE) >= 0 ? BUNDLE_WEIGHT_FULL_COLOR : BUNDLE_WEIGHT_COLOR);
                g4.fillPolygon(new int[] {(int) Math.round(4 * scale), (int) Math.round(4 * scale + length), (int) Math.round(4 * scale + length), (int) Math.round(4 * scale)}, new int[] {(int) Math.round(26 * scale), (int) Math.round(26 * scale), (int) Math.round(28 * scale), (int) Math.round(28 * scale)}, 4);
                g4.dispose();
            }

            if (amount != 1) {
                BufferedImage newItemImage = new BufferedImage((int) Math.round(40 * scale), (int) Math.round(40 * scale), BufferedImage.TYPE_INT_ARGB);
                Graphics2D g4 = newItemImage.createGraphics();
                g4.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
                g4.drawImage(itemImage, 0, 0, null);
                g4.dispose();
                Component component = Component.text(amount);
                if (amount <= 0) {
                    component = component.color(NamedTextColor.RED);
                }
                itemImages[i] = itemImage = ImageUtils.printComponentRightAligned(resourceManager.get(), newItemImage, component, InteractiveChatDiscordSrvAddon.plugin.language, version.get().isLegacyRGB(), (int) Math.round(33 * scale), (int) Math.round(17 * scale), (float) (16 * scale), ITEM_AMOUNT_TEXT_DARKEN_FACTOR).getImage();
            }
        }

        return Arrays.asList(itemImages);
    }

    public static Future<BufferedImage> getMapImage(ItemStack item, Player player) {
        if (!FilledMapUtils.isFilledMap(item)) {
            throw new IllegalArgumentException("Provided item is not a filled map");
        }
        Debug.debug("ImageGeneration creating map image with item");
        if (Bukkit.isPrimaryThread()) {
            ItemMapWrapper data = new ItemMapWrapper(item, player);
            return CompletableFuture.completedFuture(getMapImage(data.getColors(), data.getMapCursors(), player));
        } else {
            CompletableFuture<BufferedImage> future = new CompletableFuture<>();
            ItemStack finalItem = item.clone();
            Bukkit.getScheduler().runTask(InteractiveChatDiscordSrvAddon.plugin, () -> {
                ItemMapWrapper data;
                try {
                    data = new ItemMapWrapper(finalItem, player);
                } catch (Throwable e) {
                    future.completeExceptionally(e);
                    return;
                }
                Bukkit.getScheduler().runTaskAsynchronously(InteractiveChatDiscordSrvAddon.plugin, () -> {
                    try {
                        future.complete(getMapImage(data.getColors(), data.getMapCursors(), player));
                    } catch (Throwable e) {
                        future.completeExceptionally(e);
                    }
                });
            });
            return future;
        }
    }

    public static BufferedImage getMapImage(byte[] colors, List<MapCursor> mapCursors, Player player) {
        if (colors != null && colors.length != 16384) {
            throw new IllegalArgumentException("Map color array is not null or of length 16384");
        }
        InteractiveChatDiscordSrvAddon.plugin.imageCounter.incrementAndGet();
        Debug.debug("ImageGeneration creating map image with color and cursors");

        BufferedImage background = resourceManager.get().getTextureManager().getTexture(ResourceRegistry.MAP_TEXTURE_LOCATION + "map_background").getTexture();

        BufferedImage image = new BufferedImage(MAP_SIZE, MAP_SIZE, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g.drawImage(background, 0, 0, image.getWidth(), image.getHeight(), null);
        g.dispose();

        int borderOffset = (int) (image.getWidth() / 23.3333333333333333333);
        int ratio = (image.getWidth() - borderOffset * 2) / 128;

        if (colors != null) {
            for (int widthOffset = 0; widthOffset < 128; widthOffset++) {
                for (int heightOffset = 0; heightOffset < 128; heightOffset++) {
                    byte index = colors[widthOffset + heightOffset * 128];
                    if (MapPalette.TRANSPARENT != index) {
                        Color color = MapPalette.getColor(index);
                        for (int x = 0; x < ratio; x++) {
                            for (int y = 0; y < ratio; y++) {
                                image.setRGB(widthOffset * ratio + borderOffset + x, heightOffset * ratio + borderOffset + y, color.getRGB());
                            }
                        }
                    }
                }
            }
        }

        Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);

        BufferedImage asset;
        int iconWidth;
        if (resourceManager.get().getNativeServerPackFormat() >= 24) {
            asset = null;
            iconWidth = -1;
        } else {
            asset = resourceManager.get().getTextureManager().getTexture(ResourceRegistry.MAP_TEXTURE_LOCATION + "map_icons").getTexture();
            iconWidth = asset.getWidth() / MAP_ICON_PER_ROLE;
        }

        if (mapCursors != null) {
            for (MapCursor icon : mapCursors) {
                int x = icon.getX() + 128;
                int y = icon.getY() + 128;
                double rotation = (360.0 / 16.0 * (double) icon.getDirection()) + 180.0;
                Component component;
                try {
                    component = LegacyComponentSerializer.legacySection().deserializeOrNull(icon.getCaption());
                } catch (Throwable e) {
                    component = null;
                }

                BufferedImage iconImage;
                if (resourceManager.get().getNativeServerPackFormat() >= 24) {
                    String assetName = NMSAddon.getInstance().getMapCursorTypeKey(icon).value();
                    iconImage = resourceManager.get().getTextureManager().getTexture(ResourceRegistry.DEFAULT_MAP_DECORATION_LOCATION + assetName).getTexture();
                } else {
                    int typeId = icon.getType().ordinal();
                    iconImage = ImageUtils.copyAndGetSubImage(asset, typeId % MAP_ICON_PER_ROLE * iconWidth, typeId / MAP_ICON_PER_ROLE * iconWidth, iconWidth, iconWidth);
                }
                BufferedImage iconImageBig = new BufferedImage(96, 96, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g3 = iconImageBig.createGraphics();
                g3.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
                g3.drawImage(iconImage, iconImageBig.getWidth() / 6, iconImageBig.getHeight() / 6, 64, 64, null);
                g3.dispose();
                iconImage = iconImageBig;

                BufferedImage iconCan = new BufferedImage(96, 96, BufferedImage.TYPE_INT_ARGB);

                AffineTransform at = new AffineTransform();
                at.rotate(Math.toRadians(rotation), iconImage.getWidth() / 2.0, iconImage.getHeight() / 2.0);
                Graphics2D g2d = iconCan.createGraphics();
                g2d.drawImage(iconImage, at, null);
                g2d.dispose();

                int imageX = x * ratio / 2 + borderOffset;
                int imageY = y * ratio / 2 + borderOffset;

                g2.drawImage(iconCan, imageX - (iconCan.getWidth() / 2), imageY - (iconCan.getHeight() / 2), 96, 96, null);

                if (component != null) {
                    ImageUtils.printComponentShadowlessDynamicSize(resourceManager.get(), image, component, InteractiveChatDiscordSrvAddon.plugin.language, version.get().isLegacyRGB(), imageX, imageY + 32, 30, true);
                }
            }
        }
        g2.dispose();

        return image;
    }

    public static BufferedImage getToolTipImage(Component print, Key customTooltip) {
        return getToolTipImage(print, false, customTooltip);
    }

    public static BufferedImage getToolTipImage(Component print, boolean allowLineBreaks, Key customTooltip) {
        return getToolTipImage(Collections.singletonList(ToolTipComponent.text(print)), allowLineBreaks, customTooltip);
    }

    public static BufferedImage getToolTipImage(List<ToolTipComponent<?>> prints, Key customTooltip) {
        return getToolTipImage(prints, false, customTooltip);
    }

    public static BufferedImage getToolTipImage(List<ToolTipComponent<?>> prints, boolean allowLineBreaks, Key customTooltip) {
        if (prints.isEmpty() || !(prints.get(0).getType().equals(ToolTipType.TEXT))) {
            Debug.debug("ImageGeneration creating tooltip image");
        } else {
            Debug.debug("ImageGeneration creating tooltip image of " + InteractiveChatComponentSerializer.legacySection().serialize(ComponentStringUtils.resolve(prints.get(0).getToolTipComponent(ToolTipType.TEXT), translateFunction.get())));
        }

        if (allowLineBreaks) {
            List<ToolTipComponent<?>> newList = new ArrayList<>();
            for (ToolTipComponent<?> toolTip : prints) {
                if (toolTip.getType().equals(ToolTipType.TEXT)) {
                    for (Component newComponent : ComponentStyling.splitAtLineBreaks(toolTip.getToolTipComponent(ToolTipType.TEXT))) {
                        newList.add(ToolTipComponent.text(newComponent));
                    }
                } else {
                    newList.add(toolTip);
                }
            }
            prints = newList;
        }

        int requiredHeight = prints.stream().mapToInt(each -> {
            ToolTipType<?> type = each.getType();
            if (type.equals(ToolTipType.TEXT)) {
                return 20;
            } else if (type.equals(ToolTipType.IMAGE)) {
                return each.getToolTipComponent(ToolTipType.IMAGE).getHeight() + 16;
            } else {
                return 0;
            }
        }).sum() + 415;

        BufferedImage image = new BufferedImage(2240, requiredHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();

        int topX = image.getWidth() / 5 * 2;
        int maxX = 0;
        int currentY = 208;
        for (ToolTipComponent<?> print : prints) {
            ToolTipType<?> type = print.getType();
            if (type.equals(ToolTipType.TEXT)) {
                ImageUtils.ComponentPrintResult printResult = ImageUtils.printComponent(resourceManager.get(), image, print.getToolTipComponent(ToolTipType.TEXT), InteractiveChatDiscordSrvAddon.plugin.language, version.get().isLegacyRGB(), topX + 8, currentY, 16);
                int textWidth = printResult.getTextWidth();
                if (textWidth > maxX) {
                    maxX = textWidth;
                }
                currentY += 20;
            } else if (type.equals(ToolTipType.IMAGE)) {
                currentY += 5;
                BufferedImage componentImage = print.getToolTipComponent(ToolTipType.IMAGE);
                g.drawImage(componentImage, topX + 8, currentY, null);
                if (componentImage.getWidth() > maxX) {
                    maxX = componentImage.getWidth();
                }
                currentY += componentImage.getHeight() + 11;
            }
        }
        g.dispose();
        maxX += 14;

        int firstX = 0;
        outer:
        for (int x = 0; x < image.getWidth() - 9; x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                if (image.getRGB(x, y) != 0) {
                    firstX = x;
                    break outer;
                }
            }
        }
        int lastX = 0;
        for (int x = firstX; x < image.getWidth() - 9; x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                if (image.getRGB(x, y) != 0) {
                    lastX = x;
                    break;
                }
            }
        }
        firstX = Math.max(0, firstX - 8);

        int firstY = 0;
        outer:
        for (int y = 0; y < image.getHeight() - 9; y++) {
            for (int x = firstX; x <= lastX; x++) {
                if (image.getRGB(x, y) != 0) {
                    firstY = y;
                    break outer;
                }
            }
        }
        int lastY = 0;
        for (int y = firstY; y < image.getHeight() - 9; y++) {
            for (int x = firstX; x <= lastX; x++) {
                if (image.getRGB(x, y) != 0) {
                    lastY = y;
                    break;
                }
            }
        }
        firstY = Math.max(0, firstY - 8);
        BufferedImage background = new BufferedImage(maxX + 4, currentY - 196, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2 = background.createGraphics();
        if (ResourceRegistry.RESOURCE_PACK_VERSION < 46) {
            g2.setColor(TOOLTIP_BACKGROUND_COLOR);
            g2.fillRect(2, 0, background.getWidth() - 4, background.getHeight());
            g2.fillRect(0, 2, 2, background.getHeight() - 4);
            g2.fillRect(background.getWidth() - 2, 2, 2, background.getHeight() - 4);
            g2.setColor(TOOLTIP_OUTLINE_TOP_COLOR);
            g2.fillRect(4, 2, background.getWidth() - 8, 2);
            GradientPaint gradientPaint = new GradientPaint(0, 0, TOOLTIP_OUTLINE_TOP_COLOR, 0, background.getHeight() - 4, TOOLTIP_OUTLINE_BOTTOM_COLOR);
            g2.setPaint(gradientPaint);
            g2.fillRect(2, 2, 2, background.getHeight() - 4);
            g2.fillRect(background.getWidth() - 4, 2, 2, background.getHeight() - 4);
            g2.setColor(TOOLTIP_OUTLINE_BOTTOM_COLOR);
            g2.fillRect(4, background.getHeight() - 4, background.getWidth() - 8, 2);
        } else {
            String namespace = customTooltip == null ? ResourceRegistry.DEFAULT_SPRITE_LOCATION : (customTooltip.namespace() + ":");
            String customLocationPrefix = customTooltip == null ? "" : (customTooltip.value() + "_");
            BufferedImage tooltipBackground = resourceManager.get().getTextureManager().getTexture(namespace + "tooltip/" + customLocationPrefix + "background").getScaledTexture(200, 200, background.getWidth() + 32, background.getHeight() + 32);
            BufferedImage tooltipFrame = resourceManager.get().getTextureManager().getTexture(namespace + "tooltip/" + customLocationPrefix + "frame").getScaledTexture(200, 200, background.getWidth() + 32, background.getHeight() + 32);
            g2.drawImage(tooltipBackground, -16, -16, null);
            g2.drawImage(tooltipFrame, -16, -16, null);
        }
        g2.dispose();

        int offsetX = Math.max(topX - firstX, 0);
        BufferedImage output = new BufferedImage(Math.max(offsetX + lastX - topX + 9, background.getWidth()), Math.max(lastY - firstY + 7, background.getHeight()), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g3 = output.createGraphics();
        g3.drawImage(background, offsetX, 201 - firstY, null);
        g3.drawImage(image, -firstX, -firstY, null);
        g3.dispose();

        return output;
    }

    public static BufferedImage getTabListImage(List<Component> header, List<Component> footer, List<ValueTrios<OfflineICPlayer, Component, Integer>> players, boolean showAvatar, boolean showPing) {
        return getTabListImage(header, footer, players, showAvatar, showPing, TABLIST_PLAYER_DISPLAY_LIMIT);
    }

    public static BufferedImage getTabListImage(List<Component> header, List<Component> footer, List<ValueTrios<OfflineICPlayer, Component, Integer>> players, boolean showAvatar, boolean showPing, int maxPlayerDisplayed) {
        players = players.subList(0, Math.min(players.size(), maxPlayerDisplayed));
        List<ValueTrios<BufferedImage, Integer, Color>> playerImages = new ArrayList<>(players.size());
        int masterOffsetX = 0;
        for (ValueTrios<OfflineICPlayer, Component, Integer> trio : players) {
            OfflineICPlayer player = trio.getFirst();
            UUID uuid = player.getUniqueId();
            Component name = trio.getSecond();
            int ping = trio.getThird();
            BufferedImage image = new BufferedImage(2048, TABLIST_INTERNAL_HEIGHT, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = image.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            int offsetX = 0;
            if (showAvatar) {
                BufferedImage skin;
                try {
                    Player onlinePlayer = Bukkit.getPlayer(uuid);
                    if (onlinePlayer == null) {
                        CacheObject<?> cache = resourceManager.get().getResourceRegistry(ICacheManager.IDENTIFIER, ICacheManager.class).getCache(uuid + "null" + PLAYER_SKIN_CACHE_KEY);
                        if (cache == null) {
                            String value = SkinUtils.getSkinURLFromUUID(uuid);
                            skin = ImageUtils.downloadImage(value);
                            resourceManager.get().getResourceRegistry(ICacheManager.IDENTIFIER, ICacheManager.class).putCache(uuid + "null" + PLAYER_SKIN_CACHE_KEY, skin);
                            skin = ImageUtils.copyImage(skin);
                        } else {
                            skin = ImageUtils.copyImage((BufferedImage) cache.getObject());
                        }
                    } else {
                        try {
                            JSONObject json = (JSONObject) new JSONParser().parse(SkinUtils.getSkinJsonFromProfile(onlinePlayer));
                            String value = (String) ((JSONObject) ((JSONObject) json.get("textures")).get("SKIN")).get("url");
                            CacheObject<?> cache = resourceManager.get().getResourceRegistry(ICacheManager.IDENTIFIER, ICacheManager.class).getCache(uuid + value + PLAYER_SKIN_CACHE_KEY);
                            if (cache == null) {
                                skin = ImageUtils.downloadImage(value);
                                resourceManager.get().getResourceRegistry(ICacheManager.IDENTIFIER, ICacheManager.class).putCache(uuid + value + PLAYER_SKIN_CACHE_KEY, skin);
                            } else {
                                skin = (BufferedImage) cache.getObject();
                            }
                            skin = ImageUtils.copyImage(skin);
                        } catch (Exception e) {
                            String value = SkinUtils.getSkinURLFromUUID(uuid);
                            skin = ImageUtils.downloadImage(value);
                        }
                    }
                } catch (Exception e) {
                    skin = resourceManager.get().getTextureManager().getTexture(ResourceRegistry.DEFAULT_WIDE_SKIN_LOCATION).getTexture(64, 64);
                }
                BufferedImage avatar = ImageUtils.copyAndGetSubImage(skin, 8, 8, 8, 8);
                BufferedImage avatarOverlay = ImageUtils.copyAndGetSubImage(skin, 40, 8, 8, 8);
                if (ModelUtils.isRenderedUpsideDown(name)) {
                    avatar = ImageUtils.rotateImageByDegrees(avatar, 180);
                    avatarOverlay = ImageUtils.rotateImageByDegrees(avatarOverlay, 180);
                }
                g.drawImage(avatar, offsetX, (TABLIST_INTERNAL_HEIGHT - 18) / 2, 16, 16, null);
                g.drawImage(avatarOverlay, offsetX, (TABLIST_INTERNAL_HEIGHT - 18) / 2, 16, 16, null);
                offsetX += 18;
            } else {
                offsetX += 2;
            }
            g.dispose();
            ImageUtils.printComponent(resourceManager.get(), image, name, InteractiveChatDiscordSrvAddon.plugin.language, version.get().isLegacyRGB(), offsetX, (TABLIST_INTERNAL_HEIGHT - 18) / 2 - 1, 16);
            int lastX = InteractiveChatDiscordSrvAddon.plugin.playerlistCommandMinWidth;
            for (int x = InteractiveChatDiscordSrvAddon.plugin.playerlistCommandMinWidth; x < image.getWidth(); x++) {
                for (int y = 0; y < image.getHeight(); y++) {
                    if (image.getRGB(x, y) != 0) {
                        lastX = x;
                        break;
                    }
                }
            }
            if (lastX > masterOffsetX) {
                masterOffsetX = lastX;
            }
            Color color = null;
            Object hex = player.getProperties().get("tab_background_color");
            if (hex instanceof String) {
                try {
                    color = Color.decode((String) hex);
                } catch (NumberFormatException ignore) {
                }
            }
            playerImages.add(new ValueTrios<>(image, ping, color));
        }
        List<BufferedImage> playerRows = new ArrayList<>(playerImages.size());
        if (showPing) {
            masterOffsetX += 26;
        } else {
            masterOffsetX += 2;
        }
        for (ValueTrios<BufferedImage, Integer, Color> trio : playerImages) {
            BufferedImage image = trio.getFirst();
            if (showPing) {
                BufferedImage ping = getPingIcon(trio.getSecond(), false);
                Graphics2D g = image.createGraphics();
                g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
                g.drawImage(ImageUtils.resizeImageAbs(ping, 20, 16), masterOffsetX - 22, (TABLIST_INTERNAL_HEIGHT - 18) / 2, null);
                g.dispose();
            }
            BufferedImage cropped = new BufferedImage(masterOffsetX, TABLIST_INTERNAL_HEIGHT, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = cropped.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            if (trio.getThird() == null) {
                g.setColor(TABLIST_PLAYER_BACKGROUND);
            } else {
                g.setColor(trio.getThird());
            }
            g.fillRect(0, (TABLIST_INTERNAL_HEIGHT - 18) / 2, cropped.getWidth(), 16);
            g.drawImage(image, 0, 0, null);
            g.dispose();
            playerRows.add(cropped);
        }
        Map<BufferedImage, Integer> headerLines = new LinkedHashMap<>(header.size());
        for (Component line : header) {
            BufferedImage image = new BufferedImage(2048, TABLIST_INTERNAL_HEIGHT, BufferedImage.TYPE_INT_ARGB);
            ImageUtils.printComponent(resourceManager.get(), image, line, InteractiveChatDiscordSrvAddon.plugin.language, version.get().isLegacyRGB(), 0, (TABLIST_INTERNAL_HEIGHT - 18) / 2 - 1, 16);
            int lastX = 0;
            for (int x = 0; x < image.getWidth(); x++) {
                for (int y = 0; y < image.getHeight(); y++) {
                    if (image.getRGB(x, y) != 0) {
                        lastX = x;
                        break;
                    }
                }
            }
            BufferedImage cropped = new BufferedImage(Math.max(1, lastX), TABLIST_INTERNAL_HEIGHT, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = cropped.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            g.drawImage(image, 0, 0, null);
            g.dispose();
            headerLines.put(cropped, lastX);
        }
        Map<BufferedImage, Integer> footerLines = new LinkedHashMap<>(footer.size());
        for (Component line : footer) {
            BufferedImage image = new BufferedImage(2048, TABLIST_INTERNAL_HEIGHT, BufferedImage.TYPE_INT_ARGB);
            ImageUtils.printComponent(resourceManager.get(), image, line, InteractiveChatDiscordSrvAddon.plugin.language, version.get().isLegacyRGB(), 0, (TABLIST_INTERNAL_HEIGHT - 18) / 2 - 1, 16);
            int lastX = 0;
            for (int x = 0; x < image.getWidth(); x++) {
                for (int y = 0; y < image.getHeight(); y++) {
                    if (image.getRGB(x, y) != 0) {
                        lastX = x;
                        break;
                    }
                }
            }
            BufferedImage cropped = new BufferedImage(Math.max(1, lastX), TABLIST_INTERNAL_HEIGHT, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = cropped.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            g.drawImage(image, 0, 0, null);
            g.dispose();
            footerLines.put(cropped, lastX);
        }
        int columnCount = ((playerRows.size() - 1) / TABLIST_SINGLE_COLUMN_LIMIT) + 1;
        int playersPerColumn = (int) Math.ceil((double) playerRows.size() / (double) columnCount);

        BufferedImage image = new BufferedImage(((masterOffsetX + 2) * columnCount) + 2, playersPerColumn * 18 + 2, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g.setColor(TABLIST_BACKGROUND);
        g.fillRect(0, 0, image.getWidth(), image.getHeight());
        int baseOffsetY = -((TABLIST_INTERNAL_HEIGHT - 18) / 2 - 2);
        int startingIndex = 0;
        for (int column = 0; column < columnCount; column++) {
            int offsetY = baseOffsetY;
            for (int u = 0; u < playersPerColumn; u++) {
                int i = startingIndex + u;
                if (i < playerRows.size()) {
                    g.drawImage(playerRows.get(i), 2 + ((masterOffsetX + 2) * column), offsetY, null);
                }
                offsetY += 18;
            }
            startingIndex += playersPerColumn;
        }
        g.dispose();

        int maxOffsetX = Stream.concat(headerLines.values().stream(), footerLines.values().stream()).mapToInt(each -> each).max().orElse(0);
        if (maxOffsetX <= 0) {
            return image;
        } else {
            BufferedImage decoration = new BufferedImage(Math.max(image.getWidth(), maxOffsetX + 4), (headerLines.isEmpty() ? 0 : headerLines.size() * 18 + 2) + image.getHeight() + (footerLines.isEmpty() ? 2 : footerLines.size() * 18 + 4), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = decoration.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            int offsetY = -((TABLIST_INTERNAL_HEIGHT - 18) / 2 - 2);
            for (BufferedImage each : headerLines.keySet()) {
                g2.drawImage(each, (decoration.getWidth() / 2) - (each.getWidth() / 2), offsetY, null);
                offsetY += 18;
            }
            g2.drawImage(image, (decoration.getWidth() / 2) - (image.getWidth() / 2), offsetY + ((TABLIST_INTERNAL_HEIGHT - 18) / 2), null);
            offsetY += image.getHeight() + 2;
            for (BufferedImage each : footerLines.keySet()) {
                g2.drawImage(each, (decoration.getWidth() / 2) - (each.getWidth() / 2), offsetY, null);
                offsetY += 18;
            }
            g2.dispose();
            int lastY = offsetY + ((TABLIST_INTERNAL_HEIGHT - 18) / 2 - 2);
            for (int y = offsetY + ((TABLIST_INTERNAL_HEIGHT - 18) / 2 - 2); y < decoration.getHeight(); y++) {
                for (int x = 0; x < decoration.getWidth(); x++) {
                    if (decoration.getRGB(x, y) != 0) {
                        lastY = y;
                        break;
                    }
                }
            }
            BufferedImage finalDecoration = new BufferedImage(decoration.getWidth(), lastY + 3, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g3 = finalDecoration.createGraphics();
            g3.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            g3.setColor(TABLIST_BACKGROUND);
            g3.fillRect(0, 0, finalDecoration.getWidth(), finalDecoration.getHeight());
            g3.drawImage(decoration, 0, 0, null);
            g3.dispose();
            return finalDecoration;
        }
    }

    public static GenericContainerBackgroundResult getGenericContainerBackground(int rows, ContainerTitlePrintingFunction titlePrintingFunction) {
        rows = Math.max(rows, 1);
        BufferedImage image = resourceManager.get().getTextureManager().getTexture(ResourceRegistry.IC_GUI_TEXTURE_LOCATION + "generic_54").getTexture(356, 268);
        if (rows < 6) {
            BufferedImage head = image.getSubimage(0, 0, 356, 36 + (36 * rows));
            BufferedImage tail = image.getSubimage(0, 252, 356, 16);
            image = ImageUtils.appendImageBottom(head, tail, 0, 0);
        }
        BufferedImage expanded = new BufferedImage(image.getWidth() * 3, image.getHeight() * 3, BufferedImage.TYPE_INT_ARGB);
        expanded = titlePrintingFunction.apply(expanded, 18 + image.getWidth(), 13 + image.getHeight(), 16.0F, INVENTORY_DEFAULT_FONT_COLOR);
        int firstX = image.getWidth();
        for (int x = firstX; x >= 0; x--) {
            for (int y = 0; y < expanded.getHeight(); y++) {
                if (expanded.getRGB(x, y) != 0) {
                    firstX = x;
                    break;
                }
            }
        }
        int lastX = image.getWidth() * 2;
        for (int x = lastX; x < expanded.getWidth(); x++) {
            for (int y = 0; y < expanded.getHeight(); y++) {
                if (expanded.getRGB(x, y) != 0) {
                    lastX = x + 1;
                    break;
                }
            }
        }
        int firstY = image.getHeight();
        for (int y = firstY; y >= 0; y--) {
            for (int x = firstX; x < lastX; x++) {
                if (expanded.getRGB(x, y) != 0) {
                    firstY = y;
                    break;
                }
            }
        }
        int lastY = image.getHeight() * 2;
        for (int y = lastY; y < expanded.getHeight(); y++) {
            for (int x = firstX; x < lastX; x++) {
                if (expanded.getRGB(x, y) != 0) {
                    lastY = y + 1;
                    break;
                }
            }
        }
        int expandedX = image.getWidth() - firstX;
        int expandedY = image.getHeight() - firstY;
        BufferedImage resultImage = new BufferedImage(lastX - firstX, lastY - firstY, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = resultImage.createGraphics();
        g.drawImage(image, expandedX, expandedY, null);
        g.drawImage(expanded, -firstX, -firstY, null);
        g.dispose();
        return new GenericContainerBackgroundResult(resultImage, expandedX, expandedY);
    }

    public static BufferedImage getPingIcon(int ms, boolean useNoConnectionIcon) {
        if (resourceManager.get().getNativeServerPackFormat() < 16) {
            BufferedImage icons = resourceManager.get().getTextureManager().getTexture(ResourceRegistry.GUI_TEXTURE_LOCATION + "icons").getTexture();
            int scale = icons.getWidth() / 256;
            if (useNoConnectionIcon && ms < 0) {
                return icons.getSubimage(0, 55 * scale, 10 * scale, 8 * scale);
            } else if (ms < 150) {
                return icons.getSubimage(0, 15 * scale, 10 * scale, 8 * scale);
            } else if (ms < 300) {
                return icons.getSubimage(0, 24 * scale, 10 * scale, 8 * scale);
            } else if (ms < 600) {
                return icons.getSubimage(0, 31 * scale, 10 * scale, 8 * scale);
            } else if (ms < 1000) {
                return icons.getSubimage(0, 39 * scale, 10 * scale, 8 * scale);
            } else {
                return icons.getSubimage(0, 47 * scale, 10 * scale, 8 * scale);
            }
        } else {
            String location = ResourceRegistry.DEFAULT_SPRITE_LOCATION + "icon/";
            if (useNoConnectionIcon && ms < 0) {
                location += "ping_unknown";
            } else if (ms < 150) {
                location += "ping_5";
            } else if (ms < 300) {
                location += "ping_4";
            } else if (ms < 600) {
                location += "ping_3";
            } else if (ms < 1000) {
                location += "ping_2";
            } else {
                location += "ping_1";
            }
            return resourceManager.get().getTextureManager().getTexture(location).getTexture(true);
        }
    }

    public static BufferedImage getAdvancementFrame(AdvancementType advancementType, boolean completed) {
        if (advancementType.isLegacy()) {
            BufferedImage icons = resourceManager.get().getTextureManager().getTexture(ResourceRegistry.GUI_TEXTURE_LOCATION + "achievement/achievement_background").getTexture();
            int scale = icons.getWidth() / 256;
            return icons.getSubimage(0, 202 * scale, 26 * scale, 26 * scale);
        } else if (resourceManager.get().getNativeServerPackFormat() < 16) {
            BufferedImage icons = resourceManager.get().getTextureManager().getTexture(ResourceRegistry.GUI_TEXTURE_LOCATION + "advancements/widgets").getTexture();
            int scale = icons.getWidth() / 256;
            int offsetY = completed ? 0 : 26 * scale;
            switch (advancementType) {
                case CHALLENGE:
                    return icons.getSubimage(26 * scale, 128 * scale + offsetY, 26 * scale, 26 * scale);
                case GOAL:
                    return icons.getSubimage(52 * scale, 128 * scale + offsetY, 26 * scale, 26 * scale);
                case TASK:
                default:
                    return icons.getSubimage(0, 128 * scale + offsetY, 26 * scale, 26 * scale);
            }
        } else {
            String location = ResourceRegistry.DEFAULT_SPRITE_LOCATION + "advancements/";
            switch (advancementType) {
                case CHALLENGE: {
                    location += (completed ? "challenge_frame_obtained" : "challenge_frame_unobtained");
                    break;
                }
                case GOAL: {
                    location += (completed ? "goal_frame_obtained" : "goal_frame_unobtained");
                    break;
                }
                case TASK:
                default: {
                    location += (completed ? "task_frame_obtained" : "task_frame_unobtained");
                    break;
                }
            }
            return resourceManager.get().getTextureManager().getTexture(location).getTexture(true);
        }
    }

    public static BufferedImage getBundleContainerInterface(OfflineICPlayer offlineICPlayer, List<ItemStack> items) throws IOException {
        int packFormat = resourceManager.get().getNativeServerPackFormat();

        if (packFormat >= 39) {
            Fraction weight = BundleUtils.getWeight(items);
            int h = items.size() / 4;
            int padToFour = items.size() % 4;
            if (padToFour != 0) {
                padToFour = 4 - padToFour;
                h++;
            }
            BufferedImage itemBackground = resourceManager.get().getTextureManager().getTexture(ResourceRegistry.DEFAULT_SPRITE_LOCATION + "container/bundle/slot_background").getTexture(48, 48);
            BufferedImage image = new BufferedImage(192, 48 * h + 42, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = image.createGraphics();
            for (int i = 0; i < items.size(); i++) {
                BufferedImage itemImage = getSingleRawItemImage(items.get(i), offlineICPlayer);
                int gridPosition = i + padToFour;
                if (ResourceRegistry.RESOURCE_PACK_VERSION >= 46) {
                    g.drawImage(itemBackground, gridPosition % 4 * 48, gridPosition / 4 * 48 + 4, null);
                }
                g.drawImage(itemImage, gridPosition % 4 * 48 + 8, gridPosition / 4 * 48 + 12, null);
            }
            if (ResourceRegistry.RESOURCE_PACK_VERSION < 46) {
                g.setColor(weight.compareTo(Fraction.ONE) >= 0 ? BUNDLE_WEIGHT_FULL_COLOR : BUNDLE_WEIGHT_COLOR);
                g.fillRect(2, image.getHeight() - 28, (int) Math.ceil(weight.doubleValue() * 188), 22);
                g.setColor(BUNDLE_WEIGHT_OUTLINE_COLOR);
                g.fillRect(2, image.getHeight() - 30, 188, 2);
                g.fillRect(0, image.getHeight() - 28, 2, 22);
                g.fillRect(2, image.getHeight() - 28, 2, 2);
                g.fillRect(2, image.getHeight() - 8, 2, 2);
                g.fillRect(2, image.getHeight() - 6, 188, 2);
                g.fillRect(image.getWidth() - 2, image.getHeight() - 28, 2, 22);
                g.fillRect(image.getWidth() - 4, image.getHeight() - 28, 2, 2);
                g.fillRect(image.getWidth() - 4, image.getHeight() - 8, 2, 2);
            } else {
                BufferedImage inside = resourceManager.get().getTextureManager().getTexture(ResourceRegistry.DEFAULT_SPRITE_LOCATION + "container/bundle/bundle_progressbar_" + (weight.compareTo(Fraction.ONE) >= 0 ? "full" : "fill")).getScaledTexture(12, 12, (int) Math.ceil(weight.doubleValue() * 188) + 2, 26);
                BufferedImage border = resourceManager.get().getTextureManager().getTexture(ResourceRegistry.DEFAULT_SPRITE_LOCATION + "container/bundle/bundle_progressbar_border").getScaledTexture(24, 24, 192, 26);
                g.drawImage(inside, 0, image.getHeight() - 30, null);
                g.drawImage(border, 0, image.getHeight() - 30, null);
            }

            Component bundleFullnessMessage;
            if (weight.compareTo(Fraction.ONE) >= 0) {
                bundleFullnessMessage = Component.translatable(TranslationKeyUtils.getBundleFull());
            } else if (weight.compareTo(Fraction.ZERO) <= 0) {
                bundleFullnessMessage = Component.translatable(TranslationKeyUtils.getBundleEmpty());
            } else {
                bundleFullnessMessage = null;
            }

            if (bundleFullnessMessage != null) {
                BufferedImage textImage = new BufferedImage(2048, 512, BufferedImage.TYPE_INT_ARGB);
                ImageUtils.printComponent(resourceManager.get(), textImage, bundleFullnessMessage, InteractiveChatDiscordSrvAddon.plugin.language, version.get().isLegacyRGB(), 512, 256, 16);
                int lastX = 512;
                for (int x = 512; x < textImage.getWidth(); x++) {
                    for (int y = 0; y < textImage.getHeight(); y++) {
                        if (textImage.getRGB(x, y) != 0) {
                            lastX = x;
                            break;
                        }
                    }
                }
                int textCenter = (lastX - 512) / 2 + 512;
                g.drawImage(textImage, 96 - textCenter, image.getHeight() - 281, null);
            }

            g.dispose();
            return image;
        } else {
            int gridWidth = BundleUtils.getLegacyContainerGridSizeX(items.size());
            int gridHeight = BundleUtils.getLegacyContainerGridSizeY(items.size());
            boolean isFull = BundleUtils.getWeight(items).compareTo(Fraction.ONE) >= 0;

            BufferedImage image = new BufferedImage(36 * gridWidth + 4, 40 * gridHeight + 4, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = image.createGraphics();

            if (packFormat < 16) {
                BufferedImage icons = resourceManager.get().getTextureManager().getTexture(ResourceRegistry.GUI_TEXTURE_LOCATION + "container/bundle").getTexture(256, 256);

                BufferedImage topCorner = icons.getSubimage(0, 40, 2, 2);
                g.drawImage(topCorner, 0, 0, null);
                g.drawImage(topCorner, image.getWidth() - 2, 0, null);

                BufferedImage horizontalTop = icons.getSubimage(0, 40, 36, 2);
                for (int x = 2; x < image.getWidth() - 2; x += horizontalTop.getWidth()) {
                    g.drawImage(horizontalTop, x, 0, null);
                }

                BufferedImage vertical = icons.getSubimage(0, 36, 2, 40);
                for (int y = 2; y < image.getHeight() - 2; y += vertical.getHeight()) {
                    g.drawImage(vertical, 0, y, null);
                    g.drawImage(vertical, image.getWidth() - 2, y, null);
                }

                BufferedImage slot = icons.getSubimage(0, 0, 36, 40);
                BufferedImage fullSlot = icons.getSubimage(0, 80, 36, 40);

                int i = -1;
                for (int y = 2; y < image.getHeight() - 2; y += vertical.getHeight()) {
                    for (int x = 2; x < image.getWidth() - 2; x += horizontalTop.getWidth()) {
                        i++;
                        if (i < items.size()) {
                            g.drawImage(slot, x, y, null);
                            BufferedImage itemImage = getSingleRawItemImage(items.get(i), offlineICPlayer);
                            g.drawImage(itemImage, x + 2, y + 2, null);
                        } else {
                            g.drawImage(isFull ? fullSlot : slot, x, y, null);
                        }
                    }
                }

                BufferedImage horizontalBottom = icons.getSubimage(0, 120, 36, 2);
                for (int x = 2; x < image.getWidth() - 2; x += horizontalTop.getWidth()) {
                    g.drawImage(horizontalBottom, x, image.getHeight() - 2, null);
                }

                BufferedImage bottomCorner = icons.getSubimage(0, 120, 2, 2);
                g.drawImage(bottomCorner, 0, image.getHeight() - 2, null);
                g.drawImage(bottomCorner, image.getWidth() - 2, image.getHeight() - 2, null);
            } else {
                BufferedImage icons = resourceManager.get().getTextureManager().getTexture(ResourceRegistry.DEFAULT_SPRITE_LOCATION + "container/bundle/background").getTexture(64, 64);

                BufferedImage topCorner = icons.getSubimage(0, 0, 2, 2);
                g.drawImage(topCorner, 0, 0, null);
                g.drawImage(topCorner, image.getWidth() - 2, 0, null);

                BufferedImage horizontalTop = icons.getSubimage(0, 0, 36, 2);
                for (int x = 2; x < image.getWidth() - 2; x += horizontalTop.getWidth()) {
                    g.drawImage(horizontalTop, x, 0, null);
                }

                BufferedImage vertical = icons.getSubimage(0, 0, 2, 40);
                for (int y = 2; y < image.getHeight() - 2; y += vertical.getHeight()) {
                    g.drawImage(vertical, 0, y, null);
                    g.drawImage(vertical, image.getWidth() - 2, y, null);
                }

                BufferedImage slot = resourceManager.get().getTextureManager().getTexture(ResourceRegistry.DEFAULT_SPRITE_LOCATION + "container/bundle/slot").getTexture(36, 40);
                BufferedImage fullSlot = resourceManager.get().getTextureManager().getTexture(ResourceRegistry.DEFAULT_SPRITE_LOCATION + "container/bundle/blocked_slot").getTexture(36, 40);
                BufferedImage between = icons.getSubimage(0, 0, 36, 4);

                int i = -1;
                for (int y = 2; y < image.getHeight() - 2; y += vertical.getHeight()) {
                    for (int x = 2; x < image.getWidth() - 2; x += horizontalTop.getWidth()) {
                        i++;
                        if (i < items.size()) {
                            g.drawImage(slot, x, y, null);
                            g.drawImage(between, x, y + 36, null);
                            BufferedImage itemImage = getSingleRawItemImage(items.get(i), offlineICPlayer);
                            g.drawImage(itemImage, x + 2, y + 2, null);
                        } else {
                            g.drawImage(isFull ? fullSlot : slot, x, y, null);
                            g.drawImage(between, x, y + 36, null);
                        }
                    }
                }

                BufferedImage horizontalBottom = icons.getSubimage(0, 62, 36, 2);
                for (int x = 2; x < image.getWidth() - 2; x += horizontalTop.getWidth()) {
                    g.drawImage(horizontalBottom, x, image.getHeight() - 2, null);
                }

                BufferedImage bottomCorner = icons.getSubimage(0, 62, 2, 2);
                g.drawImage(bottomCorner, 0, image.getHeight() - 2, null);
                g.drawImage(bottomCorner, image.getWidth() - 2, image.getHeight() - 2, null);
            }

            g.dispose();
            return image;
        }
    }

    public static Future<List<BufferedImage>> getBookInterface(List<Component> pages) {
        CompletableFuture<List<BufferedImage>> future = new CompletableFuture<>();
        List<Supplier<BufferedImage>> suppliers = getBookInterfaceSuppliers(pages);
        Bukkit.getScheduler().runTaskAsynchronously(InteractiveChatDiscordSrvAddon.plugin, () -> future.complete(suppliers.stream().map(each -> each.get()).collect(Collectors.toList())));
        return future;
    }

    public static List<Supplier<BufferedImage>> getBookInterfaceSuppliers(List<Component> pages) {
        BufferedImage icons = resourceManager.get().getTextureManager().getTexture(ResourceRegistry.GUI_TEXTURE_LOCATION + "book").getTexture(512, 512);
        BufferedImage background = ImageUtils.copyAndGetSubImage(icons, 38, 0, 296, 364);
        BufferedImage nextPage;
        BufferedImage previousPage;
        if (resourceManager.get().getNativeServerPackFormat() < 16) {
            nextPage = ImageUtils.copyAndGetSubImage(icons, 0, 384, 46, 26);
            previousPage = ImageUtils.copyAndGetSubImage(icons, 0, 410, 46, 26);
        } else {
            nextPage = resourceManager.get().getTextureManager().getTexture(ResourceRegistry.DEFAULT_SPRITE_LOCATION + "widget/page_forward").getTexture(46, 26);
            previousPage = resourceManager.get().getTextureManager().getTexture(ResourceRegistry.DEFAULT_SPRITE_LOCATION + "widget/page_backward").getTexture(46, 26);
        }
        int totalPages = pages.size();
        List<Supplier<BufferedImage>> result = new ArrayList<>(totalPages);
        int i = 0;
        for (Component component : pages) {
            int pageNumber = ++i;
            result.add(() -> {
                BufferedImage page = ImageUtils.copyImage(background);
                Graphics2D g = page.createGraphics();
                if (pageNumber < totalPages) {
                    g.drawImage(nextPage, 195, 313, null);
                }
                if (pageNumber > 1) {
                    g.drawImage(previousPage, 48, 313, null);
                }
                Component pageHeader = Component.translatable(TranslationKeyUtils.getBookPageIndicator()).arguments(Component.text(pageNumber), Component.text(totalPages)).color(NamedTextColor.BLACK);
                ImageUtils.printComponentRightAligned(resourceManager.get(), page, pageHeader, InteractiveChatDiscordSrvAddon.plugin.language, InteractiveChat.version.isLegacyRGB(), 255, 30, 16, Double.NEGATIVE_INFINITY);

                BufferedImage temp = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);

                List<Component> lines = new ArrayList<>();
                lines.addAll(ComponentStringUtils.applyWordWrap(component, resourceManager.get().getLanguageManager().getTranslateFunction().ofLanguage(InteractiveChatDiscordSrvAddon.plugin.language), BOOK_LINE_LIMIT, new ToIntFunction<CharacterLengthProviderData>() {
                    int lastItalicExtraWidth = 0;

                    @Override
                    public int applyAsInt(CharacterLengthProviderData data) {
                        String character = data.getCharacter();
                        FontRenderResult renderResult = resourceManager.get().getFontManager().getFontProviders(data.getFont()).forCharacter(character).printCharacter(temp, character, 0, 0, 16, lastItalicExtraWidth, NamedTextColor.BLACK.value(), data.getDecorations());
                        lastItalicExtraWidth = renderResult.getItalicExtraWidth();
                        return renderResult.getWidth() + renderResult.getSpaceWidth();
                    }
                }));

                int y = 58;
                for (Component each : lines.subList(0, Math.min(lines.size(), BOOK_MAX_LINES))) {
                    each = each.colorIfAbsent(NamedTextColor.BLACK);
                    ImageUtils.printComponent(resourceManager.get(), page, each, InteractiveChatDiscordSrvAddon.plugin.language, InteractiveChat.version.isLegacyRGB(), 34, y, 16, Double.NEGATIVE_INFINITY);
                    y += 18;
                }

                g.dispose();
                return page;
            });
        }

        return result;
    }

    public static BufferedImage getPaintingImage(PaintingVariant paintingVariant) {
        BufferedImage originalPaintingImage;
        if (resourceManager.get().getNativeServerPackFormat() >= 24) {
            originalPaintingImage = resourceManager.get().getTextureManager().getTexture(paintingVariant.getKey().asString()).getTexture();
        } else if (InteractiveChat.version.isNewerOrEqualTo(MCVersion.V1_14)) {
            originalPaintingImage = resourceManager.get().getTextureManager().getTexture(ResourceRegistry.LEGACY_PAINTINGS_LOCATION + paintingVariant.getKey().value()).getTexture();
        } else {
            BufferedImage base = resourceManager.get().getTextureManager().getTexture(ResourceRegistry.LEGACY_SINGLE_PAINTING_LOCATION).getTexture();
            int scale = base.getWidth() / 256;
            originalPaintingImage = base.getSubimage(paintingVariant.getOffsetX() * scale, paintingVariant.getOffsetY() * scale, paintingVariant.getPixelWidth() * scale, paintingVariant.getPixelHeight() * scale);
        }
        int width = Math.min(243, paintingVariant.getPixelWidth() * 7);
        return ImageUtils.resizeImageFillWidth(originalPaintingImage, width);
    }

    public static class GenericContainerBackgroundResult {

        private final BufferedImage image;
        private final int expandedX;
        private final int expandedY;

        private GenericContainerBackgroundResult(BufferedImage image, int expandedX, int expandedY) {
            this.image = image;
            this.expandedX = expandedX;
            this.expandedY = expandedY;
        }

        public BufferedImage getBackgroundImage() {
            return image;
        }

        public int getExpandedX() {
            return expandedX;
        }

        public int getExpandedY() {
            return expandedY;
        }

    }

}