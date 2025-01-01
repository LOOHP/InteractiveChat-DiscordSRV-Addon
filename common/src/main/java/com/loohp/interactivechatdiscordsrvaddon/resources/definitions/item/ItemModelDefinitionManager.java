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

package com.loohp.interactivechatdiscordsrvaddon.resources.definitions.item;

import com.loohp.interactivechat.libs.org.json.simple.JSONObject;
import com.loohp.interactivechatdiscordsrvaddon.registry.ResourceRegistry;
import com.loohp.interactivechatdiscordsrvaddon.resources.AbstractManager;
import com.loohp.interactivechatdiscordsrvaddon.resources.ResourceLoadingException;
import com.loohp.interactivechatdiscordsrvaddon.resources.ResourceManager;
import com.loohp.interactivechatdiscordsrvaddon.resources.ResourcePackFile;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;

public class ItemModelDefinitionManager extends AbstractManager implements IItemsModelDefinitionManager {

    public static final ItemModelDefinition MISSING_MODEL = new ItemModelDefinition.ItemModelDefinitionModel(true, ResourceRegistry.ICD_PREFIX + "does_not_exist", Collections.emptyList());

    private static final ItemModelDefinition LEGACY_DEFINITION = new ItemModelDefinition.ItemModelDefinitionInteractiveChatDiscordSrvAddonLegacy(true);

    private Map<String, ItemModelDefinition> itemDefinitions;

    public ItemModelDefinitionManager(ResourceManager manager) {
        super(manager);
        this.itemDefinitions = new HashMap<>();
    }

    @Override
    protected void loadDirectory(String namespace, ResourcePackFile root, Object... meta) {
        if (!root.exists() || !root.isDirectory()) {
            throw new IllegalArgumentException(root.getAbsolutePath() + " is not a directory.");
        }
        Map<String, ItemModelDefinition> itemDefinitions = new HashMap<>();
        Collection<ResourcePackFile> files = root.listFilesRecursively(new String[] {"json"});
        for (ResourcePackFile file : files) {
            try {
                String key = namespace + ":" + file.getRelativePathFrom(root);
                key = key.substring(0, key.lastIndexOf("."));
                JSONObject json = readJSONObject(file);
                JSONObject rootJson = (JSONObject) json.get("model");
                if (rootJson != null) {
                    ItemModelDefinition itemModelDefinition = ItemModelDefinition.fromJson(rootJson);
                    itemDefinitions.put(key, itemModelDefinition);
                }
            } catch (Exception e) {
                new ResourceLoadingException("Unable to load item model definition " + file.getAbsolutePath(), e).printStackTrace();
            }
        }
        this.itemDefinitions.putAll(itemDefinitions);
    }

    @Override
    protected void filterResources(Pattern namespace, Pattern path) {
        Iterator<String> itr = itemDefinitions.keySet().iterator();
        while (itr.hasNext()) {
            String namespacedKey = itr.next();
            String assetNamespace = namespacedKey.substring(0, namespacedKey.indexOf(":"));
            String assetKey = namespacedKey.substring(namespacedKey.indexOf(":") + 1);
            if (!assetKey.contains(".")) {
                assetKey = assetKey + ".json";
            }
            if (namespace.matcher(assetNamespace).matches() && path.matcher(assetKey).matches()) {
                itr.remove();
            }
        }
    }

    @Override
    protected void reload() {

    }

    @Override
    public ItemModelDefinition getItemModelDefinition(String resourceLocation) {
        if (manager.hasFlag(ResourceManager.Flag.LEGACY_MODEL_DEFINITION)) {
            return LEGACY_DEFINITION;
        }
        if (!resourceLocation.contains(":")) {
            resourceLocation = ResourceRegistry.DEFAULT_NAMESPACE + ":" + resourceLocation;
        }
        return itemDefinitions.get(resourceLocation);
    }
}
