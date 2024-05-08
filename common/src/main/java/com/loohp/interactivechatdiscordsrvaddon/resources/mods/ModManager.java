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

package com.loohp.interactivechatdiscordsrvaddon.resources.mods;

import com.loohp.interactivechatdiscordsrvaddon.resources.AbstractManager;
import com.loohp.interactivechatdiscordsrvaddon.resources.ResourceManager;

import java.util.Collections;
import java.util.List;

public abstract class ModManager extends AbstractManager {

    private final String modName;
    private final List<String> modAssetsFolderNames;

    public ModManager(ResourceManager manager, String modName, List<String> modAssetsFolderNames) {
        super(manager);
        this.modName = modName;
        this.modAssetsFolderNames = Collections.unmodifiableList(modAssetsFolderNames);
    }

    public String getModName() {
        return modName;
    }

    public List<String> getModAssetsFolderNames() {
        return modAssetsFolderNames;
    }

}
