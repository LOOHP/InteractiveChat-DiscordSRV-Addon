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

package com.loohp.interactivechatdiscordsrvaddon.resources.models;

import com.loohp.interactivechat.libs.org.json.simple.JSONArray;
import com.loohp.interactivechat.libs.org.json.simple.JSONObject;
import com.loohp.interactivechatdiscordsrvaddon.registry.ResourceRegistry;
import com.loohp.interactivechatdiscordsrvaddon.resources.models.ModelDisplay.ModelDisplayPosition;
import com.loohp.interactivechatdiscordsrvaddon.resources.models.ModelElement.ModelElementRotation;
import com.loohp.interactivechatdiscordsrvaddon.resources.models.ModelFace.ModelFaceSide;
import com.loohp.interactivechatdiscordsrvaddon.resources.models.ModelOverride.ModelOverrideType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

public class BlockModel {

    public static BlockModel fromJson(IModelManager manager, String resourceLocation, JSONObject rootJson, boolean useLegacyOverrides) {
        String parent = (String) rootJson.getOrDefault("parent", null);
        boolean ambientocclusion = (boolean) rootJson.getOrDefault("ambientocclusion", true);
        ModelGUILight guiLight = rootJson.containsKey("gui_light") ? ModelGUILight.fromKey((String) rootJson.get("gui_light")) : null;
        Map<ModelDisplayPosition, ModelDisplay> display = new EnumMap<>(ModelDisplayPosition.class);
        JSONObject displayJson = (JSONObject) rootJson.get("display");
        if (displayJson != null) {
            for (Object obj : displayJson.keySet()) {
                String displayKey = obj.toString();
                JSONArray rotationArray = (JSONArray) ((JSONObject) displayJson.get(displayKey)).get("rotation");
                JSONArray translationArray = (JSONArray) ((JSONObject) displayJson.get(displayKey)).get("translation");
                JSONArray scaleArray = (JSONArray) ((JSONObject) displayJson.get(displayKey)).get("scale");
                Coordinates3D rotation;
                if (rotationArray == null) {
                    rotation = new Coordinates3D(0, 0, 0);
                } else {
                    rotation = new Coordinates3D(((Number) rotationArray.get(0)).doubleValue(), ((Number) rotationArray.get(1)).doubleValue(), ((Number) rotationArray.get(2)).doubleValue());
                }
                Coordinates3D translation;
                if (translationArray == null) {
                    translation = new Coordinates3D(0, 0, 0);
                } else {
                    translation = new Coordinates3D(((Number) translationArray.get(0)).doubleValue(), ((Number) translationArray.get(1)).doubleValue(), ((Number) translationArray.get(2)).doubleValue());
                }
                Coordinates3D scale;
                if (scaleArray == null) {
                    scale = new Coordinates3D(1, 1, 1);
                } else {
                    scale = new Coordinates3D(((Number) scaleArray.get(0)).doubleValue(), ((Number) scaleArray.get(1)).doubleValue(), ((Number) scaleArray.get(2)).doubleValue());
                }
                ModelDisplayPosition displayPos = ModelDisplayPosition.fromKey(displayKey);
                display.put(displayPos, new ModelDisplay(displayPos, rotation, translation, scale));
            }
        }
        Map<String, String> texture = new HashMap<>();
        JSONObject textureJson = (JSONObject) rootJson.get("textures");
        if (textureJson != null) {
            for (Object obj : textureJson.keySet()) {
                String textureKey = obj.toString();
                texture.put(textureKey, textureJson.get(textureKey).toString());
            }
        }
        List<ModelElement> elements = new ArrayList<>();
        JSONArray elementsArray = (JSONArray) rootJson.get("elements");
        if (elementsArray != null) {
            for (Object obj : elementsArray) {
                JSONObject elementJson = (JSONObject) obj;
                String name = (String) elementJson.get("name");
                JSONArray fromArray = (JSONArray) elementJson.get("from");
                JSONArray toArray = (JSONArray) elementJson.get("to");
                Coordinates3D from = new Coordinates3D(((Number) fromArray.get(0)).doubleValue(), ((Number) fromArray.get(1)).doubleValue(), ((Number) fromArray.get(2)).doubleValue());
                Coordinates3D to = new Coordinates3D(((Number) toArray.get(0)).doubleValue(), ((Number) toArray.get(1)).doubleValue(), ((Number) toArray.get(2)).doubleValue());
                ModelElementRotation rotation;
                JSONObject rotationJson = (JSONObject) elementJson.get("rotation");
                if (rotationJson == null) {
                    rotation = null;
                } else {
                    Coordinates3D origin;
                    JSONArray originArray = (JSONArray) rotationJson.get("origin");
                    if (originArray == null) {
                        origin = new Coordinates3D(0, 0, 0);
                    } else {
                        origin = new Coordinates3D(((Number) originArray.get(0)).doubleValue(), ((Number) originArray.get(1)).doubleValue(), ((Number) originArray.get(2)).doubleValue());
                    }
                    ModelAxis axis = ModelAxis.valueOf(rotationJson.get("axis").toString().toUpperCase());
                    double angle = ((Number) rotationJson.get("angle")).doubleValue();
                    boolean rescale = (boolean) rotationJson.getOrDefault("rescale", false);
                    rotation = new ModelElementRotation(origin, axis, angle, rescale);
                }
                boolean shade = (boolean) elementJson.getOrDefault("shade", true);
                Map<ModelFaceSide, ModelFace> face = new EnumMap<>(ModelFaceSide.class);
                JSONObject facesJson = (JSONObject) elementJson.get("faces");
                if (facesJson != null) {
                    for (Object obj1 : facesJson.keySet()) {
                        String faceKey = obj1.toString();
                        ModelFaceSide side = ModelFaceSide.fromKey(faceKey);
                        JSONObject faceJson = (JSONObject) facesJson.get(faceKey);
                        TextureUV uv;
                        JSONArray uvArray = (JSONArray) faceJson.get("uv");
                        if (uvArray == null) {
                            uv = null;
                        } else {
                            uv = new TextureUV(((Number) uvArray.get(0)).doubleValue(), ((Number) uvArray.get(1)).doubleValue(), ((Number) uvArray.get(2)).doubleValue(), ((Number) uvArray.get(3)).doubleValue());
                        }
                        String faceTexture = (String) faceJson.get("texture");
                        if (texture.containsKey(faceTexture)) {
                            faceTexture = "#" + faceTexture;
                        }
                        Object cullfaceObj = faceJson.get("cullface");
                        ModelFaceSide cullface;
                        if (cullfaceObj != null && cullfaceObj instanceof String) {
                            cullface = ModelFaceSide.fromKey((String) cullfaceObj);
                        } else {
                            cullface = side;
                        }
                        int faceRotation = ((Number) faceJson.getOrDefault("rotation", 0)).intValue();
                        int faceTintindex = ((Number) faceJson.getOrDefault("tintindex", -1)).intValue();
                        face.put(side, new ModelFace(side, uv, faceTexture, cullface, faceRotation, faceTintindex));
                    }
                }
                elements.add(new ModelElement(name, from, to, rotation, shade, face));
            }
        }
        List<ModelOverride> overrides;
        if (useLegacyOverrides) {
            overrides = new ArrayList<>();
            JSONArray overridesArray = (JSONArray) rootJson.get("overrides");
            if (overridesArray != null) {
                ListIterator<Object> itr = overridesArray.listIterator(overridesArray.size());
                while (itr.hasPrevious()) {
                    JSONObject overrideJson = (JSONObject) itr.previous();
                    JSONObject predicateJson = (JSONObject) overrideJson.get("predicate");
                    Map<ModelOverrideType, Float> predicates = new EnumMap<>(ModelOverrideType.class);
                    for (Object obj1 : predicateJson.keySet()) {
                        String predicateTypeKey = obj1.toString();
                        ModelOverrideType type = ModelOverrideType.fromKey(predicateTypeKey);
                        if (type == null) {
                            continue;
                        }
                        Object value = predicateJson.get(predicateTypeKey);
                        predicates.put(type, ((Number) value).floatValue());
                    }
                    String model = (String) overrideJson.get("model");
                    overrides.add(new ModelOverride(predicates, model));
                }
            }
        } else {
            overrides = Collections.emptyList();
        }
        return new BlockModel(manager, resourceLocation, parent, ambientocclusion, guiLight, display, texture, elements, overrides);
    }

