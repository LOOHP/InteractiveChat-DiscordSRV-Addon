package com.loohp.interactivechatdiscordsrvaddon.resources.languages;

import java.util.function.BiFunction;
import java.util.function.Function;

@FunctionalInterface
public interface TranslateFunction extends BiFunction<String, String, String> {

    String apply(String translationKey, String language);

    default Function<String, String> ofLanguage(String language) {
        return translationKey -> apply(translationKey, language);
    }

}
