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

package com.loohp.interactivechatdiscordsrvaddon.resources.mods.optifine;

import com.loohp.interactivechat.libs.org.apache.commons.io.input.BOMInputStream;
import com.loohp.interactivechat.libs.org.json.simple.JSONObject;
import com.loohp.interactivechat.libs.org.json.simple.parser.JSONParser;
import com.loohp.interactivechat.objectholders.ValuePairs;
import com.loohp.interactivechatdiscordsrvaddon.graphics.ImageUtils;
import com.loohp.interactivechatdiscordsrvaddon.registry.ResourceRegistry;
import com.loohp.interactivechatdiscordsrvaddon.resources.CustomItemTextureRegistry;
import com.loohp.interactivechatdiscordsrvaddon.resources.ModelRenderer;
import com.loohp.interactivechatdiscordsrvaddon.resources.ResourceLoadingException;
import com.loohp.interactivechatdiscordsrvaddon.resources.ResourceManager;
import com.loohp.interactivechatdiscordsrvaddon.resources.ResourcePackFile;
import com.loohp.interactivechatdiscordsrvaddon.resources.models.BlockModel;
import com.loohp.interactivechatdiscordsrvaddon.resources.models.ModelManager;
import com.loohp.interactivechatdiscordsrvaddon.resources.models.ModelOverride;
import com.loohp.interactivechatdiscordsrvaddon.resources.models.ModelOverride.ModelOverrideType;
import com.loohp.interactivechatdiscordsrvaddon.resources.mods.ModManager;
import com.loohp.interactivechatdiscordsrvaddon.resources.mods.optifine.cit.ArmorProperties;
import com.loohp.interactivechatdiscordsrvaddon.resources.mods.optifine.cit.CITGlobalProperties;
import com.loohp.interactivechatdiscordsrvaddon.resources.mods.optifine.cit.CITGlobalProperties.EnchantmentVisibilityMethod;
import com.loohp.interactivechatdiscordsrvaddon.resources.mods.optifine.cit.CITProperties;
import com.loohp.interactivechatdiscordsrvaddon.resources.mods.optifine.cit.ElytraProperties;
import com.loohp.interactivechatdiscordsrvaddon.resources.mods.optifine.cit.EnchantmentProperties;
import com.loohp.interactivechatdiscordsrvaddon.resources.mods.optifine.cit.EnchantmentProperties.OpenGLBlending;
import com.loohp.interactivechatdiscordsrvaddon.resources.mods.optifine.cit.ItemProperties;
import com.loohp.interactivechatdiscordsrvaddon.resources.textures.GeneratedTextureResource;
import com.loohp.interactivechatdiscordsrvaddon.resources.textures.TextureManager;
import com.loohp.interactivechatdiscordsrvaddon.resources.textures.TextureMeta;
import com.loohp.interactivechatdiscordsrvaddon.resources.textures.TextureResource;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

public class OptifineManager extends ModManager implements IOptifineManager {

    public static final String MOD_NAME = "Optifine";
    public static final List<String> ASSETS_FOLDERS = Collections.unmodifiableList(Arrays.asList("optifine", "mcpatcher"));
    public static final CITGlobalProperties DEFAULT_CIT_GLOBAL_PROPERTIES = new CITGlobalProperties(true, Integer.MAX_VALUE, EnchantmentVisibilityMethod.AVERAGE, 0.5);
    private static final BufferedImage BLANK_ENCHANTMENT = new BufferedImage(512, 512, BufferedImage.TYPE_INT_ARGB);

    private Map<String, ValuePairs<ResourcePackFile, ?>> assets;

    private CITGlobalProperties citGlobalProperties;
    private Map<String, ValuePairs<ResourcePackFile, CITProperties>> citOverrides;

    public OptifineManager(ResourceManager manager) {
        super(manager, MOD_NAME, ASSETS_FOLDERS);
        this.assets = new HashMap<>();
        this.citGlobalProperties = null;
        this.citOverrides = new LinkedHashMap<>();
        if (manager.hasResourceRegistry(CustomItemTextureRegistry.IDENTIFIER)) {
            manager.getResourceRegistry(CustomItemTextureRegistry.IDENTIFIER, CustomItemTextureRegistry.class).appendResolver(new OptifineItemTextureResolver(this));
        }
    }

