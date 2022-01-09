package com.loohp.interactivechatdiscordsrvaddon.resource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipEntry;

import com.loohp.interactivechat.libs.net.kyori.adventure.text.Component;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.format.NamedTextColor;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import com.loohp.interactivechat.libs.org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import com.loohp.interactivechat.libs.org.apache.commons.io.input.BOMInputStream;
import com.loohp.interactivechat.libs.org.json.simple.JSONObject;
import com.loohp.interactivechat.libs.org.json.simple.parser.JSONParser;
import com.loohp.interactivechat.utils.FileUtils;
import com.loohp.interactivechat.utils.InteractiveChatComponentSerializer;
import com.loohp.interactivechat.utils.JsonUtils;
import com.loohp.interactivechatdiscordsrvaddon.resource.fonts.FontManager;
import com.loohp.interactivechatdiscordsrvaddon.resource.languages.LanguageManager;
import com.loohp.interactivechatdiscordsrvaddon.resource.models.ModelManager;
import com.loohp.interactivechatdiscordsrvaddon.resource.textures.TextureManager;

public class ResourceManager {
	
	private List<ResourcePackInfo> resourcePackInfo;
	private ModelManager modelManager;
	private TextureManager textureManager;
	private FontManager fontManager;
	private LanguageManager languageManager;
	
	public ResourceManager() {
		this.resourcePackInfo = new LinkedList<>();
		this.modelManager = new ModelManager(this);
		this.textureManager = new TextureManager(this);
		this.fontManager = new FontManager(this);
		this.languageManager = new LanguageManager(this);
	}
	
	public ResourcePackInfo loadResources(String resourcePackName, File resourcePack) {
		extractIfNotFound(resourcePack);
		if (!resourcePack.exists() || !resourcePack.isDirectory()) {
			new IllegalArgumentException(resourcePack.getAbsolutePath() + " is not a directory nor is a zip file.").printStackTrace();
			ResourcePackInfo info = new ResourcePackInfo(resourcePackName, "Resource Pack is not a directory nor a zip file.");
			resourcePackInfo.add(0, info);
			return info;
		}
		File packMcmeta = new File(resourcePack, "pack.mcmeta");
		if (!packMcmeta.exists()) {
			new RuntimeException(resourcePackName + " does not have a pack.mcmeta").printStackTrace();
			ResourcePackInfo info = new ResourcePackInfo(resourcePackName, "pack.mcmeta not found");
			resourcePackInfo.add(0, info);
			return info;
		}
		
		JSONObject json;
		try (InputStreamReader reader = new InputStreamReader(new BOMInputStream(new FileInputStream(packMcmeta)), StandardCharsets.UTF_8)) {
			json = (JSONObject) new JSONParser().parse(reader);
		} catch (Throwable e) {
			new RuntimeException("Unable to read pack.mcmeta for " + resourcePackName, e).printStackTrace();
			ResourcePackInfo info = new ResourcePackInfo(resourcePackName, "Unable to read pack.mcmeta");
			resourcePackInfo.add(0, info);
			return info;
		}
		
		int format;
		Component description = null;
		try {
			JSONObject packJson = (JSONObject) json.get("pack");
			format = ((Number) packJson.get("pack_format")).intValue();
			String rawDescription = packJson.get("description").toString();
			if (JsonUtils.isValid(rawDescription)) {
				try {
					description = InteractiveChatComponentSerializer.gson().deserialize(rawDescription);
				} catch (Exception e) {
					description = null;
				}
			}
			if (description == null) {
				description = LegacyComponentSerializer.legacySection().deserialize(rawDescription);
			}
			if (description.color() == null) {
				description = description.color(NamedTextColor.GRAY);
			}
		} catch (Exception e) {
			new RuntimeException("Invalid pack.mcmeta for " + resourcePackName, e).printStackTrace();
			ResourcePackInfo info = new ResourcePackInfo(resourcePackName, "Invalid pack.mcmeta");
			resourcePackInfo.add(0, info);
			return info;
		}
		
		File assetsFolder = new File(resourcePack, "assets");
		try {
			loadAssets(assetsFolder);
		} catch (Exception e) {
			new RuntimeException("Unable to load assets for " + resourcePackName, e).printStackTrace();
			ResourcePackInfo info = new ResourcePackInfo(resourcePackName, false, "Unable to load assets", format, description);
			resourcePackInfo.add(0, info);
			return info;
		}
		
		ResourcePackInfo info = new ResourcePackInfo(resourcePackName, true, null, format, description);
		resourcePackInfo.add(0, info);
		return info;
	}
	
