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

package com.loohp.interactivechatdiscordsrvaddon.main;

import com.loohp.interactivechat.libs.org.json.simple.JSONObject;
import com.loohp.interactivechat.libs.org.simpleyaml.configuration.ConfigurationSection;
import com.loohp.interactivechat.libs.org.simpleyaml.configuration.file.YamlFile;
import com.loohp.interactivechat.registry.Registry;
import com.loohp.interactivechat.updater.Version;
import com.loohp.interactivechat.utils.FileUtils;
import com.loohp.interactivechat.utils.HTTPRequestUtils;
import com.loohp.interactivechatdiscordsrvaddon.libs.LibraryDownloadManager;
import com.loohp.interactivechatdiscordsrvaddon.registry.InteractiveChatRegistry;
import com.loohp.interactivechatdiscordsrvaddon.resources.ResourceDownloadManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;

public class CMLMain {

    protected static BufferedReader IN = new BufferedReader(new InputStreamReader(System.in));

    public static void launch(String[] args) {
        try {
            Enumeration<URL> enumeration = GUIMain.class.getClassLoader().getResources("plugin.yml");

            YamlFile pluginYaml = new YamlFile();
            pluginYaml.options().useComments(true);
            pluginYaml.load( enumeration.nextElement().openStream());

            String pluginName = pluginYaml.getString("name");
            String version = pluginYaml.getString("version");

            YamlFile icPluginYaml = new YamlFile();
            pluginYaml.options().useComments(true);
            pluginYaml.load( enumeration.nextElement().openStream());

            String icPluginName = icPluginYaml.getString("name");
            String icVersion = icPluginYaml.getString("version");

            System.out.println("Starting " + pluginName + " v" + version + " Tools...");
            System.out.println();
            main:
            while (true) {
                System.out.println("You are running " + pluginName + " v" + version);
                System.out.println("Paired with " + icPluginName + " v" + icVersion);
                System.out.println();
                System.out.println("Links:");
                System.out.println("SpigotMC: \"https://www.spigotmc.org/resources/83917/\"");
                System.out.println("GitHub: \"https://github.com/LOOHP/InteractiveChat-DiscordSRV-Addon\"");
                System.out.println("Discord: \"https://loohpjames.com/dev-discord\"");
                System.out.println("Build Server: \"https://ci.loohpjames.com\"");
                if (Registry.INTERACTIVE_CHAT_DISCORD_SRV_ADDON_COMPATIBLE_VERSION != InteractiveChatRegistry.INTERACTIVE_CHAT_DISCORD_SRV_ADDON_COMPATIBLE_VERSION) {
                    System.out.println();
                    System.out.println("These versions of InteractiveChat & InteractiveChat DiscordSRV Addon are incompatible! Please Upgrade!");
                    System.out.println("These versions of InteractiveChat & InteractiveChat DiscordSRV Addon are incompatible! Please Upgrade!");
                    System.out.println("These versions of InteractiveChat & InteractiveChat DiscordSRV Addon are incompatible! Please Upgrade!");
                }
                System.out.println();
                System.out.println("Select one of the tools by typing in their corresponding number");

                System.out.println("1. Check for Updates   2. Validate Plugin Configs   3.Generate Default Configs   4. Download Assets    5. Exit");

                String input = IN.readLine();
                switch (input) {
                    case "1":
                        checkForUpdates(version);
                        break;
                    case "2":
                        validConfigs();
                        break;
                    case "3":
                        generateDefaultConfigs();
                        break;
                    case "4":
                        downloadAssets();
                        break;
                    default:
                        break main;
                }
            }
        } catch (Throwable e) {
            System.err.println("An error occurred!");
            e.printStackTrace();
        }
    }

    protected static void checkForUpdates(String localPluginVersion) throws URISyntaxException, IOException {
        JSONObject response = (JSONObject) HTTPRequestUtils.getJSONResponse("https://api.loohpjames.com/spigot/data").get("InteractiveChat-DiscordSRV-Addon");
        String spigotPluginVersion = (String) ((JSONObject) response.get("latestversion")).get("release");
        String devBuildVersion = (String) ((JSONObject) response.get("latestversion")).get("devbuild");
        int spigotPluginId = (int) (long) ((JSONObject) response.get("spigotmc")).get("pluginid");
        int posOfThirdDot = localPluginVersion.indexOf(".", localPluginVersion.indexOf(".", localPluginVersion.indexOf(".") + 1) + 1);
        Version currentDevBuild = new Version(localPluginVersion);
        Version currentRelease = new Version(localPluginVersion.substring(0, posOfThirdDot >= 0 ? posOfThirdDot : localPluginVersion.length()));
        Version spigotmc = new Version(spigotPluginVersion);
        Version devBuild = new Version(devBuildVersion);
        int input;
        if (currentRelease.compareTo(spigotmc) < 0) { //update
            System.out.println("There is a new version available! (" + currentDevBuild + ")\nLocal version: " + localPluginVersion + "");
            System.out.println("You can download a new build at: https://ci.loohpjames.com/job/InteractiveChat-DiscordSRV-Addon/");
        } else if (currentDevBuild.compareTo(devBuild) < 0) { //dev build update
            System.out.println("There is a new DEV build available! (" + currentDevBuild + ")\nLocal version: " + localPluginVersion);
            System.out.println("You can download a new build at: https://ci.loohpjames.com/job/InteractiveChat-DiscordSRV-Addon/");
        } else { //latest
            System.out.println("You are already running the latest version! (" + localPluginVersion + ")");
        }
    }

