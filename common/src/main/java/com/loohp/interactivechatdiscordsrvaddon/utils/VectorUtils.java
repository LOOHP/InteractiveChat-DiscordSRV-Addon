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
