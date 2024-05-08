/*
 * This file is part of InteractiveChatDiscordSrvAddon.
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

package com.loohp.interactivechatdiscordsrvaddon.resources.models;

import com.loohp.interactivechat.libs.org.json.simple.JSONObject;
import com.loohp.interactivechatdiscordsrvaddon.registry.ResourceRegistry;
import com.loohp.interactivechatdiscordsrvaddon.resources.AbstractManager;
import com.loohp.interactivechatdiscordsrvaddon.resources.ResourceLoadingException;
import com.loohp.interactivechatdiscordsrvaddon.resources.ResourceManager;
import com.loohp.interactivechatdiscordsrvaddon.resources.ResourcePackFile;
import com.loohp.interactivechatdiscordsrvaddon.resources.models.ModelOverride.ModelOverrideType;
import com.loohp.interactivechatdiscordsrvaddon.utils.TriFunction;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;

public class ModelManager extends AbstractManager implements IModelManager {

    public static final TriFunction<IModelManager, String, JSONObject, BlockModel> DEFAULT_MODEL_PARSING_FUNCTION = (manager, key, json) -> BlockModel.fromJson(manager, key, json);

    public static final String CACHE_KEY = "ModelManager";
    public static final String BLOCK_ENTITY_BASE = "minecraft:builtin/entity";
    public static final String ITEM_BASE = "minecraft:builtin/generated";
    public static final String ITEM_BASE_LAYER = "layer";

    private Map<String, BlockModel> models;
    private TriFunction<IModelManager, String, JSONObject, ? extends BlockModel> modelParsingFunction;

    public ModelManager(ResourceManager manager) {
        super(manager);
        this.models = new HashMap<>();
        this.modelParsingFunction = DEFAULT_MODEL_PARSING_FUNCTION;
    }

    @Override
    protected void loadDirectory(String namespace, ResourcePackFile root, Object... meta) {
        if (!root.exists() || !root.isDirectory()) {
            throw new IllegalArgumentException(root.getAbsolutePath() + " is not a directory.");
        }
        Map<String, BlockModel> models = new HashMap<>();
        Collection<ResourcePackFile> files = root.listFilesRecursively(new String[] {"json"});
        for (ResourcePackFile file : files) {
            try {
                String key = namespace + ":" + file.getRelativePathFrom(root);
                key = key.substring(0, key.lastIndexOf("."));
                JSONObject rootJson = readJSONObject(file);
                BlockModel model = modelParsingFunction.apply(this, key, rootJson);
                models.put(key, model);
            } catch (Exception e) {
                new ResourceLoadingException("Unable to load block model " + file.getAbsolutePath(), e).printStackTrace();
            }
        }
        this.models.putAll(models);
    }

    @Override
    protected void filterResources(Pattern namespace, Pattern path) {
        Iterator<String> itr = models.keySet().iterator();
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

    public TriFunction<IModelManager, String, JSONObject, ? extends BlockModel> getModelParsingFunction() {
        return modelParsingFunction;
    }

    public void setModelParsingFunction(TriFunction<IModelManager, String, JSONObject, ? extends BlockModel> modelParsingFunction) {
        this.modelParsingFunction = modelParsingFunction;
    }

    @Override
    public BlockModel getRawBlockModel(String resourceLocation) {
        return models.get(resourceLocation);
    }

    public Map<String, BlockModel> getRawBlockModelMapping() {
        return Collections.unmodifiableMap(models);
    }

    @Override
    public BlockModel resolveBlockModel(String resourceLocation, boolean is1_8, Map<ModelOverrideType, Float> predicates) {
        BlockModel model = models.get(resourceLocation);
        if (model == null) {
            return null;
        }
        for (ModelOverride override : model.getOverrides()) {
            if (override.test(predicates)) {
                return resolveBlockModel(override.getModel(), is1_8, null);
            }
        }
        while (model.getParent() != null) {
            if (model.getParent().equals(ITEM_BASE)) {
                break;
            }
            if (model.getParent().equals(BLOCK_ENTITY_BASE)) {
                BlockModel builtinModel = resolveBlockModel(ResourceRegistry.BUILTIN_ENTITY_MODEL_LOCATION + resourceLocation.substring(resourceLocation.lastIndexOf("/") + 1), is1_8, predicates);
                if (builtinModel != null) {
                    return builtinModel;
                }
                break;
            }
            BlockModel parent = models.get(model.getParent());
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
