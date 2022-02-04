package com.loohp.interactivechatdiscordsrvaddon.main;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import com.loohp.interactivechat.libs.com.loohp.yamlconfiguration.YamlConfiguration;
import com.loohp.interactivechat.objectholders.ValueTrios;
import com.loohp.interactivechatdiscordsrvaddon.graphics.ImageUtils;
import com.loohp.interactivechatdiscordsrvaddon.registry.ResourceRegistry;
import com.loohp.interactivechatdiscordsrvaddon.resources.ModelRenderer;
import com.loohp.interactivechatdiscordsrvaddon.resources.ModelRenderer.RenderResult;
import com.loohp.interactivechatdiscordsrvaddon.resources.ResourceManager;
import com.loohp.interactivechatdiscordsrvaddon.resources.ResourcePackInfo;
import com.loohp.interactivechatdiscordsrvaddon.resources.models.ModelDisplay.ModelDisplayPosition;
import com.loohp.interactivechatdiscordsrvaddon.resources.models.ModelOverride.ModelOverrideType;
import com.loohp.interactivechatdiscordsrvaddon.resources.textures.GeneratedTextureResource;
import com.loohp.interactivechatdiscordsrvaddon.resources.textures.TextureResource;
import com.loohp.interactivechatdiscordsrvaddon.utils.TintUtils;
import com.loohp.interactivechatdiscordsrvaddon.utils.TintUtils.SpawnEggTintData;
import com.loohp.interactivechatdiscordsrvaddon.utils.TintUtils.TintIndexData;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileFilter;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class BlockModelRenderer extends JFrame {

    public static final Pattern COLOR_HEX_PATTERN = Pattern.compile("^#[0-9a-fA-F]{6}$");

    private String title;
    private BufferedImage image;
    private Icon icon;

    private JTextField textFieldResourceKey;
    private JSpinner spinnerWidth;
    private JSpinner spinnerHeight;
    private JTextArea textAreaResources;
    private JButton reloadResourcesButton;
    private JButton renderModelButton;
    private JPanel panel;
    private JPanel imagePanel;
    private JPanel predicateKeyPanel;
    private JPanel predicateValuePanel;
    private JButton buttonSave;
    private JLabel renderTimesLabel;
    private JButton providedTexturesButton;
    private JTextField backgroundColorTextField;
    private JProgressBar resourceBar;
    private JCheckBox altPosBox;
    private JSpinner spinnerThreads;

    private JCheckBox enchantedCheckBox;
    private Map<ModelOverrideType, JSpinner> overrideSettings;

    private JDialog providedTexturesDialog;
    private Map<JComponent, ValueTrios<Supplier<String>, JButton, JFileChooser>> providedTextureSettings;

    private ResourceManager resourceManager;
    private ModelRenderer modelRenderer;

    private LinkedList<String> keyHistory;
    private int historyIndex;
    private String currentHistoryKey;
    private String tabString;
    private int tabPosition;
    private int tabIndex;
    private List<String> tabOptions;

    private BufferedImage renderedImage;
    private Color backgroundColor;
    private String lastRenderedKey;
    private File lastSavedLocation;

    private ExecutorService executorService;
    private ReentrantLock lock;

    public BlockModelRenderer(String title, BufferedImage image, Icon icon) throws IllegalAccessException {
        this.title = title;
        this.image = image;
        this.icon = icon;
        this.resourceManager = null;
        $$$setupUI$$$();
        this.modelRenderer = new ModelRenderer(() -> 0, source -> getEnchantedImage(source), source -> getRawEnchantedImage(source), () -> 8, () -> (int) spinnerThreads.getValue());
        this.overrideSettings = new EnumMap<>(ModelOverrideType.class);
        this.providedTextureSettings = new LinkedHashMap<>();
        this.executorService = Executors.newSingleThreadExecutor();
        this.lock = new ReentrantLock(true);
        this.lastRenderedKey = "";
        this.renderedImage = null;
        this.backgroundColor = Color.BLACK;
        this.lastSavedLocation = new File(".");
        this.keyHistory = new LinkedList<>();
        this.historyIndex = -1;
        this.currentHistoryKey = null;
        this.tabString = null;
        this.tabPosition = 0;
        this.tabIndex = -1;
        this.tabOptions = null;

        setContentPane(panel);
        setTitle(title + " - Block Model Renderer");
        setSize(1200, 900);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setIconImage(image);

        resourceBar.setMaximum(10000);

        predicateKeyPanel.setLayout(new GridLayout(0, 1));
        predicateValuePanel.setLayout(new GridLayout(0, 1));
        JLabel label = new JLabel("ENCHANTED");
        enchantedCheckBox = new JCheckBox();
        enchantedCheckBox.setHorizontalAlignment(SwingConstants.RIGHT);
        predicateKeyPanel.add(label);
        predicateValuePanel.add(enchantedCheckBox);
        for (ModelOverrideType type : ModelOverrideType.values()) {
            JSpinner spinner = new JSpinner(new SpinnerNumberModel(0.0F, Integer.MIN_VALUE, Integer.MAX_VALUE, 1.0F));
            label = new JLabel(type.toString());
            predicateKeyPanel.add(label);
            predicateValuePanel.add(spinner);
            overrideSettings.put(type, spinner);
        }
        predicateValuePanel.setLayout(new GridLayout(0, 1));

        providedTexturesDialog = new JDialog(this, "Provided Textures");
        providedTexturesDialog.setIconImage(image);
        providedTexturesDialog.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        JPanel builtInProvidedTexturesPanel = new JPanel();
        builtInProvidedTexturesPanel.setLayout(new GridLayout(0, 2));
        for (Field field : ResourceRegistry.class.getFields()) {
            if (field.getName().endsWith("_PLACEHOLDER") && field.getType().equals(String.class)) {
                JLabel fieldLabel = new JLabel((String) field.get(null));
                builtInProvidedTexturesPanel.add(fieldLabel);
                JFileChooser fieldFileChooser = new JFileChooser(lastSavedLocation);
                fieldFileChooser.setFileFilter(new FileFilter() {
                    @Override
                    public boolean accept(File f) {
                        return f.isDirectory() || f.getName().endsWith(".png");
                    }

                    @Override
                    public String getDescription() {
                        return "PNG images";
                    }
                });
                JButton fieldButton = new JButton("No File Chosen");
                fieldButton.addActionListener(e -> {
                    fieldFileChooser.setSelectedFile(null);
                    fieldButton.setText("No File Chosen");
                    int result = fieldFileChooser.showOpenDialog(providedTexturesDialog);
                    if (result == 0) {
                        fieldButton.setText(fieldFileChooser.getSelectedFile().getName());
                    }
                });
                providedTextureSettings.put(fieldLabel, new ValueTrios<>(() -> fieldLabel.getText(), fieldButton, fieldFileChooser));
                builtInProvidedTexturesPanel.add(fieldButton);
            }
        }
        JButton buttonMoreProvidedTextures = new JButton("Add Custom");
        buttonMoreProvidedTextures.addActionListener(e -> {
            JTextField fieldText = new JTextField();
            builtInProvidedTexturesPanel.add(fieldText);
            JFileChooser fieldFileChooser = new JFileChooser(lastSavedLocation);
            fieldFileChooser.setFileFilter(new FileFilter() {
                @Override
                public boolean accept(File f) {
                    return f.isDirectory() || f.getName().endsWith(".png");
                }

                @Override
                public String getDescription() {
                    return "PNG images";
                }
            });
            JButton fieldButton = new JButton("No File Chosen");
            fieldButton.addActionListener(ev -> {
                fieldFileChooser.setSelectedFile(null);
                fieldButton.setText("No File Chosen");
                int result = fieldFileChooser.showOpenDialog(providedTexturesDialog);
                if (result == 0) {
                    fieldButton.setText(fieldFileChooser.getSelectedFile().getName());
                }
            });
            providedTextureSettings.put(fieldText, new ValueTrios<>(() -> fieldText.getText(), fieldButton, fieldFileChooser));
            builtInProvidedTexturesPanel.add(fieldButton);
            SwingUtilities.invokeLater(() -> providedTexturesDialog.pack());
        });
        JPanel layoutPanel = new JPanel();
        BoxLayout layout = new BoxLayout(layoutPanel, BoxLayout.Y_AXIS);
        layoutPanel.setLayout(layout);
        layoutPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        layoutPanel.add(builtInProvidedTexturesPanel);
        layoutPanel.add(buttonMoreProvidedTextures);
        buttonMoreProvidedTextures.setAlignmentX(Component.CENTER_ALIGNMENT);
        providedTexturesDialog.add(layoutPanel);
        providedTexturesDialog.pack();
        providedTexturesDialog.setLocationRelativeTo(null);

        providedTexturesDialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                Iterator<Entry<JComponent, ValueTrios<Supplier<String>, JButton, JFileChooser>>> itr = providedTextureSettings.entrySet().iterator();
                while (itr.hasNext()) {
                    Entry<JComponent, ValueTrios<Supplier<String>, JButton, JFileChooser>> entry = itr.next();
                    JComponent component = entry.getKey();
                    if (component instanceof JTextField) {
                        JTextField textField = (JTextField) component;
                        if (textField.getText().trim().isEmpty()) {
                            itr.remove();
                            builtInProvidedTexturesPanel.remove(textField);
                            builtInProvidedTexturesPanel.remove(entry.getValue().getSecond());
                        }
                    }
                }
                SwingUtilities.invokeLater(() -> providedTexturesDialog.pack());
            }
        });

        providedTexturesDialog.setResizable(false);

        providedTexturesButton.addActionListener(new ActionListener() {
            @Override
            public synchronized void actionPerformed(ActionEvent e) {
                if (!providedTexturesDialog.isVisible()) {
                    providedTexturesDialog.setVisible(true);
                }
            }
        });

        renderModelButton.addActionListener(e -> {
            executorService.submit(() -> render());
        });

        reloadResourcesButton.addActionListener(e -> {
            executorService.submit(() -> loadResources());
        });

        textFieldResourceKey.addActionListener(e -> {
            executorService.submit(() -> {
                if (renderModelButton.isEnabled()) {
                    render();
                }
            });
        });

        textFieldResourceKey.setFocusTraversalKeysEnabled(false);
        textFieldResourceKey.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (tabPosition != textFieldResourceKey.getCaretPosition()) {
                    tabPosition = textFieldResourceKey.getCaretPosition();
                    tabString = null;
                }
            }
        });
        textFieldResourceKey.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int code = e.getKeyCode();
                switch (code) {
                    case KeyEvent.VK_UP:
                        if (historyIndex + 1 < keyHistory.size()) {
                            if (historyIndex == -1 && !keyHistory.isEmpty()) {
                                currentHistoryKey = textFieldResourceKey.getText();
                                textFieldResourceKey.setText(keyHistory.getFirst());
                                historyIndex++;
                            } else {
                                if (historyIndex == 0 && !textFieldResourceKey.getText().equalsIgnoreCase(keyHistory.getFirst())) {
                                    currentHistoryKey = textFieldResourceKey.getText();
                                    historyIndex--;
                                }
                                textFieldResourceKey.setText(keyHistory.get(++historyIndex));
                            }
                            tabString = null;
                        }
                        break;
                    case KeyEvent.VK_DOWN:
                        if (historyIndex > 0) {
                            textFieldResourceKey.setText(keyHistory.get(--historyIndex));
                            tabString = null;
                        } else if (historyIndex > -1 && currentHistoryKey != null) {
                            textFieldResourceKey.setText(currentHistoryKey);
                            currentHistoryKey = null;
                            historyIndex--;
                            tabString = null;
                        }
                        break;
                    case KeyEvent.VK_TAB:
                        if (resourceManager != null) {
                            String text = textFieldResourceKey.getText().substring(0, tabPosition);
                            if (!text.equalsIgnoreCase(tabString)) {
                                tabString = text;
                                tabOptions = resourceManager.getModelManager().getRawBlockModelMapping().keySet().stream().filter(each -> each.startsWith(text)).sorted().collect(Collectors.toList());
                                if (!tabOptions.isEmpty()) {
                                    tabIndex = 0;
                                    tabPosition = textFieldResourceKey.getCaretPosition();
                                    String option = tabOptions.get(tabIndex);
                                    textFieldResourceKey.setText(option);
                                    textFieldResourceKey.setCaretPosition(Math.min(option.length(), tabPosition));
                                }
                            } else {
                                if (!tabOptions.isEmpty()) {
                                    tabIndex++;
                                    if (tabIndex >= tabOptions.size()) {
                                        tabIndex = 0;
                                    }
                                    String option = tabOptions.get(tabIndex);
                                    textFieldResourceKey.setText(option);
                                    textFieldResourceKey.setCaretPosition(Math.min(option.length(), tabPosition));
                                }
                            }
                        }
                        break;
                    case KeyEvent.VK_LEFT:
                    case KeyEvent.VK_RIGHT:
                        tabString = null;
                        SwingUtilities.invokeLater(() -> tabPosition = textFieldResourceKey.getCaretPosition());
                        break;
                    default:
                        if (tabString != null && !tabOptions.isEmpty()) {
                            textFieldResourceKey.setCaretPosition(textFieldResourceKey.getText().length());
                        }
                        tabString = null;
                        SwingUtilities.invokeLater(() -> tabPosition = textFieldResourceKey.getCaretPosition());
                        break;
                }
            }
        });

        buttonSave.addActionListener(e -> {
            executorService.submit(() -> saveImage());
        });

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

        textFieldResourceKey.setCaretPosition(tabPosition = textFieldResourceKey.getText().length());

        setLocationRelativeTo(null);
        setVisible(true);

        executorService.submit(() -> loadResources());
    }

    private void createUIComponents() {
        spinnerWidth = new JSpinner(new SpinnerNumberModel(600, 1, 8192, 1));
        spinnerHeight = new JSpinner(new SpinnerNumberModel(600, 1, 8192, 1));
        spinnerThreads = new JSpinner(new SpinnerNumberModel(Runtime.getRuntime().availableProcessors(), 1, 1024, 1));
        imagePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(backgroundColor);
                g.fillRect(0, 0, getWidth(), getHeight());
                if (renderedImage != null) {
                    g.drawImage(renderedImage, getWidth() / 2 - renderedImage.getWidth() / 2, getHeight() / 2 - renderedImage.getHeight() / 2, this);
                    buttonSave.setEnabled(true);
                } else {
                    buttonSave.setEnabled(false);
                }
            }
        };
    }

    public BufferedImage getRawEnchantedImage(BufferedImage source) {
        BufferedImage tintOriginal = resourceManager.getTextureManager().getTexture(ResourceRegistry.MISC_TEXTURE_LOCATION + "enchanted_item_glint").getTexture();
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
            if (!lock.tryLock(0, TimeUnit.MILLISECONDS)) {
                return;
            }
        } catch (InterruptedException e) {
        }
        renderModelButton.setEnabled(false);
        reloadResourcesButton.setEnabled(false);
        resourceBar.setValue(0);
        resourceBar.setVisible(true);
        textAreaResources.setText("Loading Resources...");

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
            resourceManager = new ResourceManager();
            resourceManager.loadResources(new File("InteractiveChatDiscordSrvAddon/built-in", "Default"));
            resourceBar.setValue(valuePerPack);
            for (String resourceName : resourceOrder) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                System.setErr(new PrintStream(baos));
                try {
                    File resourcePackFile = new File("InteractiveChatDiscordSrvAddon/resourcepacks/" + resourceName);
                    ResourcePackInfo info = resourceManager.loadResources(resourcePackFile);
                } catch (Exception e) {
                    StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    e.printStackTrace(pw);
                    Toolkit.getDefaultToolkit().beep();
                    JOptionPane.showMessageDialog(null, GUIMain.createLabel("Unable to load \"" + resourceName + "\":\n" + sw, 13, Color.RED), title, JOptionPane.ERROR_MESSAGE);
                }
                String error = baos.toString();
                if (!error.isEmpty()) {
                    JOptionPane.showMessageDialog(null, GUIMain.createLabel("There are errors while loading \"" + resourceName + "\":\n" + error, 13, Color.RED), title, JOptionPane.ERROR_MESSAGE);
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

        renderModelButton.setEnabled(true);
        reloadResourcesButton.setEnabled(true);
        resourceBar.setVisible(false);
        lock.unlock();
    }

    public void render() {
        try {
            if (!lock.tryLock(0, TimeUnit.MILLISECONDS)) {
                return;
            }
        } catch (InterruptedException e) {
        }
        renderModelButton.setEnabled(false);
        reloadResourcesButton.setEnabled(false);
        spinnerThreads.setEnabled(false);
        String key = textFieldResourceKey.getText();
        if (!key.contains(":")) {
            key = "minecraft:" + key;
            textFieldResourceKey.setText(key);
        }
        String finalKey = key;
        keyHistory.removeIf(each -> each.equalsIgnoreCase(finalKey));
        keyHistory.add(0, key);
        historyIndex = 0;
        currentHistoryKey = null;
        int lastSlash = key.lastIndexOf("/");
        String trimmedKey = key.substring(lastSlash < 0 ? (key.lastIndexOf(":") + 1) : lastSlash + 1);

        Map<String, TextureResource> providedTextures = new HashMap<>();
        Map<ModelOverrideType, Float> predicates = new EnumMap<>(ModelOverrideType.class);

        SpawnEggTintData tintData = TintUtils.getSpawnEggTint(trimmedKey);
        if (tintData != null) {
            BufferedImage baseImage = resourceManager.getTextureManager().getTexture(ResourceRegistry.ITEM_TEXTURE_LOCATION + "spawn_egg").getTexture();
            BufferedImage overlayImage = resourceManager.getTextureManager().getTexture(ResourceRegistry.ITEM_TEXTURE_LOCATION + "spawn_egg_overlay").getTexture(baseImage.getWidth(), baseImage.getHeight());

            BufferedImage colorBase = ImageUtils.changeColorTo(ImageUtils.copyImage(baseImage), tintData.getBase());
            BufferedImage colorOverlay = ImageUtils.changeColorTo(ImageUtils.copyImage(overlayImage), tintData.getOverlay());

            baseImage = ImageUtils.multiply(baseImage, colorBase);
            overlayImage = ImageUtils.multiply(overlayImage, colorOverlay);

            providedTextures.put(ResourceRegistry.SPAWN_EGG_PLACEHOLDER, new GeneratedTextureResource(baseImage));
            providedTextures.put(ResourceRegistry.SPAWN_EGG_OVERLAY_PLACEHOLDER, new GeneratedTextureResource(overlayImage));
        }

        TintIndexData tintIndexData = TintUtils.getTintData(trimmedKey);
        for (Entry<ModelOverrideType, JSpinner> entry : overrideSettings.entrySet()) {
            float value = ((Number) entry.getValue().getValue()).floatValue();
            if (value != 0F) {
                predicates.put(entry.getKey(), value);
            }
        }

        for (ValueTrios<Supplier<String>, JButton, JFileChooser> data : providedTextureSettings.values()) {
            String texturePlaceholder = data.getFirst().get();
            File file = data.getThird().getSelectedFile();
            if (file != null && file.getName().endsWith(".png")) {
                try {
                    providedTextures.put(texturePlaceholder, new GeneratedTextureResource(ImageIO.read(file)));
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }
        modelRenderer.reloadPoolSize();
        long start = System.currentTimeMillis();
        try {
            RenderResult result = modelRenderer.render((int) spinnerWidth.getValue(), (int) spinnerHeight.getValue(), (int) spinnerWidth.getValue(), (int) spinnerHeight.getValue(), resourceManager, false, key, ModelDisplayPosition.GUI, predicates, providedTextures, tintIndexData, enchantedCheckBox.isSelected(), altPosBox.isSelected());
            long end = System.currentTimeMillis();
            if (result.isSuccessful()) {
                renderedImage = result.getImage();
                imagePanel.repaint();
                renderTimesLabel.setText((end - start) + "ms");
                lastRenderedKey = key;
            } else {
                Toolkit.getDefaultToolkit().beep();
                JOptionPane.showMessageDialog(null, GUIMain.createLabel("Render Rejected:\n" + result.getRejectedReason(), 13, Color.RED), title, JOptionPane.ERROR_MESSAGE);
            }
        } catch (Throwable e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            Toolkit.getDefaultToolkit().beep();
            JOptionPane.showMessageDialog(null, GUIMain.createLabel("An error occurred!\n" + sw, 13, Color.RED), title, JOptionPane.ERROR_MESSAGE);
        }
        renderModelButton.setEnabled(true);
        reloadResourcesButton.setEnabled(true);
        spinnerThreads.setEnabled(true);
        lock.unlock();
    }

    public synchronized void saveImage() {
        if (renderedImage == null) {
            return;
        }
        String key = lastRenderedKey;
        if (!lastRenderedKey.isEmpty()) {
            int lastSlash = key.lastIndexOf("/");
            key = key.substring(lastSlash < 0 ? key.lastIndexOf(":") : lastSlash);
        }
        File file;
        while (true) {
            JFileChooser fileChooser = new JFileChooser(lastSavedLocation);
            fileChooser.setSelectedFile(new File(lastSavedLocation, key + ".png"));
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
            ImageIO.write(renderedImage, "png", file);
        } catch (IOException e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            Toolkit.getDefaultToolkit().beep();
            JOptionPane.showMessageDialog(null, GUIMain.createLabel("An error occurred!\n" + sw, 13, Color.RED), title, JOptionPane.ERROR_MESSAGE);
        }
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
        panel.setLayout(new GridLayoutManager(7, 13, new Insets(0, 0, 0, 0), -1, -1));
        final JLabel label1 = new JLabel();
        label1.setText("Model Resource Key");
        panel.add(label1, new GridConstraints(0, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Width");
        panel.add(label2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        panel.add(spinnerWidth, new GridConstraints(1, 1, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, new Dimension(100, -1), new Dimension(100, -1), new Dimension(100, -1), 0, false));
        textFieldResourceKey = new JTextField();
        textFieldResourceKey.setText("minecraft:item/");
        textFieldResourceKey.setToolTipText("Example: \"minecraft:item/stone\"");
        panel.add(textFieldResourceKey, new GridConstraints(0, 2, 1, 10, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("Height");
        panel.add(label3, new GridConstraints(1, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        panel.add(spinnerHeight, new GridConstraints(1, 4, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, new Dimension(100, -1), new Dimension(100, -1), new Dimension(100, -1), 0, false));
        textAreaResources = new JTextArea();
        textAreaResources.setEditable(false);
        panel.add(textAreaResources, new GridConstraints(2, 11, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(250, 50), null, 0, false));
        panel.add(imagePanel, new GridConstraints(2, 0, 5, 11, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        renderModelButton = new JButton();
        renderModelButton.setText("Render Model");
        panel.add(renderModelButton, new GridConstraints(0, 12, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        predicateKeyPanel = new JPanel();
        predicateKeyPanel.setLayout(new GridBagLayout());
        panel.add(predicateKeyPanel, new GridConstraints(5, 11, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        reloadResourcesButton = new JButton();
        reloadResourcesButton.setText("Reload Resources");
        panel.add(reloadResourcesButton, new GridConstraints(1, 12, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        buttonSave = new JButton();
        buttonSave.setEnabled(false);
        buttonSave.setText("Save Image");
        panel.add(buttonSave, new GridConstraints(6, 12, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        predicateValuePanel = new JPanel();
        predicateValuePanel.setLayout(new GridBagLayout());
        predicateValuePanel.setEnabled(true);
        panel.add(predicateValuePanel, new GridConstraints(5, 12, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        renderTimesLabel = new JLabel();
        renderTimesLabel.setText("0ms");
        panel.add(renderTimesLabel, new GridConstraints(6, 11, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setText("Customization");
        panel.add(label4, new GridConstraints(4, 11, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        providedTexturesButton = new JButton();
        providedTexturesButton.setText("Provided Textures");
        panel.add(providedTexturesButton, new GridConstraints(4, 12, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel.add(spacer1, new GridConstraints(1, 10, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JLabel label5 = new JLabel();
        label5.setText("Background Color");
        label5.setToolTipText("Visual background color");
        panel.add(label5, new GridConstraints(1, 5, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        backgroundColorTextField = new JTextField();
        backgroundColorTextField.setColumns(8);
        backgroundColorTextField.setHorizontalAlignment(11);
        backgroundColorTextField.setText("#000000");
        backgroundColorTextField.setToolTipText("Visual background color");
        panel.add(backgroundColorTextField, new GridConstraints(1, 6, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        resourceBar = new JProgressBar();
        panel.add(resourceBar, new GridConstraints(3, 11, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        altPosBox = new JCheckBox();
        altPosBox.setText("Alternate Position");
        panel.add(altPosBox, new GridConstraints(1, 9, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label6 = new JLabel();
        label6.setText("Threads");
        panel.add(label6, new GridConstraints(1, 7, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        panel.add(spinnerThreads, new GridConstraints(1, 8, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, new Dimension(100, -1), new Dimension(100, -1), new Dimension(100, -1), 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel;
    }

}