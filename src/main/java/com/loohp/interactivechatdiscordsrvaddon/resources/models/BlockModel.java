package com.loohp.interactivechatdiscordsrvaddon.resources.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.loohp.interactivechatdiscordsrvaddon.registry.ResourceRegistry;
import com.loohp.interactivechatdiscordsrvaddon.resources.models.ModelDisplay.ModelDisplayPosition;
import com.loohp.interactivechatdiscordsrvaddon.resources.models.ModelFace.ModelFaceSide;

public class BlockModel {
	
	private String parent;
	private boolean ambientocclusion;
	private Map<ModelDisplayPosition, ModelDisplay> display;
	private Map<String, String> textures;
	private List<ModelElement> elements;
	private List<ModelOverride> overrides;
	
	public BlockModel(String parent, boolean ambientocclusion, Map<ModelDisplayPosition, ModelDisplay> display, Map<String, String> textures, List<ModelElement> elements, List<ModelOverride> overrides) {
		this.parent = parent;
		this.ambientocclusion = ambientocclusion;
		this.display = Collections.unmodifiableMap(display);
		this.textures = Collections.unmodifiableMap(textures);
		this.elements = Collections.unmodifiableList(elements);
		this.overrides = Collections.unmodifiableList(overrides);
	}
	
	public static BlockModel resolve(BlockModel childrenModel) {
		boolean ambientocclusion = childrenModel.isAmbientocclusion();
		Map<ModelDisplayPosition, ModelDisplay> display = new EnumMap<>(ModelDisplayPosition.class);
		display.putAll(childrenModel.getDisplay());
		Map<String, String> textures = new HashMap<>();
		textures.putAll(childrenModel.getTextures());
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
		List<ModelElement> elements = new ArrayList<>(childrenModel.getElements());
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
			elements.set(i, new ModelElement(element.getFrom(), element.getTo(), element.getRotation(), element.isShade(), faces));
		}
		return new BlockModel(childrenModel.getRawParent(), ambientocclusion, display, textures, elements, childrenModel.getOverrides());
	}
	
	public static BlockModel resolve(BlockModel parentModel, BlockModel childrenModel) {
		String parent = parentModel.getRawParent();
		boolean ambientocclusion = childrenModel.isAmbientocclusion();
		Map<ModelDisplayPosition, ModelDisplay> display = new EnumMap<>(ModelDisplayPosition.class);
		display.putAll(parentModel.getDisplay());
		display.putAll(childrenModel.getDisplay());
		Map<String, String> textures = new HashMap<>();
		textures.putAll(parentModel.getTextures());
		textures.putAll(childrenModel.getTextures());
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
		List<ModelElement> elements = new ArrayList<>(childrenModel.getElements().isEmpty() ? parentModel.getElements() : childrenModel.getElements());
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
			elements.set(i, new ModelElement(element.getFrom(), element.getTo(), element.getRotation(), element.isShade(), faces));
		}
		return new BlockModel(parent, ambientocclusion, display, textures, elements, parentModel.getOverrides());
	}
	
	public String getRawParent() {
		return parent;
	}

	public String getParent() {
		return parent == null ? null : (parent.contains(":") ? parent : ResourceRegistry.DEFAULT_NAMESPACE + ":" + parent);
	}

	public boolean isAmbientocclusion() {
		return ambientocclusion;
	}
	
	public Map<ModelDisplayPosition, ModelDisplay> getDisplay() {
		return display;
	}

	public ModelDisplay getDisplay(ModelDisplayPosition position) {
		return display.get(position);
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

}