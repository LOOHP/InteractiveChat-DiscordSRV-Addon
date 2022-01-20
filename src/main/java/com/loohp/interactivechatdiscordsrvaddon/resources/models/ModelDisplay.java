package com.loohp.interactivechatdiscordsrvaddon.resources.models;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ModelDisplay {
	
	private ModelDisplayPosition position;
	private Coordinates3D rotation;
	private Coordinates3D translation;
	private Coordinates3D scale;	
	
	public ModelDisplay(ModelDisplayPosition position, Coordinates3D rotation, Coordinates3D translation, Coordinates3D scale) {
		this.position = position;
		this.rotation = rotation;
		this.translation = translation;
		this.scale = scale;
	}

	public ModelDisplayPosition getPosition() {
		return position;
	}

	public Coordinates3D getRotation() {
		return rotation;
	}

	public Coordinates3D getTranslation() {
		return translation;
	}

	public Coordinates3D getScale() {
		return scale;
	}

	public static enum ModelDisplayPosition {
		
		THIRDPERSON_RIGHTHAND("thirdperson_righthand", "thirdperson"),
		THIRDPERSON_LEFTHAND(THIRDPERSON_RIGHTHAND, "thirdperson_lefthand"),
		FIRSTPERSON_RIGHTHAND("firstperson_righthand", "firstperson"),
		FIRSTPERSON_LEFTHAND(FIRSTPERSON_RIGHTHAND, "firstperson_lefthand"),
		GUI("gui"),
		HEAD("head"),
		GROUND("ground"),
		FIXED("fixed");
		
		private ModelDisplayPosition fallback;
		private Set<String> keys;
		
		ModelDisplayPosition(ModelDisplayPosition fallback, String... keys) {
			this.keys = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(keys)));
			this.fallback = fallback;
		}
		
		ModelDisplayPosition(String... keys) {
			this(null, keys);
		}
		
		public Set<String> getKeys() {
			return keys;
		}
		
		public boolean hasFallback() {
			return fallback != null;
		}

		public ModelDisplayPosition getFallback() {
			return fallback;
		}

		public static ModelDisplayPosition fromKey(String key) {
			for (ModelDisplayPosition position : values()) {
				if (position.getKeys().contains(key.toLowerCase())) {
					return position;
				}
			}
			return null;
		}

	}

}