    @Override
    protected void loadDirectory(String namespace, ResourcePackFile root, Object... meta) {
        JSONParser parser = new JSONParser();
        for (ResourcePackFile file : root.listFilesRecursively()) {
            try {
                String path = file.getRelativePathFrom(root);
                String key = namespace + ":" + root.getName() + "/" + path;
                String extension = "";
                if (key.lastIndexOf(".") >= 0) {
                    extension = key.substring(key.lastIndexOf(".") + 1);
                }
                if (path.equalsIgnoreCase("cit.properties")) {
                    if (citGlobalProperties == null) {
                        InputStreamReader reader = new InputStreamReader(new BOMInputStream(file.getInputStream()), StandardCharsets.UTF_8);
                        Properties properties = new Properties();
                        properties.load(reader);
                        reader.close();
                        citGlobalProperties = CITGlobalProperties.fromProperties(properties);
                    }
                } else {
                    if (extension.equalsIgnoreCase("json")) {
                        InputStreamReader reader = new InputStreamReader(new BOMInputStream(file.getInputStream()), StandardCharsets.UTF_8);
                        JSONObject rootJson = (JSONObject) parser.parse(reader);
                        reader.close();
                        assets.put(key, new ValuePairs<>(file, BlockModel.fromJson(this, key.substring(0, key.length() - (extension.isEmpty() ? 0 : extension.length() + 1)), rootJson)));
                    } else if (extension.equalsIgnoreCase("png")) {
                        assets.put(key, new ValuePairs<>(file, new TextureResource(this, key, file, true)));
                    } else if (extension.equalsIgnoreCase("mcmeta")) {
                        InputStreamReader reader = new InputStreamReader(new BOMInputStream(file.getInputStream()), StandardCharsets.UTF_8);
                        JSONObject rootJson = (JSONObject) parser.parse(reader);
                        reader.close();
                        TextureMeta textureMeta = TextureMeta.fromJson(this, key, file, rootJson);
                        assets.put(key, new ValuePairs<>(file, textureMeta));
                    } else if (extension.equalsIgnoreCase("properties")) {
                        if (path.startsWith("cit/")) {
                            InputStreamReader reader = new InputStreamReader(new BOMInputStream(file.getInputStream()), StandardCharsets.UTF_8);
                            Properties properties = new Properties();
                            properties.load(reader);
                            reader.close();
                            CITProperties citProperties = CITProperties.fromProperties(file, properties);
                            citOverrides.put(key, new ValuePairs<>(file, citProperties));
                        }
                    } else {
                        assets.put(key, new ValuePairs<>(file, null));
                    }
                }
            } catch (Exception e) {
                new ResourceLoadingException("Unable to load optifine asset " + file.getAbsolutePath(), e).printStackTrace();
            }
        }
    }

    @Override
    protected void filterResources(Pattern namespace, Pattern path) {
        Iterator<String> itr = assets.keySet().iterator();
        while (itr.hasNext()) {
            String namespacedKey = itr.next();
            String assetNamespace = namespacedKey.substring(0, namespacedKey.indexOf(":"));
            String assetKey = namespacedKey.substring(namespacedKey.indexOf(":") + 1);
            if (namespace.matcher(assetNamespace).matches() && path.matcher(assetKey).matches()) {
                itr.remove();
            }
        }

        Iterator<String> itr2 = citOverrides.keySet().iterator();
        while (itr2.hasNext()) {
            String namespacedKey = itr2.next();
            String assetNamespace = namespacedKey.substring(0, namespacedKey.indexOf(":"));
            String assetKey = namespacedKey.substring(namespacedKey.indexOf(":") + 1);
            if (namespace.matcher(assetNamespace).matches() && path.matcher(assetKey).matches()) {
                itr2.remove();
            }
        }
    }

    @Override
    protected void reload() {

    }

