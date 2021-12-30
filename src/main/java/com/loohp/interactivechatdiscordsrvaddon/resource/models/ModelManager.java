package com.loohp.interactivechatdiscordsrvaddon.resource.models;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.loohp.interactivechat.libs.org.apache.commons.io.FileUtils;
import com.loohp.interactivechat.libs.org.json.simple.JSONArray;
import com.loohp.interactivechat.libs.org.json.simple.JSONObject;
import com.loohp.interactivechat.libs.org.json.simple.parser.JSONParser;
import com.loohp.interactivechatdiscordsrvaddon.Cache;
import com.loohp.interactivechatdiscordsrvaddon.InteractiveChatDiscordSrvAddon;
import com.loohp.interactivechatdiscordsrvaddon.registies.ResourceRegistry;
import com.loohp.interactivechatdiscordsrvaddon.resource.ResourceManager;
import com.loohp.interactivechatdiscordsrvaddon.resource.models.ModelDisplay.ModelDisplayPosition;
import com.loohp.interactivechatdiscordsrvaddon.resource.models.ModelElement.ModelElementRotation;
import com.loohp.interactivechatdiscordsrvaddon.resource.models.ModelFace.ModelFaceSide;
import com.loohp.interactivechatdiscordsrvaddon.resource.models.ModelOverride.ModelOverrideType;

public class ModelManager {
	
	public static final String CACHE_KEY = "ModelManager";
	public static final String BLOCK_ENTITY_BASE = "builtin/entity";
	public static final String ITEM_BASE = "builtin/generated";
	public static final String ITEM_BASE_LAYER = "layer";
	
	private Map<String, BlockModel> models;

	public ModelManager(ResourceManager manager) {
		this.models = new HashMap<>();
	}
	
	@SuppressWarnings("unchecked")
	public void loadDirectory(String namespace, File root) {
		if (!root.exists() || !root.isDirectory()) {
			throw new IllegalArgumentException(root.getAbsolutePath() + " is not a directory.");
		}
		JSONParser parser = new JSONParser();
		Map<String, BlockModel> models = new HashMap<>();
		Collection<File> files = FileUtils.listFiles(root, new String[] {"json"}, true);
		for (File file : files) {
			try {
				String key = namespace + ":" + file.getParentFile().getAbsolutePath().replace("\\", "/").replace(root.getAbsolutePath().replace("\\", "/") + "/", "") + "/" + file.getName();
				key = key.substring(0, key.lastIndexOf("."));
				JSONObject rootJson = (JSONObject) parser.parse(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
				String parent = (String) rootJson.getOrDefault("parent", null);
				boolean ambientocclusion = (boolean) rootJson.getOrDefault("ambientocclusion", true);
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
							rotation =  new Coordinates3D(0, 0, 0);
						} else {
							rotation = new Coordinates3D(((Number) rotationArray.get(0)).doubleValue(), ((Number) rotationArray.get(1)).doubleValue(), ((Number) rotationArray.get(2)).doubleValue());
						}
						Coordinates3D translation;
						if (translationArray == null) {
							translation =  new Coordinates3D(0, 0, 0);
						} else {
							translation = new Coordinates3D(((Number) translationArray.get(0)).doubleValue(), ((Number) translationArray.get(1)).doubleValue(), ((Number) translationArray.get(2)).doubleValue());
						}
						Coordinates3D scale;
						if (scaleArray == null) {
							scale =  new Coordinates3D(1, 1, 1);
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
								String cullfaceString = (String) faceJson.get("cullface");
								ModelFaceSide cullface = cullfaceString == null ? null : ModelFaceSide.fromKey(cullfaceString);
								int faceRotation = ((Number) faceJson.getOrDefault("rotation", 0)).intValue();
								int faceTintindex = ((Number) faceJson.getOrDefault("tintindex", -1)).intValue();
								face.put(side, new ModelFace(side, uv, faceTexture, cullface, faceRotation, faceTintindex));
							}
						}
						elements.add(new ModelElement(from, to, rotation, shade, face));
					}
				}
				List<ModelOverride> overrides = new ArrayList<>();
				JSONArray overridesArray = (JSONArray) rootJson.get("overrides");
				if (overridesArray != null) {
					for (Object obj : overridesArray) {
						JSONObject overrideJson = (JSONObject) obj;
						JSONObject predicateJson = (JSONObject) overrideJson.get("predicate");
						Map<ModelOverrideType, Float> predicates = new EnumMap<>(ModelOverrideType.class);
						for (Object obj1 : predicateJson.keySet()) {
							String predicateTypeKey = obj1.toString();
							ModelOverrideType type = ModelOverrideType.fromKey(predicateTypeKey);
							Object value = predicateJson.get(predicateTypeKey);
							predicates.put(type, ((Number) value).floatValue());
						}
						String model = (String) overrideJson.get("model");
						overrides.add(new ModelOverride(predicates, model));
					}
				}
				Collections.reverse(overrides);
				models.put(key, new BlockModel(parent, ambientocclusion, display, texture, elements, overrides));
			} catch (Exception e) {
				new RuntimeException("Unable to load block model " + file.getAbsolutePath(), e).printStackTrace();
			}
		}
		this.models.putAll(models);
	}
	
	public BlockModel getRawBlockModel(String resourceLocation) {
		return models.get(resourceLocation);
	}
	
	public Map<String, BlockModel> getRawBlockModelMapping() {
		return Collections.unmodifiableMap(models);
	}
	
	public void clear() {
		clearModels();
	}
	
	public void clearModels() {
		models.clear();
	}
	
	public BlockModel resolveBlockModel(String resourceLocation, Map<ModelOverrideType, Float> predicates) {
		String cacheKey = CACHE_KEY + "/" + resourceLocation + "/" + (predicates == null ? "null" : predicates.entrySet().stream().map(entry -> entry.getKey().name().toLowerCase() + ":" + entry.getValue().toString()).collect(Collectors.joining(";")));
		Cache<?> cachedModel = Cache.getCache(cacheKey);
		if (cachedModel != null) {
			return (BlockModel) cachedModel.getObject();
		}
		
		BlockModel model = models.get(resourceLocation);
		if (model == null) {
			return null;
		}
		for (ModelOverride override : model.getOverrides()) {
			if (override.test(predicates)) {
				return resolveBlockModel(override.getModel(), null);
			}
		}
		if (model.getParent() != null) {
			while (model.getParent() != null) {
				if (model.getRawParent().equals(ITEM_BASE)) {
					break;
				}
				if (model.getRawParent().equals(BLOCK_ENTITY_BASE)) {
					BlockModel builtinModel = resolveBlockModel(ResourceRegistry.BUILTIN_ENTITY_LOCATION + resourceLocation.substring(resourceLocation.lastIndexOf("/") + 1), predicates);
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
						return resolveBlockModel(override.getModel(), null);
					}
				}
				model = BlockModel.resolve(parent, model);
			}
		}
		model = BlockModel.resolve(model);
		
		Cache.putCache(cacheKey, model, InteractiveChatDiscordSrvAddon.plugin.cacheTimeout);
		return model;
	}

}
