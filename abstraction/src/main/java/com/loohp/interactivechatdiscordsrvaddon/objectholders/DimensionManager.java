/*
 * This file is part of InteractiveChatDiscordSrvAddon-Abstraction.
 *
 * Copyright (C) 2024. LoohpJames <jamesloohp@gmail.com>
 * Copyright (C) 2024. Contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General License for more details.
 *
 * You should have received a copy of the GNU General License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.loohp.interactivechatdiscordsrvaddon.objectholders;

import com.loohp.interactivechat.libs.net.kyori.adventure.key.Key;

import java.util.OptionalLong;

public interface DimensionManager {

    boolean hasFixedTime();

    OptionalLong getFixedTime();

    float timeOfDay(long i);

    boolean hasSkyLight();

    boolean hasCeiling();

    boolean ultraWarm();

    boolean natural();

    double coordinateScale();

    boolean createDragonFight();

    boolean piglinSafe();

    boolean bedWorks();

    boolean respawnAnchorWorks();

    boolean hasRaids();

    int minY();

    int height();

    int logicalHeight();

    Key infiniburn();

    Key effectsLocation();

    float ambientLight();

}
