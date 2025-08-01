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

package com.loohp.interactivechatdiscordsrvaddon.nms;

import com.loohp.interactivechat.InteractiveChat;
import com.loohp.interactivechatdiscordsrvaddon.InteractiveChatDiscordSrvAddon;

import java.lang.reflect.InvocationTargetException;

public class NMSAddon {

    private static NMSAddonWrapper instance;

    @SuppressWarnings("deprecation")
    public synchronized static NMSAddonWrapper getInstance() {
        if (instance != null) {
            return instance;
        }
        try {
            Class<NMSAddonWrapper> nmsImplClass = (Class<NMSAddonWrapper>) Class.forName("com.loohp.interactivechatdiscordsrvaddon.nms." + InteractiveChat.version.name());
            instance = nmsImplClass.getConstructor().newInstance();
            NMSAddonWrapper.setup(instance, InteractiveChatDiscordSrvAddon.plugin);
            return instance;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
            if (InteractiveChat.version.isSupported()) {
                throw new RuntimeException("Missing NMSWrapper implementation for version " + InteractiveChat.version.name(), e);
            } else {
                throw new RuntimeException("No NMSWrapper implementation for UNSUPPORTED version " + InteractiveChat.version.name(), e);
            }
        }
    }

}