    private final IModelManager manager;
    private final String resourceLocation;

    private final String parent;
    private final boolean ambientocclusion;
    private final ModelGUILight guiLight;
    private final Map<ModelDisplayPosition, ModelDisplay> display;
    private final Map<String, String> textures;
    private final List<ModelElement> elements;
    private final List<ModelOverride> overrides;

    public BlockModel(IModelManager manager, String resourceLocation, String parent, boolean ambientocclusion, ModelGUILight guiLight, Map<ModelDisplayPosition, ModelDisplay> display, Map<String, String> textures, List<ModelElement> elements, List<ModelOverride> overrides) {
        this.resourceLocation = resourceLocation;
        this.manager = manager;
        this.parent = parent;
        this.ambientocclusion = ambientocclusion;
        this.guiLight = guiLight;
        this.display = Collections.unmodifiableMap(display);
        this.textures = Collections.unmodifiableMap(textures);
        this.elements = Collections.unmodifiableList(elements);
        this.overrides = Collections.unmodifiableList(overrides);
    }

    public IModelManager getManager() {
        return manager;
    }

    public String getResourceLocation() {
        return resourceLocation;
    }

    protected String getRawParent() {
        return parent;
    }

    public String getParent() {
        if (parent == null) {
            return null;
        }
        if (parent.contains(":")) {
            return parent;
        }
        return ResourceRegistry.DEFAULT_NAMESPACE + ":" + parent;
    }

