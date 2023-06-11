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

package com.loohp.interactivechatdiscordsrvaddon.resources.languages;

import com.loohp.interactivechat.libs.org.apache.commons.io.input.BOMInputStream;
import com.loohp.interactivechat.libs.org.json.simple.JSONObject;
import com.loohp.interactivechat.libs.org.json.simple.parser.JSONParser;
import com.loohp.interactivechatdiscordsrvaddon.resources.AbstractManager;
import com.loohp.interactivechatdiscordsrvaddon.resources.ResourceLoadingException;
import com.loohp.interactivechatdiscordsrvaddon.resources.ResourceManager;
import com.loohp.interactivechatdiscordsrvaddon.resources.ResourcePackFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class LanguageManager extends AbstractManager implements ILanguageManager {

    public static TranslateFunction defaultTranslateFunction(LanguageManager manager) {
        return (translationKey, language) -> {
            Map<String, String> mapping = manager.translations.get(language);
            if (mapping == null) {
                return translationKey;
            }
            return mapping.getOrDefault(translationKey, translationKey);
        };
    }

    public static Supplier<Collection<String>> defaultAvailableLanguagesSupplier(LanguageManager manager) {
        return () -> manager.translations.keySet();
    }

    private Map<String, LanguageMeta> languageMeta;
    private Map<String, Map<String, String>> translations;
    private List<Consumer<LanguageReloadEvent>> reloadListeners;
    private TranslateFunction translateFunction;
    private Supplier<Collection<String>> availableLanguagesSupplier;

    public LanguageManager(ResourceManager manager) {
        super(manager);
        this.languageMeta = new HashMap<>();
        this.translations = new HashMap<>();
        this.reloadListeners = Collections.synchronizedList(new LinkedList<>());
        this.translateFunction = defaultTranslateFunction(this);
        this.availableLanguagesSupplier = defaultAvailableLanguagesSupplier(this);
    }

    @Override
    protected void loadDirectory(String namespace, ResourcePackFile root, Object... meta) {
        if (!root.exists() || !root.isDirectory()) {
            throw new IllegalArgumentException(root.getAbsolutePath() + " is not a directory.");
        }
        if (meta.length >= 0) {
            try {
                this.languageMeta.putAll((Map<? extends String, ? extends LanguageMeta>) meta[0]);
            } catch (Throwable e) {
                new ResourceLoadingException("Invalid meta arguments, Map<? extends String, ? extends LanguageMeta> expected!", e).printStackTrace();
            }
        }
        JSONParser parser = new JSONParser();
        Map<String, Map<String, String>> translations = new HashMap<>();
        for (ResourcePackFile file : root.listFilesRecursively()) {
            String name = file.getName();
            if (!manager.hasFlag(ResourceManager.Flag.LEGACY_PRE_FLATTEN) && name.endsWith(".json")) {
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
                    new ResourceLoadingException("Unable to load language " + file.getAbsolutePath(), e).printStackTrace();
                }
            } else if (manager.hasFlag(ResourceManager.Flag.LEGACY_PRE_FLATTEN) && name.endsWith(".lang")) {
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
                    new ResourceLoadingException("Unable to load language " + file.getAbsolutePath(), e).printStackTrace();
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

    @Override
    protected void filterResources(Pattern namespace, Pattern path) {
        Iterator<String> itr = translations.keySet().iterator();
        while (itr.hasNext()) {
            String namespacedKey = itr.next();
            String assetNamespace = namespacedKey.substring(0, namespacedKey.indexOf(":"));
            String assetKey = namespacedKey.substring(namespacedKey.indexOf(":") + 1);
            if (!assetKey.contains(".")) {
                assetKey = assetKey + (manager.hasFlag(ResourceManager.Flag.LEGACY_PRE_FLATTEN) ? ".lang" : ".json");
            }
            if (namespace.matcher(assetNamespace).matches() && path.matcher(assetKey).matches()) {
                itr.remove();
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
        this.translateFunction = defaultTranslateFunction(this);
        reload();
    }

    public TranslateFunction getTranslateFunction() {
        return translateFunction;
    }

    public void setTranslateFunction(TranslateFunction translateFunction) {
        this.translateFunction = translateFunction;
        reload();
    }

    @Override
    public String applyTranslations(String str, String language) {
        return translateFunction.apply(str, language);
    }

    @Override
    public List<String> getAvailableLanguages() {
        return availableLanguagesSupplier.get().stream().sorted().collect(Collectors.toList());
    }

    public void setAvailableLanguagesSupplier(Supplier<Collection<String>> availableLanguagesSupplier) {
        this.availableLanguagesSupplier = availableLanguagesSupplier;
        reload();
    }

    public void resetAvailableLanguagesSupplier() {
        this.availableLanguagesSupplier = defaultAvailableLanguagesSupplier(this);
        reload();
    }

    public Map<String, LanguageMeta> getAllLanguageMeta() {
        return Collections.unmodifiableMap(languageMeta);
    }

    @Override
    public LanguageMeta getLanguageMeta(String language) {
        return languageMeta.get(language);
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
