package com.github.dhaval2404.material_icon_generator.widget;

import com.github.dhaval2404.material_icon_generator.IconModel;
import com.github.dhaval2404.material_icon_generator.util.BufferedImageTranscoder;
import com.github.dhaval2404.material_icon_generator.util.ColorUtil;
import com.github.dhaval2404.material_icon_generator.util.MouseClickListener;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.TextBrowseFolderListener;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.openapi.util.JDOMUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.sun.org.apache.xml.internal.serializer.OutputPropertiesFactory;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.NamedNodeMap;
import org.xml.sax.SAXException;

import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleState;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.basic.ComboPopup;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * Copyright 2014-2015 Material Design Icon Generator (Yusuke Konishi)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * This file was modified by the author from the following original file:
 * https://raw.githubusercontent.com/konifar/android-material-design-icon-generator-plugin/master/src/main/java/com/konifar/material_icon_generator/MaterialDesignIconGenerateDialog.java
 *
 */
public class MaterialDesignIconGenerateDialog extends DialogWrapper {

    private static final String TITLE = "Material Design Icon Generator";
    private static final String FILE_ICON_COMBOBOX_XML = "/template.xml";
    private static final String COLOR_PALETTE_COMBOBOX_XML = "/palette.xml";

    private static final String URL_OVERVIEW = "https://material.io/resources/icons/";
    private static final String URL_REPOSITORY = "https://github.com/Dhaval2404/material-icon-generator-plugin";
    private static final String ERROR_ICON_NOT_SELECTED = "Please select icon.";
    private static final String ERROR_FILE_NAME_EMPTY = "Please input file name.";
    private static final String ERROR_SIZE_CHECK_EMPTY = "Please check icon size.";
    private static final String ERROR_RESOURCE_DIR_NOTHING_PREFIX = "Can not find resource dir: ";
    private static final String OK_BUTTON_LABEL = "Generate";
    private static final String CANCEL_BUTTON_LABEL = "Close";

    private static final String ICON_WARNING = "/error_outline_black_36.png";
    private static final String ICON_DONE = "/thumb_up_alt_black_36.png";

    private IconModel model;
    private Map<String, String> colorPaletteMap;

    private JPanel panelMain;
    private JLabel imageLabel;
    private JComboBox<String> comboBoxTheme;
    private JComboBox<String> comboBoxDp;
    private JComboBox<String> comboBoxColor;
    private JTextField textFieldColorCode;
    private FilterComboBox comboBoxIcon;
    private JTextField textFieldFileName;
    private JLabel labelOverview;
    private JLabel labelRepository;
    private JCheckBox checkBoxXxxhdpi;
    private TextFieldWithBrowseButton resDirectoryName;

    private JRadioButton radioImage;
    private JPanel panelImageSize;
    private JCheckBox checkBoxMdpi;
    private JCheckBox checkBoxHdpi;
    private JCheckBox checkBoxXhdpi;
    private JCheckBox checkBoxXxhdpi;

    private JRadioButton radioVector;
    private JPanel panelVector;
    private JCheckBox checkBoxDrawable;

    public MaterialDesignIconGenerateDialog(@Nullable Project project, String defaultResourcePath) {
        super(project, true);

        setTitle(TITLE);
        setResizable(true);

        initIconComboBox();
        initColorComboBox();
        initThemeComboBox();
        initDpComboBox();
        initFileName();
        initResDirectoryName(project, defaultResourcePath);
        initImageTypeRadioButton();
        initSizeCheckBox();
        initVectorCheckBox();
        initFileCustomColor();

        initLabelLink(labelOverview, URL_OVERVIEW);
        initLabelLink(labelRepository, URL_REPOSITORY);

        model = createModel();

        model.setIconAndFileName((String) comboBoxIcon.getSelectedItem());
        textFieldFileName.setText(model.getFileName());
        showIconPreview();

        setOKButtonText(OK_BUTTON_LABEL);
        setCancelButtonText(CANCEL_BUTTON_LABEL);

        init();
    }

