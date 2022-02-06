package com.loohp.interactivechatdiscordsrvaddon.resources.languages;

import com.loohp.interactivechat.libs.org.apache.commons.io.input.BOMInputStream;
import com.loohp.interactivechat.libs.org.json.simple.JSONObject;
import com.loohp.interactivechat.libs.org.json.simple.parser.JSONParser;
import com.loohp.interactivechatdiscordsrvaddon.resources.AbstractManager;
import com.loohp.interactivechatdiscordsrvaddon.resources.ResourceManager;
import com.loohp.interactivechatdiscordsrvaddon.resources.ResourcePackFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class LanguageManager extends AbstractManager {

    private Map<String, Map<String, String>> translations;
    private List<Consumer<LanguageReloadEvent>> reloadListeners;
    private TranslateFunction translateFunction;
    private Supplier<Collection<String>> availableLanguagesSupplier;

    public LanguageManager(ResourceManager manager) {
        super(manager);
        this.translations = new HashMap<>();
        this.reloadListeners = Collections.synchronizedList(new LinkedList<>());
        this.translateFunction = (translationKey, language) -> {
            Map<String, String> mapping = translations.get(language);
            if (mapping == null) {
                return translationKey;
            }
            return mapping.getOrDefault(translationKey, translationKey);
        };
        this.availableLanguagesSupplier = () -> translations.keySet();
    }

    @Override
    protected void loadDirectory(String namespace, ResourcePackFile root) {
        if (!root.exists() || !root.isDirectory()) {
            throw new IllegalArgumentException(root.getAbsolutePath() + " is not a directory.");
        }
        JSONParser parser = new JSONParser();
        Map<String, Map<String, String>> translations = new HashMap<>();
        for (ResourcePackFile file : root.listFilesRecursively()) {
            String name = file.getName();
            if (name.endsWith(".json")) {
                try {
                    InputStreamReader reader = new InputStreamReader(new BOMInputStream(file.getInputStream()), StandardCharsets.UTF_8);
                    JSONObject json = (JSONObject) parser.parse(reader);
                    reader.close();
                    Map<String, String> mapping = new HashMap<>();
                    for (Object obj : json.keySet()) {
                        try {
                            String key = (String) obj;
                            mapping.put(key, (String) json.get(key));
                        } catch (Exception e) {
                        }
                    }
                    translations.put(file.getName().substring(0, file.getName().lastIndexOf(".")), mapping);
                } catch (Exception e) {
                    new RuntimeException("Unable to load language " + file.getAbsolutePath(), e).printStackTrace();
                }
            } else if (name.endsWith(".lang")) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(new BOMInputStream(file.getInputStream()), StandardCharsets.UTF_8))) {
                    Map<String, String> mapping = new HashMap<>();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        int separator = line.indexOf("=");
                        if (separator >= 0) {
                            mapping.put(line.substring(0, separator), line.substring(separator + 1));
                        }
                    }
                    translations.put(file.getName().substring(0, file.getName().lastIndexOf(".")), mapping);
                } catch (Exception e) {
                    new RuntimeException("Unable to load language " + file.getAbsolutePath(), e).printStackTrace();
                }
            }
        }
        for (Entry<String, Map<String, String>> entry : translations.entrySet()) {
            String key = entry.getKey();
            Map<String, String> mapping = this.translations.get(key);
            if (mapping == null) {
                this.translations.put(key, entry.getValue());
            } else {
                mapping.putAll(entry.getValue());
            }
        }
    }

    public void registerReloadListener(Consumer<LanguageReloadEvent> listener) {
        reloadListeners.add(listener);
    }

    public void unregisterReloadListener(Consumer<LanguageReloadEvent> listener) {
        reloadListeners.removeIf(each -> each.equals(listener));
    }

    public void clearTranslateFunction() {
        this.translateFunction = (translationKey, language) -> {
            Map<String, String> mapping = translations.get(language);
            if (mapping == null) {
                return translationKey;
            }
            return mapping.getOrDefault(translationKey, translationKey);
        };
        reload();
    }

    public TranslateFunction getTranslateFunction() {
        return translateFunction;
    }

    public void setTranslateFunction(TranslateFunction translateFunction) {
        this.translateFunction = translateFunction;
        reload();
    }

    public String applyTranslations(String str, String language) {
        return translateFunction.apply(str, language);
    }

    public List<String> getAvailableLanguages() {
        return availableLanguagesSupplier.get().stream().sorted().collect(Collectors.toList());
    }

    public void setAvailableLanguagesSupplier(Supplier<Collection<String>> availableLanguagesSupplier) {
        this.availableLanguagesSupplier = availableLanguagesSupplier;
        reload();
    }

    public void resetAvailableLanguagesSupplier() {
        this.availableLanguagesSupplier = () -> translations.keySet();
        reload();
    }

    @Override
    protected void reload() {
        Map<String, Map<String, String>> outerMap = new HashMap<>();
        for (Entry<String, Map<String, String>> entry : translations.entrySet()) {
            outerMap.put(entry.getKey(), Collections.unmodifiableMap(entry.getValue()));
        }
        LanguageReloadEvent event = new LanguageReloadEvent(this, Collections.unmodifiableMap(outerMap));
        reloadListeners.forEach(each -> each.accept(event));
    }

    public static class LanguageReloadEvent {

        private LanguageManager languageManager;
        private Map<String, Map<String, String>> translations;

        public LanguageReloadEvent(LanguageManager languageManager, Map<String, Map<String, String>> translations) {
            this.languageManager = languageManager;
            this.translations = translations;
        }

        public LanguageManager getLanguageManager() {
            return languageManager;
        }

        public Map<String, Map<String, String>> getTranslations() {
            return translations;
        }

    }

}
