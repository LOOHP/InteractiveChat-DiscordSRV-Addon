/*
 * This file is part of InteractiveChatDiscordSrvAddon2.
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

package com.loohp.interactivechatdiscordsrvaddon.resources.mods.optifine.cit;

import com.loohp.interactivechat.objectholders.ICMaterial;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.IntegerRange;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.PercentageOrIntegerRange;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlot;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class EnchantmentProperties extends CITProperties {

    private final int layer;
    private final double speed;
    private final double rotation;
    private final double duration;
    private final OpenGLBlending blend;

    private final String texture;

    public EnchantmentProperties(int weight, Set<ICMaterial> items, IntegerRange stackSize, PercentageOrIntegerRange damage, int damageMask, EquipmentSlot hand, Map<Enchantment, IntegerRange> enchantments, Map<String, CITValueMatcher> nbtMatch, int layer, double speed, double rotation, double duration, OpenGLBlending blend, String texture) {
        super(weight, items, stackSize, damage, damageMask, hand, enchantments, nbtMatch);
        this.layer = layer;
        this.speed = speed;
        this.rotation = rotation;
        this.duration = duration;
        this.blend = blend;
        this.texture = texture;
    }

    public int getLayer() {
        return layer;
    }

    public double getSpeed() {
        return speed;
    }

    public double getRotation() {
        return rotation;
    }

    public double getDuration() {
        return duration;
    }

    public OpenGLBlending getBlend() {
        return blend;
    }

    public String getTexture() {
        return texture;
    }

    @Override
    public String getOverrideAsset(String path, String extension) {
        if (extension.equalsIgnoreCase("png")) {
            return texture;
        } else {
            return null;
        }
    }

    public static class OpenGLBlending {

        public static final OpenGLBlending REPLACE = of(null, null, null, null);
        public static final OpenGLBlending GLINT = of(OpenGLBlendMode.GL_SRC_COLOR, OpenGLBlendMode.GL_ONE);
        public static final OpenGLBlending ALPHA = of(OpenGLBlendMode.GL_SRC_ALPHA, OpenGLBlendMode.GL_ONE_MINUS_SRC_ALPHA);
        public static final OpenGLBlending ADD = of(OpenGLBlendMode.GL_SRC_ALPHA, OpenGLBlendMode.GL_ONE);
        public static final OpenGLBlending SUBTRACT = of(OpenGLBlendMode.GL_ONE_MINUS_DST_COLOR, OpenGLBlendMode.GL_ZERO);
        public static final OpenGLBlending MULTIPLY = of(OpenGLBlendMode.GL_DST_COLOR, OpenGLBlendMode.GL_ONE_MINUS_SRC_ALPHA);
        public static final OpenGLBlending DODGE = of(OpenGLBlendMode.GL_ONE, OpenGLBlendMode.GL_ONE);
        public static final OpenGLBlending BURN = of(OpenGLBlendMode.GL_ZERO, OpenGLBlendMode.GL_ONE_MINUS_SRC_COLOR);
        public static final OpenGLBlending SCREEN = of(OpenGLBlendMode.GL_ONE, OpenGLBlendMode.GL_ONE_MINUS_SRC_COLOR);
        public static final OpenGLBlending OVERLAY = of(OpenGLBlendMode.GL_DST_COLOR, OpenGLBlendMode.GL_SRC_COLOR);

        private static final Map<String, OpenGLBlending> PRESETS = new HashMap<>();

        static {
            try {
                for (Field field : OpenGLBlendMode.class.getFields()) {
                    if (field.getType().equals(OpenGLBlending.class) && Modifier.isFinal(field.getModifiers()) && Modifier.isStatic(field.getModifiers())) {
                        PRESETS.put(field.getName().toLowerCase(), (OpenGLBlending) field.get(null));
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        
        public static OpenGLBlending of(OpenGLBlendMode srcComposite, OpenGLBlendMode desComposite) {
            return of(srcComposite, desComposite, OpenGLBlendMode.GL_ZERO, OpenGLBlendMode.GL_ONE);
        }

        public static OpenGLBlending of(OpenGLBlendMode srcColorComposite, OpenGLBlendMode desColorComposite, OpenGLBlendMode srcAlphaComposite, OpenGLBlendMode desAlphaComposite) {
            return new OpenGLBlending(srcColorComposite, desColorComposite, srcAlphaComposite, desAlphaComposite);
        }

        public static OpenGLBlending fromString(String input) {
            String[] sections = input.split(" ");
            switch (sections.length) {
                case 1: {
                    return PRESETS.get(sections[0].toLowerCase());
                }
                case 2: {
                    return of(OpenGLBlendMode.fromOpenGL(sections[0]), OpenGLBlendMode.fromOpenGL(sections[1]));
                }
                case 4: {
                    return of(OpenGLBlendMode.fromOpenGL(sections[0]), OpenGLBlendMode.fromOpenGL(sections[1]), OpenGLBlendMode.fromOpenGL(sections[2]), OpenGLBlendMode.fromOpenGL(sections[3]));
                }
            }
            return null;
        }
        
        private final OpenGLBlendMode srcColor;
        private final OpenGLBlendMode desColor;
        private final OpenGLBlendMode srcAlpha;
        private final OpenGLBlendMode desAlpha;

        private OpenGLBlending(OpenGLBlendMode srcColor, OpenGLBlendMode desColor, OpenGLBlendMode srcAlpha, OpenGLBlendMode desAlpha) {
            this.srcColor = srcColor;
            this.desColor = desColor;
            this.srcAlpha = srcAlpha;
            this.desAlpha = desAlpha;
        }

        public OpenGLBlendMode getSrcColor() {
            return srcColor;
        }

        public OpenGLBlendMode getDesColor() {
            return desColor;
        }

        public OpenGLBlendMode getSrcAlpha() {
            return srcAlpha;
        }

        public OpenGLBlendMode getDesAlpha() {
            return desAlpha;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            OpenGLBlending that = (OpenGLBlending) o;
            return srcColor == that.srcColor && desColor == that.desColor && srcAlpha == that.srcAlpha && desAlpha == that.desAlpha;
        }

        @Override
        public int hashCode() {
            return Objects.hash(srcColor, desColor, srcAlpha, desAlpha);
        }

    }

    public enum OpenGLBlendMode {

        GL_ZERO(0),
        GL_ONE(1),
        GL_SRC_COLOR(768),
        GL_ONE_MINUS_SRC_COLOR(769),
        GL_DST_COLOR(774),
        GL_ONE_MINUS_DST_COLOR(775),
        GL_SRC_ALPHA(770),
        GL_ONE_MINUS_SRC_ALPHA(771),
        GL_DST_ALPHA(772),
        GL_ONE_MINUS_DST_ALPHA(773);

        private static final OpenGLBlendMode[] VALUES = values();

        private final int openGLValue;

        OpenGLBlendMode(int openGLValue) {
            this.openGLValue = openGLValue;
        }

        public int getOpenGLValue() {
            return openGLValue;
        }

        public String getOpenGLName() {
            return name();
        }

        public static OpenGLBlendMode fromOpenGL(String name) {
            if (name.startsWith("0x")) {
                try {
                    return fromOpenGL(Integer.parseInt(name.substring(2), 16));
                } catch (NumberFormatException ignore) {
                }
            }
            try {
                return fromOpenGL(Integer.parseInt(name));
            } catch (NumberFormatException ignore) {
            }
            for (OpenGLBlendMode blend : VALUES) {
                if (blend.getOpenGLName().equalsIgnoreCase(name)) {
                    return blend;
                }
            }
            return null;
        }

        public static OpenGLBlendMode fromOpenGL(int value) {
            for (OpenGLBlendMode blend : VALUES) {
                if (blend.getOpenGLValue() == value) {
                    return blend;
                }
            }
            return null;
        }

    }

}
