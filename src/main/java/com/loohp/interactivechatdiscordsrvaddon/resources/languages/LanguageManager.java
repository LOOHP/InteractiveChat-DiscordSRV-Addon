package com.loohp.interactivechatdiscordsrvaddon.resources.languages;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.loohp.interactivechat.libs.org.apache.commons.io.input.BOMInputStream;
import com.loohp.interactivechat.libs.org.json.simple.JSONObject;
import com.loohp.interactivechat.libs.org.json.simple.parser.JSONParser;
import com.loohp.interactivechat.utils.LanguageUtils;
import com.loohp.interactivechatdiscordsrvaddon.InteractiveChatDiscordSrvAddon;
import com.loohp.interactivechatdiscordsrvaddon.resources.AbstractManager;
import com.loohp.interactivechatdiscordsrvaddon.resources.ResourceManager;
import com.loohp.interactivechatdiscordsrvaddon.resources.ResourcePackFile;

public class LanguageManager extends AbstractManager {
	
	private Map<String, Map<String, String>> translations;
	
	public LanguageManager(ResourceManager manager) {
		super(manager);
		this.translations = new HashMap<>();
	}
	
	@Override
	protected void loadDirectory(String namespace, ResourcePackFile root) {
		if (!root.exists() || !root.isDirectory()) {
			throw new IllegalArgumentException(root.getAbsolutePath() + " is not a directory.");
		}
		JSONParser parser = new JSONParser();
		Map<String, Map<String, String>> translations = new HashMap<>();
		for (ResourcePackFile file : root.listFilesRecursively()) {
			if (file.getName().endsWith(".json")) {
				try {
					InputStreamReader reader = new InputStreamReader(new BOMInputStream(file.getInputStream()), StandardCharsets.UTF_8);
					JSONObject json = (JSONObject) parser.parse(reader);
					reader.close();
					Map<String, String> mapping = new HashMap<>();
					for (Object obj : json.keySet()) {
						try {
							String key = (String) obj;
							mapping.put(key, (String) json.get(key));
						} catch (Exception e) {}
					}
					translations.put(file.getName().substring(0, file.getName().lastIndexOf(".")), mapping);
				} catch (Exception e) {
					new RuntimeException("Unable to load language " + file.getAbsolutePath(), e).printStackTrace();
				}
			}
		}
		this.translations.putAll(translations);
	}
	
	public void reloadLanguages() {
		LanguageUtils.clearPluginTranslations(InteractiveChatDiscordSrvAddon.plugin);
		for (Entry<String, Map<String, String>> entry : translations.entrySet()) {
			LanguageUtils.loadPluginTranslations(InteractiveChatDiscordSrvAddon.plugin, entry.getKey(), entry.getValue());
		}
	}
    
}
