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

package com.loohp.interactivechatdiscordsrvaddon.objectholders;

import com.loohp.interactivechatdiscordsrvaddon.resources.ResourceLoadingException;

import java.util.Objects;

@SuppressWarnings("DuplicateExpressions")
public class IntegerRange {

    private final int min;
    private final int max;

    public IntegerRange(int min, int max) {
        this.min = min;
        this.max = max;
    }

    public IntegerRange(String strValue) {
        switch (strValue.length() - strValue.replace("-", "").length()) {
            case 0: {
                min = Integer.parseInt(strValue);
                max = min;
                break;
            }
            case 1: {
                if (strValue.startsWith("-")) {
                    min = Integer.parseInt(strValue);
                    max = min;
                } else if (strValue.endsWith("-")) {
                    min = Integer.parseInt(strValue.substring(0, strValue.length() - 1));
                    max = Integer.MAX_VALUE;
                } else {
                    String[] split = strValue.split("-");
                    min = Integer.parseInt(split[0]);
                    max = Integer.parseInt(split[1]);
                }
                break;
            }
            case 2: {
                if (strValue.startsWith("--")) {
                    min = Integer.MIN_VALUE;
                    max = Integer.parseInt(strValue.substring(1));
                } else if (strValue.startsWith("-") && strValue.endsWith("-")) {
                    min = Integer.parseInt(strValue.substring(0, strValue.length() - 1));
                    max = Integer.MAX_VALUE;
                } else if (strValue.startsWith("-") && !strValue.endsWith("-") && !strValue.contains("--")) {
                    int lastDash = strValue.lastIndexOf('-');
                    min = Integer.parseInt(strValue.substring(0, lastDash));
                    max = Integer.parseInt(strValue.substring(lastDash + 1));
                } else {
                    throw new ResourceLoadingException("Could not parse range");
                }
                break;
            }
            case 3: {
                if (!strValue.contains("---") && strValue.startsWith("-")) {
                    String[] split = strValue.split("--");
                    if (split.length != 2 || split[0].isEmpty() || split[1].isEmpty()) {
                        throw new ResourceLoadingException("Could not parse range");
                    }

                    min = Integer.parseInt(split[0]);
                    max = -Integer.parseInt(split[1]);
                } else {
                    throw new ResourceLoadingException("Could not parse range");
                }
                break;
            }
            default:
                throw new ResourceLoadingException("Could not parse range");
        }
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }

    public boolean test(int value) {
        return min <= value && max >= value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        IntegerRange that = (IntegerRange) o;
        return min == that.min && max == that.max;
    }

    @Override
    public int hashCode() {
        return Objects.hash(min, max);
    }

}
