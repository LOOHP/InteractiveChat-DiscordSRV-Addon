package com.loohp.interactivechatdiscordsrvaddon.main;

import javax.swing.JOptionPane;
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

    public static void main(String[] args) {
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
                Toolkit.getDefaultToolkit().beep();
                JOptionPane.showMessageDialog(null, "InteractiveChat.jar is required run InteractiveChat DiscordSRV Addon tools.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void run(String[] args) {
        if (GraphicsEnvironment.isHeadless() || Arrays.asList(args).contains("--nogui")) {
            CMLMain.launch(args);
        } else {
            GUIMain.launch(args);
        }
    }

}
