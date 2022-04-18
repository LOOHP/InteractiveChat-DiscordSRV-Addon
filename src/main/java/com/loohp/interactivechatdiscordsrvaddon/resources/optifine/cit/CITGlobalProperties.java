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

package com.loohp.interactivechatdiscordsrvaddon.resources.optifine.cit;

import java.util.Properties;

public class CITGlobalProperties {

    public static CITGlobalProperties fromProperties(Properties properties) {
        boolean useGlint = Boolean.parseBoolean(properties.getProperty("useGlint", "true"));
        int cap = Integer.parseInt(properties.getProperty("cap", Integer.MAX_VALUE + ""));
        String method = properties.getProperty("method", "average");
        double fade = Double.parseDouble(properties.getProperty("fade", "0.5"));
        return new CITGlobalProperties(useGlint, cap, method, fade);
    }

    private boolean useGlint;
    private int cap;
    private String method;
    private double fade;

    public CITGlobalProperties(boolean useGlint, int cap, String method, double fade) {
        this.useGlint = useGlint;
        this.cap = cap;
        this.method = method;
        this.fade = fade;
    }

    public boolean isUseGlint() {
        return useGlint;
    }

    public int getCap() {
        return cap;
    }

    public String getMethod() {
        return method;
    }

    public double getFade() {
        return fade;
    }

}
