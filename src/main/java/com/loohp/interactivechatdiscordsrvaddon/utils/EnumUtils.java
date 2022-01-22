package com.loohp.interactivechatdiscordsrvaddon.utils;

import java.util.ArrayList;
import java.util.List;

public class EnumUtils {

    public static <E extends Enum<E>> List<E> valuesBetween(Class<E> clazz, E start, E end) {
        return valuesBetween(clazz, start, end, true);
    }

    public static <E extends Enum<E>> List<E> valuesBetween(Class<E> clazz, E start, E end, boolean includeEnd) {
        List<E> includedValues = new ArrayList<>();
        if (start.equals(end)) {
            includedValues.add(start);
            return includedValues;
        }
        E[] values = clazz.getEnumConstants();
        boolean flag = false;
        for (E e : values) {
            if (flag) {
                if (e.equals(end)) {
                    if (includeEnd) {
                        includedValues.add(e);
                    }
                    return includedValues;
                } else {
                    includedValues.add(e);
                }
            } else {
                if (e.equals(start)) {
                    flag = true;
                    includedValues.add(e);
                }
            }
        }
        return includedValues;
    }

}