    public boolean isAmbientocclusion() {
        return ambientocclusion;
    }

    public ModelGUILight getRawGUILight() {
        return guiLight;
    }

    public ModelGUILight getGUILight() {
        return guiLight == null ? ModelGUILight.SIDE : guiLight;
    }

    public Map<ModelDisplayPosition, ModelDisplay> getRawDisplay() {
        return display;
    }

    public ModelDisplay getDisplay(ModelDisplayPosition position) {
        ModelDisplay modelDisplay = display.get(position);
        if (modelDisplay != null) {
            return modelDisplay;
        } else if (position.hasFallback()) {
            return display.get(position.getFallback());
        }
        return null;
    }

    public Map<String, String> getTextures() {
        return textures;
    }

    public List<ModelElement> getElements() {
        return elements;
    }

    public List<ModelOverride> getOverrides() {
        return overrides;
    }

    public BlockModel resolve(boolean post1_8) {
        boolean ambientocclusion = this.isAmbientocclusion();
        Map<ModelDisplayPosition, ModelDisplay> display = new EnumMap<>(ModelDisplayPosition.class);
        display.putAll(this.getRawDisplay());
        Map<String, String> textures = new HashMap<>(this.getTextures());
        for (Entry<String, String> entry : textures.entrySet()) {
            String value = entry.getValue();
            if (value.startsWith("#")) {
                String var = value.substring(1);
                String mapped = textures.get(var);
                if (mapped != null) {
                    entry.setValue(mapped);
                }
            }
        }
        List<ModelElement> elements = new ArrayList<>(this.getElements());
        for (int i = 0; i < elements.size(); i++) {
            ModelElement element = elements.get(i);
            Map<ModelFaceSide, ModelFace> faces = new EnumMap<>(ModelFaceSide.class);
            faces.putAll(element.getFaces());
            for (Entry<ModelFaceSide, ModelFace> entry : faces.entrySet()) {
                ModelFace face = entry.getValue();
                String value = entry.getValue().getTexture();
                if (value.startsWith("#")) {
                    String var = value.substring(1);
                    String mapped = textures.get(var);
                    if (mapped != null) {
                        entry.setValue(face.cloneWithNewTexture(mapped));
                    }
                }
            }
            elements.set(i, new ModelElement(element.getName(), element.getFrom(), element.getTo(), element.getRotation(), element.isShade(), faces));
        }
        BlockModel newBlockModel = new BlockModel(this.getManager(), this.getResourceLocation(), this.getRawParent(), ambientocclusion, this.getRawGUILight(), display, textures, elements, this.getOverrides());
        if (post1_8) {
            String newRawParent = newBlockModel.getRawParent();
            if (newRawParent == null) {
                return newBlockModel.getManager().getRawBlockModel(ResourceRegistry.IC_OLD_BASE_BLOCK_MODEL).resolve(newBlockModel, post1_8);
            } else if (newRawParent.equals(ModelManager.ITEM_BASE)) {
                return newBlockModel.getManager().getRawBlockModel(ResourceRegistry.IC_OLD_BASE_ITEM_MODEL).resolve(newBlockModel, post1_8);
            }
        }
        return newBlockModel;
    }

