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

package com.loohp.interactivechatdiscordsrvaddon.utils;

public class ICU4JUtils {

    private static boolean icu4JPresent = false;
    private static boolean icu4jClassNotFoundThrown = false;

    public static boolean icu4JAvailable() {
        if (icu4JPresent) {
            return true;
        } else if (icu4jClassNotFoundThrown) {
            return false;
        }
        try {
            Class.forName("com.ibm.icu.text.Bidi");
            return icu4JPresent = true;
        } catch (Throwable e) {
            e.printStackTrace();
            icu4jClassNotFoundThrown = true;
            return icu4JPresent = false;
        }
    }

    public static String shaping(String str) {
        if (!icu4JPresent) {
            return str;
        }
        try {
            return new com.ibm.icu.text.ArabicShaping(8).shape(str);
        } catch (com.ibm.icu.text.ArabicShapingException e) {
            //ignore
        } catch (Throwable e) {
            if (!icu4jClassNotFoundThrown && e instanceof ClassNotFoundException) {
                e.printStackTrace();
                icu4jClassNotFoundThrown = true;
            }
        }
        return str;
    }

    public static String bidirectionalShaping(String str) {
        if (!icu4JPresent) {
            return str;
        }
        try {
            com.ibm.icu.text.Bidi bidi = new com.ibm.icu.text.Bidi((new com.ibm.icu.text.ArabicShaping(8)).shape(str), 126);
            bidi.setReorderingMode(0);
            return bidi.writeReordered(2);
        } catch (com.ibm.icu.text.ArabicShapingException e) {
            //ignore
        } catch (Throwable e) {
            if (!icu4jClassNotFoundThrown && e instanceof ClassNotFoundException) {
                e.printStackTrace();
                icu4jClassNotFoundThrown = true;
            }
        }
        return str;
    }

    public static byte[] getBidirectionalLevels(char[] str) {
        if (!icu4JPresent) {
            return null;
        }
        try {
            com.ibm.icu.text.Bidi bidi = new com.ibm.icu.text.Bidi(str, 0, null, 0, str.length, 126);
            bidi.setReorderingMode(0);
            return bidi.getLevels();
        } catch (Throwable e) {
            if (!icu4jClassNotFoundThrown && e instanceof ClassNotFoundException) {
                e.printStackTrace();
                icu4jClassNotFoundThrown = true;
            }
        }
        return null;
    }

    public static void bidirectionalReorderVisually(byte[] levels, char[] chars) {
        Character[] characters = new Character[chars.length];
        for (int i = 0; i < chars.length; i++) {
            characters[i] = chars[i];
        }
        bidirectionalReorderVisually(levels, characters);
        for (int i = 0; i < chars.length; i++) {
            chars[i] = characters[i];
        }
    }

    public static void bidirectionalReorderVisually(byte[] levels, Object[] objects) {
        if (!icu4JPresent) {
            return;
        }
        try {
            com.ibm.icu.text.Bidi.reorderVisually(levels, 0, objects, 0, objects.length);
        } catch (Throwable e) {
            if (!icu4jClassNotFoundThrown && e instanceof ClassNotFoundException) {
                e.printStackTrace();
                icu4jClassNotFoundThrown = true;
            }
        }
    }

}
