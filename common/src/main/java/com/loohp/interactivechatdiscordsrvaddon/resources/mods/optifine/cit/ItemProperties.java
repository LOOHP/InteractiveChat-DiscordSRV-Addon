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

package com.loohp.interactivechatdiscordsrvaddon.resources.mods.optifine.cit;

import com.loohp.interactivechat.objectholders.ICMaterial;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.IntegerRange;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.PercentageOrIntegerRange;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlot;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class ItemProperties extends CITProperties {

    private final Map<String, String> models;
    private final Map<String, String> textures;

    public ItemProperties(int weight, Set<ICMaterial> items, IntegerRange stackSize, PercentageOrIntegerRange damage, int damageMask, EquipmentSlot hand, Map<Enchantment, IntegerRange> enchantments, Map<String, CITValueMatcher> nbtMatch, Map<String, String> models, Map<String, String> textures) {
        super(weight, items, stackSize, damage, damageMask, hand, enchantments, nbtMatch);
        this.models = models;
        this.textures = textures;
    }

    public Map<String, String> getModels() {
        return Collections.unmodifiableMap(models);
    }

    public Map<String, String> getTextures() {
        return Collections.unmodifiableMap(textures);
    }

    @Override
    public String getOverrideAsset(String path, String extension) {
        int pos = path.lastIndexOf("/");
        String match = pos < 0 ? path : path.substring(pos + 1);
        if (extension.equalsIgnoreCase("png")) {
            return textures.get(match);
        } else if (extension.equalsIgnoreCase("json")) {
            return models.get(match);
        } else {
            return null;
        }
    }

}