    @Override
    public Function<BlockModel, ValuePairs<BlockModel, Map<String, TextureResource>>> getItemPostResolveFunction(EquipmentSlot heldSlot, ItemStack itemStack, boolean is1_8, Map<ModelOverrideType, Float> predicates, UnaryOperator<String> translateFunction) {
        if (itemStack == null || itemStack.getType().equals(Material.AIR)) {
            return ModelRenderer.DEFAULT_POST_RESOLVE_FUNCTION;
        }
        ValuePairs<ResourcePackFile, ItemProperties> citOverride = getCITOverride(heldSlot, itemStack, translateFunction, ItemProperties.class);
        if (citOverride == null) {
            return ModelRenderer.DEFAULT_POST_RESOLVE_FUNCTION;
        } else {
            return blockModel -> {
                Map<String, TextureResource> overrideTextures = new HashMap<>();
                CITProperties citProperties = citOverride.getSecond();
                String modelName = blockModel == null ? null : blockModel.getResourceLocation();
                if (modelName != null) {
                    if (modelName.contains(":")) {
                        modelName = modelName.substring(modelName.indexOf(":") + 1);
                    }
                    if (modelName.contains("/")) {
                        modelName = modelName.substring(modelName.lastIndexOf("/") + 1);
                    }
                }
                for (String type : Arrays.asList("png", "json")) {
                    String rootPath = citProperties.getOverrideAsset("", type);
                    if (rootPath != null) {
                        String extension = rootPath.substring(rootPath.lastIndexOf(".") + 1);
                        String resourceLocation = resolveAsset(citOverride.getFirst(), rootPath, extension);
                        if (extension.equalsIgnoreCase("png")) {
                            overrideTextures.put("", getTexture(resourceLocation));
                        } else if (extension.equalsIgnoreCase("json")) {
                            blockModel = resolveBlockModel(resourceLocation, is1_8, predicates);
                        }
                    } else {
                        if (modelName != null) {
                            String path = citProperties.getOverrideAsset(modelName, type);
                            if (path != null) {
                                String extension = path.substring(path.lastIndexOf(".") + 1);
                                String resourceLocation = resolveAsset(citOverride.getFirst(), path, extension);
                                if (extension.equalsIgnoreCase("png")) {
                                    overrideTextures.put("layer0", getTexture(resourceLocation));
                                } else if (extension.equalsIgnoreCase("json")) {
                                    blockModel = resolveBlockModel(resourceLocation, is1_8, predicates);
                                }
                            }
                        }
                    }
                }
                for (Entry<String, String> entry : blockModel.getTextures().entrySet()) {
                    String key = entry.getKey();
                    String pathK = citProperties.getOverrideAsset(entry.getKey(), "png");
                    if (pathK != null) {
                        String extension = pathK.substring(pathK.lastIndexOf(".") + 1);
                        String resourceLocation = resolveAsset(citOverride.getFirst(), pathK, extension);
                        if (extension.equalsIgnoreCase("png")) {
                            overrideTextures.put(key, getTexture(resourceLocation));
                        }
                    } else {
                        String value = entry.getValue();
                        if (value.contains(":")) {
                            value = value.substring(value.indexOf(":") + 1);
                        }
                        if (value.contains("/")) {
                            value = value.substring(value.indexOf("/") + 1);
                        }
                        String pathV = citProperties.getOverrideAsset(value, "png");
                        if (pathV != null) {
                            String extension = pathV.substring(pathV.lastIndexOf(".") + 1);
                            String resourceLocation = resolveAsset(citOverride.getFirst(), pathV, extension);
                            if (extension.equalsIgnoreCase("png")) {
                                overrideTextures.put(key, getTexture(resourceLocation));
                            }
                        }
                    }
                }
                return new ValuePairs<>(blockModel, overrideTextures);
            };
        }
    }

