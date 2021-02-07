package com.github.dhaval2404.material_icon_generator;

import com.github.dhaval2404.material_icon_generator.constant.Theme;
import com.github.dhaval2404.material_icon_generator.util.BufferedImageTranscoder;
import com.github.dhaval2404.material_icon_generator.util.FileUtil;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
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
 * https://raw.githubusercontent.com/konifar/android-material-design-icon-generator-plugin/master/src/main/java/com/konifar/material_icon_generator/IconModel.java
 *
 */
public class IconModel {

    private static final String PATH_DRAWABLE_PREFIX = "drawable-";
    private static final String VECTOR_VIEWPORT_SIZE = "24.0";
    private static final String UNDERBAR = "_";
    private static final String PNG_SUFFIX = ".png";
    private static final String XML_SUFFIX = ".xml";

    private String iconName;
    private String displayColorName;
    private String colorCode;
    private String dp;
    private Theme theme;
    private String fileName;
    private String resDir;

    private boolean mdpi;
    private boolean hdpi;
    private boolean xhdpi;
    private boolean xxhdpi;
    private boolean xxxhdpi;

    private boolean isVectorType;
    private boolean drawable;

    private final Map<String, String> iconVersionMap;

    public IconModel(String iconName,
                     String displayColorName,
                     String colorCode,
                     String theme,
                     String dp,
                     String fileName,
                     String resDir,
                     boolean mdpi,
                     boolean hdpi,
                     boolean xhdpi,
                     boolean xxhdpi,
                     boolean xxxhdpi,
                     boolean isVectorType,
                     boolean drawable,
                     Map<String, String> iconVersionMap) {
        this.iconName = iconName;
        this.displayColorName = displayColorName;
        this.colorCode = colorCode;
        this.theme = Theme.getTheme(theme);
        this.dp = dp;
        this.fileName = fileName;
        this.resDir = resDir;
        this.mdpi = mdpi;
        this.hdpi = hdpi;
        this.xhdpi = xhdpi;
        this.xxhdpi = xxhdpi;
        this.xxxhdpi = xxxhdpi;
        this.isVectorType = isVectorType;
        this.drawable = drawable;
        this.iconVersionMap = iconVersionMap;
    }

    public Image getPreviewImage() {
        if (iconName != null) {
            int size = getSize() * 2;
            String url = theme.getPreviewUrl(getIconId(), iconVersionMap.get(iconName));
            return new BufferedImageTranscoder(url, size).getBufferedImage();
        } else {
            return null;
        }
    }

    /**
     * <p>
     * C:\Users\Dhaval\AppData\Local\Temp\material_icon_generator6659
     * ------>res
     * ------------>drawable
     * ------------------>baseline_3d_rotation_24.xml
     * ------------>drawable-hdpi
     * ------------------>baseline_3d_rotation_black_18.png(x1.5)
     * ------------------>baseline_3d_rotation_black_24.png(x1.5)
     * ------------------>baseline_3d_rotation_black_36.png(x1.5)
     * ------------------>baseline_3d_rotation_black_48.png(x1.5)
     * ------------>drawable-mdpi
     * ------------------>baseline_3d_rotation_black_18.png
     * ------------------>baseline_3d_rotation_black_24.png
     * ------------------>baseline_3d_rotation_black_36.png
     * ------------------>baseline_3d_rotation_black_48.png
     * ------------>drawable-xhdpi
     * ------------>drawable-xxhdpi
     * <p>
     *
     * @return Vector Icon File Path
     * @throws IOException throw error if failed to download
     */
    public File getVectorIcon(File iconDir) {
        //Resource Dir
        File resDir = new File(iconDir, "res");

        //Drawable Dir
        File drawableDir = new File(resDir, "drawable");

        //Icon Path
        //baseline_3d_rotation_24.xml
        String iconName = String.format("%s_%s_24.xml", theme.getValue(), getIconId());
        return new File(drawableDir, iconName);
    }

    public File getIcon(File iconDir, String size) {
        //Resource Dir
        File resDir = new File(iconDir, "res");

        //Drawable Dir
        File drawableDir = new File(resDir, PATH_DRAWABLE_PREFIX + size);

        //Icon Path
        //baseline_3d_rotation_black_24.png
        String iconName = String.format("%s_%s_black_%d.png", theme.getValue(), getIconId(), getSize());
        return new File(drawableDir, iconName);
    }

