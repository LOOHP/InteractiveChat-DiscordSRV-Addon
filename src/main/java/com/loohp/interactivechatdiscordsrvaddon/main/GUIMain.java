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

import com.loohp.interactivechat.libs.com.loohp.yamlconfiguration.ConfigurationSection;
import com.loohp.interactivechat.libs.com.loohp.yamlconfiguration.YamlConfiguration;
import com.loohp.interactivechat.libs.org.json.simple.JSONObject;
import com.loohp.interactivechat.registry.Registry;
import com.loohp.interactivechat.updater.Version;
import com.loohp.interactivechat.utils.FileUtils;
import com.loohp.interactivechat.utils.HTTPRequestUtils;
import com.loohp.interactivechatdiscordsrvaddon.libs.LibraryDownloadManager;
import com.loohp.interactivechatdiscordsrvaddon.registry.InteractiveChatRegistry;
import com.loohp.interactivechatdiscordsrvaddon.resources.ResourceDownloadManager;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;

public class GUIMain {

    private static final DecimalFormat FORMAT = new DecimalFormat("0.0");

    public static void launch(String[] args) {
        String title = "InteractiveChat DiscordSRV Addon Tools";
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            Enumeration<URL> enumeration = GUIMain.class.getClassLoader().getResources("plugin.yml");

            YamlConfiguration pluginYaml = new YamlConfiguration(enumeration.nextElement().openStream());

            String pluginName = pluginYaml.getString("name");
            String version = pluginYaml.getString("version");

            YamlConfiguration icPluginYaml = new YamlConfiguration(enumeration.nextElement().openStream());

            String icPluginName = icPluginYaml.getString("name");
            String icVersion = icPluginYaml.getString("version");

            String append;
            if (compatible()) {
                append = "Select one of the tools below";
            } else {
                append = "<p style=\"color:red;\"><b>These versions of InteractiveChat & InteractiveChat DiscordSRV Addon are incompatible!<br>Please Upgrade!</b></p>";
            }

            BufferedImage image = ImageIO.read(GUIMain.class.getClassLoader().getResourceAsStream("icon.png"));
            Icon icon = new ImageIcon(image);

            title = pluginName + " v" + version + " Tools";

            String message = "<html><center><b>You are running " + pluginName + " v" + version + "</b><br>" +
                "(Paired with " + icPluginName + " v" + icVersion + ")<br>" + append +
                "<html/>";

            JLabel messageLabel = createLabel(message, 15);
            messageLabel.setHorizontalAlignment(SwingConstants.CENTER);

            BufferedImage resizedIcon = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = resizedIcon.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g.drawImage(image, 0, 0, 32, 32, null);
            g.dispose();

            main:
            while (true) {
                int input = JOptionPane.showOptionDialog(null, messageLabel, title, JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, icon, new Object[] {"Check for Updates", "Validate Plugin Configs", "Generate Default Configs", "Download Assets", "Block Model Renderer (1.13+)", "Minecraft Font Renderer (1.13+)", "Visit Links"}, null);
                switch (input) {
                    case 0:
                        checkForUpdates(title, icon, version);
                        break;
                    case 1:
                        validConfigs(title, icon);
                        break;
                    case 2:
                        generateDefaultConfigs(title, icon);
                        break;
                    case 3:
                        downloadAssets(title, resizedIcon, icon);
                        break;
                    case 4:
                        blockModelRenderer(title, resizedIcon, icon);
                        break main;
                    case 5:
                        minecraftFontRenderer(title, resizedIcon, icon);
                        break main;
                    case 6:
                        visitLinks(title, icon);
                        break;
                    default:
                        break main;
                }
            }
        } catch (Throwable e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            Toolkit.getDefaultToolkit().beep();
            JOptionPane.showMessageDialog(null, createLabel("An error occurred!\n" + sw, 13, Color.RED), title, JOptionPane.ERROR_MESSAGE);
        }
    }

    protected static void visitLinks(String title, Icon icon) throws URISyntaxException, IOException {
        int input = JOptionPane.showOptionDialog(null, createLabel("Visit links through buttons below!", 15), title, JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, icon, new Object[] {"SpigotMC", "GitHub", "Discord Server", "Build Server"}, null);
        if (Desktop.isDesktopSupported()) {
            Desktop dt = Desktop.getDesktop();
            switch (input) {
                case -1:
                    break;
                case 0:
                    dt.browse(new URI("https://www.spigotmc.org/resources/83917/"));
                    break;
                case 1:
                    dt.browse(new URI("https://github.com/LOOHP/InteractiveChat-DiscordSRV-Addon"));
                    break;
                case 2:
                    dt.browse(new URI("http://dev.discord.loohpjames.com"));
                    break;
                case 3:
                    dt.browse(new URI("https://ci.loohpjames.com"));
                    break;
            }
        }
    }

    protected static void checkForUpdates(String title, Icon icon, String localPluginVersion) throws URISyntaxException, IOException {
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
            input = JOptionPane.showOptionDialog(null, createLabel("There is a new version available! (" + currentDevBuild + ")\nLocal version: " + localPluginVersion, 15), title, JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, icon, new Object[] {"OK", "Download Link"}, null);
        } else if (currentDevBuild.compareTo(devBuild) < 0) { //dev build update
            input = JOptionPane.showOptionDialog(null, createLabel("There is a new DEV build available! (" + currentDevBuild + ")\nLocal version: " + localPluginVersion, 15), title, JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, icon, new Object[] {"OK", "Download Link"}, null);
        } else { //latest
            JOptionPane.showMessageDialog(null, createLabel("You are already running the latest version! (" + localPluginVersion + ")", 15), title, JOptionPane.INFORMATION_MESSAGE, icon);
            input = 0;
        }
        if (input == 1) {
            if (Desktop.isDesktopSupported()) {
                Desktop dt = Desktop.getDesktop();
                dt.browse(new URI("https://ci.loohpjames.com/job/InteractiveChat-DiscordSRV-Addon/"));
            }
        }
    }

    protected static void validConfigs(String title, Icon icon) throws IOException {
        File folder = new File("InteractiveChatDiscordSrvAddon");
        if (!folder.exists() || !folder.isDirectory()) {
            Toolkit.getDefaultToolkit().beep();
            JOptionPane.showMessageDialog(null, createLabel("Error: Plugin folder not found", 15, Color.RED), title, JOptionPane.ERROR_MESSAGE, icon);
            return;
        }
        Map<File, List<String>> results = new LinkedHashMap<>();
        for (File file : folder.listFiles()) {
            String fileName = file.getName();
            if (fileName.endsWith(".yml")) {
                YamlConfiguration yaml = new YamlConfiguration(new FileInputStream(file));
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
        JOptionPane.showMessageDialog(null, createLabel(message.toString(), 13), title, JOptionPane.INFORMATION_MESSAGE, icon);
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

    protected static void generateDefaultConfigs(String title, Icon icon) throws IOException {
        File folder = new File("InteractiveChatDiscordSrvAddon", "generated");
        FileUtils.removeFolderRecursively(folder);
        folder.mkdirs();
        FileUtils.copy(GUIMain.class.getClassLoader().getResourceAsStream("config.yml"), new File(folder, "config.yml"));
        if (folder != null) {
            JOptionPane.showMessageDialog(null, createLabel("Files saved at: " + folder.getAbsolutePath(), 15), title, JOptionPane.INFORMATION_MESSAGE, icon);
        }
    }

    protected static void downloadAssets(String title, BufferedImage image, Icon icon) {
        File defaultAssetsFolder = new File("InteractiveChatDiscordSrvAddon/built-in", "Default");
        defaultAssetsFolder.mkdirs();
        File libsFolder = new File("InteractiveChatDiscordSrvAddon", "libs");
        libsFolder.mkdirs();

        JPanel panel = new JPanel();
        panel.add(GUIMain.createLabel("Select Minecraft Version: ", 13));
        JComboBox<String> options = new JComboBox<>();
        for (String version : ResourceDownloadManager.getMinecraftVersions()) {
            options.addItem(version);
        }
        panel.add(options);
        int result = JOptionPane.showOptionDialog(null, panel, title, JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, icon, null, null);
        if (result < 0) {
            return;
        }

        ResourceDownloadManager resourceDownloadManager = new ResourceDownloadManager((String) options.getSelectedItem(), defaultAssetsFolder);
        LibraryDownloadManager libraryDownloadManager = new LibraryDownloadManager(libsFolder);
        JFrame frame = new JFrame(title);
        frame.setIconImage(image);
        frame.setSize(800, 175);
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        panel.setLayout(new GridLayout(0, 1));

        JLabel label = GUIMain.createLabel("<html>Downloading Assets:<html/>", 13);
        label.setSize(800, 125);
        panel.add(label);

        JProgressBar progressBar = new JProgressBar(0, 10000);
        panel.add(progressBar);

        frame.add(panel, BorderLayout.CENTER);
        frame.setResizable(false);
        frame.setVisible(true);

        CompletableFuture<Void> future = new CompletableFuture<>();
        new Thread(() -> {
            resourceDownloadManager.downloadResources((type, fileName, percentage) -> {
                switch (type) {
                    case CLIENT_DOWNLOAD:
                        label.setText("<html>Downloading Assets:<br>Downloading client jar<html/>");
                        break;
                    case EXTRACT:
                        label.setText("<html>Downloading Assets:<br>Extracting " + fileName + "<html/>");
                        break;
                    case DOWNLOAD:
                        label.setText("<html>Downloading Assets:<br>Downloading " + fileName + "<html/>");
                        progressBar.setValue(Math.min(9999, (int) (percentage * 100)));
                        break;
                    case DONE:
                        label.setText("<html>Done!<html/>");
                        break;
                }
            });
            libraryDownloadManager.downloadLibraries((downloadResult, jarName) -> {
                if (downloadResult) {
                    label.setText("<html>Downloaded library \"" + jarName + "\"<html/>");
                }
            });
            future.complete(null);
        }).start();
        future.join();
        progressBar.setValue(9999);

        JOptionPane.showMessageDialog(null, createLabel("Assets saved at: " + defaultAssetsFolder.getAbsolutePath(), 15), title, JOptionPane.INFORMATION_MESSAGE, icon);

        frame.setVisible(false);
        frame.dispose();
    }

    protected static void blockModelRenderer(String title, BufferedImage image, Icon icon) throws IllegalAccessException {
        new BlockModelRenderer(title, image, icon);
    }

    protected static void minecraftFontRenderer(String title, BufferedImage image, Icon icon) {
        new MinecraftFontRenderer(title, image, icon);
    }

    protected static JLabel createLabel(String message, float fontSize) {
        return createLabel(message, fontSize, Color.BLACK);
    }

    protected static JLabel createLabel(String message, float fontSize, Color color) {
        JLabel label = new JLabel("<html>" + message.replace("\n", "<br>") + "<html/>");
        label.setFont(label.getFont().deriveFont(Font.PLAIN).deriveFont(fontSize));
        label.setForeground(color);
        return label;
    }

    protected static boolean compatible() {
        try {
            return Registry.class.getField("INTERACTIVE_CHAT_DISCORD_SRV_ADDON_COMPATIBLE_VERSION").getInt(null) == InteractiveChatRegistry.class.getField("INTERACTIVE_CHAT_DISCORD_SRV_ADDON_COMPATIBLE_VERSION").getInt(null);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
            return false;
        }
    }

}
