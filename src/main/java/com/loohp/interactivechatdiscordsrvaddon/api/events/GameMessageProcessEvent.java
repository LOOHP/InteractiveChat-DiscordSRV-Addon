package com.loohp.interactivechatdiscordsrvaddon.api.events;

import com.loohp.interactivechat.libs.net.kyori.adventure.text.Component;
import com.loohp.interactivechat.objectholders.ICPlayer;

/**
 * This is the base class of all GameMessageProcessEvents
 *
 * @author LOOHP
 */
public class GameMessageProcessEvent extends GameMessageEvent {

    private int processId;
    private String title;

    public GameMessageProcessEvent(ICPlayer sender, String title, Component component, boolean cancel, int processId) {
        super(sender, component, cancel);
        this.processId = processId;
        this.title = title;
    }

    public int getProcessId() {
        return processId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