    protected static void validConfigs() throws IOException {
        File folder = new File("InteractiveChatDiscordSrvAddon");
        if (!folder.exists() || !folder.isDirectory()) {
            System.out.println("Error: Plugin folder not found");
            return;
        }
        Map<File, List<String>> results = new LinkedHashMap<>();
        for (File file : folder.listFiles()) {
            String fileName = file.getName();
            if (fileName.endsWith(".yml")) {
                YamlFile yaml = new YamlFile();
                yaml.options().useComments(true);
                yaml.load( Files.newInputStream(file.toPath()));
                results.put(file, validateConfigurationSection("", yaml));
            }
        }
        StringBuilder message = new StringBuilder("Validation Results: (Plugin Folder: " + folder.getAbsolutePath() + ")\n");
        for (Entry<File, List<String>> entry : results.entrySet()) {
            String fileName = entry.getKey().getName();
            List<String> errors = entry.getValue();
            message.append("\n").append(fileName).append(": ");
            if (errors.isEmpty()) {
                message.append("Valid!\n");
            } else {
                message.append("\n");
                for (String error : errors) {
                    message.append(error).append("\n");
                }
            }
        }
        message.append("\nNote that a valid config doesn't mean REGEX are valid.");
        System.out.println(message);
    }

    protected static List<String> validateConfigurationSection(String currentPath, ConfigurationSection section) {
        List<String> errors = new LinkedList<>();
        try {
            for (String key : section.getKeys(false)) {
                String path = currentPath.isEmpty() ? key : currentPath + "." + key;
                try {
                    Object value = section.get(key);
                    if (value instanceof ConfigurationSection) {
                        errors.addAll(validateConfigurationSection(path, (ConfigurationSection) value));
                    }
                } catch (Throwable e) {
                    errors.add("Failed to parse option around: " + path);
                }
            }
        } catch (Throwable e) {
            errors.add("Failed to parse option around: " + currentPath);
        }
        return errors;
    }

    protected static void generateDefaultConfigs() throws IOException {
        File folder = new File("InteractiveChatDiscordSrvAddon", "generated");
        FileUtils.removeFolderRecursively(folder);
        folder.mkdirs();
        FileUtils.copy(GUIMain.class.getClassLoader().getResourceAsStream("config.yml"), new File(folder, "config.yml"));
        if (folder != null) {
            System.out.println("Files saved at: " + folder.getAbsolutePath());
        }
    }

    protected static void downloadAssets() throws IOException {
        File defaultAssetsFolder = new File("InteractiveChatDiscordSrvAddon/built-in", "Default");
        defaultAssetsFolder.mkdirs();
        File libsFolder = new File("InteractiveChatDiscordSrvAddon", "libs");
        libsFolder.mkdirs();

        System.out.println("Available Minecraft Versions:");
        for (String version : ResourceDownloadManager.getMinecraftVersions()) {
            System.out.println(version);
        }
        System.out.println();
        System.out.println("Select Minecraft Version: (Type the version string)");
        String input;
        while (true) {
            input = IN.readLine();
            if (ResourceDownloadManager.getMinecraftVersions().contains(input)) {
                break;
            }
            System.out.println("That is not a valid Minecraft version!");
            System.out.println();
        }

        ResourceDownloadManager downloadManager = new ResourceDownloadManager(input, defaultAssetsFolder);
        LibraryDownloadManager libraryDownloadManager = new LibraryDownloadManager(libsFolder);

        CompletableFuture<Void> future = new CompletableFuture<>();
        new Thread(() -> {
            downloadManager.downloadResources((type, fileName, percentage) -> {
                switch (type) {
                    case CLIENT_DOWNLOAD:
                        if (percentage == 0.0) {
                            System.out.println("Downloading client jar");
                        }
                        break;
                    case EXTRACT:
                        System.out.println("Extracting " + fileName);
                        break;
                    case DOWNLOAD:
                        System.out.println("Downloading " + fileName);
                        break;
                    case DONE:
                        System.out.println("Done!");
                        break;
                }
            });
            libraryDownloadManager.downloadLibraries((downloadResult, jarName, percentage) -> {
                if (downloadResult) {
                    System.out.println("Downloaded library \"" + jarName + "\"");
                }
            });
            future.complete(null);
        }).start();
        future.join();

        System.out.println("Assets saved at: " + defaultAssetsFolder.getAbsolutePath());
    }

}
