package com.loohp.interactivechatdiscordsrvaddon.utils;

import org.bukkit.util.Vector;

public class VectorUtils {

    public static final double _2PI = 2 * Math.PI;

    public static double getBearing(Vector from, Vector to) {
        double x1 = from.getX();
        double z1 = from.getZ();

        double theta1 = Math.atan2(-x1, z1);
        double yaw1 = Math.toDegrees((theta1 + _2PI) % _2PI) % 360;

        double x2 = to.getX();
        double z2 = to.getZ();

        double theta2 = Math.atan2(-x2, z2);
        double yaw2 = Math.toDegrees((theta2 + _2PI) % _2PI) % 360;

        double zero = 360 - yaw1;
        return (yaw2 + zero) % 360;
    }

}
