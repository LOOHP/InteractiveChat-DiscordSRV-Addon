package com.loohp.interactivechatdiscordsrvaddon.resources.models;

import java.util.Collections;
import java.util.Map;

import com.loohp.interactivechatdiscordsrvaddon.resources.models.ModelFace.ModelFaceSide;

public class ModelElement {
	
	private Coordinates3D from;
	private Coordinates3D to;
	private ModelElementRotation rotation;
	private boolean shade;
	private Map<ModelFaceSide, ModelFace> face;
	
	public ModelElement(Coordinates3D from, Coordinates3D to, ModelElementRotation rotation, boolean shade, Map<ModelFaceSide, ModelFace> face) {
		this.from = from;
		this.to = to;
		this.rotation = rotation;
		this.shade = shade;
		this.face = Collections.unmodifiableMap(face);
	}

	public Coordinates3D getFrom() {
		return from;
	}

	public Coordinates3D getTo() {
		return to;
	}

	public ModelElementRotation getRotation() {
		return rotation;
	}

	public boolean isShade() {
		return shade;
	}
	
	public Map<ModelFaceSide, ModelFace> getFaces() {
		return face;
	}

	public ModelFace getFace(ModelFaceSide side) {
		return face.get(side);
	}

	public static class ModelElementRotation {
		
		private Coordinates3D origin;
		private ModelAxis axis;
		private double angle;
		private boolean rescale;
		
		public ModelElementRotation(Coordinates3D origin, ModelAxis axis, double angle, boolean rescale) {
			this.origin = origin;
			this.axis = axis;
			this.angle = angle;
			this.rescale = rescale;
		}

		public Coordinates3D getOrigin() {
			return origin;
		}

		public ModelAxis getAxis() {
			return axis;
		}

		public double getAngle() {
			return angle;
		}

		public boolean isRescale() {
			return rescale;
		}
		
	}

}
