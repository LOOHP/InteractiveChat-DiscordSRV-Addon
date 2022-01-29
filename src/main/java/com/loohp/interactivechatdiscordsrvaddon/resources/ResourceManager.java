package com.loohp.interactivechatdiscordsrvaddon.resources;

import com.loohp.interactivechat.libs.net.kyori.adventure.text.Component;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.format.NamedTextColor;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import com.loohp.interactivechat.libs.org.apache.commons.io.input.BOMInputStream;
import com.loohp.interactivechat.libs.org.json.simple.JSONObject;
import com.loohp.interactivechat.libs.org.json.simple.parser.JSONParser;
import com.loohp.interactivechat.utils.InteractiveChatComponentSerializer;
import com.loohp.interactivechat.utils.JsonUtils;
import com.loohp.interactivechatdiscordsrvaddon.resources.fonts.FontManager;
import com.loohp.interactivechatdiscordsrvaddon.resources.languages.LanguageManager;
import com.loohp.interactivechatdiscordsrvaddon.resources.models.ModelManager;
import com.loohp.interactivechatdiscordsrvaddon.resources.textures.TextureManager;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

public class ResourceManager implements AutoCloseable {

    private List<ResourcePackInfo> resourcePackInfo;
    private ModelManager modelManager;
    private TextureManager textureManager;
    private FontManager fontManager;
    private LanguageManager languageManager;

    private AtomicBoolean isValid;
    private UUID uuid;

    public ResourceManager() {
        this.resourcePackInfo = new LinkedList<>();
        this.modelManager = new ModelManager(this);
        this.textureManager = new TextureManager(this);
        this.fontManager = new FontManager(this);
        this.languageManager = new LanguageManager(this);

        this.isValid = new AtomicBoolean(true);
        this.uuid = UUID.randomUUID();
    }

    public synchronized ResourcePackInfo loadResources(File resourcePackFile) {
        String resourcePackName = resourcePackFile.getName();
        if (!resourcePackFile.exists()) {
            new IllegalArgumentException(resourcePackFile.getAbsolutePath() + " is not a directory nor is a zip file.").printStackTrace();
            ResourcePackInfo info = new ResourcePackInfo(null, resourcePackName, "Resource Pack is not a directory nor a zip file.");
            resourcePackInfo.add(0, info);
            return info;
        }
        ResourcePackFile resourcePack;
        if (resourcePackFile.isDirectory()) {
            resourcePack = new ResourcePackSystemFile(resourcePackFile);
        } else {
            try {
                resourcePack = new ResourcePackZipEntryFile(resourcePackFile);
            } catch (IOException e) {
                new IllegalArgumentException(resourcePackFile.getAbsolutePath() + " is an invalid zip file.").printStackTrace();
                ResourcePackInfo info = new ResourcePackInfo(null, resourcePackName, "Resource Pack is an invalid zip file.");
                resourcePackInfo.add(0, info);
                return info;
            }
        }
        ResourcePackFile packMcmeta = resourcePack.getChild("pack.mcmeta");
        if (!packMcmeta.exists()) {
            new RuntimeException(resourcePackName + " does not have a pack.mcmeta").printStackTrace();
            ResourcePackInfo info = new ResourcePackInfo(resourcePack, resourcePackName, "pack.mcmeta not found");
            resourcePackInfo.add(0, info);
            return info;
        }

        JSONObject json;
        try (InputStreamReader reader = new InputStreamReader(new BOMInputStream(packMcmeta.getInputStream()), StandardCharsets.UTF_8)) {
            json = (JSONObject) new JSONParser().parse(reader);
        } catch (Throwable e) {
            new RuntimeException("Unable to read pack.mcmeta for " + resourcePackName, e).printStackTrace();
            ResourcePackInfo info = new ResourcePackInfo(resourcePack, resourcePackName, "Unable to read pack.mcmeta");
            resourcePackInfo.add(0, info);
            return info;
        }

        int format;
        Component description = null;
        try {
            JSONObject packJson = (JSONObject) json.get("pack");
            format = ((Number) packJson.get("pack_format")).intValue();
            String rawDescription = packJson.get("description").toString();
            if (JsonUtils.isValid(rawDescription)) {
                try {
                    description = InteractiveChatComponentSerializer.gson().deserialize(rawDescription);
                } catch (Exception e) {
                    description = null;
                }
            }
            if (description == null) {
                description = LegacyComponentSerializer.legacySection().deserialize(rawDescription);
            }
            if (description.color() == null) {
                description = description.color(NamedTextColor.GRAY);
            }
        } catch (Exception e) {
            new RuntimeException("Invalid pack.mcmeta for " + resourcePackName, e).printStackTrace();
            ResourcePackInfo info = new ResourcePackInfo(resourcePack, resourcePackName, "Invalid pack.mcmeta");
            resourcePackInfo.add(0, info);
            return info;
        }

        ResourcePackFile assetsFolder = resourcePack.getChild("assets");
        try {
            loadAssets(assetsFolder);
        } catch (Exception e) {
            new RuntimeException("Unable to load assets for " + resourcePackName, e).printStackTrace();
            ResourcePackInfo info = new ResourcePackInfo(resourcePack, resourcePackName, false, "Unable to load assets", format, description);
            resourcePackInfo.add(0, info);
            return info;
        }

        ResourcePackInfo info = new ResourcePackInfo(resourcePack, resourcePackName, true, null, format, description);
        resourcePackInfo.add(0, info);
        return info;
    }

    private void loadAssets(ResourcePackFile assetsFolder) throws Exception {
        if (!assetsFolder.exists() || !assetsFolder.isDirectory()) {
            throw new IllegalArgumentException(assetsFolder.getAbsolutePath() + " is not a directory.");
        }
        Collection<ResourcePackFile> folders = assetsFolder.listFilesAndFolders();
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
                    ((AbstractManager) textureManager).loadDirectory(namespace, textures);
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
                    ((AbstractManager) languageManager).loadDirectory(namespace, lang);
                }
            }
        }
        modelManager.reload();
        textureManager.reload();
        fontManager.reload();
        languageManager.reload();
    }

    public List<ResourcePackInfo> getResourcePackInfo() {
        return Collections.unmodifiableList(resourcePackInfo);
    }

    public void setResourcePackInfo(List<ResourcePackInfo> resourcePackInfo) {
        this.resourcePackInfo = resourcePackInfo;
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
        }
    }

}
