package com.github.dhaval2404.material_icon_generator.util;

import java.awt.Color;

/**
 * Color Utility
 * <p>
 * Created by Dhaval Patel on 24 January 2019.
 */
public class ColorUtil {

    /**
     * {@link Color#decode} only supports opaque colors. This replicates that code but adds support
     * for alpha stored as the highest byte.
     */
    public static Color decodeColor(String argbColor) throws NumberFormatException {
        long colorBytes = Long.decode(argbColor);
        if (argbColor.length() < 8) {
            colorBytes |= 0xFF << 24;
        }
        // Must cast to int otherwise java chooses the float constructor instead of the int constructor
        return new Color((int) (colorBytes >> 16) & 0xFF, (int) (colorBytes >> 8) & 0xFF, (int) colorBytes & 0xFF, (int) (colorBytes >> 24) & 0xFF);
    }

    public static int combineAlpha(int first, int second) {
        return (first * second) / 255;
    }

}
