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

package com.loohp.interactivechatdiscordsrvaddon.resources.textures;

import com.loohp.interactivechatdiscordsrvaddon.resources.AbstractManager;
import com.loohp.interactivechatdiscordsrvaddon.resources.ResourceManager;
import com.loohp.interactivechatdiscordsrvaddon.resources.ResourcePackFile;

import java.awt.image.BufferedImage;
import java.util.regex.Pattern;

public class GeneratedTextureResource extends TextureResource {

    private BufferedImage image;

    public GeneratedTextureResource(ResourceManager manager, String resourceKey, BufferedImage image) {
        super(new GeneratedTextureManager(manager), resourceKey, null, image, null);
        this.image = image;
    }

    public GeneratedTextureResource(ResourceManager manager, BufferedImage image) {
        this(manager, null, image);
    }

    public GeneratedTextureResource(ResourceManager manager, String resourceKey, ResourcePackFile file) {
        super(new GeneratedTextureManager(manager), resourceKey, file, false, null);
    }

    public GeneratedTextureResource(ResourceManager manager, ResourcePackFile file) {
        this(manager, null, file);
    }

    @Override
    public TextureMeta getTextureMeta() {
        return null;
    }

    public static class GeneratedTextureManager extends AbstractManager implements ITextureManager {

        private GeneratedTextureManager(ResourceManager manager) {
            super(manager);
        }

        @Override
        protected void loadDirectory(String namespace, ResourcePackFile root, Object... meta) {
            throw new UnsupportedOperationException("Cannot operate on Generated Texture Managers");
        }

        @Override
        protected void filterResources(Pattern namespace, Pattern path) {
            throw new UnsupportedOperationException("Cannot operate on Generated Texture Managers");
        }

        @Override
        protected void reload() {
            throw new UnsupportedOperationException("Cannot operate on Generated Texture Managers");
        }

        @Override
        public TextureResource getMissingTexture() {
            return manager.getTextureManager().getMissingTexture();
        }

        @Override
        public TextureResource getTexture(String resourceLocation, boolean returnMissingTexture) {
            throw new UnsupportedOperationException("Cannot operate on Generated Texture Managers");
        }

    }

}
