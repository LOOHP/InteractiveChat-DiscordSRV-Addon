package com.loohp.interactivechatdiscordsrvaddon.API;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.entity.Player;

import com.loohp.interactivechatdiscordsrvaddon.Listeners.DiscordAttachmentEvents;
import com.loohp.interactivechatdiscordsrvaddon.Listeners.DiscordAttachmentEvents.DiscordAttachmentData;
import com.loohp.interactivechatdiscordsrvaddon.Wrappers.GraphicsToPacketMapWrapper;

public class InteractiveChatDiscordSrvAddonAPI {
	
	public static Map<UUID, DiscordAttachmentData> getActiveDiscordAttachments() {
		return Collections.unmodifiableMap(DiscordAttachmentEvents.DATA);
	}
	
	public static Map<Player, GraphicsToPacketMapWrapper> getActivePlayerImageMapViews() {
		return Collections.unmodifiableMap(DiscordAttachmentEvents.MAP_VIEWERS);
	}
	
	public static GraphicsToPacketMapWrapper getDiscordImageWrapperByUUID(UUID uuid) {
		Optional<DiscordAttachmentData> opt = DiscordAttachmentEvents.DATA.values().stream().filter(each -> each.getUniqueId().equals(uuid)).findFirst();
		if (opt.isPresent() && opt.get().isImage()) {
			return opt.get().getImageMap();
		} else {
			return null;
		}
	}

}