    @Override
    public TextureResource getElytraOverrideTextures(EquipmentSlot heldSlot, ItemStack itemStack, UnaryOperator<String> translateFunction) {
        if (itemStack == null || !itemStack.getType().equals(Material.valueOf("ELYTRA"))) {
            return null;
        }
        ValuePairs<ResourcePackFile, ElytraProperties> citOverride = getCITOverride(heldSlot, itemStack, translateFunction, ElytraProperties.class);
        if (citOverride == null) {
            return null;
        }
        String path = citOverride.getSecond().getOverrideAsset("", "png");
        if (path != null) {
            String extension = path.substring(path.lastIndexOf(".") + 1);
            String resourceLocation = resolveAsset(citOverride.getFirst(), path, "png");
            return getTexture(resourceLocation);
        }
        return null;
    }

    @Override
    public TextureResource getArmorOverrideTextures(String layer, EquipmentSlot heldSlot, ItemStack itemStack, UnaryOperator<String> translateFunction) {
        if (itemStack == null || itemStack.getType().equals(Material.AIR)) {
            return null;
        }
        ValuePairs<ResourcePackFile, ArmorProperties> citOverride = getCITOverride(heldSlot, itemStack, translateFunction, ArmorProperties.class);
        if (citOverride == null) {
            return null;
        }
        String path = citOverride.getSecond().getOverrideAsset(layer, "png");
        if (path == null) {
            path = citOverride.getSecond().getOverrideAsset("", "png");
        }
        if (path != null) {
            String extension = path.substring(path.lastIndexOf(".") + 1);
            String resourceLocation = resolveAsset(citOverride.getFirst(), path, "png");
            return getTexture(resourceLocation);
        }
        return null;
    }

    @Override
    public List<ValuePairs<TextureResource, OpenGLBlending>> getEnchantmentGlintOverrideTextures(EquipmentSlot heldSlot, ItemStack itemStack, UnaryOperator<String> translateFunction) {
        Map<String, TextureResource> overrideTextures = new HashMap<>();
        if (itemStack == null || itemStack.getType().equals(Material.AIR)) {
            return getCITGlobalProperties().isUseGlint() ? Collections.emptyList() : Collections.singletonList(new ValuePairs<>(new GeneratedTextureResource(manager, BLANK_ENCHANTMENT), OpenGLBlending.ADD));
        }
        List<ValuePairs<ResourcePackFile, EnchantmentProperties>> citOverrides = getCITOverrides(heldSlot, itemStack, translateFunction, EnchantmentProperties.class);
        List<ValuePairs<TextureResource, OpenGLBlending>> result = new ArrayList<>();
        Object2IntMap<TextureResource> layer = new Object2IntOpenHashMap<>();
        for (ValuePairs<ResourcePackFile, EnchantmentProperties> citOverride : citOverrides) {
            EnchantmentProperties enchantmentProperties = citOverride.getSecond();
            String path = enchantmentProperties.getOverrideAsset("", "png");
            if (path != null) {
                int currentLayer = enchantmentProperties.getLayer();
                Iterator<Object2IntMap.Entry<TextureResource>> itr = layer.object2IntEntrySet().iterator();
                while (itr.hasNext()) {
                    Object2IntMap.Entry<TextureResource> entry = itr.next();
                    if (entry.getIntValue() == currentLayer) {
                        itr.remove();
                        result.removeIf(each -> each.getFirst().equals(entry.getKey()));
                    }
                }
                String extension = path.substring(path.lastIndexOf(".") + 1);
                String resourceLocation = resolveAsset(citOverride.getFirst(), path, "png");
                BufferedImage texture = getTexture(resourceLocation).getTexture();
                Map<Enchantment, Integer> stackEnchantments = itemStack.getEnchantments();
                float intensity;
                switch (getCITGlobalProperties().getMethod()) {
                    case AVERAGE: {
                        Enchantment enchantment = null;
                        for (Enchantment enchantmentMatch : enchantmentProperties.getEnchantments().keySet()) {
                            if (stackEnchantments.containsKey(enchantmentMatch)) {
                                enchantment = enchantmentMatch;
                                break;
                            }
                        }
                        if (enchantment == null) {
                            intensity = 0F;
                        } else {
                            float sum = 0F;
                            for (Integer value : stackEnchantments.values()) {
                                sum += value;
                            }
                            intensity = (float) stackEnchantments.get(enchantment) / sum;
                        }
                        break;
                    }
                    case LAYERED: {
                        Enchantment enchantment = null;
                        for (Enchantment enchantmentMatch : enchantmentProperties.getEnchantments().keySet()) {
                            if (stackEnchantments.containsKey(enchantmentMatch)) {
                                enchantment = enchantmentMatch;
                                break;
                            }
                        }
                        if (enchantment == null) {
                            intensity = 0F;
                        } else {
                            float max = 0F;
                            for (Integer value : stackEnchantments.values()) {
                                if (value > max) {
                                    max = value;
                                }
                            }
                            intensity = (float) stackEnchantments.get(enchantment) / max;
                        }
                        break;
                    }
                    default: {
                        intensity = 1F;
                        break;
                    }
                }
                if (intensity == 0F) {
                    texture = new BufferedImage(texture.getWidth(), texture.getHeight(), BufferedImage.TYPE_INT_ARGB);
                } else if (intensity < 1F) {
                    BufferedImage blackImage = new BufferedImage(texture.getWidth(), texture.getHeight(), BufferedImage.TYPE_INT_ARGB);
                    Graphics2D g = blackImage.createGraphics();
                    g.setColor(new Color(0, 0, 0, Math.min(255, (int) (255 * (1F - intensity)))));
                    g.fillRect(0, 0, blackImage.getWidth(), blackImage.getHeight());
                    g.dispose();
                    Graphics2D g2 = texture.createGraphics();
                    g2.drawImage(blackImage, 0, 0, null);
                    g2.dispose();
                }
                texture = ImageUtils.rotateImageByDegrees(texture, enchantmentProperties.getRotation());
                GeneratedTextureResource textureResource = new GeneratedTextureResource(manager, texture);
                result.add(new ValuePairs<>(textureResource, enchantmentProperties.getBlend()));
                layer.put(textureResource, currentLayer);
            }
        }
        if (result.isEmpty()) {
            return getCITGlobalProperties().isUseGlint() ? Collections.emptyList() : Collections.singletonList(new ValuePairs<>(new GeneratedTextureResource(manager, BLANK_ENCHANTMENT), OpenGLBlending.ADD));
        }
        result.sort(Comparator.comparing(each -> layer.getOrDefault(each, 0)));
        for (int i = 0; i < result.size(); i++) {
            if (result.get(i).getSecond().equals(OpenGLBlending.REPLACE)) {
                for (int u = 0; u < i; u++) {
                    //noinspection SuspiciousListRemoveInLoop
                    result.remove(u);
                }
                i = 1;
            }
        }
        while (result.size() > getCITGlobalProperties().getCap() && !result.isEmpty()) {
            result.remove(0);
        }
        return result;
    }

