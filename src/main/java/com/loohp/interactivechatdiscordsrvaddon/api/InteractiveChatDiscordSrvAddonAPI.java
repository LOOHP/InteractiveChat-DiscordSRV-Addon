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

package com.loohp.interactivechatdiscordsrvaddon.api;

import com.loohp.interactivechatdiscordsrvaddon.InteractiveChatDiscordSrvAddon;
import com.loohp.interactivechatdiscordsrvaddon.listeners.InboundToGameEvents;
import com.loohp.interactivechatdiscordsrvaddon.listeners.InboundToGameEvents.DiscordAttachmentData;
import com.loohp.interactivechatdiscordsrvaddon.resources.ResourceManager;
import com.loohp.interactivechatdiscordsrvaddon.wrappers.GraphicsToPacketMapWrapper;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class InteractiveChatDiscordSrvAddonAPI {

    /**
     * Whether the plugin is ready
     *
     * @return true/false
     */
    public static boolean isReady() {
        return InteractiveChatDiscordSrvAddon.isReady;
    }

    /**
     * Get the current active resource manager<br>
     * A new instance is created whenever the plugin is reloaded<br>
     * Null will be returned if the plugin had yet to finish setting up, or when there is an error.
     *
     * @return the current resource manager or null
     */
    public static ResourceManager getCurrentResourceManager() {
        return InteractiveChatDiscordSrvAddon.plugin.resourceManager;
    }

    /**
     * Get all active discord attachments
     *
     * @return A mapping of the assigned UUID to the discord attachments
     */
    public static Map<UUID, DiscordAttachmentData> getActiveDiscordAttachments() {
        return Collections.unmodifiableMap(InboundToGameEvents.DATA);
    }

    /**
     * Get all active image preview maps
     *
     * @return A mapping of currently viewing players to the image preview maps
     */
    public static Map<Player, GraphicsToPacketMapWrapper> getActivePlayerImageMapViews() {
        return Collections.unmodifiableMap(InboundToGameEvents.MAP_VIEWERS);
    }

    /**
     * Get the preview image map by the assigned uuid
     *
     * @param uuid the uuid of the image wrapper
     * @return The image preview map (Could be null)
     */
    public static GraphicsToPacketMapWrapper getDiscordImageWrapperByUUID(UUID uuid) {
        Optional<DiscordAttachmentData> opt = InboundToGameEvents.DATA.values().stream().filter(each -> each.getUniqueId().equals(uuid)).findFirst();
        DiscordAttachmentData data;
        if (opt.isPresent() && (data = opt.get()).isImage()) {
            return data.getImageMap();
        } else {
            return null;
        }
    }

}
