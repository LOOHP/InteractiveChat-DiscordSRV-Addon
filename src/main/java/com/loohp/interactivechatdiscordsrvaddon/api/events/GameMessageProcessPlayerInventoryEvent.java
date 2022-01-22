package com.loohp.interactivechatdiscordsrvaddon.api.events;

import com.loohp.interactivechat.libs.net.kyori.adventure.text.Component;
import com.loohp.interactivechat.objectholders.ICPlayer;
import org.bukkit.inventory.Inventory;

/**
 * This event is called after the plugin translate a player inventory
 * placeholder. You can change the contents of the inventory/item in this event.
 *
 * @author LOOHP
 */
public class GameMessageProcessPlayerInventoryEvent extends GameMessageProcessInventoryEvent {

    public GameMessageProcessPlayerInventoryEvent(ICPlayer sender, String title, Component component, boolean cancel,
                                                  int processId, Inventory inventory) {
        super(sender, title, component, cancel, processId, inventory);
    }

}