    public static String resolveAsset(ResourcePackFile currentFile, String path, String extension) {
        if (!path.toLowerCase().endsWith(extension.toLowerCase())) {
            path += "." + extension;
        }
        if (path.contains(":")) {
            String[] sections = path.split(":", 2);
            if (sections[1].indexOf("/") == sections[1].lastIndexOf("/")) {
                if (extension.equalsIgnoreCase("png")) {
                    sections[1] = "textures/" + sections[1];
                } else if (extension.equalsIgnoreCase("json")) {
                    sections[1] = "models/" + sections[1];
                }
            }
            path = "assets/" + sections[0] + "/" + sections[1];
        }
        if (!path.contains("/")) {
            path = currentFile.getParentFile().getChild(path).getRelativePathFrom(currentFile.getPackRootFile());
            path = path.substring(path.indexOf("assets/"));
        }
        if (!path.startsWith("assets/")) {
            String[] section = currentFile.getRelativePathFrom(currentFile.getPackRootFile()).split("/");
            path = section[0] + "/" + section[1] + "/" + path;
        }
        path = new File("temp").toPath().relativize(new File("temp", path).toPath()).toString().replace("\\", "/");
        String[] sections = path.split("/", 3);
        return sections[1] + ":" + sections[2];
    }

    public CITGlobalProperties getRawCITGlobalProperties() {
        return citGlobalProperties;
    }

    @Override
    public CITGlobalProperties getCITGlobalProperties() {
        return citGlobalProperties == null ? DEFAULT_CIT_GLOBAL_PROPERTIES : citGlobalProperties;
    }

