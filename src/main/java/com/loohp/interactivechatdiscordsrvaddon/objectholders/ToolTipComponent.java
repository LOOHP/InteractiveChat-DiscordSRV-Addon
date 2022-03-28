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

package com.loohp.interactivechatdiscordsrvaddon.objectholders;

import com.loohp.interactivechat.libs.net.kyori.adventure.text.Component;

import java.awt.image.BufferedImage;

public class ToolTipComponent<T> {

    public static ToolTipComponent<Component> of(Component component) {
        return new ToolTipComponent<>(component);
    }

    public static ToolTipComponent<BufferedImage> of(BufferedImage image) {
        return new ToolTipComponent<>(image);
    }

    private T toolTipComponent;

    private ToolTipComponent(T toolTipComponent) {
        if (!(toolTipComponent instanceof Component) && !(toolTipComponent instanceof BufferedImage)) {
            throw new IllegalArgumentException("ToolTipComponent can only be created with Component or BufferedImage");
        }
        this.toolTipComponent = toolTipComponent;
    }

    public T getToolTipComponent() {
        return toolTipComponent;
    }

}
