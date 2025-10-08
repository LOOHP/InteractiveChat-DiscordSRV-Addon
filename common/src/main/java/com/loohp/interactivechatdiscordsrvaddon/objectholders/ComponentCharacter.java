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

package com.loohp.interactivechatdiscordsrvaddon.objectholders;

import com.loohp.interactivechat.libs.net.kyori.adventure.text.object.ObjectContents;

import java.util.OptionalInt;

public interface ComponentCharacter {

    static ComponentCharacterText text(char character) {
        return new ComponentCharacterText(character);
    }

    static ComponentCharacterObject object(ObjectContents objectContents) {
        return new ComponentCharacterObject(objectContents);
    }
    
    OptionalInt getCharForShaper();

    class ComponentCharacterText implements ComponentCharacter {

        private final char character;

        private ComponentCharacterText(char character) {
            this.character = character;
        }

        public char getText() {
            return character;
        }

        @Override
        public OptionalInt getCharForShaper() {
            return OptionalInt.of(character);
        }
    }

    class ComponentCharacterObject implements ComponentCharacter {

        private final ObjectContents objectContents;

        private ComponentCharacterObject(ObjectContents objectContents) {
            this.objectContents = objectContents;
        }

        public ObjectContents getObjectContents() {
            return objectContents;
        }

        @Override
        public OptionalInt getCharForShaper() {
            return OptionalInt.empty();
        }
    }

}
