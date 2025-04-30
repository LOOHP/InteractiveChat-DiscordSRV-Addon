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

package com.loohp.interactivechatdiscordsrvaddon.utils;

import com.ibm.icu.text.ArabicShaping;
import com.ibm.icu.text.Bidi;
import com.ibm.icu.text.BidiRun;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.Component;
import com.loohp.interactivechat.objectholders.ValuePairs;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.CharacterData;
import it.unimi.dsi.fastutil.chars.CharObjectPair;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class I18nUtils {

    public static String shaping(String str) {
        try {
            return new ArabicShaping(8).shape(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }

    public static List<CharObjectPair<CharacterData>> bidirectionalReorder(Component component, boolean rightToLeft) {
        ValuePairs<String, List<CharObjectPair<CharacterData>>> pair = CharacterData.fromComponent(component, str -> shaping(str));
        List<CharObjectPair<CharacterData>> data = pair.getSecond();
        Bidi bidi = new Bidi(pair.getFirst(), rightToLeft ? 127 : 126);
        bidi.setReorderingMode(0);
        List<CharObjectPair<CharacterData>> result = new ArrayList<>(data.size());
        int totalRuns = bidi.countRuns();
        for (int i = 0; i < totalRuns; i++) {
            BidiRun bidiRun = bidi.getVisualRun(i);
            int start = bidiRun.getStart();
            int limit = bidiRun.getLimit();
            List<CharObjectPair<CharacterData>> subResult = new ArrayList<>(bidiRun.getLength());
            for (int u = start; u < limit; u++) {
                if (u < 0 || u >= data.size()) {
                    continue;
                }
                subResult.add(data.get(u));
            }
            if (bidiRun.isOddRun()) {
                ListIterator<CharObjectPair<CharacterData>> itr = subResult.listIterator(subResult.size());
                while (itr.hasPrevious()) {
                    result.add(itr.previous());
                }
            } else {
                result.addAll(subResult);
            }
        }
        return result;
    }

}
