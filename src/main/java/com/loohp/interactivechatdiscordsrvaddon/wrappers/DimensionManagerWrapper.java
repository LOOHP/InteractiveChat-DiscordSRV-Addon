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

package com.loohp.interactivechatdiscordsrvaddon.wrappers;

import com.loohp.interactivechat.InteractiveChat;
import com.loohp.interactivechat.utils.MCVersion;
import com.loohp.interactivechat.utils.NMSUtils;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.FieldAccessor;
import org.bukkit.World;
import org.bukkit.World.Environment;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.OptionalLong;

public class DimensionManagerWrapper {

    private static Class<?> craftWorldClass;
    private static Method getHandleMethod;
    private static Class<?> worldServerClass;
    private static Class<?> dimensionManagerClass;
    private static Method getDimensionManagerMethod;

    private static FieldAccessor<OptionalLong> fixedTimeField;
    private static FieldAccessor<Boolean> hasSkylightField;
    private static FieldAccessor<Boolean> hasCeilingField;
    private static FieldAccessor<Boolean> ultraWarmField;
    private static FieldAccessor<Boolean> naturalField;
    private static FieldAccessor<Double> coordinateScaleField;
    private static FieldAccessor<Boolean> createDragonFightField;
    private static FieldAccessor<Boolean> piglinSafeField;
    private static FieldAccessor<Boolean> bedWorksField;
    private static FieldAccessor<Boolean> respawnAnchorWorksField;
    private static FieldAccessor<Boolean> hasRaidsField;
    private static FieldAccessor<Integer> minYField;
    private static FieldAccessor<Integer> heightField;
    private static FieldAccessor<Integer> logicalHeightField;
    private static FieldAccessor<String> infiniburnField;
    private static FieldAccessor<String> effectsLocationField;
    private static FieldAccessor<Float> ambientLightField;

    private static Method tagKeyGetMinecraftKeyMethod;

