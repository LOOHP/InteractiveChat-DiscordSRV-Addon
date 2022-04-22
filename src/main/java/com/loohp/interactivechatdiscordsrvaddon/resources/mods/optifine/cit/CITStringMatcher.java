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

package com.loohp.interactivechatdiscordsrvaddon.resources.mods.optifine.cit;

import java.util.regex.Pattern;

public abstract class CITStringMatcher {

    private final String value;

    public CITStringMatcher(String value) {
        this.value = value;
    }

    public abstract boolean matches(String value);

    public String value() {
        return value;
    }

    public static class DirectMatcher extends CITStringMatcher {

        protected final String pattern;

        public DirectMatcher(String pattern) {
            super(pattern);
            this.pattern = pattern;
        }

        @Override
        public boolean matches(String value) {
            return pattern.equals(value);
        }

    }

    public static class RegexMatcher extends CITStringMatcher {

        protected final Pattern pattern;

        public RegexMatcher(String pattern) {
            this(Pattern.compile(pattern));
        }

        protected RegexMatcher(Pattern pattern) {
            super(pattern.pattern());
            this.pattern = pattern;
        }

        @Override
        public boolean matches(String value) {
            return this.pattern.matcher(value).matches();
        }

    }

    public static class PatternMatcher extends CITStringMatcher {

        protected final String pattern;

        public PatternMatcher(String pattern) {
            super(pattern);
            this.pattern = pattern;
        }

        @Override
        public boolean matches(String value) {
            return matchesPattern(value, this.pattern, 0, value.length(), 0, pattern.length());
        }

        /**
         * Author: Paul "prupe" Rupe<br>
         * Taken and modified from MCPatcher under public domain licensing.<br>
         * <a href="https://bitbucket.org/prupe/mcpatcher/src/1aa45839b2cd029143809edfa60ec59e5ef75f80/newcode/src/com/prupe/mcpatcher/mal/nbt/NBTRule.java#lines-269:301">https://bitbucket.org/prupe/mcpatcher/src/1aa45839b2cd029143809edfa60ec59e5ef75f80/newcode/src/com/prupe/mcpatcher/mal/nbt/NBTRule.java#lines-269:301</a>
         */
        protected boolean matchesPattern(String value, String pattern, int curV, int maxV, int curG, int maxG) {
            for (; curG < maxG; curG++, curV++) {
                char g = pattern.charAt(curG);
                if (g == '*') {
                    while (true) {
                        if (matchesPattern(value, pattern, curV, maxV, curG + 1, maxG)) {
                            return true;
                        }
                        if (curV >= maxV) {
                            break;
                        }
                        curV++;
                    }
                    return false;
                } else if (curV >= maxV) {
                    break;
                } else if (g == '?') {
                    continue;
                }
                if (g == '\\' && curG + 1 < maxG) {
                    curG++;
                    g = pattern.charAt(curG);
                }

                if (!charsEqual(g, value.charAt(curV))) {
                    return false;
                }
            }
            return curG == maxG && curV == maxV;
        }

        protected boolean charsEqual(char p, char v) {
            return p == v;
        }

    }

    public static class IRegexMatcher extends RegexMatcher {

        public IRegexMatcher(String pattern) {
            super(Pattern.compile(pattern, Pattern.CASE_INSENSITIVE));
        }

    }

    public static class IPatternMatcher extends PatternMatcher {

        public IPatternMatcher(String pattern) {
            super(pattern.toLowerCase());
        }

        @Override
        protected boolean charsEqual(char p, char v) {
            return p == v || p == Character.toLowerCase(v);
        }

    }

}