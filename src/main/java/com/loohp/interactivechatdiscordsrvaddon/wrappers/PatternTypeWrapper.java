package com.loohp.interactivechatdiscordsrvaddon.wrappers;

import org.bukkit.block.banner.PatternType;

import java.util.EnumMap;
import java.util.Map;

public enum PatternTypeWrapper {

    BASE("base", "b", false),
    SQUARE_BOTTOM_LEFT("square_bottom_left", "bl"),
    SQUARE_BOTTOM_RIGHT("square_bottom_right", "br"),
    SQUARE_TOP_LEFT("square_top_left", "tl"),
    SQUARE_TOP_RIGHT("square_top_right", "tr"),
    STRIPE_BOTTOM("stripe_bottom", "bs"),
    STRIPE_TOP("stripe_top", "ts"),
    STRIPE_LEFT("stripe_left", "ls"),
    STRIPE_RIGHT("stripe_right", "rs"),
    STRIPE_CENTER("stripe_center", "cs"),
    STRIPE_MIDDLE("stripe_middle", "ms"),
    STRIPE_DOWNRIGHT("stripe_downright", "drs"),
    STRIPE_DOWNLEFT("stripe_downleft", "dls"),
    STRIPE_SMALL("small_stripes", "ss"),
    CROSS("cross", "cr"),
    STRAIGHT_CROSS("straight_cross", "sc"),
    TRIANGLE_BOTTOM("triangle_bottom", "bt"),
    TRIANGLE_TOP("triangle_top", "tt"),
    TRIANGLES_BOTTOM("triangles_bottom", "bts"),
    TRIANGLES_TOP("triangles_top", "tts"),
    DIAGONAL_LEFT("diagonal_left", "ld"),
    DIAGONAL_RIGHT("diagonal_up_right", "rd"),
    DIAGONAL_LEFT_MIRROR("diagonal_up_left", "lud"),
    DIAGONAL_RIGHT_MIRROR("diagonal_right", "rud"),
    CIRCLE_MIDDLE("circle", "mc"),
    RHOMBUS_MIDDLE("rhombus", "mr"),
    HALF_VERTICAL("half_vertical", "vh"),
    HALF_HORIZONTAL("half_horizontal", "hh"),
    HALF_VERTICAL_MIRROR("half_vertical_right", "vhr"),
    HALF_HORIZONTAL_MIRROR("half_horizontal_bottom", "hhb"),
    BORDER("border", "bo"),
    CURLY_BORDER("curly_border", "cbo"),
    GRADIENT("gradient", "gra"),
    GRADIENT_UP("gradient_up", "gru"),
    BRICKS("bricks", "bri"),
    GLOBE("globe", "glb", true),
    CREEPER("creeper", "cre", true),
    SKULL("skull", "sku", true),
    FLOWER("flower", "flo", true),
    MOJANG("mojang", "moj", true),
    PIGLIN("piglin", "pig", true);

    private static final Map<PatternType, PatternTypeWrapper> MAPPING = new EnumMap<>(PatternType.class);

    static {
        for (PatternType type : PatternType.values()) {
            MAPPING.put(type, valueOf(type.name()));
        }
    }

    private String assetName;

    PatternTypeWrapper(String assetName, String s1, boolean flag) {
        this.assetName = assetName;
    }

    PatternTypeWrapper(String assetName, String s1) {
        this(assetName, s1, false);
    }

    public static PatternTypeWrapper fromPatternType(PatternType type) {
        return MAPPING.get(type);
    }

    public String getAssetName() {
        return assetName;
    }
}