    public File getIconDirectory() throws IOException {
        if (iconName != null) {
            String iconId = getIconId();
            String version = iconVersionMap.get(iconName);
            String url = theme.getDownloadUrl(iconId, version);
            return FileUtil.getIconDirectory(url);
        } else {
            return null;
        }
    }

    private String getIconName(String shortName, String colorName) {
        return getIconName(shortName, colorName, this.theme, this.dp, isVectorType, true);
    }

    private String getIconName(String shortName, String colorName, Theme theme, String dp, boolean isVectorType, boolean isResource) {
        StringBuilder sb = new StringBuilder();
        sb.append(theme.getValue());
        sb.append(UNDERBAR);
        sb.append(shortName);
        sb.append(UNDERBAR);

        if (!isResource) {

            if (!isVectorType) {
                //Add Color for PNG image only
                sb.append(colorName);
                sb.append(UNDERBAR);
            }

            if (dp.contains("dp")) {
                dp = dp.replaceAll("dp", "");
            }
        } else {
            sb.append(colorName);
            sb.append(UNDERBAR);
        }

        sb.append(dp);

        String suffix = isVectorType ? XML_SUFFIX : PNG_SUFFIX;
        sb.append(suffix);
        return sb.toString();
    }

    public String getResourcePath() {
        return resDir;
    }

    public String getCopyPath(String size) {
        StringBuilder sb = new StringBuilder();
        sb.append(getResourcePath());
        sb.append(File.separator);
        sb.append(PATH_DRAWABLE_PREFIX);
        sb.append(size);
        sb.append(File.separator);
        sb.append(fileName);

        return sb.toString();
    }

    public String getVectorCopyPath(String dir) {
        StringBuilder sb = new StringBuilder();
        sb.append(getResourcePath());
        sb.append(File.separator);
        sb.append(dir);
        sb.append(File.separator);
        sb.append(fileName);

        return sb.toString();
    }

    public void setIconAndFileName(String iconName) {
        if (iconName == null) {
            this.iconName = "";
            this.fileName = "";
        } else {
            this.iconName = iconName;
            setFileNameFromIconName();
        }
    }

    public void setDpAndFileName(String dp) {
        this.dp = dp;
        setFileNameFromIconName();
    }

    public void setThemeAndFileName(String theme) {
        this.theme = Theme.getTheme(theme);
        setFileNameFromIconName();
    }

    public void setDisplayColorName(String displayColorName) {
        this.displayColorName = displayColorName;
        setFileNameFromIconName();
    }

    public void setVectorTypeAndFileName(boolean vectorType) {
        isVectorType = vectorType;
        setFileNameFromIconName();
    }

    private void setFileNameFromIconName() {
        String[] fileString = this.iconName.split("/");
        if (fileString.length > 1) this.fileName = getIconName(fileString[1], displayColorName);
    }

    public void setResDir(String resDir) {
        this.resDir = resDir;
    }

    public boolean isMdpi() {
        return mdpi;
    }

    public void setMdpi(boolean mdpi) {
        this.mdpi = mdpi;
    }

    public boolean isHdpi() {
        return hdpi;
    }

    public void setHdpi(boolean hdpi) {
        this.hdpi = hdpi;
    }

    public boolean isXhdpi() {
        return xhdpi;
    }

    public void setXhdpi(boolean xhdpi) {
        this.xhdpi = xhdpi;
    }

    public boolean isXxhdpi() {
        return xxhdpi;
    }

    public void setXxhdpi(boolean xxhdpi) {
        this.xxhdpi = xxhdpi;
    }

    public boolean isXxxhdpi() {
        return xxxhdpi;
    }

    public void setXxxhdpi(boolean xxxhdpi) {
        this.xxxhdpi = xxxhdpi;
    }

    public String getDp() {
        return dp;
    }

    public int getSize() {
        int size = 24;
        if (dp.contains("dp")) {
            size = Integer.parseInt(dp.replaceAll("dp", ""));
        }
        return size;
    }

    public String getViewportSize() {
        return VECTOR_VIEWPORT_SIZE;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getColorCode() {
        return colorCode;
    }

    public void setColorCode(String colorCode) {
        this.colorCode = colorCode;
    }

    public boolean isVectorType() {
        return isVectorType;
    }

    public boolean isDrawable() {
        return drawable;
    }

    public void setDrawable(boolean drawable) {
        this.drawable = drawable;
    }

    private String getIconId() {
        String[] fileString = iconName.split("/");
        if (fileString.length > 1) return fileString[1];
        else return iconName;
    }

}