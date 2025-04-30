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

package com.loohp.interactivechatdiscordsrvaddon.objectholders;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

public class SteppedIntegerRange extends IntegerRange implements Iterable<Integer> {

    private final int step;

    public SteppedIntegerRange(int singleValue) {
        super(singleValue, singleValue);
        this.step = 1;
    }

    public SteppedIntegerRange(int min, int max, int step) {
        super(min, max);
        this.step = step;
    }

    public int getStep() {
        return step;
    }

    public int getTotalSteps() {
        return (getMax() - getMin()) / step + 1;
    }

    @Override
    public String toString() {
        return super.toString() + " step " + step;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        SteppedIntegerRange that = (SteppedIntegerRange) o;
        return step == that.step;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), step);
    }

    @Override
    public Iterator<Integer> iterator() {
        return new SteppedIntegerIterator(this);
    }

    public static class SteppedIntegerIterator implements Iterator<Integer> {

        private final SteppedIntegerRange range;
        private int current;

        public SteppedIntegerIterator(SteppedIntegerRange range) {
            this.range = range;
            this.current = range.getMin() - range.getStep();
        }

        @Override
        public boolean hasNext() {
            return current < range.getMax();
        }

        @Override
        public Integer next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            return current += range.getStep();
        }
    }
}
