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

package com.loohp.interactivechatdiscordsrvaddon.graphics;

import com.loohp.blockmodelrenderer.blending.BlendingMode;
import com.loohp.blockmodelrenderer.blending.BlendingModes;
import com.loohp.interactivechatdiscordsrvaddon.resources.mods.optifine.cit.EnchantmentProperties.OpenGLBlending;

public class BlendingUtils {

    public static BlendingModes convert(OpenGLBlending openGLBlending) {
        return BlendingModes.of(BlendingMode.fromOpenGL(openGLBlending.getSrcColor().getOpenGLValue()),
                                BlendingMode.fromOpenGL(openGLBlending.getDesColor().getOpenGLValue()),
                                BlendingMode.fromOpenGL(openGLBlending.getSrcAlpha().getOpenGLValue()),
                                BlendingMode.fromOpenGL(openGLBlending.getDesAlpha().getOpenGLValue()));
    }

}
