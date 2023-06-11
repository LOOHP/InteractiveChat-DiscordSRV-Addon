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

package com.loohp.interactivechatdiscordsrvaddon.resources;

import java.util.regex.Pattern;

public abstract class AbstractManager implements IAbstractManager {

    protected final ResourceManager manager;

    public AbstractManager(ResourceManager manager) {
        this.manager = manager;
    }

    @Override
    public ResourceManager getManager() {
        return manager;
    }

    @Override
    public boolean isValid() {
        return manager.isValid();
    }

    protected abstract void loadDirectory(String namespace, ResourcePackFile root, Object... meta);

    protected abstract void filterResources(Pattern namespace, Pattern path);

    protected abstract void reload();

}
