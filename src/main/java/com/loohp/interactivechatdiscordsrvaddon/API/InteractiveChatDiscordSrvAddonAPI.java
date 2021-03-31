package com.loohp.interactivechatdiscordsrvaddon.API;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.entity.Player;

import com.loohp.interactivechatdiscordsrvaddon.Listeners.InboundToGameEvents;
import com.loohp.interactivechatdiscordsrvaddon.Listeners.InboundToGameEvents.DiscordAttachmentData;
import com.loohp.interactivechatdiscordsrvaddon.Wrappers.GraphicsToPacketMapWrapper;

public class InteractiveChatDiscordSrvAddonAPI {
	
	/**
	 * Get all active discord attachments
	 * @return A mapping of the assigned UUID to the discord attachments
	 */
	public static Map<UUID, DiscordAttachmentData> getActiveDiscordAttachments() {
		return Collections.unmodifiableMap(InboundToGameEvents.DATA);
	}
	
	/**
	 * Get all active image preview maps
	 * @return A mapping of currently viewing players to the image preview maps
	 */
	public static Map<Player, GraphicsToPacketMapWrapper> getActivePlayerImageMapViews() {
		return Collections.unmodifiableMap(InboundToGameEvents.MAP_VIEWERS);
	}
	
	/**
	 * Get the preview image map by the assigned uuid
	 * @param uuid
	 * @return The image preview map (Could be null)
	 */
	public static GraphicsToPacketMapWrapper getDiscordImageWrapperByUUID(UUID uuid) {
		Optional<DiscordAttachmentData> opt = InboundToGameEvents.DATA.values().stream().filter(each -> each.getUniqueId().equals(uuid)).findFirst();
		if (opt.isPresent() && opt.get().isImage()) {
			return opt.get().getImageMap();
		} else {
			return null;
		}
	}

}
