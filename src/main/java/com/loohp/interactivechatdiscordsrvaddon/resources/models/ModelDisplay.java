package com.loohp.interactivechatdiscordsrvaddon.resources.models;

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
		
		THIRDPERSON_RIGHTHAND,
		THIRDPERSON_LEFTHAND,
		FIRSTPERSON_RIGHTHAND,
		FIRSTPERSON_LEFTHAND,
		GUI,
		HEAD,
		GROUND,
		FIXED;
		
		public static ModelDisplayPosition fromKey(String key) {
			for (ModelDisplayPosition position : values()) {
				if (key.toUpperCase().equals(position.toString())) {
					return position;
				}
			}
			return null;
		}

	}

}
