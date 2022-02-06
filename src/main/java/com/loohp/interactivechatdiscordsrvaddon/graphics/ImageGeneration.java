package com.loohp.interactivechatdiscordsrvaddon.graphics;

import com.loohp.interactivechat.InteractiveChat;
import com.loohp.interactivechat.libs.com.cryptomorin.xseries.XMaterial;
import com.loohp.interactivechat.libs.io.github.bananapuncher714.nbteditor.NBTEditor;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.Component;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.format.NamedTextColor;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import com.loohp.interactivechat.libs.org.json.simple.JSONArray;
import com.loohp.interactivechat.libs.org.json.simple.JSONObject;
import com.loohp.interactivechat.libs.org.json.simple.parser.JSONParser;
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
import com.loohp.interactivechat.utils.ItemStackUtils;
import com.loohp.interactivechat.utils.MCVersion;
import com.loohp.interactivechat.utils.SkinUtils;
import com.loohp.interactivechat.utils.XMaterialUtils;
import com.loohp.interactivechatdiscordsrvaddon.Cache;
import com.loohp.interactivechatdiscordsrvaddon.InteractiveChatDiscordSrvAddon;
import com.loohp.interactivechatdiscordsrvaddon.debug.Debug;
import com.loohp.interactivechatdiscordsrvaddon.registry.ResourceRegistry;
import com.loohp.interactivechatdiscordsrvaddon.resources.ModelRenderer.PlayerModelItem;
import com.loohp.interactivechatdiscordsrvaddon.resources.ModelRenderer.PlayerModelItemPosition;
import com.loohp.interactivechatdiscordsrvaddon.resources.ModelRenderer.RenderResult;
import com.loohp.interactivechatdiscordsrvaddon.resources.models.ModelDisplay.ModelDisplayPosition;
import com.loohp.interactivechatdiscordsrvaddon.resources.models.ModelOverride.ModelOverrideType;
import com.loohp.interactivechatdiscordsrvaddon.resources.textures.GeneratedTextureResource;
import com.loohp.interactivechatdiscordsrvaddon.resources.textures.TextureManager;
import com.loohp.interactivechatdiscordsrvaddon.resources.textures.TextureResource;
import com.loohp.interactivechatdiscordsrvaddon.utils.ItemRenderUtils;
import com.loohp.interactivechatdiscordsrvaddon.utils.ItemRenderUtils.ItemStackProcessResult;
import com.loohp.interactivechatdiscordsrvaddon.utils.ModelUtils;
import com.loohp.interactivechatdiscordsrvaddon.utils.TintUtils.TintIndexData;
import com.loohp.interactivechatdiscordsrvaddon.wrappers.ItemMapWrapper;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.map.MapCursor;
import org.bukkit.map.MapPalette;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

@SuppressWarnings("deprecation")
public class ImageGeneration {

    public static final int MAP_ICON_PER_ROLE = InteractiveChat.version.isLegacy() ? 4 : 16;
    public static final int SPACING = 36;
    public static final double ITEM_AMOUNT_TEXT_DARKEN_FACTOR = 75.0 / 255.0;
    public static final Color ENCHANTMENT_GLINT_LEGACY_COLOR = new Color(164, 84, 255);
    public static final String PLAYER_CAPE_CACHE_KEY = "PlayerCapeTexture";
    public static final String PLAYER_SKIN_CACHE_KEY = "PlayerSkinTexture";
    public static final String INVENTORY_CACHE_KEY = "Inventory";
    public static final String PLAYER_INVENTORY_CACHE_KEY = "PlayerInventory";
    public static final int TABLIST_SINGLE_COLUMN_LIMIT = 24;
    public static final Color TABLIST_BACKGROUND = new Color(68, 68, 68);
    public static final Color TABLIST_PLAYER_BACKGROUND = new Color(107, 107, 107);
    private static final String OPTIFINE_CAPE_URL = "https://optifine.net/capes/%s.png";
    private static final String PLAYER_INFO_URL = "https://sessionserver.mojang.com/session/minecraft/profile/%s";

    public static BufferedImage getMissingImage(int width, int length) {
        return TextureManager.getMissingImage(width, length);
    }