    public BlockModel resolve(BlockModel parentModel, boolean post1_8) {
        String parent = parentModel.getRawParent();
        ModelGUILight guiLight = this.getRawGUILight();
        if (parentModel.getRawGUILight() != null) {
            guiLight = parentModel.getRawGUILight();
        }
        Map<ModelDisplayPosition, ModelDisplay> display = new EnumMap<>(ModelDisplayPosition.class);
        display.putAll(parentModel.getRawDisplay());
        display.putAll(this.getRawDisplay());
        Map<String, String> textures = new HashMap<>();
        textures.putAll(parentModel.getTextures());
        textures.putAll(this.getTextures());
        for (Entry<String, String> entry : textures.entrySet()) {
            String value = entry.getValue();
            if (value.startsWith("#")) {
                String var = value.substring(1);
                String mapped = textures.get(var);
                if (mapped != null) {
                    entry.setValue(mapped);
                }
            }
        }
        List<ModelElement> elements = new ArrayList<>(this.getElements().isEmpty() ? parentModel.getElements() : this.getElements());
        for (int i = 0; i < elements.size(); i++) {
            ModelElement element = elements.get(i);
            Map<ModelFaceSide, ModelFace> faces = new EnumMap<>(ModelFaceSide.class);
            faces.putAll(element.getFaces());
            for (Entry<ModelFaceSide, ModelFace> entry : faces.entrySet()) {
                ModelFace face = entry.getValue();
                String value = entry.getValue().getTexture();
                if (value.startsWith("#")) {
                    String var = value.substring(1);
                    String mapped = textures.get(var);
                    if (mapped != null) {
                        entry.setValue(face.cloneWithNewTexture(mapped));
                    }
                }
            }
            elements.set(i, new ModelElement(element.getName(), element.getFrom(), element.getTo(), element.getRotation(), element.isShade(), faces));
        }
        return new BlockModel(this.getManager(), this.getResourceLocation(), parent, this.isAmbientocclusion(), guiLight, display, textures, elements, parentModel.getOverrides());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BlockModel model = (BlockModel) o;
        return ambientocclusion == model.ambientocclusion && Objects.equals(manager, model.manager) && Objects.equals(resourceLocation, model.resourceLocation) && Objects.equals(parent, model.parent) && guiLight == model.guiLight && Objects.equals(display, model.display) && Objects.equals(textures, model.textures) && Objects.equals(elements, model.elements) && Objects.equals(overrides, model.overrides);
    }

    @Override
    public int hashCode() {
        return Objects.hash(manager, resourceLocation, parent, ambientocclusion, guiLight, display, textures, elements, overrides);
    }

}
