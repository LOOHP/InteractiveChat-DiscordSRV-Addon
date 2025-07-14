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

package com.loohp.interactivechatdiscordsrvaddon.resources;

import com.loohp.interactivechat.libs.com.google.gson.GsonBuilder;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.Component;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.format.NamedTextColor;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import com.loohp.interactivechat.libs.org.apache.commons.io.input.BOMInputStream;
import com.loohp.interactivechat.libs.org.json.simple.JSONArray;
import com.loohp.interactivechat.libs.org.json.simple.JSONObject;
import com.loohp.interactivechat.libs.org.json.simple.parser.JSONParser;
import com.loohp.interactivechat.utils.InteractiveChatComponentSerializer;
import com.loohp.interactivechatdiscordsrvaddon.registry.ResourceRegistry;
import com.loohp.interactivechatdiscordsrvaddon.resources.definitions.equipment.EquipmentModelDefinitionManager;
import com.loohp.interactivechatdiscordsrvaddon.resources.definitions.item.ItemModelDefinitionManager;
import com.loohp.interactivechatdiscordsrvaddon.resources.fonts.FontManager;
import com.loohp.interactivechatdiscordsrvaddon.resources.languages.LanguageManager;
import com.loohp.interactivechatdiscordsrvaddon.resources.languages.LanguageMeta;
import com.loohp.interactivechatdiscordsrvaddon.resources.models.ModelManager;
import com.loohp.interactivechatdiscordsrvaddon.resources.mods.ModManager;
import com.loohp.interactivechatdiscordsrvaddon.resources.textures.TextureManager;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public class ResourceManager implements AutoCloseable {

    private final int nativeServerPackFormat;
    private final List<ResourcePackInfo> resourcePackInfo;

    private final Map<String, IResourceRegistry> resourceRegistries;

    private final ItemModelDefinitionManager itemModelDefinitionManager;
    private final EquipmentModelDefinitionManager equipmentModelDefinitionManager;
    private final ModelManager modelManager;
    private final TextureManager textureManager;
    private final FontManager fontManager;
    private final LanguageManager languageManager;

    private final Map<String, ModManager> modManagers;

    private final Set<Flag> flags;
    private final Map<String, TextureAtlases> textureAtlases;

    private final BiFunction<File, ResourcePackType, DefaultResourcePackInfo> defaultResourcePackInfoFunction;
    private final AtomicBoolean resourcesLoaded;
    private final AtomicBoolean isValid;
    private final UUID uuid;

    public ResourceManager(int nativeServerPackFormat, Collection<ModManagerSupplier<?>> modManagerProviders, Collection<ResourceRegistrySupplier<?>> resourceManagerUtilsProviders, BiFunction<File, ResourcePackType, DefaultResourcePackInfo> defaultResourcePackInfoFunction, Flag... flags) {
        this.nativeServerPackFormat = nativeServerPackFormat;

        this.resourcePackInfo = new ArrayList<>();
        this.defaultResourcePackInfoFunction = defaultResourcePackInfoFunction;

        this.flags = flags.length == 0 ? Collections.emptySet() : Collections.unmodifiableSet(EnumSet.copyOf(Arrays.asList(flags)));
        this.textureAtlases = new HashMap<>();

        this.resourcesLoaded = new AtomicBoolean(false);
        this.isValid = new AtomicBoolean(true);
        this.uuid = UUID.randomUUID();

        this.resourceRegistries = new HashMap<>();
        for (ResourceRegistrySupplier<?> resourceRegistrySupplier : resourceManagerUtilsProviders) {
            IResourceRegistry resourceRegistry = resourceRegistrySupplier.init(this);
            this.resourceRegistries.put(resourceRegistry.getRegistryIdentifier(), resourceRegistry);
        }

        this.itemModelDefinitionManager = new ItemModelDefinitionManager(this);
        this.equipmentModelDefinitionManager = new EquipmentModelDefinitionManager(this);
        this.modelManager = new ModelManager(this);
        this.textureManager = new TextureManager(this);
        this.fontManager = new FontManager(this);
        this.languageManager = new LanguageManager(this);

        this.modManagers = new HashMap<>();
        for (ModManagerSupplier<?> modManagerProvider : modManagerProviders) {
            ModManager modManager = modManagerProvider.init(this);
            this.modManagers.put(modManager.getModName(), modManager);
        }
    }

    public ResourceManager(int nativeServerPackFormat, Collection<ModManagerSupplier<?>> modManagerProviders, Collection<ResourceRegistrySupplier<?>> resourceManagerUtilsProviders, PackFormat defaultResourcePackVersion, Flag... flags) {
        this(nativeServerPackFormat, modManagerProviders, resourceManagerUtilsProviders, (resourcePackFile, type) -> {
            return new ResourceManager.DefaultResourcePackInfo(Component.text(resourcePackFile.getName()), defaultResourcePackVersion, Component.text("The default look and feel of Minecraft (Modified by LOOHP)"));
        }, flags);
    }

    public synchronized Map<ResourcePackSource, ResourcePackInfo> loadResources(List<ResourcePackSource> resourcePackSources, BiConsumer<ResourcePackSource, ResourcePackInfo> progressListener) {
        if (!isValid()) {
            throw new IllegalStateException("ResourceManager already closed!");
        }
        if (hasResourcesLoaded()) {
            throw new IllegalStateException("ResourceManager already loaded resources!");
        }
        Map<ResourcePackSource, ResourcePackInfo> result = new HashMap<>(resourcePackSources.size());

        Map<ResourcePackSource, ResourcePackFile> resourcePackFilesMap = new HashMap<>();
        Map<String, TextureAtlases> textureAtlasesMap = new HashMap<>();
        for (ResourcePackSource resourcePackSource : resourcePackSources) {
            File resourcePackFile = resourcePackSource.getResourcePackFile();
            ResourcePackType type = resourcePackSource.getType();
            boolean defaultResource = resourcePackSource.isDefaultResource();

            try {
                DefaultResourcePackInfo defaultResourcePackInfo = defaultResource ? defaultResourcePackInfoFunction.apply(resourcePackFile, type) : null;

                String resourcePackNameStr = resourcePackFile.getName();
                Component resourcePackName = Component.text(resourcePackNameStr);
                if (!resourcePackFile.exists()) {
                    new IllegalArgumentException(resourcePackFile.getAbsolutePath() + " is not a directory nor is a zip file.").printStackTrace();
                    ResourcePackInfo info = new ResourcePackInfo(this, null, type, resourcePackName, "Resource Pack is not a directory nor a zip file.");
                    resourcePackInfo.add(0, info);
                    result.put(resourcePackSource, info);
                    continue;
                }
                ResourcePackFile resourcePack;
                if (resourcePackFile.isDirectory()) {
                    resourcePack = new ResourcePackSystemFile(resourcePackFile);
                } else {
                    try {
                        resourcePack = new ResourcePackZipEntryFile(resourcePackFile);
                    } catch (IOException e) {
                        new IllegalArgumentException(resourcePackFile.getAbsolutePath() + " is an invalid zip file.", e).printStackTrace();
                        ResourcePackInfo info = new ResourcePackInfo(this, null, type, resourcePackName, "Resource Pack is an invalid zip file.");
                        resourcePackInfo.add(0, info);
                        result.put(resourcePackSource, info);
                        continue;
                    }
                }

                ResourcePackFile assetsFolder = resourcePack.getChild("assets");
                Map<String, TextureAtlases> textureAtlases = loadAtlases(assetsFolder);

                for (Map.Entry<String, TextureAtlases> entry : textureAtlases.entrySet()) {
                    String key = entry.getKey();
                    TextureAtlases atlases = entry.getValue();
                    TextureAtlases existing = textureAtlasesMap.get(key);
                    if (existing == null) {
                        textureAtlasesMap.put(key, atlases);
                    } else {
                        textureAtlasesMap.put(key, existing.merge(atlases));
                    }
                }

                resourcePackFilesMap.put(resourcePackSource, resourcePack);
            } catch (Exception e) {
                new ResourceLoadingException("Unable to load resource pack from file " + resourcePackFile.getAbsolutePath(), e).printStackTrace();
            }
        }

        this.textureAtlases.putAll(textureAtlasesMap);

        for (ResourcePackSource resourcePackSource : resourcePackSources) {
            ResourcePackFile resourcePack = resourcePackFilesMap.get(resourcePackSource);
            if (resourcePack == null) {
                progressListener.accept(resourcePackSource, null);
                continue;
            }
            File resourcePackFile = resourcePackSource.getResourcePackFile();
            ResourcePackType type = resourcePackSource.getType();
            boolean defaultResource = resourcePackSource.isDefaultResource();

            try {
                DefaultResourcePackInfo defaultResourcePackInfo = defaultResource ? defaultResourcePackInfoFunction.apply(resourcePackFile, type) : null;
                String resourcePackNameStr = resourcePackFile.getName();
                Component resourcePackName = Component.text(resourcePackNameStr);

                ResourcePackFile packMcmeta = resourcePack.getChild("pack.mcmeta");
                if (!packMcmeta.exists()) {
                    new ResourceLoadingException(resourcePackNameStr + " does not have a pack.mcmeta").printStackTrace();
                    ResourcePackInfo info = new ResourcePackInfo(this, resourcePack, type, resourcePackName, "pack.mcmeta not found");
                    resourcePackInfo.add(0, info);
                    result.put(resourcePackSource, info);
                    continue;
                }

                JSONObject json;
                try (InputStreamReader reader = new InputStreamReader(new BOMInputStream(packMcmeta.getInputStream()), StandardCharsets.UTF_8)) {
                    json = (JSONObject) new JSONParser().parse(reader);
                } catch (Throwable e) {
                    new ResourceLoadingException("Unable to read pack.mcmeta for " + resourcePackNameStr, e).printStackTrace();
                    ResourcePackInfo info = new ResourcePackInfo(this, resourcePack, type, resourcePackName, "Unable to read pack.mcmeta");
                    resourcePackInfo.add(0, info);
                    result.put(resourcePackSource, info);
                    continue;
                }

                PackFormat format;
                Component description = null;
                Map<String, LanguageMeta> languageMeta = new HashMap<>();
                List<PackOverlay> overlays = new ArrayList<>();
                List<ResourceFilterBlock> resourceFilterBlocks;
                try {
                    JSONObject packJson = (JSONObject) json.get("pack");
                    if (packJson == null && defaultResource) {
                        resourcePackName = defaultResourcePackInfo.getName();
                        format = defaultResourcePackInfo.getVersion();
                        description = defaultResourcePackInfo.getDescription();
                    } else {
                        int majorFormat = ((Number) packJson.get("pack_format")).intValue();
                        if (packJson.containsKey("supported_formats")) {
                            Object supportedFormatsObj = packJson.get("supported_formats");
                            if (supportedFormatsObj instanceof Number) {
                                int supportedFormat = ((Number) supportedFormatsObj).intValue();
                                format = PackFormat.version(majorFormat, supportedFormat, supportedFormat);
                            } else if (supportedFormatsObj instanceof JSONArray) {
                                JSONArray supportedFormats = (JSONArray) supportedFormatsObj;
                                format = PackFormat.version(majorFormat, ((Number) supportedFormats.get(0)).intValue(), ((Number) supportedFormats.get(1)).intValue());
                            } else if (supportedFormatsObj instanceof JSONObject) {
                                JSONObject supportedFormats = (JSONObject) supportedFormatsObj;
                                format = PackFormat.version(majorFormat, ((Number) supportedFormats.get("min_inclusive")).intValue(), ((Number) supportedFormats.get("max_inclusive")).intValue());
                            } else {
                                throw new IllegalArgumentException("Don't know how to read supported_formats " + supportedFormatsObj);
                            }
                        } else {
                            format = PackFormat.version(majorFormat);
                        }
                        Object descriptionObj = packJson.get("description");
                        if (descriptionObj instanceof JSONObject) {
                            String descriptionJson = new GsonBuilder().create().toJson(descriptionObj);
                            try {
                                description = InteractiveChatComponentSerializer.gson().deserialize(descriptionJson);
                            } catch (Exception e) {
                                description = null;
                            }
                        }
                        if (description == null) {
                            String rawDescription = packJson.get("description").toString();
                            try {
                                description = InteractiveChatComponentSerializer.gson().deserialize(rawDescription);
                            } catch (Exception e) {
                                description = null;
                            }
                            if (description == null) {
                                description = LegacyComponentSerializer.legacySection().deserialize(rawDescription);
                            }
                        }
                    }
                    description = description.applyFallbackStyle(NamedTextColor.GRAY);

                    JSONObject languageJson = (JSONObject) json.get("language");
                    if (languageJson != null) {
                        for (Object obj : languageJson.keySet()) {
                            String language = (String) obj;
                            JSONObject meta = (JSONObject) languageJson.get(language);
                            String region = (String) meta.get("region");
                            String name = (String) meta.get("name");
                            boolean bidirectional = (boolean) meta.get("bidirectional");
                            languageMeta.put(language, new LanguageMeta(language, region, name, bidirectional));
                        }
                    }

                    JSONObject overlaysJson = (JSONObject) json.get("overlays");
                    if (overlaysJson != null) {
                        JSONArray entriesArray = (JSONArray) overlaysJson.get("entries");
                        for (Object obj : entriesArray) {
                            JSONObject entry = (JSONObject) obj;

                            PackFormat overlayFormat;
                            Object formatsObj = entry.get("formats");
                            if (formatsObj instanceof Number) {
                                int supportedFormat = ((Number) formatsObj).intValue();
                                overlayFormat = PackFormat.version(supportedFormat, supportedFormat);
                            } else if (formatsObj instanceof JSONArray) {
                                JSONArray supportedFormats = (JSONArray) formatsObj;
                                overlayFormat = PackFormat.version(((Number) supportedFormats.get(0)).intValue(), ((Number) supportedFormats.get(1)).intValue());
                            } else if (formatsObj instanceof JSONObject) {
                                JSONObject supportedFormats = (JSONObject) formatsObj;
                                overlayFormat = PackFormat.version(((Number) supportedFormats.get("min_inclusive")).intValue(), ((Number) supportedFormats.get("max_inclusive")).intValue());
                            } else {
                                throw new IllegalArgumentException("Don't know how to read supported_formats " + formatsObj);
                            }

                            String directory = (String) entry.get("directory");
                            overlays.add(0, new PackOverlay(overlayFormat, directory));
                        }
                    }

                    JSONObject filterJson = (JSONObject) json.get("filter");
                    JSONArray filterBlockArray;
                    if (filterJson != null && (filterBlockArray = (JSONArray) filterJson.get("block")) != null) {
                        resourceFilterBlocks = ResourceFilterBlock.fromJson(filterBlockArray);
                    } else {
                        resourceFilterBlocks = Collections.emptyList();
                    }
                } catch (Exception e) {
                    new ResourceLoadingException("Invalid pack.mcmeta for " + resourcePackNameStr, e).printStackTrace();
                    ResourcePackInfo info = new ResourcePackInfo(this, resourcePack, type, resourcePackName, "Invalid pack.mcmeta");
                    resourcePackInfo.add(0, info);
                    result.put(resourcePackSource, info);
                    continue;
                }

                BufferedImage icon = null;
                ResourcePackFile packIcon = resourcePack.getChild("pack.png");
                if (packIcon.exists()) {
                    try (InputStream inputStream = packIcon.getInputStream()) {
                        icon = ImageIO.read(inputStream);
                    } catch (Exception ignore) {
                    }
                }

                ResourcePackFile assetsFolder = resourcePack.getChild("assets");

                for (PackOverlay overlay : overlays) {
                    if (overlay.getFormats().isCompatible(nativeServerPackFormat)) {
                        try {
                            ResourcePackFile overlayAssetsFolder = resourcePack.getChild(overlay.getDirectory()).getChild("assets");
                            Map<String, TextureAtlases> overlayTextureAtlases = loadAtlases(overlayAssetsFolder);
                            for (Map.Entry<String, TextureAtlases> entry : overlayTextureAtlases.entrySet()) {
                                String namespace = entry.getKey();
                                TextureAtlases atlases = textureAtlases.get(namespace);
                                if (atlases == null) {
                                    textureAtlases.put(namespace, entry.getValue());
                                } else {
                                    textureAtlases.put(namespace, atlases.merge(entry.getValue()));
                                }
                            }
                        } catch (Throwable e) {
                            new ResourceLoadingException("Unable to load overlay " + overlay.getDirectory() + " for pack " + resourcePackNameStr, e).printStackTrace();
                        }
                    }
                }

                ResourcePackInfo info = new ResourcePackInfo(this, resourcePack, type, resourcePackName, true, null, format, description, languageMeta, icon, resourceFilterBlocks, overlays);
                resourcePackInfo.add(0, info);

                try {
                    filterResources(resourceFilterBlocks);
                    loadAssets(assetsFolder, languageMeta, textureAtlases);
                    for (PackOverlay overlay : overlays) {
                        try {
                            if (overlay.getFormats().isCompatible(nativeServerPackFormat)) {
                                loadAssets(resourcePack.getChild(overlay.getDirectory()).getChild("assets"), languageMeta, textureAtlases);
                            }
                        } catch (Throwable e) {
                            new ResourceLoadingException("Unable to load overlay " + overlay.getDirectory() + " for pack " + resourcePackNameStr, e).printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    new ResourceLoadingException("Unable to load assets for " + resourcePackNameStr, e).printStackTrace();
                    resourcePackInfo.remove(0);
                    info = new ResourcePackInfo(this, resourcePack, type, resourcePackName, false, "Unable to load assets", format, description, languageMeta, icon, resourceFilterBlocks, overlays);
                    resourcePackInfo.add(0, info);
                    result.put(resourcePackSource, info);
                    continue;
                }

                result.put(resourcePackSource, info);
                progressListener.accept(resourcePackSource, info);
            } catch (Exception e) {
                new ResourceLoadingException("Unable to load resource pack from file " + resourcePackFile.getAbsolutePath(), e).printStackTrace();
                progressListener.accept(resourcePackSource, null);
            }
        }
        resourcesLoaded.set(true);
        return result;
    }

    private void filterResources(List<ResourceFilterBlock> resourceFilterBlocks) {
        for (ResourceFilterBlock resourceFilterBlock : resourceFilterBlocks) {
            Pattern namespace = resourceFilterBlock.getNamespace();
            Pattern path = resourceFilterBlock.getPath();

            ((AbstractManager) itemModelDefinitionManager).filterResources(namespace, path);
            ((AbstractManager) equipmentModelDefinitionManager).filterResources(namespace, path);
            ((AbstractManager) modelManager).filterResources(namespace, path);
            ((AbstractManager) textureManager).filterResources(namespace, path);
            ((AbstractManager) fontManager).filterResources(namespace, path);
            ((AbstractManager) languageManager).filterResources(namespace, path);
            for (ModManager modManager : modManagers.values()) {
                modManager.filterResources(namespace, path);
            }
        }

        ((AbstractManager) itemModelDefinitionManager).reload();
        ((AbstractManager) equipmentModelDefinitionManager).reload();
        ((AbstractManager) modelManager).reload();
        ((AbstractManager) textureManager).reload();
        ((AbstractManager) fontManager).reload();
        ((AbstractManager) languageManager).reload();
        for (ModManager modManager : modManagers.values()) {
            modManager.reload();
        }
    }

    private Map<String, TextureAtlases> loadAtlases(ResourcePackFile assetsFolder) {
        if (!assetsFolder.exists() || !assetsFolder.isDirectory()) {
            throw new IllegalArgumentException(assetsFolder.getAbsolutePath() + " is not a directory.");
        }
        Collection<ResourcePackFile> folders = assetsFolder.listFilesAndFolders();
        Map<String, TextureAtlases> atlasesByNamespace = new HashMap<>();
        for (ResourcePackFile folder : folders) {
            if (folder.isDirectory()) {
                String namespace = folder.getName();
                ResourcePackFile atlases = folder.getChild("atlases");
                if (atlases.exists() && atlases.isDirectory()) {
                    atlasesByNamespace.put(namespace, TextureAtlases.fromAtlasesFolder(atlases));
                }
            }
        }
        return Collections.unmodifiableMap(atlasesByNamespace);
    }

    private void loadAssets(ResourcePackFile assetsFolder, Map<String, LanguageMeta> languageMeta, Map<String, TextureAtlases> textureAtlases) {
        if (!assetsFolder.exists() || !assetsFolder.isDirectory()) {
            throw new IllegalArgumentException(assetsFolder.getAbsolutePath() + " is not a directory.");
        }
        Collection<ResourcePackFile> folders = assetsFolder.listFilesAndFolders();
        for (ResourcePackFile folder : folders) {
            if (folder.isDirectory()) {
                String namespace = folder.getName();
                ResourcePackFile equipment = folder.getChild("equipment");
                if (equipment.exists() && equipment.isDirectory()) {
                    ((AbstractManager) equipmentModelDefinitionManager).loadDirectory(namespace, equipment);
                }
            }
        }
        for (ResourcePackFile folder : folders) {
            if (folder.isDirectory()) {
                String namespace = folder.getName();
                ResourcePackFile items = folder.getChild("items");
                if (items.exists() && items.isDirectory()) {
                    ((AbstractManager) itemModelDefinitionManager).loadDirectory(namespace, items);
                }
            }
        }
        for (ResourcePackFile folder : folders) {
            if (folder.isDirectory()) {
                String namespace = folder.getName();
                ResourcePackFile models = folder.getChild("models");
                if (models.exists() && models.isDirectory()) {
                    ((AbstractManager) modelManager).loadDirectory(namespace, models);
                }
            }
        }
        for (ResourcePackFile folder : folders) {
            if (folder.isDirectory()) {
                String namespace = folder.getName();
                ResourcePackFile textures = folder.getChild("textures");
                if (textures.exists() && textures.isDirectory()) {
                    if (ResourceRegistry.RESOURCE_PACK_VERSION <= 9) {
                        ((AbstractManager) textureManager).loadDirectory(namespace, textures);
                    } else {
                        ((AbstractManager) textureManager).loadDirectory(namespace, textures, textureAtlases.getOrDefault(namespace, TextureAtlases.EMPTY_ATLAS));
                    }
                }
            }
        }
        for (ResourcePackFile folder : folders) {
            if (folder.isDirectory()) {
                String namespace = folder.getName();
                ResourcePackFile font = folder.getChild("font");
                if (font.exists() && font.isDirectory()) {
                    ((AbstractManager) fontManager).loadDirectory(namespace, font);
                }
            }
        }
        for (ResourcePackFile folder : folders) {
            if (folder.isDirectory()) {
                String namespace = folder.getName();
                ResourcePackFile lang = folder.getChild("lang");
                if (lang.exists() && lang.isDirectory()) {
                    ((AbstractManager) languageManager).loadDirectory(namespace, lang, languageMeta);
                }
            }
        }
        for (ModManager modManager : modManagers.values()) {
            for (String folderName : modManager.getModAssetsFolderNames()) {
                for (ResourcePackFile folder : folders) {
                    if (folder.isDirectory()) {
                        String namespace = folder.getName();
                        ResourcePackFile modFolder = folder.getChild(folderName);
                        if (modFolder.exists() && modFolder.isDirectory()) {
                            modManager.loadDirectory(namespace, modFolder);
                        }
                    }
                }
            }
        }

        ((AbstractManager) modelManager).reload();
        ((AbstractManager) textureManager).reload();
        ((AbstractManager) fontManager).reload();
        ((AbstractManager) languageManager).reload();
        for (ModManager modManager : modManagers.values()) {
            modManager.reload();
        }
    }

    public List<ResourcePackInfo> getResourcePackInfo() {
        return Collections.unmodifiableList(resourcePackInfo);
    }

    public int getNativeServerPackFormat() {
        return nativeServerPackFormat;
    }

    public ItemModelDefinitionManager getItemModelDefinitionManager() {
        return itemModelDefinitionManager;
    }

    public EquipmentModelDefinitionManager getEquipmentModelDefinitionManager() {
        return equipmentModelDefinitionManager;
    }

    public ModelManager getModelManager() {
        return modelManager;
    }

    public TextureManager getTextureManager() {
        return textureManager;
    }

    public FontManager getFontManager() {
        return fontManager;
    }

    public LanguageManager getLanguageManager() {
        return languageManager;
    }

    public ModManager getModManager(String modName) {
        return modManagers.get(modName);
    }

    public boolean hasModManager(String modName) {
        return modManagers.containsKey(modName);
    }

    public <T extends ModManager> T getModManager(String modName, Class<T> managerClass) {
        return (T) getModManager(modName);
    }

    public <T extends ModManager> boolean hasModManager(String modName, Class<T> managerClass) {
        return managerClass.isInstance(getModManager(modName));
    }

    public Map<String, ModManager> getModManagers() {
        return Collections.unmodifiableMap(modManagers);
    }

    public IResourceRegistry getResourceRegistry(String identifier) {
        return resourceRegistries.get(identifier);
    }

    public boolean hasResourceRegistry(String identifier) {
        return resourceRegistries.containsKey(identifier);
    }

    public <T extends IResourceRegistry> T getResourceRegistry(String identifier, Class<T> registryClass) {
        return (T) getResourceRegistry(identifier);
    }

    public <T extends IResourceRegistry> boolean hasResourceRegistry(String identifier, Class<T> registryClass) {
        return registryClass.isInstance(getResourceRegistry(identifier));
    }

    public Map<String, IResourceRegistry> getResourceRegistries() {
        return Collections.unmodifiableMap(resourceRegistries);
    }

    public Set<Flag> getFlags() {
        return flags;
    }

    public boolean hasLegacyFlags() {
        return flags.stream().anyMatch(f -> f.isLegacyFlag());
    }

    public boolean hasFlag(Flag flag) {
        return flags.contains(flag);
    }

    public boolean hasFlags(Flag... flags) {
        return this.flags.containsAll(Arrays.asList(flags));
    }

    public ResourcePackFile findResource(String resourceLocation) {
        if (!resourceLocation.contains(":")) {
            resourceLocation = "minecraft:" + resourceLocation;
        }
        String[] paths = ("assets/" + resourceLocation).split("[:/]");
        outer: for (ResourcePackInfo info : resourcePackInfo) {
            ResourcePackFile file = info.getResourcePackFile();
            for (int i = 0; i < paths.length; i++) {
                String path = paths[i];
                if (i < paths.length - 1 && !file.isDirectory()) {
                    continue outer;
                }
                file = file.getChild(path);
                if (!file.exists()) {
                    continue outer;
                }
            }
            return file;
        }
        return null;
    }

    public boolean hasResourcesLoaded() {
        return resourcesLoaded.get();
    }

    public Map<String, TextureAtlases> getTextureAtlases() {
        return Collections.unmodifiableMap(textureAtlases);
    }

    public boolean isValid() {
        return isValid.get();
    }

    protected UUID getUuid() {
        return uuid;
    }

    @Override
    public synchronized void close() {
        if (isValid.getAndSet(false)) {
            for (ResourcePackInfo info : resourcePackInfo) {
                if (info.getResourcePackFile() != null) {
                    info.getResourcePackFile().close();
                }
            }
            for (IResourceRegistry resourceRegistry : resourceRegistries.values()) {
                resourceRegistry.close();
            }

            itemModelDefinitionManager.close();
            equipmentModelDefinitionManager.close();
            modelManager.close();
            textureManager.close();
            fontManager.close();
            languageManager.close();
            for (ModManager modManager : modManagers.values()) {
                modManager.close();
            }
        }
    }

    @FunctionalInterface
    public interface ModManagerSupplier<T extends ModManager> extends Function<ResourceManager, T> {

        T init(ResourceManager resourceManager);

        @Override
        default T apply(ResourceManager resourceManager) {
            return init(resourceManager);
        }

    }

    @FunctionalInterface
    public interface ResourceRegistrySupplier<T extends IResourceRegistry> extends Function<ResourceManager, T> {

        T init(ResourceManager resourceManager);

        @Override
        default T apply(ResourceManager resourceManager) {
            return init(resourceManager);
        }

    }

    public static class DefaultResourcePackInfo {

        private final Component name;
        private final PackFormat version;
        private final Component description;

        public DefaultResourcePackInfo(Component name, PackFormat version, Component description) {
            this.name = name;
            this.version = version;
            this.description = description;
        }

        public Component getName() {
            return name;
        }

        public PackFormat getVersion() {
            return version;
        }

        public Component getDescription() {
            return description;
        }

    }

    public enum Flag {

        LEGACY_PRE_FLATTEN(true),
        LEGACY_HARDCODED_SPACE_FONT(true),
        LEGACY_MODEL_DEFINITION(true);

        private final static Flag[] VALUES = values();

        private final boolean legacy;

        Flag(boolean legacy) {
            this.legacy = legacy;
        }

        Flag() {
            this(false);
        }

        public boolean isLegacyFlag() {
            return legacy;
        }

        public static Flag[] build(boolean... values) {
            return IntStream.range(0, values.length).filter(i -> values[i]).mapToObj(i -> VALUES[i]).toArray(Flag[]::new);
        }

    }

}