    private void initImageTypeRadioButton() {
        radioImage.addItemListener(itemEvent -> toggleImageType(!radioImage.isSelected()));

        radioVector.addItemListener(itemEvent -> toggleImageType(radioVector.isSelected()));

        panelImageSize.addMouseListener(new MouseClickListener() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                toggleImageType(radioImage.isSelected());
            }
        });

        panelVector.addMouseListener(new MouseClickListener() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                toggleImageType(!radioVector.isSelected());
            }
        });

        radioImage.setSelected(true);
    }

    private void toggleImageType(boolean shouldVectorSelected) {
        radioVector.setSelected(shouldVectorSelected);
        radioImage.setSelected(!shouldVectorSelected);

        panelVector.setEnabled(shouldVectorSelected);
        panelImageSize.setEnabled(!shouldVectorSelected);

        checkBoxDrawable.setEnabled(shouldVectorSelected);

        checkBoxHdpi.setEnabled(!shouldVectorSelected);
        checkBoxMdpi.setEnabled(!shouldVectorSelected);
        checkBoxXhdpi.setEnabled(!shouldVectorSelected);
        checkBoxXxhdpi.setEnabled(!shouldVectorSelected);
        checkBoxXxxhdpi.setEnabled(!shouldVectorSelected);

        if (model != null) {
            model.setVectorTypeAndFileName(shouldVectorSelected);
            textFieldFileName.setText(model.getFileName());
        }
    }

    private void initSizeCheckBox() {
        checkBoxMdpi.addItemListener(event -> {
            if (model != null) model.setMdpi(checkBoxMdpi.isSelected());
        });

        checkBoxHdpi.addItemListener(event -> {
            if (model != null) model.setHdpi(checkBoxHdpi.isSelected());
        });

        checkBoxXhdpi.addItemListener(event -> {
            if (model != null) model.setXhdpi(checkBoxXhdpi.isSelected());
        });

        checkBoxXxhdpi.addItemListener(event -> {
            if (model != null) model.setXxhdpi(checkBoxXxhdpi.isSelected());
        });

        checkBoxXxxhdpi.addItemListener(event -> {
            if (model != null) model.setXxxhdpi(checkBoxXxxhdpi.isSelected());
        });
    }

    private void initVectorCheckBox() {
        checkBoxDrawable.addItemListener(event -> {
            if (model != null) model.setDrawable(checkBoxDrawable.isSelected());
        });
    }

    private void initFileName() {
        textFieldFileName.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent event) {
                setText();
            }

            @Override
            public void removeUpdate(DocumentEvent event) {
                setText();
            }

            @Override
            public void changedUpdate(DocumentEvent event) {
                setText();
            }

            private void setText() {
                if (model != null) model.setFileName(textFieldFileName.getText());
            }
        });
    }

    private void initFileCustomColor() {
        textFieldColorCode.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent event) {
                setText();
            }

            @Override
            public void removeUpdate(DocumentEvent event) {
                setText();
            }

            @Override
            public void changedUpdate(DocumentEvent event) {
                setText();
            }

            private void setText() {
                if (model != null) {
                    if (StringUtils.isEmpty(textFieldColorCode.getText())) {
                        model.setColorCode(null);
                        showIconPreview();
                        comboBoxColor.setSelectedItem("");
                        return;
                    }
                    try {
                        ColorUtil.decodeColor(textFieldColorCode.getText());
                        model.setColorCode(textFieldColorCode.getText());
                        showIconPreview();
                    } catch (NumberFormatException e) {
                        model.setColorCode(null);
                        comboBoxColor.setSelectedItem("");
                        showIconPreview();
                    }
                }
            }
        });
    }

    private void initResDirectoryName(Project project, String defaultResourcePath) {
        resDirectoryName.setText(defaultResourcePath);

        resDirectoryName.addBrowseFolderListener(new TextBrowseFolderListener(
                new FileChooserDescriptor(false, true, false, false, false, false), project));
        resDirectoryName.getTextField().getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent event) {
                setText();
            }

            @Override
            public void removeUpdate(DocumentEvent event) {
                setText();
            }

            @Override
            public void changedUpdate(DocumentEvent event) {
                setText();
            }

            private void setText() {
                if (model != null) model.setResDir(resDirectoryName.getText());
            }
        });
    }

    private void initDpComboBox() {
        comboBoxDp.setSelectedIndex(1);         // 24dp

        comboBoxDp.addActionListener(event -> {
            model.setDpAndFileName((String) comboBoxDp.getSelectedItem());
            textFieldFileName.setText(model.getFileName());
            showIconPreview();
        });

        comboBoxDp.getAccessibleContext().addPropertyChangeListener(event -> {
            if (AccessibleContext.ACCESSIBLE_STATE_PROPERTY.equals(event.getPropertyName())
                    && AccessibleState.FOCUSED.equals(event.getNewValue())
                    && comboBoxDp.getAccessibleContext().getAccessibleChild(0) instanceof ComboPopup) {
                ComboPopup popup = (ComboPopup) comboBoxDp.getAccessibleContext().getAccessibleChild(0);
                JList list = popup.getList();
                comboBoxDp.setSelectedItem(String.valueOf(list.getSelectedValue()));
            }
        });
    }

    private void initThemeComboBox() {
        comboBoxTheme.setSelectedIndex(0);         // Fill icon

        comboBoxTheme.addActionListener(event -> {
            model.setThemeAndFileName((String) comboBoxTheme.getSelectedItem());
            textFieldFileName.setText(model.getFileName());
            showIconPreview();
        });

        comboBoxTheme.getAccessibleContext().addPropertyChangeListener(event -> {
            if (AccessibleContext.ACCESSIBLE_STATE_PROPERTY.equals(event.getPropertyName())
                    && AccessibleState.FOCUSED.equals(event.getNewValue())
                    && comboBoxTheme.getAccessibleContext().getAccessibleChild(0) instanceof ComboPopup) {
                ComboPopup popup = (ComboPopup) comboBoxTheme.getAccessibleContext().getAccessibleChild(0);
                JList list = popup.getList();
                comboBoxTheme.setSelectedItem(String.valueOf(list.getSelectedValue()));
            }
        });
    }

    private void initColorComboBox() {
        colorPaletteMap = new HashMap<>();

        try {
            Element root = JDOMUtil.load(getClass().getResourceAsStream(COLOR_PALETTE_COMBOBOX_XML));

            List<Element> elements = root.getChildren();
            for (org.jdom.Element element : elements) {
                String key = element.getAttributeValue("id");
                colorPaletteMap.put(key, element.getText());
                comboBoxColor.addItem(key);
            }
        } catch (JDOMException | IOException e) {
            e.printStackTrace();
        }

        comboBoxColor.getAccessibleContext().addPropertyChangeListener(event -> {
            if (AccessibleContext.ACCESSIBLE_STATE_PROPERTY.equals(event.getPropertyName())
                    && AccessibleState.FOCUSED.equals(event.getNewValue())
                    && comboBoxColor.getAccessibleContext().getAccessibleChild(0) instanceof ComboPopup) {
                ComboPopup popup = (ComboPopup) comboBoxColor.getAccessibleContext().getAccessibleChild(0);
                JList list = popup.getList();
                comboBoxColor.setSelectedItem(String.valueOf(list.getSelectedValue()));
            }
        });

        comboBoxColor.addActionListener(event -> {
            if (model != null) {
                model.setDisplayColorName((String) comboBoxColor.getSelectedItem());
                String value = colorPaletteMap.get(comboBoxColor.getSelectedItem());
                textFieldColorCode.setText(value);
                textFieldFileName.setText(model.getFileName());
            }
        });

        comboBoxColor.setSelectedIndex(0);
        String value = colorPaletteMap.get(comboBoxColor.getSelectedItem());
        textFieldColorCode.setText(value);
    }

    private IconModel createModel() {
        final String iconName = (String) comboBoxIcon.getSelectedItem();
        final String displayColorName = (String) comboBoxColor.getSelectedItem();
        final String colorCode = textFieldColorCode.getText();
        final String dp = (String) comboBoxDp.getSelectedItem();
        final String theme = (String) comboBoxTheme.getSelectedItem();
        final String fileName = textFieldFileName.getText();
        final String resDir = resDirectoryName.getText();

        final boolean mdpi = checkBoxMdpi.isSelected();
        final boolean hdpi = checkBoxHdpi.isSelected();
        final boolean xdpi = checkBoxXhdpi.isSelected();
        final boolean xxdpi = checkBoxXxhdpi.isSelected();
        final boolean xxxdpi = checkBoxXxxhdpi.isSelected();

        final boolean isVectorType = radioVector.isSelected();
        final boolean drawable = checkBoxDrawable.isSelected();

        return new IconModel(iconName, displayColorName, colorCode, theme, dp, fileName, resDir,
                mdpi, hdpi, xdpi, xxdpi, xxxdpi, isVectorType, drawable);
    }

    private void showIconPreview() {
        if (model == null) return;

        new Thread(() -> {
            try {
                Image img = model.getPreviewImage();
                Image colorImg = generateColoredIcon((BufferedImage) img);
                ImageIcon icon = new ImageIcon(colorImg);
                imageLabel.setIcon(icon);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return panelMain;
    }

    private void initIconComboBox() {
        try {
            //Document doc = JDOMUtil.loadDocument(getClass().getResourceAsStream(FILE_ICON_COMBOBOX_XML));
            //List<Element> elements = doc.getRootElement().getChildren();
            Element root = JDOMUtil.load(getClass().getResourceAsStream(FILE_ICON_COMBOBOX_XML));
            List<Element> elements = root.getChildren();

            for (org.jdom.Element element : elements) {
                comboBoxIcon.addItem(element.getText());
            }
        } catch (JDOMException | IOException e) {
            e.printStackTrace();
        }

        comboBoxIcon.addActionListener(event -> {
            if (model != null) {
                model.setIconAndFileName((String) comboBoxIcon.getSelectedItem());
                textFieldFileName.setText(model.getFileName());
                showIconPreview();
            }
        });

        comboBoxIcon.setSelectedIndex(0);
    }

    @Override
    protected void doOKAction() {
        if (model == null) return;

        if (alreadyFileExists()) {
            final int option = JOptionPane.showConfirmDialog(panelMain,
                    "File already exists, overwrite this ?",
                    "File exists",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.PLAIN_MESSAGE,
                    new ImageIcon(getClass().getResource(ICON_WARNING)));

            if (option == JOptionPane.YES_OPTION) {
                create();
            }
        } else {
            create();
        }

    }

    private void create() {
        try {
            createIcons();

            JOptionPane.showConfirmDialog(panelMain,
                    "Icon created successfully.",
                    "Material design icon created",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE,
                    new ImageIcon(getClass().getResource(ICON_DONE)));

            LocalFileSystem.getInstance().refresh(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createIcons() throws IOException {
        File iconDir = model.getIconDirectory();
        if (model.isVectorType()) {
            if (model.isDrawable()) createVectorIcon(iconDir, checkBoxDrawable.getText());
        } else {
            if (model.isMdpi()) createImageIcon(iconDir, checkBoxMdpi.getText());
            if (model.isHdpi()) createImageIcon(iconDir, checkBoxHdpi.getText());
            if (model.isXhdpi()) createImageIcon(iconDir, checkBoxXhdpi.getText());
            if (model.isXxhdpi()) createImageIcon(iconDir, checkBoxXxhdpi.getText());
            if (model.isXxxhdpi()) createImageIcon(iconDir, checkBoxXxxhdpi.getText());
        }
        try {
            if (iconDir != null) {
                FileUtils.deleteDirectory(iconDir);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private boolean alreadyFileExists() {
        JCheckBox[] checkBoxes = {checkBoxMdpi, checkBoxHdpi, checkBoxXhdpi, checkBoxXxhdpi, checkBoxXxxhdpi, checkBoxDrawable};

        for (JCheckBox checkBox : checkBoxes) {
            if (checkBox.isSelected()) {
                File copyFile;
                if (checkBox == checkBoxDrawable) {
                    copyFile = new File(model.getVectorCopyPath(checkBox.getText()));
                } else {
                    copyFile = new File(model.getCopyPath(checkBox.getText()));
                }
                if (copyFile.exists() && copyFile.isFile()) {
                    return true;
                }
            }
        }
        return false;
    }

    private void createImageIcon(File iconDir, String size) {
        File copyFile = new File(model.getCopyPath(size));
        try {
            copyFile.getParentFile().mkdirs();

            File iconFile = model.getIcon(iconDir, size);
            copyFile(iconFile, copyFile);
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalStateException(e);
        }
    }

    private void createVectorIcon(File iconDir, String vectorDrawableDir) {
        File copyFile = new File(model.getVectorCopyPath(vectorDrawableDir));
        try {
            copyFile.getParentFile().mkdirs();

            File iconFile = model.getVectorIcon(iconDir);

            FileUtils.copyFile(iconFile, copyFile);

            changeColorAndSize(copyFile);
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalStateException(e);
        }
    }

    private void changeColorAndSize(File destFile) {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            org.w3c.dom.Document doc = docBuilder.parse(destFile.getAbsolutePath());

            // Edit Size
            org.w3c.dom.Element rootElement = doc.getDocumentElement();
            NamedNodeMap rootAttrs = rootElement.getAttributes();
            rootAttrs.getNamedItem("android:width").setTextContent(model.getDp()); // 24dp
            rootAttrs.getNamedItem("android:height").setTextContent(model.getDp()); // 24dp

            String viewportSize = model.getViewportSize();
            if (viewportSize != null) {
                rootAttrs.getNamedItem("android:viewportWidth").setTextContent(viewportSize); // 24.0
                rootAttrs.getNamedItem("android:viewportHeight").setTextContent(viewportSize); // 24.0
            }

            //Add Color Tint
            rootAttrs.getNamedItem("android:tint").setTextContent(model.getColorCode());

            /*
                // Edit color
                // Will Tint Color instead of Edit
                NodeList nodeList = rootElement.getElementsByTagName("path");
                for (int i = 0, size = nodeList.getLength(); i < size; i++) {
                    NamedNodeMap pathAttrs = nodeList.item(i).getAttributes();
                    if (pathAttrs != null) {
                        Node node = pathAttrs.getNamedItem("android:fillColor");
                        if (node != null) node.setTextContent(model.getColorCode());
                    }
                }
            */

            // Write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputPropertiesFactory.S_KEY_INDENT_AMOUNT, "4");
            StreamResult result = new StreamResult(destFile);
            transformer.transform(new DOMSource(doc), result);
        } catch (ParserConfigurationException | SAXException | TransformerException | IOException e) {
            e.printStackTrace();
        }
    }

    private void copyFile(File originalPath, File destFile) throws IOException {
        try {
            InputStream is = new FileInputStream(originalPath);
            BufferedImage img = generateColoredIcon(ImageIO.read(is));
            ImageIO.write(img, "png", destFile);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private BufferedImage generateColoredIcon(BufferedImage image) {
        Color color = null;
        if (model.getColorCode() != null) {
            String colorString = model.getColorCode();
            color = ColorUtil.decodeColor(colorString);
        }
        if (color == null) return image;

        int width = image.getWidth();
        int height = image.getHeight();
        boolean hasAlpha = image.getColorModel().hasAlpha();

        BufferedImage newImage = BufferedImageTranscoder.getEmptyBufferedImage(width, height);
        WritableRaster raster = newImage.getRaster();
        for (int xx = 0; xx < width; xx++) {
            for (int yy = 0; yy < height; yy++) {
                int originalPixel = image.getRGB(xx, yy);
                int originalAlpha;
                if (hasAlpha) {
                    originalAlpha = new Color(originalPixel, true).getAlpha();
                } else {
                    // Due to ImageIO's issue, `hasAlpha` is assigned `false` although PNG file has alpha channel.
                    // Regarding PNG files of Material Icon, in this case, the file is 1bit depth binary(BLACK or WHITE).
                    // Therefore BLACK is `alpha = 0` and WHITE is `alpha = 255`
                    originalAlpha = originalPixel == 0xFF000000 ? 0 : 255;
                }

                int[] pixels = new int[4];
                pixels[0] = color.getRed();
                pixels[1] = color.getGreen();
                pixels[2] = color.getBlue();
                pixels[3] = ColorUtil.combineAlpha(originalAlpha, color.getAlpha());
                raster.setPixel(xx, yy, pixels);
            }
        }
        return newImage;
    }

    private void initLabelLink(JLabel label, final String url) {
        label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        label.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent event) {
                if (event.getClickCount() > 0) {
                    if (Desktop.isDesktopSupported()) {
                        Desktop desktop = Desktop.getDesktop();
                        try {
                            URI uri = new URI(url);
                            desktop.browse(uri);
                        } catch (IOException | URISyntaxException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    @Nullable
    @Override
    protected ValidationInfo doValidate() {
        if (StringUtils.isEmpty(comboBoxIcon.getInputText().trim())) {
            return new ValidationInfo(ERROR_ICON_NOT_SELECTED, comboBoxIcon);
        }

        if (StringUtils.isEmpty(textFieldFileName.getText().trim())) {
            return new ValidationInfo(ERROR_FILE_NAME_EMPTY, textFieldFileName);
        }

        if (!checkBoxMdpi.isSelected() && !checkBoxHdpi.isSelected() && !checkBoxXhdpi.isSelected()
                && !checkBoxXxhdpi.isSelected() && !checkBoxXxxhdpi.isSelected()) {
            return new ValidationInfo(ERROR_SIZE_CHECK_EMPTY, checkBoxMdpi);
        }

        File resourcePath = new File(model.getResourcePath());
        if (!resourcePath.exists() || !resourcePath.isDirectory()) {
            return new ValidationInfo(ERROR_RESOURCE_DIR_NOTHING_PREFIX + resourcePath, panelMain);
        }

        return null;
    }
}
