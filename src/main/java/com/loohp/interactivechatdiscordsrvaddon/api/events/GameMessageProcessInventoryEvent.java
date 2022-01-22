package com.loohp.interactivechatdiscordsrvaddon.api.events;

import com.loohp.interactivechat.libs.net.kyori.adventure.text.Component;
import com.loohp.interactivechat.objectholders.ICPlayer;
import org.bukkit.inventory.Inventory;

/**
 * This event is called after the plugin translate a normal inventory
 * placeholder. (Currently only used by the enderchest) You can change the
 * contents of the inventory/item in this event.
 * <p>
 * For player inventories, please use GameMessageProcessPlayerInventoryEvent
 *
 * @author LOOHP
 */
public class GameMessageProcessInventoryEvent extends GameMessageProcessEvent {

    private Inventory inventory;

    public GameMessageProcessInventoryEvent(ICPlayer sender, String title, Component component, boolean cancel, int processId,
                                            Inventory inventory) {
        super(sender, title, component, cancel, processId);
        this.inventory = inventory;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

}
