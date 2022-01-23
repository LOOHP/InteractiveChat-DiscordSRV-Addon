package com.loohp.interactivechatdiscordsrvaddon.resources.models;

import com.loohp.blockmodelrenderer.render.Vector;

public enum ModelGUILight {

    FRONT(new Vector(0, 0, 1), 0.2, 0.85),
    SIDE(new Vector(-0.5, 0.65, 0.9), 0.1, 1.0);

    public static ModelGUILight fromKey(String key) {
        for (ModelGUILight type : values()) {
            if (key.toUpperCase().equals(type.toString())) {
                return type;
            }
        }
        return null;
    }

    private Vector lightVector;
    private double ambientLevel;
    private double maxLevel;

    ModelGUILight(Vector lightVector, double ambientLevel, double maxLevel) {
        this.lightVector = lightVector;
        this.ambientLevel = ambientLevel;
        this.maxLevel = maxLevel;
    }

    public Vector getLightVector() {
        return lightVector.clone();
    }

    public double getAmbientLevel() {
        return ambientLevel;
    }

    public double getMaxLevel() {
        return maxLevel;
    }

}
