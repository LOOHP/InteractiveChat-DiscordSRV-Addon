/*
 * This file is part of InteractiveChatDiscordSrvAddon2.
 *
 * Copyright (C) 2020 - 2025. LoohpJames <jamesloohp@gmail.com>
 * Copyright (C) 2020 - 2025. Contributors
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

package com.loohp.interactivechatdiscordsrvaddon.resources.mods.chime;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ChimePredicateEnums {

    public enum ItemInHand {

        MAIN("main"),
        OFF("off"),
        EITHER("either"),
        NEITHER("neither", "none");

        private Set<String> names;

        ItemInHand(String... names) {
            this.names = new HashSet<>(Arrays.asList(names));
        }

        public Set<String> getNames() {
            return names;
        }

        public static ItemInHand fromName(String name) {
            for (ItemInHand itemInHand : values()) {
                if (itemInHand.getNames().stream().anyMatch(each -> each.equalsIgnoreCase(name))) {
                    return itemInHand;
                }
            }
            return null;
        }

    }

    public enum TargetType {

        BLOCK,
        ENTITY,
        MISS;

        public static TargetType fromName(String name) {
            for (TargetType targetType : values()) {
                if (targetType.name().equalsIgnoreCase(name)) {
                    return targetType;
                }
            }
            return null;
        }

    }

}
