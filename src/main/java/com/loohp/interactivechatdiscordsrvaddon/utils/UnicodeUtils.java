package com.loohp.interactivechatdiscordsrvaddon.utils;

public class UnicodeUtils {

    private static boolean icu4jClassNotFoundThrown = false;

    public static String bidirectionalShaping(String str) {
        if (icu4jClassNotFoundThrown) {
            return str;
        }
        try {
            com.ibm.icu.text.Bidi bidi = new com.ibm.icu.text.Bidi((new com.ibm.icu.text.ArabicShaping(8)).shape(str), 127);
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

}