    static {
        if (InteractiveChat.version.isNewerOrEqualTo(MCVersion.V1_16)) {
            try {
                craftWorldClass = NMSUtils.getNMSClass("org.bukkit.craftbukkit.%s.CraftWorld");
                getHandleMethod = craftWorldClass.getMethod("getHandle");
                worldServerClass = getHandleMethod.getReturnType();
                dimensionManagerClass = NMSUtils.getNMSClass("net.minecraft.server.%s.DimensionManager", "net.minecraft.world.level.dimension.DimensionManager");
                getDimensionManagerMethod = Arrays.stream(worldServerClass.getMethods()).filter(each -> each.getReturnType().equals(dimensionManagerClass)).findFirst().get();

                if (InteractiveChat.version.isNewerOrEqualTo(MCVersion.V1_19)) {
                    fixedTimeField = FieldAccessor.fromField(dimensionManagerClass.getDeclaredField("k"));
                    hasSkylightField = FieldAccessor.fromField(dimensionManagerClass.getDeclaredField("l"));
                    hasCeilingField = FieldAccessor.fromField(dimensionManagerClass.getDeclaredField("m"));
                    ultraWarmField = FieldAccessor.fromField(dimensionManagerClass.getDeclaredField("n"));
                    naturalField = FieldAccessor.fromField(dimensionManagerClass.getDeclaredField("o"));
                    coordinateScaleField = FieldAccessor.fromField(dimensionManagerClass.getDeclaredField("p"));
                    bedWorksField = FieldAccessor.fromField(dimensionManagerClass.getDeclaredField("q"));
                    respawnAnchorWorksField = FieldAccessor.fromField(dimensionManagerClass.getDeclaredField("r"));
                    minYField = FieldAccessor.fromField(dimensionManagerClass.getDeclaredField("s"));
                    heightField = FieldAccessor.fromField(dimensionManagerClass.getDeclaredField("t"));
                    logicalHeightField = FieldAccessor.fromField(dimensionManagerClass.getDeclaredField("u"));
                    if (InteractiveChat.version.isNewerOrEqualTo(MCVersion.V1_18_2)) {
                        infiniburnField = FieldAccessor.fromField(dimensionManagerClass.getDeclaredField("v"), obj -> {
                            try {
                                return tagKeyGetMinecraftKeyMethod.invoke(obj).toString();
                            } catch (IllegalAccessException | InvocationTargetException e) {
                                throw new RuntimeException(e);
                            }
                        });
                    }
                    effectsLocationField = FieldAccessor.fromField(dimensionManagerClass.getDeclaredField("w"), obj -> obj.toString());
                    ambientLightField = FieldAccessor.fromField(dimensionManagerClass.getDeclaredField("x"));

                    tagKeyGetMinecraftKeyMethod = infiniburnField.getField().getType().getMethod("b");

                    Class<?> monstersSettingClass = dimensionManagerClass.getDeclaredField("y").getType();
                    Field piglinSafeSubField = monstersSettingClass.getDeclaredField("b");
                    Field hasRaidsSubField = monstersSettingClass.getDeclaredField("c");

                    createDragonFightField = null;
                    piglinSafeField = FieldAccessor.fromField(dimensionManagerClass.getDeclaredField("y"), obj -> {
                        piglinSafeSubField.setAccessible(true);
                        try {
                            return (boolean) piglinSafeSubField.get(obj);
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                    });
                    hasRaidsField = FieldAccessor.fromField(dimensionManagerClass.getDeclaredField("y"), obj -> {
                        hasRaidsSubField.setAccessible(true);
                        try {
                            return (boolean) hasRaidsSubField.get(obj);
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                    });
                } else if (InteractiveChat.version.isNewerOrEqualTo(MCVersion.V1_18)) {
                    fixedTimeField = FieldAccessor.fromField(dimensionManagerClass.getDeclaredField("w"));
                    hasSkylightField = FieldAccessor.fromField(dimensionManagerClass.getDeclaredField("x"));
                    hasCeilingField = FieldAccessor.fromField(dimensionManagerClass.getDeclaredField("y"));
                    ultraWarmField = FieldAccessor.fromField(dimensionManagerClass.getDeclaredField("z"));
                    naturalField = FieldAccessor.fromField(dimensionManagerClass.getDeclaredField("A"));
                    coordinateScaleField = FieldAccessor.fromField(dimensionManagerClass.getDeclaredField("B"));
                    createDragonFightField = FieldAccessor.fromField(dimensionManagerClass.getDeclaredField("C"));
                    piglinSafeField = FieldAccessor.fromField(dimensionManagerClass.getDeclaredField("D"));
                    bedWorksField = FieldAccessor.fromField(dimensionManagerClass.getDeclaredField("E"));
                    respawnAnchorWorksField = FieldAccessor.fromField(dimensionManagerClass.getDeclaredField("F"));
                    hasRaidsField = FieldAccessor.fromField(dimensionManagerClass.getDeclaredField("G"));
                    minYField = FieldAccessor.fromField(dimensionManagerClass.getDeclaredField("H"));
                    heightField = FieldAccessor.fromField(dimensionManagerClass.getDeclaredField("I"));
                    logicalHeightField = FieldAccessor.fromField(dimensionManagerClass.getDeclaredField("J"));
                    if (InteractiveChat.version.isNewerOrEqualTo(MCVersion.V1_18_2)) {
                        infiniburnField = FieldAccessor.fromField(dimensionManagerClass.getDeclaredField("K"), obj -> {
                            try {
                                return tagKeyGetMinecraftKeyMethod.invoke(obj).toString();
                            } catch (IllegalAccessException | InvocationTargetException e) {
                                throw new RuntimeException(e);
                            }
                        });
                    } else {
                        infiniburnField = FieldAccessor.fromField(dimensionManagerClass.getDeclaredField("K"), obj -> obj.toString());
                    }
                    effectsLocationField = FieldAccessor.fromField(dimensionManagerClass.getDeclaredField("L"), obj -> obj.toString());
                    ambientLightField = FieldAccessor.fromField(dimensionManagerClass.getDeclaredField("M"));

                    tagKeyGetMinecraftKeyMethod = infiniburnField.getField().getType().getMethod("b");
                } else if (InteractiveChat.version.isNewerOrEqualTo(MCVersion.V1_17)) {
                    fixedTimeField = FieldAccessor.fromField(dimensionManagerClass.getDeclaredField("u"));
                    hasSkylightField = FieldAccessor.fromField(dimensionManagerClass.getDeclaredField("v"));
                    hasCeilingField = FieldAccessor.fromField(dimensionManagerClass.getDeclaredField("w"));
                    ultraWarmField = FieldAccessor.fromField(dimensionManagerClass.getDeclaredField("x"));
                    naturalField = FieldAccessor.fromField(dimensionManagerClass.getDeclaredField("y"));
                    coordinateScaleField = FieldAccessor.fromField(dimensionManagerClass.getDeclaredField("z"));
                    createDragonFightField = FieldAccessor.fromField(dimensionManagerClass.getDeclaredField("A"));
                    piglinSafeField = FieldAccessor.fromField(dimensionManagerClass.getDeclaredField("B"));
                    bedWorksField = FieldAccessor.fromField(dimensionManagerClass.getDeclaredField("C"));
                    respawnAnchorWorksField = FieldAccessor.fromField(dimensionManagerClass.getDeclaredField("D"));
                    hasRaidsField = FieldAccessor.fromField(dimensionManagerClass.getDeclaredField("E"));
                    minYField = FieldAccessor.fromField(dimensionManagerClass.getDeclaredField("F"));
                    heightField = FieldAccessor.fromField(dimensionManagerClass.getDeclaredField("G"));
                    logicalHeightField = FieldAccessor.fromField(dimensionManagerClass.getDeclaredField("H"));
                    infiniburnField = FieldAccessor.fromField(dimensionManagerClass.getDeclaredField("J"), obj -> obj.toString());
                    effectsLocationField = FieldAccessor.fromField(dimensionManagerClass.getDeclaredField("K"), obj -> obj.toString());
                    ambientLightField = FieldAccessor.fromField(dimensionManagerClass.getDeclaredField("L"));
                } else if (InteractiveChat.version.isNewerOrEqualTo(MCVersion.V1_16)) {
                    fixedTimeField = FieldAccessor.fromField(dimensionManagerClass.getDeclaredField("fixedTime"));
                    hasSkylightField = FieldAccessor.fromField(dimensionManagerClass.getDeclaredField("hasSkylight"));
                    hasCeilingField = FieldAccessor.fromField(dimensionManagerClass.getDeclaredField("hasCeiling"));
                    ultraWarmField = FieldAccessor.fromField(dimensionManagerClass.getDeclaredField("ultraWarm"));
                    naturalField = FieldAccessor.fromField(dimensionManagerClass.getDeclaredField("natural"));
                    coordinateScaleField = FieldAccessor.fromField(dimensionManagerClass.getDeclaredField("shrunk"));
                    createDragonFightField = FieldAccessor.fromField(dimensionManagerClass.getDeclaredField("createDragonBattle"));
                    piglinSafeField = FieldAccessor.fromField(dimensionManagerClass.getDeclaredField("piglinSafe"));
                    bedWorksField = FieldAccessor.fromField(dimensionManagerClass.getDeclaredField("bedWorks"));
                    respawnAnchorWorksField = FieldAccessor.fromField(dimensionManagerClass.getDeclaredField("respawnAnchorWorks"));
                    hasRaidsField = FieldAccessor.fromField(dimensionManagerClass.getDeclaredField("hasRaids"));
                    minYField = FieldAccessor.ofValue(0);
                    heightField = FieldAccessor.ofValue(256);
                    logicalHeightField = FieldAccessor.fromField(dimensionManagerClass.getDeclaredField("logicalHeight"));
                    infiniburnField = FieldAccessor.fromField(dimensionManagerClass.getDeclaredField("infiniburn"), obj -> obj.toString());
                    if (InteractiveChat.version.isNewerOrEqualTo(MCVersion.V1_16_2)) {
                        effectsLocationField = FieldAccessor.fromField(dimensionManagerClass.getDeclaredField("effects"), obj -> obj.toString());
                    } else {
                        effectsLocationField = FieldAccessor.ofValue("");
                    }
                    ambientLightField = FieldAccessor.fromField(dimensionManagerClass.getDeclaredField("ambientLight"));
                }
            } catch (ReflectiveOperationException e) {
                e.printStackTrace();
            }
        }
    }

    private Object nmsDimensionManager;
    private Environment environment;

    public DimensionManagerWrapper(World world) {
        if (InteractiveChat.version.isOlderThan(MCVersion.V1_16)) {
            throw new RuntimeException("DimensionManagerWrapper can only be used on Minecraft version 1.16 or above");
        }
        try {
            Object craftWorldObject = craftWorldClass.cast(world);
            Object nmsWorldServerObject = getHandleMethod.invoke(craftWorldObject);
            this.nmsDimensionManager = getDimensionManagerMethod.invoke(nmsWorldServerObject);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        this.environment = world.getEnvironment();
    }

    public boolean hasFixedTime() {
        return getFixedTime().isPresent();
    }

    public OptionalLong getFixedTime() {
        return fixedTimeField.getOrDefault(nmsDimensionManager);
    }

    public float timeOfDay(long i) {
        double d0 = (double) getFixedTime().orElse(i) / 24000.0D - 0.25D;
        long i0 = (long) d0;
        d0 = d0 - (double) (d0 < (double) i0 ? i0 - 1L : i0);
        double d1 = 0.5D - Math.cos(d0 * 3.141592653589793D) / 2.0D;

        return (float) (d0 * 2.0D + d1) / 3.0F;
    }

    public boolean hasSkyLight() {
        return hasSkylightField.getOrDefault(nmsDimensionManager);
    }

    public boolean hasCeiling() {
        return hasCeilingField.getOrDefault(nmsDimensionManager);
    }

    public boolean ultraWarm() {
        return ultraWarmField.getOrDefault(nmsDimensionManager);
    }

    public boolean natural() {
        return naturalField.getOrDefault(nmsDimensionManager);
    }

    public double coordinateScale() {
        return coordinateScaleField.getOrDefault(nmsDimensionManager);
    }

    public boolean createDragonFight() {
        if (InteractiveChat.version.isNewerOrEqualTo(MCVersion.V1_19)) {
            return environment.equals(Environment.THE_END);
        }
        return createDragonFightField.getOrDefault(nmsDimensionManager);
    }

    public boolean piglinSafe() {
        return piglinSafeField.getOrDefault(nmsDimensionManager);
    }

    public boolean bedWorks() {
        return bedWorksField.getOrDefault(nmsDimensionManager);
    }

    public boolean respawnAnchorWorks() {
        return respawnAnchorWorksField.getOrDefault(nmsDimensionManager);
    }

    public boolean hasRaids() {
        return hasRaidsField.getOrDefault(nmsDimensionManager);
    }

    public int minY() {
        return minYField.getOrDefault(nmsDimensionManager);
    }

    public int height() {
        return heightField.getOrDefault(nmsDimensionManager);
    }

    public int logicalHeight() {
        return logicalHeightField.getOrDefault(nmsDimensionManager);
    }

    public String infiniburn() {
        return infiniburnField.getOrDefault(nmsDimensionManager);
    }

    public String effectsLocation() {
        return effectsLocationField.getOrDefault(nmsDimensionManager);
    }

    public float ambientLight() {
        return ambientLightField.getOrDefault(nmsDimensionManager);
    }

}
