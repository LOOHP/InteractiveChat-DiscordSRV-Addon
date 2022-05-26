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

import com.loohp.interactivechatdiscordsrvaddon.resources.models.ModelFace.ModelFaceSide;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ModelElement that = (ModelElement) o;
        return shade == that.shade && Objects.equals(from, that.from) && Objects.equals(to, that.to) && Objects.equals(rotation, that.rotation) && Objects.equals(face, that.face);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to, rotation, shade, face);
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