	private void loadAssets(File assetsFolder) throws Exception {
		if (!assetsFolder.exists() || !assetsFolder.isDirectory()) {
			throw new IllegalArgumentException(assetsFolder.getAbsolutePath() + " is not a directory.");
		}
		for (File folder : assetsFolder.listFiles()) {
			if (folder.isDirectory()) {
				String namespace = folder.getName();
				File models = new File(folder, "models");
				if (models.exists() && models.isDirectory()) {
					modelManager.loadDirectory(namespace, models);
				}
				File textures = new File(folder, "textures");
				if (textures.exists() && textures.isDirectory()) {
					textureManager.loadDirectory(namespace, textures);
				}
				File font = new File(folder, "font");
				if (font.exists() && font.isDirectory()) {
					fontManager.loadDirectory(namespace, font);
				}
				File lang = new File(folder, "lang");
				if (lang.exists() && lang.isDirectory()) {
					languageManager.loadDirectory(namespace, lang);
				}
			}
		}
		fontManager.reloadFonts();
		languageManager.reloadLanguages();
	}

	public List<ResourcePackInfo> getResourcePackInfo() {
		return Collections.unmodifiableList(resourcePackInfo);
	}

	public void setResourcePackInfo(List<ResourcePackInfo> resourcePackInfo) {
		this.resourcePackInfo = resourcePackInfo;
	}

	public ModelManager getModelManager() {
		return modelManager;
	}

	public TextureManager getTextureManager() {
		return textureManager;
	}

	public FontManager getFontManager() {
		return fontManager;
	}
	
	public LanguageManager getLanguageManager() {
		return languageManager;
	}

	private static boolean extractIfNotFound(File resourceFile) {
		if (resourceFile.exists()) {
			return true;
		} else {
			resourceFile.mkdirs();
			File zipFile = new File(resourceFile.getParent(), resourceFile.getName() + ".zip");
			if (zipFile.exists()) {
				try (ZipArchiveInputStream zip = new ZipArchiveInputStream(new FileInputStream(zipFile), StandardCharsets.UTF_8.toString(), false, true, true)) {
					while (true) {
						ZipEntry entry = zip.getNextZipEntry();
						if (entry == null) {
							break;
						}
						String name = entry.getName();
						if (entry.isDirectory()) {
							File folder = new File(resourceFile, name).getParentFile();
							folder.mkdirs();
						} else {
							String fileName = getEntryName(name);
							
							ByteArrayOutputStream baos = new ByteArrayOutputStream();
							byte[] byteChunk = new byte[4096];
							int n;
							while ((n = zip.read(byteChunk)) > 0) {
								baos.write(byteChunk, 0, n);
							}
							byte[] currentEntry = baos.toByteArray();
							
							File folder = new File(resourceFile, name).getParentFile();
							folder.mkdirs();
							File file = new File(folder, fileName);
							if (file.exists()) {
								file.delete();
							}
							FileUtils.copy(new ByteArrayInputStream(currentEntry), file);
						}
					}
					return true;
				} catch (Exception e) {
					e.printStackTrace();
					return false;
				}
			} else {
				return false;
			}
		}
	}
	
	private static String getEntryName(String name) {
		int pos = name.lastIndexOf("/");
		if (pos >= 0) {
			return name.substring(pos + 1);
		}
		pos = name.lastIndexOf("\\");
		if (pos >= 0) {
			return name.substring(pos + 1);
		}
		return name;
	}

}
