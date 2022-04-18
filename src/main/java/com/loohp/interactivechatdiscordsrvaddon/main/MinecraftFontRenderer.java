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

import com.loohp.interactivechat.libs.com.loohp.yamlconfiguration.YamlConfiguration;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.Component;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.format.NamedTextColor;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import com.loohp.interactivechat.utils.ChatColorUtils;
import com.loohp.interactivechat.utils.ComponentStyling;
import com.loohp.interactivechatdiscordsrvaddon.graphics.ImageUtils;
import com.loohp.interactivechatdiscordsrvaddon.libs.URLClassLoaderAccess;
import com.loohp.interactivechatdiscordsrvaddon.registry.ResourceRegistry;
import com.loohp.interactivechatdiscordsrvaddon.resources.ResourceDownloadManager;
import com.loohp.interactivechatdiscordsrvaddon.resources.ResourceManager;
import com.loohp.interactivechatdiscordsrvaddon.resources.ResourcePackInfo;
import com.loohp.interactivechatdiscordsrvaddon.resources.ResourcePackType;
import com.loohp.interactivechatdiscordsrvaddon.resources.languages.LanguageManager;
import com.loohp.interactivechatdiscordsrvaddon.resources.textures.TextureAnimation;
import com.loohp.interactivechatdiscordsrvaddon.resources.textures.TextureMeta;
import com.loohp.interactivechatdiscordsrvaddon.resources.textures.TextureProperties;
import com.loohp.interactivechatdiscordsrvaddon.resources.textures.TextureResource;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class MinecraftFontRenderer extends JFrame {

    public static final Pattern COLOR_HEX_PATTERN = Pattern.compile("^#[0-9a-fA-F]{6}$");

    private static final URLClassLoaderAccess LOADER_ACCESS = URLClassLoaderAccess.create((URLClassLoader) GUIMain.class.getClassLoader());

    private String title;
    private BufferedImage image;
    private Icon icon;

    private JTextArea textAreaInput;
    private JTextArea textAreaResources;
    private JButton reloadResourcesButton;
    private JButton saveButton;
    private JCheckBox legacyTextCheckBox;
    private JSpinner spinnerOffsetX;
    private JSpinner spinnerOffsetY;
    private JPanel imagePanel;
    private JPanel panel;
    private JProgressBar resourceBar;
    private JTextField backgroundColorTextField;
    private JComboBox<LanguageData> comboBoxLanguages;
    private JButton buttonDownloadLanguages;
    private JScrollPane scrollPaneTextInput;
    private JCheckBox glowingTextBox;

    private ResourceManager resourceManager;

    private AtomicReference<BufferedImage> renderedImage;
    private AtomicReference<List<Component>> renderingComponents;
    private Color backgroundColor;
    private File lastSavedLocation;

    private ExecutorService executorService;
    private ReentrantLock resourceLock;
    private ReentrantLock updateTextImageLock;
    private ReentrantLock repaintLock;

    public MinecraftFontRenderer(String title, BufferedImage image, Icon icon) {
        this.title = title;
        this.image = image;
        this.icon = icon;
        this.resourceManager = null;
        this.renderedImage = new AtomicReference<>();
        this.renderingComponents = new AtomicReference<>();
        this.backgroundColor = Color.BLACK;
        this.lastSavedLocation = new File(".");
        $$$setupUI$$$();
        this.executorService = Executors.newSingleThreadExecutor();
        this.resourceLock = new ReentrantLock(true);
        this.updateTextImageLock = new ReentrantLock(true);
        this.repaintLock = new ReentrantLock(true);

        for (File jarFile : new File("InteractiveChatDiscordSrvAddon", "libs").listFiles()) {
            String jarName = jarFile.getName();
            if (jarName.endsWith(".jar")) {
                try {
                    LOADER_ACCESS.addURL(jarFile.toURI().toURL());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        setContentPane(panel);
        setTitle(title + " - Minecraft Font Renderer");
        setSize(1300, 1000);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setIconImage(image);

        resourceBar.setMaximum(10000);

        backgroundColorTextField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                String hex = backgroundColorTextField.getText();
                if (!hex.startsWith("#")) {
                    hex = "#" + hex;
                }
                if (COLOR_HEX_PATTERN.matcher(hex).matches()) {
                    backgroundColor = hex2Rgb(hex);
                    imagePanel.repaint();
                }
                backgroundColorTextField.setText(rgb2Hex(backgroundColor).toUpperCase());
            }
        });
        backgroundColorTextField.addActionListener(e -> {
            String hex = backgroundColorTextField.getText();
            if (!hex.startsWith("#")) {
                hex = "#" + hex;
            }
            if (COLOR_HEX_PATTERN.matcher(hex).matches()) {
                backgroundColor = hex2Rgb(hex);
                imagePanel.repaint();
            }
            backgroundColorTextField.setText(rgb2Hex(backgroundColor).toUpperCase());
        });

        reloadResourcesButton.addActionListener(e -> {
            executorService.submit(() -> loadResources());
        });

        saveButton.addActionListener(e -> {
            executorService.submit(() -> saveImage());
        });

        textAreaInput.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                SwingUtilities.invokeLater(() -> {
                    executorService.submit(() -> {
                        updateTextComponent();
                        repaintTextImage();
                    });
                });
            }
        });
        spinnerOffsetX.addChangeListener(e -> scheduleTextReload());
        spinnerOffsetY.addChangeListener(e -> scheduleTextReload());

        legacyTextCheckBox.addActionListener(e -> {
            if (!textAreaInput.getText().isEmpty()) {
                if (legacyTextCheckBox.isSelected()) {
                    Toolkit.getDefaultToolkit().beep();
                    int input = JOptionPane.showOptionDialog(null, GUIMain.createLabel("Changing to legacy text will remove formatting!", 13, Color.BLACK), title, JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, new Object[] {"Continue", "Cancel"}, null);
                    if (input == 0) {
                        try {
                            textAreaInput.setText(LegacyComponentSerializer.builder().useUnusualXRepeatedCharacterHexFormat().character(ChatColorUtils.COLOR_CHAR).build().serialize(GsonComponentSerializer.gson().deserialize(textAreaInput.getText())).replace(ChatColorUtils.COLOR_CHAR, '&'));
                        } catch (Throwable ex) {
                            ex.printStackTrace();
                        }
                    } else {
                        legacyTextCheckBox.setSelected(false);
                    }
                } else {
                    try {
                        textAreaInput.setText(GsonComponentSerializer.gson().serialize(LegacyComponentSerializer.legacySection().deserialize(ChatColorUtils.translateAlternateColorCodes('&', textAreaInput.getText(), false, true, Collections.emptyList()))));
                    } catch (Throwable ex) {
                        ex.printStackTrace();
                    }
                }
            }
            scheduleTextReload();
        });

        buttonDownloadLanguages.addActionListener(e -> {
            executorService.submit(() -> {
                downloadAllLanguages(title, image, icon);
                loadResources();
            });
        });

        JPopupMenu menu = new JPopupMenu("Actions");
        JMenuItem copy = new JMenuItem("Copy image");
        copy.addActionListener(event -> copyImage());
        menu.add(copy);
        JMenuItem save = new JMenuItem("Save image as...");
        save.addActionListener(event -> saveImage());
        menu.add(save);

        imagePanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    menu.show(imagePanel, e.getX(), e.getY());
                }
            }
        });

        comboBoxLanguages.addActionListener(e -> scheduleTextReload());

        glowingTextBox.addActionListener(e -> scheduleTextReload());

        setLocationRelativeTo(null);
        setVisible(true);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentMoved(ComponentEvent e) {
                updateTextAreaInputSize();
            }

            @Override
            public void componentResized(ComponentEvent e) {
                updateTextAreaInputSize();
            }
        });

        executorService.submit(() -> loadResources());
    }

    private void createUIComponents() {
        spinnerOffsetX = new JSpinner(new SpinnerNumberModel(10, -8192, 8192, 1));
        spinnerOffsetY = new JSpinner(new SpinnerNumberModel(10, -8192, 8192, 1));
        imagePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(backgroundColor);
                g.fillRect(0, 0, getWidth(), getHeight());
                updateImage();
                BufferedImage image = renderedImage.get();
                if (image != null) {
                    g.drawImage(image, getWidth() / 2 - image.getWidth() / 2, getHeight() / 2 - image.getHeight() / 2, this);
                    saveButton.setEnabled(true);
                } else {
                    saveButton.setEnabled(false);
                }
            }
        };
    }

    private void scheduleTextReload() {
        executorService.submit(() -> {
            updateTextComponent();
            repaintTextImage();
        });
    }

    public void updateTextAreaInputSize() {
        int maxX = getWidth() - textAreaResources.getWidth();
        int y = textAreaInput.getHeight();
        if (textAreaInput.getWidth() > maxX) {
            textAreaInput.setMaximumSize(new Dimension(maxX, y));
            textAreaInput.setPreferredSize(new Dimension(maxX, y));
            textAreaInput.setSize(maxX, y);
        }
    }

    public void repaintTextImage() {
        try {
            if (!repaintLock.tryLock(0, TimeUnit.MILLISECONDS)) {
                return;
            }
        } catch (InterruptedException e) {
        }
        imagePanel.repaint();
        repaintLock.unlock();
    }

    public BufferedImage getRawEnchantedImage(BufferedImage source) {
        TextureResource resource = resourceManager.getTextureManager().getTexture(ResourceRegistry.MISC_TEXTURE_LOCATION + "enchanted_item_glint");
        BufferedImage tintOriginal = resource.getTexture();
        if (resource.hasTextureMeta()) {
            TextureMeta meta = resource.getTextureMeta();
            if (meta.hasProperties()) {
                TextureProperties properties = meta.getProperties();
                if (properties.isBlur()) {
                    tintOriginal = ImageUtils.applyGaussianBlur(tintOriginal);
                }
            }
            if (meta.hasAnimation()) {
                TextureAnimation animation = meta.getAnimation();
                if (animation.hasWidth() && animation.hasHeight()) {
                    tintOriginal = ImageUtils.copyAndGetSubImage(tintOriginal, 0, 0, animation.getWidth(), animation.getHeight());
                } else {
                    tintOriginal = ImageUtils.copyAndGetSubImage(tintOriginal, 0, 0, tintOriginal.getWidth(), tintOriginal.getWidth());
                }
            }
        }
        BufferedImage tintImage = new BufferedImage(source.getWidth(), source.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g3 = tintImage.createGraphics();
        g3.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g3.drawImage(tintOriginal, 0, 0, tintImage.getWidth() * 4, tintImage.getHeight() * 4, null);
        g3.dispose();
        return tintImage;
    }

    public BufferedImage getEnchantedImage(BufferedImage source) {
        return ImageUtils.additionNonTransparent(source, getRawEnchantedImage(source), ResourceRegistry.ENCHANTMENT_GLINT_FACTOR);
    }

    public Color hex2Rgb(String colorStr) {
        return new Color(Integer.valueOf(colorStr.substring(1, 3), 16), Integer.valueOf(colorStr.substring(3, 5), 16), Integer.valueOf(colorStr.substring(5, 7), 16));
    }

    public String rgb2Hex(Color color) {
        return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
    }

    public void loadResources() {
        try {
            if (!resourceLock.tryLock(0, TimeUnit.MILLISECONDS)) {
                return;
            }
        } catch (InterruptedException e) {
        }
        reloadResourcesButton.setEnabled(false);
        resourceBar.setValue(0);
        resourceBar.setVisible(true);
        textAreaResources.setText("Loading Resources...");
        updateTextAreaInputSize();

        List<String> resourceOrder;
        int valuePerPack;
        try {
            YamlConfiguration yaml = new YamlConfiguration(new FileInputStream("InteractiveChatDiscordSrvAddon/config.yml"));
            resourceOrder = yaml.getStringList("Resources.Order");
            Collections.reverse(resourceOrder);
            valuePerPack = (int) ((1.0 / (double) (resourceOrder.size() + 1)) * 10000);
        } catch (IOException e) {
            Toolkit.getDefaultToolkit().beep();
            JOptionPane.showMessageDialog(null, GUIMain.createLabel("There is an error while loading from config:\n" + e.getMessage(), 13, Color.RED), title, JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (resourceManager != null) {
            resourceManager.close();
        }

        PrintStream original = System.err;
        try {
            resourceManager = new ResourceManager(false);
            resourceManager.loadResources(new File("InteractiveChatDiscordSrvAddon/built-in", "Default"), ResourcePackType.BUILT_IN);
            resourceBar.setValue(valuePerPack);
            for (String resourceName : resourceOrder) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                System.setErr(new PrintStream(baos));
                try {
                    File resourcePackFile = new File("InteractiveChatDiscordSrvAddon/resourcepacks/" + resourceName);
                    ResourcePackInfo info = resourceManager.loadResources(resourcePackFile, ResourcePackType.LOCAL);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String error = baos.toString();
                if (!error.isEmpty()) {
                    ForkJoinPool.commonPool().execute(() -> JOptionPane.showMessageDialog(null, GUIMain.createLabel("There are errors while loading \"" + resourceName + "\":\n" + error, 13, Color.RED), title, JOptionPane.ERROR_MESSAGE));
                }
                resourceBar.setValue(resourceBar.getValue() + valuePerPack);
            }
        } catch (Throwable e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            Toolkit.getDefaultToolkit().beep();
            JOptionPane.showMessageDialog(null, GUIMain.createLabel("An error occurred!\n" + sw, 13, Color.RED), title, JOptionPane.ERROR_MESSAGE);
        }
        System.setErr(original);

        textAreaResources.setText("Loaded Resources:\n");
        for (ResourcePackInfo info : resourceManager.getResourcePackInfo()) {
            textAreaResources.append(" - " + info.getName());
            if (!info.getStatus()) {
                textAreaResources.append(" (Failed)");
            }
            textAreaResources.append("\n");
        }

        List<LanguageData> languages = getAllLanguageData(resourceManager.getLanguageManager());
        String lastSelected = comboBoxLanguages.getSelectedItem() == null ? null : ((LanguageData) comboBoxLanguages.getSelectedItem()).getLanguage();
        comboBoxLanguages.removeAllItems();
        for (LanguageData language : languages) {
            comboBoxLanguages.addItem(language);
        }
        Optional<LanguageData> optLanguage = languages.stream().filter(each -> each.getLanguage().equalsIgnoreCase(lastSelected == null ? "en_us" : lastSelected)).findFirst();
        if (optLanguage.isPresent()) {
            comboBoxLanguages.setSelectedItem(optLanguage.get());
        } else {
            comboBoxLanguages.setSelectedIndex(0);
        }
        reloadResourcesButton.setEnabled(true);
        resourceBar.setVisible(false);
        updateTextAreaInputSize();
        resourceLock.unlock();
    }

    private void updateTextComponent() {
        if (resourceManager == null) {
            return;
        }
        try {
            if (!updateTextImageLock.tryLock(0, TimeUnit.MILLISECONDS)) {
                return;
            }
        } catch (InterruptedException e) {
        }
        try {
            String text = textAreaInput.getText();
            Component component;
            if (text.isEmpty()) {
                component = Component.empty();
            } else {
                try {
                    if (legacyTextCheckBox.isSelected()) {
                        component = LegacyComponentSerializer.legacySection().deserialize(ChatColorUtils.translateAlternateColorCodes('&', text, false, true, Collections.emptyList()));
                    } else {
                        component = GsonComponentSerializer.gson().deserialize(text);
                    }
                } catch (Throwable e) {
                    component = PlainTextComponentSerializer.plainText().deserialize(e.getLocalizedMessage().replaceAll("\\t", "    ")).color(NamedTextColor.RED);
                }
            }

            List<Component> prints = new ArrayList<>(ComponentStyling.splitAtLineBreaks(component));
            renderingComponents.set(prints);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        updateTextImageLock.unlock();
    }

    private synchronized void updateImage() {
        List<Component> prints = renderingComponents.get();
        if (prints == null) {
            renderedImage.set(null);
            return;
        }

        int w = imagePanel.getWidth();
        int h = imagePanel.getHeight();
        BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

        int x = (int) spinnerOffsetX.getValue();
        int y = (int) spinnerOffsetY.getValue();
        LanguageData language = (LanguageData) comboBoxLanguages.getSelectedItem();
        for (Component print : prints) {
            if (y > h) {
                break;
            }
            if (glowingTextBox.isSelected()) {
                ImageUtils.printComponentGlowing(resourceManager, image, print, language.getLanguage(), false, x, y, 16);
            } else {
                ImageUtils.printComponent(resourceManager, image, print, language.getLanguage(), false, x, y, 16);
            }
            y += 20;
        }

        renderedImage.set(image);
    }

    public synchronized void copyImage() {
        BufferedImage image = renderedImage.get();
        if (image == null) {
            return;
        }
        TransferableImage trans = new TransferableImage(image);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(trans, null);
    }

    public synchronized void saveImage() {
        BufferedImage image = renderedImage.get();
        if (image == null) {
            return;
        }
        File file;
        while (true) {
            JFileChooser fileChooser = new JFileChooser(lastSavedLocation);
            fileChooser.setSelectedFile(new File(lastSavedLocation, "text.png"));
            int result = fileChooser.showSaveDialog(this);
            if (result != 0) {
                return;
            }
            file = fileChooser.getSelectedFile();
            if (file.getParentFile() != null) {
                lastSavedLocation = file.getParentFile();
            }
            if (file.exists()) {
                Toolkit.getDefaultToolkit().beep();
                int input = JOptionPane.showOptionDialog(null, GUIMain.createLabel(file.getName() + " already exists! Do you want to overwrite?", 15), title, JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, new Object[] {"Overwrite", "Cancel"}, null);
                if (input == 0) {
                    break;
                }
            } else {
                break;
            }
        }
        try {
            ImageIO.write(image, "png", file);
        } catch (IOException e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            Toolkit.getDefaultToolkit().beep();
            JOptionPane.showMessageDialog(null, GUIMain.createLabel("An error occurred!\n" + sw, 13, Color.RED), title, JOptionPane.ERROR_MESSAGE);
        }
    }

    protected void downloadAllLanguages(String title, BufferedImage image, Icon icon) {
        File defaultAssetsFolder = new File("InteractiveChatDiscordSrvAddon/built-in", "Default");
        defaultAssetsFolder.mkdirs();

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

        ResourceDownloadManager downloadManager = new ResourceDownloadManager((String) options.getSelectedItem(), defaultAssetsFolder);
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
            downloadManager.downloadLanguages((type, fileName, percentage) -> {
                switch (type) {
                    case DOWNLOAD:
                        label.setText("<html>Downloading Assets:<br>Downloading " + fileName + "<html/>");
                        progressBar.setValue(Math.min(9999, (int) (percentage * 100)));
                        break;
                    case DONE:
                        label.setText("<html>Done!<html/>");
                        break;
                }
            });
            future.complete(null);
        }).start();
        future.join();
        progressBar.setValue(9999);

        JOptionPane.showMessageDialog(null, GUIMain.createLabel("Assets saved at: " + defaultAssetsFolder.getAbsolutePath(), 15), title, JOptionPane.INFORMATION_MESSAGE, icon);

        frame.setVisible(false);
        frame.dispose();
    }

    private List<LanguageData> getAllLanguageData(LanguageManager languageManager) {
        return languageManager.getAvailableLanguages().stream().map(language -> {
            String name = languageManager.applyTranslations("language.name", language);
            String region = languageManager.applyTranslations("language.region", language);
            return new LanguageData(language, name.equalsIgnoreCase("language.name") ? null : name, region.equalsIgnoreCase("language.region") ? null : region);
        }).collect(Collectors.toList());
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        createUIComponents();
        panel = new JPanel();
        panel.setLayout(new com.loohp.interactivechat.libs.com.intellij.uiDesigner.core.GridLayoutManager(7, 13, new Insets(10, 10, 10, 10), -1, -1));
        final JLabel label1 = new JLabel();
        label1.setText("Text Input");
        panel.add(label1, new com.loohp.interactivechat.libs.com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 3, com.loohp.interactivechat.libs.com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.loohp.interactivechat.libs.com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.loohp.interactivechat.libs.com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.loohp.interactivechat.libs.com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        textAreaResources = new JTextArea();
        textAreaResources.setColumns(0);
        textAreaResources.setEditable(false);
        textAreaResources.setLineWrap(false);
        textAreaResources.setText("Loading Resources...");
        panel.add(textAreaResources, new com.loohp.interactivechat.libs.com.intellij.uiDesigner.core.GridConstraints(1, 11, 1, 2, com.loohp.interactivechat.libs.com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.loohp.interactivechat.libs.com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.loohp.interactivechat.libs.com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.loohp.interactivechat.libs.com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(-1, 200), new Dimension(150, -1), null, 0, false));
        reloadResourcesButton = new JButton();
        reloadResourcesButton.setText("Reload Resources");
        panel.add(reloadResourcesButton, new com.loohp.interactivechat.libs.com.intellij.uiDesigner.core.GridConstraints(0, 12, 1, 1, com.loohp.interactivechat.libs.com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.loohp.interactivechat.libs.com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.loohp.interactivechat.libs.com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.loohp.interactivechat.libs.com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Offset X");
        panel.add(label2, new com.loohp.interactivechat.libs.com.intellij.uiDesigner.core.GridConstraints(4, 0, 1, 1, com.loohp.interactivechat.libs.com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.loohp.interactivechat.libs.com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.loohp.interactivechat.libs.com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.loohp.interactivechat.libs.com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        panel.add(spinnerOffsetX, new com.loohp.interactivechat.libs.com.intellij.uiDesigner.core.GridConstraints(4, 1, 1, 1, com.loohp.interactivechat.libs.com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.loohp.interactivechat.libs.com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.loohp.interactivechat.libs.com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.loohp.interactivechat.libs.com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("Offset Y");
        panel.add(label3, new com.loohp.interactivechat.libs.com.intellij.uiDesigner.core.GridConstraints(4, 2, 1, 1, com.loohp.interactivechat.libs.com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.loohp.interactivechat.libs.com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.loohp.interactivechat.libs.com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.loohp.interactivechat.libs.com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        panel.add(spinnerOffsetY, new com.loohp.interactivechat.libs.com.intellij.uiDesigner.core.GridConstraints(4, 3, 1, 1, com.loohp.interactivechat.libs.com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.loohp.interactivechat.libs.com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.loohp.interactivechat.libs.com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.loohp.interactivechat.libs.com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        panel.add(imagePanel, new com.loohp.interactivechat.libs.com.intellij.uiDesigner.core.GridConstraints(5, 0, 2, 11, com.loohp.interactivechat.libs.com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.loohp.interactivechat.libs.com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.loohp.interactivechat.libs.com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.loohp.interactivechat.libs.com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        saveButton = new JButton();
        saveButton.setText("Save");
        panel.add(saveButton, new com.loohp.interactivechat.libs.com.intellij.uiDesigner.core.GridConstraints(6, 12, 1, 1, com.loohp.interactivechat.libs.com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.loohp.interactivechat.libs.com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.loohp.interactivechat.libs.com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.loohp.interactivechat.libs.com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setText("Background Color");
        label4.setToolTipText("Visual Background Color");
        panel.add(label4, new com.loohp.interactivechat.libs.com.intellij.uiDesigner.core.GridConstraints(4, 4, 1, 1, com.loohp.interactivechat.libs.com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.loohp.interactivechat.libs.com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.loohp.interactivechat.libs.com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.loohp.interactivechat.libs.com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        backgroundColorTextField = new JTextField();
        backgroundColorTextField.setColumns(8);
        backgroundColorTextField.setHorizontalAlignment(11);
        backgroundColorTextField.setText("#000000");
        panel.add(backgroundColorTextField, new com.loohp.interactivechat.libs.com.intellij.uiDesigner.core.GridConstraints(4, 5, 1, 1, com.loohp.interactivechat.libs.com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.loohp.interactivechat.libs.com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.loohp.interactivechat.libs.com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.loohp.interactivechat.libs.com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        legacyTextCheckBox = new JCheckBox();
        legacyTextCheckBox.setSelected(false);
        legacyTextCheckBox.setText("Legacy Text");
        panel.add(legacyTextCheckBox, new com.loohp.interactivechat.libs.com.intellij.uiDesigner.core.GridConstraints(0, 10, 1, 1, com.loohp.interactivechat.libs.com.intellij.uiDesigner.core.GridConstraints.ANCHOR_EAST, com.loohp.interactivechat.libs.com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.loohp.interactivechat.libs.com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.loohp.interactivechat.libs.com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final com.loohp.interactivechat.libs.com.intellij.uiDesigner.core.Spacer spacer1 = new com.loohp.interactivechat.libs.com.intellij.uiDesigner.core.Spacer();
        panel.add(spacer1, new com.loohp.interactivechat.libs.com.intellij.uiDesigner.core.GridConstraints(4, 10, 1, 1, com.loohp.interactivechat.libs.com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.loohp.interactivechat.libs.com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.loohp.interactivechat.libs.com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final com.loohp.interactivechat.libs.com.intellij.uiDesigner.core.Spacer spacer2 = new com.loohp.interactivechat.libs.com.intellij.uiDesigner.core.Spacer();
        panel.add(spacer2, new com.loohp.interactivechat.libs.com.intellij.uiDesigner.core.GridConstraints(0, 11, 1, 1, com.loohp.interactivechat.libs.com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.loohp.interactivechat.libs.com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.loohp.interactivechat.libs.com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, 1, null, null, null, 0, false));
        final JLabel label5 = new JLabel();
        label5.setText("Language");
        label5.setToolTipText("");
        panel.add(label5, new com.loohp.interactivechat.libs.com.intellij.uiDesigner.core.GridConstraints(4, 6, 1, 1, com.loohp.interactivechat.libs.com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.loohp.interactivechat.libs.com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.loohp.interactivechat.libs.com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.loohp.interactivechat.libs.com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        comboBoxLanguages = new JComboBox();
        comboBoxLanguages.setToolTipText("Language for Translatable Components");
        panel.add(comboBoxLanguages, new com.loohp.interactivechat.libs.com.intellij.uiDesigner.core.GridConstraints(4, 7, 1, 1, com.loohp.interactivechat.libs.com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.loohp.interactivechat.libs.com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.loohp.interactivechat.libs.com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.loohp.interactivechat.libs.com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final com.loohp.interactivechat.libs.com.intellij.uiDesigner.core.Spacer spacer3 = new com.loohp.interactivechat.libs.com.intellij.uiDesigner.core.Spacer();
        panel.add(spacer3, new com.loohp.interactivechat.libs.com.intellij.uiDesigner.core.GridConstraints(3, 11, 3, 2, com.loohp.interactivechat.libs.com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.loohp.interactivechat.libs.com.intellij.uiDesigner.core.GridConstraints.FILL_VERTICAL, 1, com.loohp.interactivechat.libs.com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        resourceBar = new JProgressBar();
        panel.add(resourceBar, new com.loohp.interactivechat.libs.com.intellij.uiDesigner.core.GridConstraints(2, 11, 1, 2, com.loohp.interactivechat.libs.com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.loohp.interactivechat.libs.com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.loohp.interactivechat.libs.com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.loohp.interactivechat.libs.com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        buttonDownloadLanguages = new JButton();
        buttonDownloadLanguages.setText("Download All Languages");
        panel.add(buttonDownloadLanguages, new com.loohp.interactivechat.libs.com.intellij.uiDesigner.core.GridConstraints(4, 8, 1, 1, com.loohp.interactivechat.libs.com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.loohp.interactivechat.libs.com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.loohp.interactivechat.libs.com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.loohp.interactivechat.libs.com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        scrollPaneTextInput = new JScrollPane();
        panel.add(scrollPaneTextInput, new com.loohp.interactivechat.libs.com.intellij.uiDesigner.core.GridConstraints(1, 0, 3, 11, com.loohp.interactivechat.libs.com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.loohp.interactivechat.libs.com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.loohp.interactivechat.libs.com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.loohp.interactivechat.libs.com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        textAreaInput = new JTextArea();
        textAreaInput.setColumns(0);
        textAreaInput.setLineWrap(true);
        textAreaInput.setWrapStyleWord(true);
        scrollPaneTextInput.setViewportView(textAreaInput);
        glowingTextBox = new JCheckBox();
        glowingTextBox.setText("Glowing");
        panel.add(glowingTextBox, new com.loohp.interactivechat.libs.com.intellij.uiDesigner.core.GridConstraints(4, 9, 1, 1, com.loohp.interactivechat.libs.com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.loohp.interactivechat.libs.com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.loohp.interactivechat.libs.com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.loohp.interactivechat.libs.com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.loohp.interactivechat.libs.com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        label1.setLabelFor(scrollPaneTextInput);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel;
    }

    private static class LanguageData {

        private String language;
        private String name;
        private String region;

        private String displayName;

        public LanguageData(String language, String name, String region) {
            this.language = language;
            this.name = name;
            this.region = region;

            if (name == null) {
                this.displayName = "[" + language + "]";
            } else {
                StringBuilder sb = new StringBuilder("\u202A").append(name);
                if (region != null) {
                    sb.append(" \u202A(").append(region).append(")");
                }
                this.displayName = sb.append(" [").append(language).append("]").toString();
            }
        }

        public String getLanguage() {
            return language;
        }

        public String getName() {
            return name;
        }

        public String getRegion() {
            return region;
        }

        public String getDisplayName() {
            return displayName;
        }

        @Override
        public String toString() {
            return displayName;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            LanguageData that = (LanguageData) o;
            return language.equals(that.language);
        }

        @Override
        public int hashCode() {
            return Objects.hash(language);
        }

    }

}