    public static BufferedImage getRawEnchantedImage(BufferedImage source) {
        BufferedImage tintOriginal = InteractiveChatDiscordSrvAddon.plugin.resourceManager.getTextureManager().getTexture(ResourceRegistry.MISC_TEXTURE_LOCATION + "enchanted_item_glint").getTexture();
        if (InteractiveChat.version.isOlderOrEqualTo(MCVersion.V1_14)) {
            BufferedImage tinted = ImageUtils.changeColorTo(ImageUtils.copyImage(tintOriginal), ENCHANTMENT_GLINT_LEGACY_COLOR);
            tintOriginal = ImageUtils.multiply(tintOriginal, tinted);
        }
        BufferedImage tintImage = new BufferedImage(source.getWidth(), source.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g3 = tintImage.createGraphics();
        g3.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g3.drawImage(tintOriginal, 0, 0, tintImage.getWidth() * 4, tintImage.getHeight() * 4, null);
        g3.dispose();

        return tintImage;
    }

    public static BufferedImage getEnchantedImage(BufferedImage source) {
        return ImageUtils.additionNonTransparent(source, getRawEnchantedImage(source), ResourceRegistry.ENCHANTMENT_GLINT_FACTOR);
    }

    public static BufferedImage getItemStackImage(ItemStack item, OfflineICPlayer player) throws IOException {
        return getItemStackImage(item, player, false);
    }

    public static BufferedImage getItemStackImage(ItemStack item, OfflineICPlayer player, boolean alternateAir) throws IOException {
        InteractiveChatDiscordSrvAddon.plugin.imageCounter.incrementAndGet();
        Debug.debug("ImageGeneration creating item stack image " + item);

        BufferedImage background = new BufferedImage(36, 36, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = background.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);

        BufferedImage itemImage;
        if (item == null || item.getType().equals(Material.AIR)) {
            if (alternateAir) {
                itemImage = InteractiveChatDiscordSrvAddon.plugin.resourceManager.getTextureManager().getTexture(ResourceRegistry.IC_BLOCK_TEXTURE_LOCATION + "air_alternate").getTexture(32, 32);
            } else {
                g.dispose();
                return background;
            }
        } else {
            itemImage = getRawItemImage(item, player);
        }

        if (itemImage != null) {
            g.drawImage(itemImage, 0, 0, null);
        }
        g.dispose();

        return background;
    }

    public static BufferedImage getInventoryImage(Inventory inventory, OfflineICPlayer player) throws Exception {
        InteractiveChatDiscordSrvAddon.plugin.imageCounter.incrementAndGet();
        InteractiveChatDiscordSrvAddon.plugin.inventoryImageCounter.incrementAndGet();
        Debug.debug("ImageGeneration creating inventory image of " + player.getName());

        String key = INVENTORY_CACHE_KEY + HashUtils.createSha1("Inventory", inventory);
        if (!inventory.contains(XMaterial.COMPASS.parseMaterial()) && !inventory.contains(XMaterial.CLOCK.parseMaterial()) && Stream.of(inventory.getContents()).anyMatch(each -> each != null && NBTEditor.contains(each, "CustomModelData"))) {
            Cache<?> cache = Cache.getCache(key);
            if (cache != null) {
                return ImageUtils.copyImage((BufferedImage) cache.getObject());
            }
        }

        int rows = inventory.getSize() / 9;
        BufferedImage background = InteractiveChatDiscordSrvAddon.plugin.resourceManager.getTextureManager().getTexture(ResourceRegistry.IC_GUI_TEXTURE_LOCATION + rows + "_rows").getTexture();

        BufferedImage target = new BufferedImage(background.getWidth(), background.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = target.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g.drawImage(background, 0, 0, null);

        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack item = inventory.getItem(i);
            if (item == null || item.getType().equals(Material.AIR)) {
                continue;
            }

            BufferedImage itemImage = getRawItemImage(item, player);

            if (itemImage != null) {
                g.drawImage(itemImage, 18 + (SPACING * (i % 9)), 18 + (SPACING * (i / 9)), null);
            }
        }
        g.dispose();

        Cache.putCache(key, target, InteractiveChatDiscordSrvAddon.plugin.cacheTimeout);

        return target;
    }

    public static BufferedImage getPlayerInventoryImage(Inventory inventory, OfflineICPlayer player) throws Exception {
        InteractiveChatDiscordSrvAddon.plugin.imageCounter.incrementAndGet();
        InteractiveChatDiscordSrvAddon.plugin.inventoryImageCounter.incrementAndGet();
        Debug.debug("ImageGeneration creating player inventory image of " + player.getName());

        Object playerInventoryData = player.getProperty("player_inventory");
        BufferedImage background;
        if (playerInventoryData != null && playerInventoryData instanceof BufferedImage) {
            background = ImageUtils.copyImage((BufferedImage) playerInventoryData);
        } else {
            background = InteractiveChatDiscordSrvAddon.plugin.resourceManager.getTextureManager().getTexture(ResourceRegistry.IC_GUI_TEXTURE_LOCATION + "player_inventory").getTexture();
        }

        String key = PLAYER_INVENTORY_CACHE_KEY + HashUtils.createSha1(player.getUniqueId().toString(), inventory) + ImageUtils.hash(background);
        if (!inventory.contains(XMaterial.COMPASS.parseMaterial()) && !inventory.contains(XMaterial.CLOCK.parseMaterial()) && Stream.of(inventory.getContents()).anyMatch(each -> each != null && NBTEditor.contains(each, "CustomModelData"))) {
            Cache<?> cache = Cache.getCache(key);
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

            BufferedImage itemImage = getRawItemImage(item, player);

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

            BufferedImage itemImage = getRawItemImage(item, player);

            if (itemImage != null) {
                g.drawImage(itemImage, 18 + (SPACING * (i % 9)), 170 + (SPACING * ((i - 9) / 9)), null);
            }
        }

        //boots
        ItemStack boots = inventory.getItem(i);
        if (boots == null || boots.getType().equals(Material.AIR)) {
            g.drawImage(InteractiveChatDiscordSrvAddon.plugin.resourceManager.getTextureManager().getTexture(ResourceRegistry.ITEM_TEXTURE_LOCATION + "empty_armor_slot_boots").getTexture(32, 32), 18, 126 - (SPACING * (i - 36)), 32, 32, null);
        } else {
            BufferedImage itemImage = getRawItemImage(boots, player);
            if (itemImage != null) {
                g.drawImage(itemImage, 18, 126 - (SPACING * (i - 36)), null);
            }
        }
        i++;

        //leggings
        ItemStack leggings = inventory.getItem(i);
        if (leggings == null || leggings.getType().equals(Material.AIR)) {
            g.drawImage(InteractiveChatDiscordSrvAddon.plugin.resourceManager.getTextureManager().getTexture(ResourceRegistry.ITEM_TEXTURE_LOCATION + "empty_armor_slot_leggings").getTexture(32, 32), 18, 126 - (SPACING * (i - 36)), 32, 32, null);
        } else {
            BufferedImage itemImage = getRawItemImage(leggings, player);
            if (itemImage != null) {
                g.drawImage(itemImage, 18, 126 - (SPACING * (i - 36)), null);
            }
        }
        i++;

        //chestplate
        ItemStack chestplate = inventory.getItem(i);
        if (chestplate == null || chestplate.getType().equals(Material.AIR)) {
            g.drawImage(InteractiveChatDiscordSrvAddon.plugin.resourceManager.getTextureManager().getTexture(ResourceRegistry.ITEM_TEXTURE_LOCATION + "empty_armor_slot_chestplate").getTexture(32, 32), 18, 126 - (SPACING * (i - 36)), 32, 32, null);
        } else {
            BufferedImage itemImage = getRawItemImage(chestplate, player);
            if (itemImage != null) {
                g.drawImage(itemImage, 18, 126 - (SPACING * (i - 36)), null);
            }
        }
        i++;

        //helmet
        ItemStack helmet = inventory.getItem(i);
        if (helmet == null || helmet.getType().equals(Material.AIR)) {
            g.drawImage(InteractiveChatDiscordSrvAddon.plugin.resourceManager.getTextureManager().getTexture(ResourceRegistry.ITEM_TEXTURE_LOCATION + "empty_armor_slot_helmet").getTexture(32, 32), 18, 126 - (SPACING * (i - 36)), 32, 32, null);
        } else {
            BufferedImage itemImage = getRawItemImage(helmet, player);
            if (itemImage != null) {
                g.drawImage(itemImage, 18, 126 - (SPACING * (i - 36)), null);
            }
        }
        i++;

        //offhand
        if (!InteractiveChat.version.isOld()) {
            ItemStack offhand = inventory.getItem(i);
            if (offhand == null || offhand.getType().equals(Material.AIR)) {
                g.drawImage(InteractiveChatDiscordSrvAddon.plugin.resourceManager.getTextureManager().getTexture(ResourceRegistry.ITEM_TEXTURE_LOCATION + "empty_armor_slot_shield").getTexture(32, 32), 162, 126, 32, 32, null);
            } else {
                BufferedImage itemImage = getRawItemImage(offhand, player);
                if (itemImage != null) {
                    g.drawImage(itemImage, 162, 126, null);
                }
            }
        }

        //puppet
        EntityEquipment equipment = player.getEquipment();
        BufferedImage puppet = getFullBodyImage(player, equipment.getHelmet(), equipment.getChestplate(), equipment.getLeggings(), equipment.getBoots());
        g.drawImage(puppet, 45, -10, null);

        g.dispose();

        Cache.putCache(key, target, InteractiveChatDiscordSrvAddon.plugin.cacheTimeout);

        return target;
    }

    private static BufferedImage getFullBodyImage(OfflineICPlayer player, ItemStack helmet, ItemStack chestplate, ItemStack leggings, ItemStack boots) throws IOException {
        InteractiveChatDiscordSrvAddon.plugin.imageCounter.incrementAndGet();
        Debug.debug("ImageGeneration creating puppet image of " + player.getName());

        BufferedImage skin = null;
        boolean slim = false;
        BufferedImage cape;
        try {
            JSONObject json;
            if (player instanceof ICPlayer && ((ICPlayer) player).isLocal()) {
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
                        Cache<?> cache = Cache.getCache(player.getUniqueId().toString() + url + PLAYER_CAPE_CACHE_KEY);
                        if (cache == null) {
                            cape = ImageUtils.downloadImage(url);
                            Cache.putCache(player.getUniqueId().toString() + url + PLAYER_CAPE_CACHE_KEY, cape, InteractiveChatDiscordSrvAddon.plugin.cacheTimeout);
                        } else {
                            cape = (BufferedImage) cache.getObject();
                        }
                    } else {
                        String url = OPTIFINE_CAPE_URL.replaceAll("%s", CustomStringUtils.escapeReplaceAllMetaCharacters(player.getName()));
                        Cache<?> cache = Cache.getCache(player.getUniqueId().toString() + url + PLAYER_CAPE_CACHE_KEY);
                        if (cache == null) {
                            try {
                                cape = ImageUtils.downloadImage(url);
                                Cache.putCache(player.getUniqueId().toString() + url + PLAYER_CAPE_CACHE_KEY, cape, InteractiveChatDiscordSrvAddon.plugin.cacheTimeout);
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
                    Cache<?> cache = Cache.getCache(player.getUniqueId().toString() + value + PLAYER_SKIN_CACHE_KEY);
                    if (cache == null) {
                        skin = ImageUtils.downloadImage(value);
                        Cache.putCache(player.getUniqueId().toString() + value + PLAYER_SKIN_CACHE_KEY, skin, InteractiveChatDiscordSrvAddon.plugin.cacheTimeout);
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
                skin = InteractiveChatDiscordSrvAddon.plugin.resourceManager.getTextureManager().getTexture(ResourceRegistry.ENTITY_TEXTURE_LOCATION + "alex").getTexture();
            } else {
                skin = InteractiveChatDiscordSrvAddon.plugin.resourceManager.getTextureManager().getTexture(ResourceRegistry.ENTITY_TEXTURE_LOCATION + "steve").getTexture();
            }
        }

        BufferedImage elytraImage = null;
        BufferedImage image = new BufferedImage(556, 748, BufferedImage.TYPE_INT_ARGB);
        Map<String, TextureResource> providedTextures = new HashMap<>();
        Map<PlayerModelItemPosition, PlayerModelItem> modelItems = new HashMap<>();

        providedTextures.put(ResourceRegistry.SKIN_FULL_TEXTURE_PLACEHOLDER, new GeneratedTextureResource(skin));

        if (ItemStackUtils.isWearable(leggings)) {
            XMaterial type = XMaterialUtils.matchXMaterial(leggings);
            BufferedImage leggingsImage = null;
            switch (type) {
                case LEATHER_LEGGINGS:
                    leggingsImage = InteractiveChatDiscordSrvAddon.plugin.resourceManager.getTextureManager().getTexture(ResourceRegistry.ARMOR_TEXTURE_LOCATION + "leather_layer_2").getTexture();
                    LeatherArmorMeta meta = (LeatherArmorMeta) leggings.getItemMeta();
                    Color color = new Color(meta.getColor().asRGB());
                    BufferedImage armorOverlay = InteractiveChatDiscordSrvAddon.plugin.resourceManager.getTextureManager().getTexture(ResourceRegistry.ARMOR_TEXTURE_LOCATION + "leather_layer_2_overlay").getTexture();
                    BufferedImage colorOverlay = ImageUtils.changeColorTo(ImageUtils.copyImage(leggingsImage), color);
                    leggingsImage = ImageUtils.multiply(leggingsImage, colorOverlay);

                    Graphics2D g2 = leggingsImage.createGraphics();
                    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
                    g2.drawImage(armorOverlay, 0, 0, null);
                    g2.dispose();
                    break;
                case CHAINMAIL_LEGGINGS:
                    leggingsImage = InteractiveChatDiscordSrvAddon.plugin.resourceManager.getTextureManager().getTexture(ResourceRegistry.ARMOR_TEXTURE_LOCATION + "chainmail_layer_2").getTexture();
                    break;
                case GOLDEN_LEGGINGS:
                    leggingsImage = InteractiveChatDiscordSrvAddon.plugin.resourceManager.getTextureManager().getTexture(ResourceRegistry.ARMOR_TEXTURE_LOCATION + "gold_layer_2").getTexture();
                    break;
                case IRON_LEGGINGS:
                    leggingsImage = InteractiveChatDiscordSrvAddon.plugin.resourceManager.getTextureManager().getTexture(ResourceRegistry.ARMOR_TEXTURE_LOCATION + "iron_layer_2").getTexture();
                    break;
                case DIAMOND_LEGGINGS:
                    leggingsImage = InteractiveChatDiscordSrvAddon.plugin.resourceManager.getTextureManager().getTexture(ResourceRegistry.ARMOR_TEXTURE_LOCATION + "diamond_layer_2").getTexture();
                    break;
                case NETHERITE_LEGGINGS:
                    leggingsImage = InteractiveChatDiscordSrvAddon.plugin.resourceManager.getTextureManager().getTexture(ResourceRegistry.ARMOR_TEXTURE_LOCATION + "netherite_layer_2").getTexture();
                    break;
                default:
                    break;
            }
            if (leggingsImage != null) {
                if (leggings.getEnchantments().size() > 0) {
                    leggingsImage = getEnchantedImage(leggingsImage);
                }
                providedTextures.put(ResourceRegistry.LEGGINGS_TEXTURE_PLACEHOLDER, new GeneratedTextureResource(leggingsImage));
            }
        }

        if (ItemStackUtils.isWearable(boots)) {
            XMaterial type = XMaterialUtils.matchXMaterial(boots);
            BufferedImage bootsImage = null;
            switch (type) {
                case LEATHER_BOOTS:
                    bootsImage = InteractiveChatDiscordSrvAddon.plugin.resourceManager.getTextureManager().getTexture(ResourceRegistry.ARMOR_TEXTURE_LOCATION + "leather_layer_1").getTexture();
                    LeatherArmorMeta meta = (LeatherArmorMeta) boots.getItemMeta();
                    Color color = new Color(meta.getColor().asRGB());
                    BufferedImage armorOverlay = InteractiveChatDiscordSrvAddon.plugin.resourceManager.getTextureManager().getTexture(ResourceRegistry.ARMOR_TEXTURE_LOCATION + "leather_layer_1_overlay").getTexture();
                    BufferedImage colorOverlay = ImageUtils.changeColorTo(ImageUtils.copyImage(bootsImage), color);

                    bootsImage = ImageUtils.multiply(bootsImage, colorOverlay);

                    Graphics2D g2 = bootsImage.createGraphics();
                    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
                    g2.drawImage(armorOverlay, 0, 0, null);
                    g2.dispose();
                    break;
                case CHAINMAIL_BOOTS:
                    bootsImage = InteractiveChatDiscordSrvAddon.plugin.resourceManager.getTextureManager().getTexture(ResourceRegistry.ARMOR_TEXTURE_LOCATION + "chainmail_layer_1").getTexture();
                    break;
                case GOLDEN_BOOTS:
                    bootsImage = InteractiveChatDiscordSrvAddon.plugin.resourceManager.getTextureManager().getTexture(ResourceRegistry.ARMOR_TEXTURE_LOCATION + "gold_layer_1").getTexture();
                    break;
                case IRON_BOOTS:
                    bootsImage = InteractiveChatDiscordSrvAddon.plugin.resourceManager.getTextureManager().getTexture(ResourceRegistry.ARMOR_TEXTURE_LOCATION + "iron_layer_1").getTexture();
                    break;
                case DIAMOND_BOOTS:
                    bootsImage = InteractiveChatDiscordSrvAddon.plugin.resourceManager.getTextureManager().getTexture(ResourceRegistry.ARMOR_TEXTURE_LOCATION + "diamond_layer_1").getTexture();
                    break;
                case NETHERITE_BOOTS:
                    bootsImage = InteractiveChatDiscordSrvAddon.plugin.resourceManager.getTextureManager().getTexture(ResourceRegistry.ARMOR_TEXTURE_LOCATION + "netherite_layer_1").getTexture();
                    break;
                default:
                    break;
            }
            if (bootsImage != null) {
                if (boots.getEnchantments().size() > 0) {
                    bootsImage = getEnchantedImage(bootsImage);
                }

                providedTextures.put(ResourceRegistry.BOOTS_TEXTURE_PLACEHOLDER, new GeneratedTextureResource(bootsImage));
            }
        }

        if (ItemStackUtils.isWearable(chestplate)) {
            XMaterial type = XMaterialUtils.matchXMaterial(chestplate);
            BufferedImage chestplateImage = null;
            boolean isArmor = true;
            switch (type) {
                case LEATHER_CHESTPLATE:
                    chestplateImage = InteractiveChatDiscordSrvAddon.plugin.resourceManager.getTextureManager().getTexture(ResourceRegistry.ARMOR_TEXTURE_LOCATION + "leather_layer_1").getTexture();
                    LeatherArmorMeta meta = (LeatherArmorMeta) chestplate.getItemMeta();
                    Color color = new Color(meta.getColor().asRGB());
                    BufferedImage armorOverlay = InteractiveChatDiscordSrvAddon.plugin.resourceManager.getTextureManager().getTexture(ResourceRegistry.ARMOR_TEXTURE_LOCATION + "leather_layer_1_overlay").getTexture();
                    BufferedImage colorOverlay = ImageUtils.changeColorTo(ImageUtils.copyImage(chestplateImage), color);
                    chestplateImage = ImageUtils.multiply(chestplateImage, colorOverlay);

                    Graphics2D g2 = chestplateImage.createGraphics();
                    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
                    g2.drawImage(armorOverlay, 0, 0, null);
                    g2.dispose();
                    break;
                case CHAINMAIL_CHESTPLATE:
                    chestplateImage = InteractiveChatDiscordSrvAddon.plugin.resourceManager.getTextureManager().getTexture(ResourceRegistry.ARMOR_TEXTURE_LOCATION + "chainmail_layer_1").getTexture();
                    break;
                case GOLDEN_CHESTPLATE:
                    chestplateImage = InteractiveChatDiscordSrvAddon.plugin.resourceManager.getTextureManager().getTexture(ResourceRegistry.ARMOR_TEXTURE_LOCATION + "gold_layer_1").getTexture();
                    break;
                case IRON_CHESTPLATE:
                    chestplateImage = InteractiveChatDiscordSrvAddon.plugin.resourceManager.getTextureManager().getTexture(ResourceRegistry.ARMOR_TEXTURE_LOCATION + "iron_layer_1").getTexture();
                    break;
                case DIAMOND_CHESTPLATE:
                    chestplateImage = InteractiveChatDiscordSrvAddon.plugin.resourceManager.getTextureManager().getTexture(ResourceRegistry.ARMOR_TEXTURE_LOCATION + "diamond_layer_1").getTexture();
                    break;
                case NETHERITE_CHESTPLATE:
                    chestplateImage = InteractiveChatDiscordSrvAddon.plugin.resourceManager.getTextureManager().getTexture(ResourceRegistry.ARMOR_TEXTURE_LOCATION + "netherite_layer_1").getTexture();
                    break;
                case ELYTRA:
                    isArmor = false;
                    chestplateImage = new BufferedImage(150, 150, BufferedImage.TYPE_INT_ARGB);
                    BufferedImage wing = cape == null ? InteractiveChatDiscordSrvAddon.plugin.resourceManager.getTextureManager().getTexture(ResourceRegistry.ENTITY_TEXTURE_LOCATION + "elytra").getTexture() : cape;
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

                    if (chestplate.getEnchantments().size() > 0) {
                        chestplateImage = getEnchantedImage(chestplateImage);
                    }
                    elytraImage = chestplateImage;
                default:
                    break;
            }
            if (isArmor && chestplateImage != null) {
                if (chestplate.getEnchantments().size() > 0) {
                    chestplateImage = getEnchantedImage(chestplateImage);
                }

                providedTextures.put(ResourceRegistry.CHESTPLATE_TEXTURE_PLACEHOLDER, new GeneratedTextureResource(chestplateImage));
            }
        }

        if (helmet != null && !helmet.getType().equals(Material.AIR)) {
            XMaterial type = XMaterialUtils.matchXMaterial(helmet);
            BufferedImage helmetImage = null;
            boolean isArmor = true;
            switch (type) {
                case LEATHER_HELMET:
                    helmetImage = InteractiveChatDiscordSrvAddon.plugin.resourceManager.getTextureManager().getTexture(ResourceRegistry.ARMOR_TEXTURE_LOCATION + "leather_layer_1").getTexture();
                    LeatherArmorMeta meta = (LeatherArmorMeta) helmet.getItemMeta();
                    Color color = new Color(meta.getColor().asRGB());
                    BufferedImage armorOverlay = InteractiveChatDiscordSrvAddon.plugin.resourceManager.getTextureManager().getTexture(ResourceRegistry.ARMOR_TEXTURE_LOCATION + "leather_layer_1_overlay").getTexture();
                    BufferedImage colorOverlay = ImageUtils.changeColorTo(ImageUtils.copyImage(helmetImage), color);

                    helmetImage = ImageUtils.multiply(helmetImage, colorOverlay);

                    Graphics2D g2 = helmetImage.createGraphics();
                    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
                    g2.drawImage(armorOverlay, 0, 0, null);
                    g2.dispose();
                    break;
                case CHAINMAIL_HELMET:
                    helmetImage = InteractiveChatDiscordSrvAddon.plugin.resourceManager.getTextureManager().getTexture(ResourceRegistry.ARMOR_TEXTURE_LOCATION + "chainmail_layer_1").getTexture();
                    break;
                case GOLDEN_HELMET:
                    helmetImage = InteractiveChatDiscordSrvAddon.plugin.resourceManager.getTextureManager().getTexture(ResourceRegistry.ARMOR_TEXTURE_LOCATION + "gold_layer_1").getTexture();
                    break;
                case IRON_HELMET:
                    helmetImage = InteractiveChatDiscordSrvAddon.plugin.resourceManager.getTextureManager().getTexture(ResourceRegistry.ARMOR_TEXTURE_LOCATION + "iron_layer_1").getTexture();
                    break;
                case DIAMOND_HELMET:
                    helmetImage = InteractiveChatDiscordSrvAddon.plugin.resourceManager.getTextureManager().getTexture(ResourceRegistry.ARMOR_TEXTURE_LOCATION + "diamond_layer_1").getTexture();
                    break;
                case NETHERITE_HELMET:
                    helmetImage = InteractiveChatDiscordSrvAddon.plugin.resourceManager.getTextureManager().getTexture(ResourceRegistry.ARMOR_TEXTURE_LOCATION + "netherite_layer_1").getTexture();
                    break;
                case TURTLE_HELMET:
                    helmetImage = InteractiveChatDiscordSrvAddon.plugin.resourceManager.getTextureManager().getTexture(ResourceRegistry.ARMOR_TEXTURE_LOCATION + "turtle_layer_1").getTexture();
                    break;
                default:
                    isArmor = false;
                    String key = ModelUtils.getItemModelKey(type);
                    ItemStackProcessResult itemProcessResult = ItemRenderUtils.processItemForRendering(player, helmet);
                    boolean enchanted = itemProcessResult.requiresEnchantmentGlint();
                    Map<ModelOverrideType, Float> predicate = itemProcessResult.getPredicates();
                    String modelKey = itemProcessResult.getDirectLocation() == null ? ResourceRegistry.ITEM_MODEL_LOCATION + key : itemProcessResult.getDirectLocation();
                    Map<String, TextureResource> itemProvidedTextures = itemProcessResult.getProvidedTextures();
                    TintIndexData tintIndexData = itemProcessResult.getTintIndexData();
                    modelItems.put(PlayerModelItemPosition.HELMET, new PlayerModelItem(PlayerModelItemPosition.HELMET, modelKey, predicate, enchanted, itemProvidedTextures, tintIndexData));
                    break;
            }
            if (isArmor) {
                if (helmet.getEnchantments().size() > 0) {
                    helmetImage = getEnchantedImage(helmetImage);
                }
                providedTextures.put(ResourceRegistry.HELMET_TEXTURE_PLACEHOLDER, new GeneratedTextureResource(helmetImage));
            }
        }

        if (InteractiveChatDiscordSrvAddon.plugin.renderHandHeldItems) {
            ItemStack rightHand = player.isRightHanded() ? player.getMainHandItem() : player.getOffHandItem();
            if (rightHand != null) {
                String key = ModelUtils.getItemModelKey(XMaterialUtils.matchXMaterial(rightHand));
                ItemStackProcessResult itemProcessResult = ItemRenderUtils.processItemForRendering(player, rightHand);
                boolean enchanted = itemProcessResult.requiresEnchantmentGlint();
                Map<ModelOverrideType, Float> predicate = itemProcessResult.getPredicates();
                String modelKey = itemProcessResult.getDirectLocation() == null ? ResourceRegistry.ITEM_MODEL_LOCATION + key : itemProcessResult.getDirectLocation();
                Map<String, TextureResource> itemProvidedTextures = itemProcessResult.getProvidedTextures();
                TintIndexData tintIndexData = itemProcessResult.getTintIndexData();
                modelItems.put(PlayerModelItemPosition.RIGHT_HAND, new PlayerModelItem(PlayerModelItemPosition.RIGHT_HAND, modelKey, predicate, enchanted, itemProvidedTextures, tintIndexData));
            }
            ItemStack leftHand = player.isRightHanded() ? player.getOffHandItem() : player.getMainHandItem();
            if (leftHand != null) {
                String key = ModelUtils.getItemModelKey(XMaterialUtils.matchXMaterial(leftHand));
                ItemStackProcessResult itemProcessResult = ItemRenderUtils.processItemForRendering(player, leftHand);
                boolean enchanted = itemProcessResult.requiresEnchantmentGlint();
                Map<ModelOverrideType, Float> predicate = itemProcessResult.getPredicates();
                String modelKey = itemProcessResult.getDirectLocation() == null ? ResourceRegistry.ITEM_MODEL_LOCATION + key : itemProcessResult.getDirectLocation();
                Map<String, TextureResource> itemProvidedTextures = itemProcessResult.getProvidedTextures();
                TintIndexData tintIndexData = itemProcessResult.getTintIndexData();
                modelItems.put(PlayerModelItemPosition.LEFT_HAND, new PlayerModelItem(PlayerModelItemPosition.LEFT_HAND, modelKey, predicate, enchanted, itemProvidedTextures, tintIndexData));
            }
        }

        RenderResult renderResult = InteractiveChatDiscordSrvAddon.plugin.modelRenderer.renderPlayer(image.getWidth(), image.getHeight(), InteractiveChatDiscordSrvAddon.plugin.resourceManager, InteractiveChat.version.isOld(), slim, providedTextures, TintIndexData.EMPTY_INSTANCE, modelItems);
        Graphics2D g = image.createGraphics();
        g.drawImage(ImageUtils.resizeImageAbs(renderResult.getImage(), 117, 159), -1, 12, null);
        g.dispose();

        if (elytraImage != null) {
            ImageUtils.drawTransparent(image, ImageUtils.resizeImage(elytraImage, 0.9), 14, 75);
        }

        return image;
    }

    private static BufferedImage getRawItemImage(ItemStack item, OfflineICPlayer player) throws IOException {
        InteractiveChatDiscordSrvAddon.plugin.imageCounter.incrementAndGet();
        Debug.debug("ImageGeneration creating raw item stack image " + (item == null ? "null" : ItemNBTUtils.getNMSItemStackJson(item)));

        XMaterial xMaterial = XMaterialUtils.matchXMaterial(item);
        int amount = item.getAmount();
        String key = ModelUtils.getItemModelKey(xMaterial);
        ItemStackProcessResult processResult = ItemRenderUtils.processItemForRendering(player, item);
        boolean requiresEnchantmentGlint = processResult.requiresEnchantmentGlint();
        Map<ModelOverrideType, Float> predicates = processResult.getPredicates();
        Map<String, TextureResource> providedTextures = processResult.getProvidedTextures();
        TintIndexData tintIndexData = processResult.getTintIndexData();
        String directLocation = processResult.getDirectLocation();

        BufferedImage itemImage;
        RenderResult renderResult = InteractiveChatDiscordSrvAddon.plugin.modelRenderer.render(32, 32, InteractiveChatDiscordSrvAddon.plugin.resourceManager, InteractiveChat.version.isOld(), directLocation == null ? ResourceRegistry.ITEM_MODEL_LOCATION + key : directLocation, ModelDisplayPosition.GUI, predicates, providedTextures, tintIndexData, requiresEnchantmentGlint);
        if (renderResult.isSuccessful()) {
            itemImage = renderResult.getImage();
        } else {
            Debug.debug("ImageGeneration creating missing Image for material " + xMaterial);
            itemImage = TextureManager.getMissingImage(32, 32);
        }

        if (item.getType().getMaxDurability() > 0) {
            int durability = item.getType().getMaxDurability() - (InteractiveChat.version.isLegacy() ? item.getDurability() : ((Damageable) item.getItemMeta()).getDamage());
            int maxDur = item.getType().getMaxDurability();
            double percentage = ((double) durability / (double) maxDur);
            if (percentage < 1) {
                int hue = (int) (125 * percentage);
                int length = (int) (26 * percentage);
                Color color = Color.getHSBColor((float) hue / 360, 1, 1);

                Graphics2D g4 = itemImage.createGraphics();
                g4.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
                g4.setColor(Color.BLACK);
                g4.fillPolygon(new int[] {4, 30, 30, 4}, new int[] {26, 26, 30, 30}, 4);
                g4.setColor(color);
                g4.fillPolygon(new int[] {4, 4 + length, 4 + length, 4}, new int[] {26, 26, 28, 28}, 4);
                g4.dispose();
            }
        }

        if (amount != 1) {
            BufferedImage newItemImage = new BufferedImage(40, 40, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g4 = newItemImage.createGraphics();
            g4.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            g4.drawImage(itemImage, 0, 0, null);
            Component component = Component.text(amount);
            if (amount <= 0) {
                component = component.color(NamedTextColor.RED);
            }
            newItemImage = ImageUtils.printComponentRightAligned(InteractiveChatDiscordSrvAddon.plugin.resourceManager, newItemImage, component, InteractiveChatDiscordSrvAddon.plugin.language, InteractiveChat.version.isLegacyRGB(), 33, 17, 16, ITEM_AMOUNT_TEXT_DARKEN_FACTOR);
            g4.dispose();
            itemImage = newItemImage;
        }

        return itemImage;
    }

    public static BufferedImage getMapImage(ItemStack item, Player player) throws Exception {
        if (!FilledMapUtils.isFilledMap(item)) {
            throw new IllegalArgumentException("Provided item is not a filled map");
        }
        InteractiveChatDiscordSrvAddon.plugin.imageCounter.incrementAndGet();
        Debug.debug("ImageGeneration creating map image");

        BufferedImage background = InteractiveChatDiscordSrvAddon.plugin.resourceManager.getTextureManager().getTexture(ResourceRegistry.MAP_TEXTURE_LOCATION + "map_background").getTexture();

        BufferedImage image = new BufferedImage(1120, 1120, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g.drawImage(background, 0, 0, 1120, 1120, null);
        g.dispose();

        int borderOffset = (int) (image.getWidth() / 23.3333333333333333333);
        int ratio = (image.getWidth() - borderOffset * 2) / 128;

        ItemMapWrapper data = new ItemMapWrapper(item, player);
        for (int widthOffset = 0; widthOffset < 128; widthOffset++) {
            for (int heightOffset = 0; heightOffset < 128; heightOffset++) {
                byte index = data.getColors()[widthOffset + heightOffset * 128];
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

        Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);

        BufferedImage asset = InteractiveChatDiscordSrvAddon.plugin.resourceManager.getTextureManager().getTexture(ResourceRegistry.MAP_TEXTURE_LOCATION + "map_icons").getTexture();
        int iconWidth = asset.getWidth() / MAP_ICON_PER_ROLE;

        for (MapCursor icon : data.getMapCursors()) {
            int x = icon.getX() + 128;
            int y = icon.getY() + 128;
            double rotation = (360.0 / 16.0 * (double) icon.getDirection()) + 180.0;
            int type = icon.getType().ordinal();
            Component component;
            try {
                component = LegacyComponentSerializer.legacySection().deserializeOrNull(icon.getCaption());
            } catch (Throwable e) {
                component = null;
            }

            //String name
            BufferedImage iconImage = ImageUtils.copyAndGetSubImage(asset, type % MAP_ICON_PER_ROLE * iconWidth, type / MAP_ICON_PER_ROLE * iconWidth, iconWidth, iconWidth);
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
                ImageUtils.printComponentNoShadow(InteractiveChatDiscordSrvAddon.plugin.resourceManager, image, component, InteractiveChatDiscordSrvAddon.plugin.language, InteractiveChat.version.isLegacyRGB(), imageX, imageY + 32, 30, true);
            }
        }
        g2.dispose();

        return image;
    }

    public static BufferedImage getToolTipImage(Component print) {
        return getToolTipImage(Arrays.asList(print), false);
    }

    public static BufferedImage getToolTipImage(Component print, boolean allowLineBreaks) {
        return getToolTipImage(Arrays.asList(print), allowLineBreaks);
    }

    public static BufferedImage getToolTipImage(List<Component> prints) {
        return getToolTipImage(prints, false);
    }

    public static BufferedImage getToolTipImage(List<Component> prints, boolean allowLineBreaks) {
        if (prints.isEmpty()) {
            Debug.debug("ImageGeneration creating tooltip image");
        } else {
            Debug.debug("ImageGeneration creating tooltip image of " + InteractiveChatComponentSerializer.bungeecordApiLegacy().serialize(prints.get(0)));
        }

        if (allowLineBreaks) {
            List<Component> newList = new ArrayList<>();
            for (Component component : prints) {
                newList.addAll(ComponentStyling.splitAtLineBreaks(component));
            }
            prints = newList;
        }

        BufferedImage image = new BufferedImage(2240, prints.size() * 20 + 15, BufferedImage.TYPE_INT_ARGB);

        int topX = image.getWidth() / 5 * 2;
        for (int i = 0; i < prints.size(); i++) {
            Component text = prints.get(i);
            ImageUtils.printComponent(InteractiveChatDiscordSrvAddon.plugin.resourceManager, image, text, InteractiveChatDiscordSrvAddon.plugin.language, InteractiveChat.version.isLegacyRGB(), topX + 8, 8 + 20 * i, 16);
        }

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

        BufferedImage background = new BufferedImage(lastX - topX + 9, image.getHeight(), BufferedImage.TYPE_INT_ARGB);

        Graphics2D g = background.createGraphics();
        g.setColor(new Color(36, 1, 92));
        g.fillRect(2, 2, background.getWidth() - 4, background.getHeight() - 4);
        g.setColor(new Color(16, 1, 16));
        g.fillRect(4, 4, background.getWidth() - 8, background.getHeight() - 8);
        g.fillRect(0, 2, 2, background.getHeight() - 4);
        g.fillRect(background.getWidth() - 2, 2, 2, background.getHeight() - 4);
        g.fillRect(2, 0, background.getWidth() - 4, 2);
        g.fillRect(2, background.getHeight() - 2, background.getWidth() - 4, 2);
        g.dispose();

        int offsetX = Math.max(topX - firstX, 0);
        BufferedImage output = new BufferedImage(offsetX + background.getWidth(), background.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = output.createGraphics();
        g2.drawImage(background, offsetX, 0, null);
        g2.drawImage(image, -firstX, 0, null);

        return output;
    }

    public static BufferedImage getTabListImage(List<Component> header, List<Component> footer, List<ValueTrios<UUID, Component, Integer>> players, boolean showAvatar, boolean showPing) {
        List<ValuePairs<BufferedImage, Integer>> playerImages = new ArrayList<>(players.size());
        int masterOffsetX = 0;
        for (ValueTrios<UUID, Component, Integer> trio : players) {
            UUID uuid = trio.getFirst();
            Component name = trio.getSecond();
            int ping = trio.getThird();
            BufferedImage image = new BufferedImage(4096, 1042, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = image.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            int offsetX = 0;
            if (showAvatar) {
                BufferedImage skin;
                try {
                    Player onlinePlayer = Bukkit.getPlayer(uuid);
                    if (onlinePlayer == null) {
                        Cache<?> cache = Cache.getCache(uuid + "null" + PLAYER_SKIN_CACHE_KEY);
                        if (cache == null) {
                            String value = SkinUtils.getSkinURLFromUUID(uuid);
                            skin = ImageUtils.downloadImage(value);
                            Cache.putCache(uuid + "null" + PLAYER_SKIN_CACHE_KEY, skin, InteractiveChatDiscordSrvAddon.plugin.cacheTimeout);
                            skin = ImageUtils.copyImage(skin);
                        } else {
                            skin = ImageUtils.copyImage((BufferedImage) cache.getObject());
                        }
                    } else {
                        try {
                            JSONObject json = (JSONObject) new JSONParser().parse(SkinUtils.getSkinJsonFromProfile(onlinePlayer));
                            String value = (String) ((JSONObject) ((JSONObject) json.get("textures")).get("SKIN")).get("url");
                            Cache<?> cache = Cache.getCache(uuid + value + PLAYER_SKIN_CACHE_KEY);
                            if (cache == null) {
                                skin = ImageUtils.downloadImage(value);
                                Cache.putCache(uuid + value + PLAYER_SKIN_CACHE_KEY, skin, InteractiveChatDiscordSrvAddon.plugin.cacheTimeout);
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
                    skin = InteractiveChatDiscordSrvAddon.plugin.resourceManager.getTextureManager().getTexture(ResourceRegistry.ENTITY_TEXTURE_LOCATION + "steve").getTexture(64, 64);
                }
                BufferedImage avatar = ImageUtils.copyAndGetSubImage(skin, 8, 8, 8, 8);
                BufferedImage avatarOverlay = ImageUtils.copyAndGetSubImage(skin, 40, 8, 8, 8);
                g.drawImage(avatar, offsetX, 512, 16, 16, null);
                g.drawImage(avatarOverlay, offsetX, 512, 16, 16, null);
                offsetX += 18;
            } else {
                offsetX += 2;
            }
            g.dispose();
            ImageUtils.printComponent(InteractiveChatDiscordSrvAddon.plugin.resourceManager, image, name, InteractiveChatDiscordSrvAddon.plugin.language, InteractiveChat.version.isLegacyRGB(), offsetX, 511, 16);
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
            playerImages.add(new ValuePairs<>(image, ping));
        }
        List<BufferedImage> playerRows = new ArrayList<>(playerImages.size());
        if (showPing) {
            masterOffsetX += 26;
        } else {
            masterOffsetX += 2;
        }
        for (ValuePairs<BufferedImage, Integer> pair : playerImages) {
            BufferedImage image = pair.getFirst();
            if (showPing) {
                BufferedImage ping = getPingIcon(pair.getSecond());
                Graphics2D g = image.createGraphics();
                g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
                g.drawImage(ImageUtils.resizeImageAbs(ping, 20, 14), masterOffsetX - 22, 514, null);
                g.dispose();
            }
            BufferedImage cropped = new BufferedImage(masterOffsetX, 1042, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = cropped.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            g.setColor(TABLIST_PLAYER_BACKGROUND);
            g.fillRect(0, 512, cropped.getWidth(), 16);
            g.drawImage(image, 0, 0, null);
            g.dispose();
            playerRows.add(cropped);
        }
        Map<BufferedImage, Integer> headerLines = new LinkedHashMap<>(header.size());
        for (Component line : header) {
            BufferedImage image = new BufferedImage(4096, 1042, BufferedImage.TYPE_INT_ARGB);
            ImageUtils.printComponent(InteractiveChatDiscordSrvAddon.plugin.resourceManager, image, line, InteractiveChatDiscordSrvAddon.plugin.language, InteractiveChat.version.isLegacyRGB(), 0, 511, 16);
            int lastX = 0;
            for (int x = 0; x < image.getWidth(); x++) {
                for (int y = 0; y < image.getHeight(); y++) {
                    if (image.getRGB(x, y) != 0) {
                        lastX = x;
                        break;
                    }
                }
            }
            BufferedImage cropped = new BufferedImage(Math.max(1, lastX), 1042, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = cropped.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            g.drawImage(image, 0, 0, null);
            g.dispose();
            headerLines.put(cropped, lastX);
        }
        Map<BufferedImage, Integer> footerLines = new LinkedHashMap<>(footer.size());
        for (Component line : footer) {
            BufferedImage image = new BufferedImage(4096, 1042, BufferedImage.TYPE_INT_ARGB);
            ImageUtils.printComponent(InteractiveChatDiscordSrvAddon.plugin.resourceManager, image, line, InteractiveChatDiscordSrvAddon.plugin.language, InteractiveChat.version.isLegacyRGB(), 0, 511, 16);
            int lastX = 0;
            for (int x = 0; x < image.getWidth(); x++) {
                for (int y = 0; y < image.getHeight(); y++) {
                    if (image.getRGB(x, y) != 0) {
                        lastX = x;
                        break;
                    }
                }
            }
            BufferedImage cropped = new BufferedImage(Math.max(1, lastX), 1042, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = cropped.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            g.drawImage(image, 0, 0, null);
            g.dispose();
            footerLines.put(cropped, lastX);
        }
        BufferedImage image;
        if (playerRows.size() <= TABLIST_SINGLE_COLUMN_LIMIT) {
            image = new BufferedImage(masterOffsetX + 4, playerRows.size() * 18 + 2, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = image.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            g.setColor(TABLIST_BACKGROUND);
            g.fillRect(0, 0, image.getWidth(), image.getHeight());
            int offsetY = -510;
            for (BufferedImage each : playerRows) {
                g.drawImage(each, 2, offsetY, null);
                offsetY += 18;
            }
            g.dispose();
        } else {
            image = new BufferedImage(masterOffsetX * 2 + 6, (int) Math.ceil((double) playerRows.size() / 2) * 18 + 2, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = image.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            g.setColor(TABLIST_BACKGROUND);
            g.fillRect(0, 0, image.getWidth(), image.getHeight());
            int offsetY = -510;
            int half = (int) Math.ceil((double) playerRows.size() / 2);
            for (int i = 0; i < half; i++) {
                g.drawImage(playerRows.get(i), 2, offsetY, null);
                offsetY += 18;
            }
            offsetY = -510;
            for (int i = half; i < playerRows.size(); i++) {
                g.drawImage(playerRows.get(i), masterOffsetX + 4, offsetY, null);
                offsetY += 18;
            }
            if (playerRows.size() % 2 == 1) {
                g.setColor(TABLIST_PLAYER_BACKGROUND);
                g.fillRect(masterOffsetX + 4, offsetY + 512, masterOffsetX, 16);
            }
            g.dispose();
        }
        int maxOffsetX = Stream.concat(headerLines.values().stream(), footerLines.values().stream()).mapToInt(each -> each).max().orElse(0);
        if (maxOffsetX <= 0) {
            return image;
        } else {
            BufferedImage decoration = new BufferedImage(Math.max(image.getWidth(), maxOffsetX + 4), (headerLines.isEmpty() ? 0 : headerLines.size() * 18 + 2) + image.getHeight() + (footerLines.isEmpty() ? 2 : footerLines.size() * 18 + 4), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = decoration.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            int offsetY = -510;
            for (BufferedImage each : headerLines.keySet()) {
                g.drawImage(each, (decoration.getWidth() / 2) - (each.getWidth() / 2), offsetY, null);
                offsetY += 18;
            }
            g.drawImage(image, (decoration.getWidth() / 2) - (image.getWidth() / 2), offsetY + 512, null);
            offsetY += image.getHeight() + 2;
            for (BufferedImage each : footerLines.keySet()) {
                g.drawImage(each, (decoration.getWidth() / 2) - (each.getWidth() / 2), offsetY, null);
                offsetY += 18;
            }
            g.dispose();
            int lastY = offsetY + 510;
            for (int y = offsetY + 510; y < decoration.getHeight(); y++) {
                for (int x = 0; x < decoration.getWidth(); x++) {
                    if (decoration.getRGB(x, y) != 0) {
                        lastY = y;
                        break;
                    }
                }
            }
            BufferedImage finalDecoration = new BufferedImage(decoration.getWidth(), lastY + 3, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = finalDecoration.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            g2.setColor(TABLIST_BACKGROUND);
            g2.fillRect(0, 0, finalDecoration.getWidth(), finalDecoration.getHeight());
            g2.drawImage(decoration, 0, 0, null);
            g2.dispose();
            return finalDecoration;
        }
    }

    public static BufferedImage getPingIcon(int ms) {
        BufferedImage icons = InteractiveChatDiscordSrvAddon.plugin.resourceManager.getTextureManager().getTexture(ResourceRegistry.GUI_TEXTURE_LOCATION + "icons").getTexture();
        int scale = icons.getWidth() / 256;
        if (ms < 0) {
            return ImageUtils.copyAndGetSubImage(icons, 0, 56 * scale, 10 * scale, 7 * scale);
        } else if (ms < 150) {
            return ImageUtils.copyAndGetSubImage(icons, 0, 16 * scale, 10 * scale, 7 * scale);
        } else if (ms < 300) {
            return ImageUtils.copyAndGetSubImage(icons, 0, 24 * scale, 10 * scale, 7 * scale);
        } else if (ms < 600) {
            return ImageUtils.copyAndGetSubImage(icons, 0, 32 * scale, 10 * scale, 7 * scale);
        } else if (ms < 1000) {
            return ImageUtils.copyAndGetSubImage(icons, 0, 40 * scale, 10 * scale, 7 * scale);
        } else {
            return ImageUtils.copyAndGetSubImage(icons, 0, 48 * scale, 10 * scale, 7 * scale);
        }
    }

}
