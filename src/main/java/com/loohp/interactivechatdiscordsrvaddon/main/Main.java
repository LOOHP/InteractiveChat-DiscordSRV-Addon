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

import com.loohp.interactivechatdiscordsrvaddon.libs.LibraryLoader;

import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class Main {

    public static void main(String[] args) throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        boolean found = false;
        for (File file : new File(".").listFiles()) {
            String fileName = file.getName();
            if (fileName.endsWith(".jar")) {
                try {
                    ZipFile zip = new ZipFile(file);
                    ZipEntry entry = zip.getEntry("plugin.yml");
                    if (entry != null) {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(zip.getInputStream(entry), StandardCharsets.UTF_8));
                        String line;
                        while ((line = reader.readLine()) != null) {
                            if (line.trim().startsWith("name:") && line.trim().endsWith("InteractiveChat")) {
                                found = true;
                                break;
                            }
                        }
                        reader.close();
                    }
                    zip.close();
                } catch (IOException e) {
                }
            }
            if (found) {
                try {
                    URLClassLoader child = new URLClassLoader(new URL[] {Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().toURL(), file.toURI().toURL()}, null);
                    Class<?> classToLoad = Class.forName("com.loohp.interactivechat.main.Main", true, child);
                    Method method = classToLoad.getMethod("mainInteractiveChatDiscordSrvAddon", String[].class);
                    method.invoke(null, new Object[] {args});
                } catch (MalformedURLException | ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException | URISyntaxException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
        if (!found) {
            if (GraphicsEnvironment.isHeadless() || Arrays.asList(args).contains("--nogui")) {
                System.out.println("InteractiveChat.jar is required run addon tools.");
            } else {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                Toolkit.getDefaultToolkit().beep();
                JOptionPane.showMessageDialog(null, "InteractiveChat.jar is required run InteractiveChat DiscordSRV Addon tools.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void run(String[] args) {
        LibraryLoader.loadLibraries(new File("InteractiveChatDiscordSrvAddon", "libs"));
        if (GraphicsEnvironment.isHeadless() || Arrays.asList(args).contains("--nogui")) {
            CMLMain.launch(args);
        } else {
            GUIMain.launch(args);
        }
    }

}