    public Map<String, ValuePairs<ResourcePackFile, CITProperties>> getCITOverrides() {
        return citOverrides;
    }

    @Override
    public <T extends CITProperties> ValuePairs<ResourcePackFile, T> getCITOverride(EquipmentSlot heldSlot, ItemStack itemStack, UnaryOperator<String> translateFunction, Class<T> type) {
        ValuePairs<ResourcePackFile, T> result = null;
        int weight = Integer.MIN_VALUE;
        for (ValuePairs<ResourcePackFile, CITProperties> pair : citOverrides.values()) {
            CITProperties citProperties = pair.getSecond();
            if (type.isInstance(citProperties) && citProperties.getWeight() > weight && citProperties.test(heldSlot, itemStack, translateFunction)) {
                result = new ValuePairs<>(pair.getFirst(), (T) citProperties);
            }
        }
        return result;
    }

    @Override
    public <T extends CITProperties> List<ValuePairs<ResourcePackFile, T>> getCITOverrides(EquipmentSlot heldSlot, ItemStack itemStack, UnaryOperator<String> translateFunction, Class<T> type) {
        List<ValuePairs<ResourcePackFile, T>> result = new ArrayList<>();
        for (ValuePairs<ResourcePackFile, CITProperties> pair : citOverrides.values()) {
            CITProperties citProperties = pair.getSecond();
            if (type.isInstance(citProperties) && citProperties.test(heldSlot, itemStack, translateFunction)) {
                result.add(new ValuePairs<>(pair.getFirst(), (T) citProperties));
            }
        }
        result.sort(Comparator.comparing(each -> each.getSecond().getWeight()));
        return result;
    }

    @Override
    public TextureResource getTexture(String resourceLocation, boolean returnMissingTexture) {
        TextureResource textureResource = manager.getTextureManager().getTexture(resourceLocation, false);
        if (textureResource != null) {
            return textureResource;
        }
        ValuePairs<ResourcePackFile, ?> asset = assets.get(resourceLocation);
        if (asset != null && asset.getSecond() instanceof TextureResource) {
            return (TextureResource) asset.getSecond();
        }
        if (returnMissingTexture) {
            return TextureManager.getMissingTexture(manager);
        } else {
            return null;
        }
    }

    @Override
    public BlockModel getRawBlockModel(String resourceLocation) {
        BlockModel model = manager.getModelManager().getRawBlockModel(resourceLocation);
        if (model != null) {
            return model;
        }
        ValuePairs<ResourcePackFile, ?> asset = assets.get(resourceLocation);
        if (asset != null && asset.getSecond() instanceof BlockModel) {
            return (BlockModel) asset.getSecond();
        }
        return null;
    }

    @Override
    public BlockModel resolveBlockModel(String resourceLocation, boolean is1_8, Map<ModelOverrideType, Float> predicates) {
        BlockModel model = getRawBlockModel(resourceLocation);
        if (model == null) {
            return null;
        }
        for (ModelOverride override : model.getOverrides()) {
            if (override.test(predicates)) {
                return resolveBlockModel(override.getModel(), is1_8, null);
            }
        }
        while (model.getParent() != null) {
            if (model.getRawParent().equals(ModelManager.ITEM_BASE)) {
                break;
            }
            if (model.getRawParent().equals(ModelManager.BLOCK_ENTITY_BASE)) {
                BlockModel builtinModel = resolveBlockModel(ResourceRegistry.BUILTIN_ENTITY_MODEL_LOCATION + resourceLocation.substring(resourceLocation.lastIndexOf("/") + 1), is1_8, predicates);
                if (builtinModel != null) {
                    return builtinModel;
                }
                break;
            }
            BlockModel parent = getRawBlockModel(model.getParent());
            if (parent == null) {
                break;
            }
            for (ModelOverride override : model.getOverrides()) {
                if (override.test(predicates)) {
                    return resolveBlockModel(override.getModel(), is1_8, null);
                }
            }
            model = model.resolve(parent, is1_8);
        }
        return model.resolve( is1_8);
    }

}
