package com.github.dhaval2404.material_icon_generator.constant;

/**
 * Supported Theme by Plugin
 * <p>
 * Created by Dhaval Patel on 24 January 2019.
 */
public enum Theme {
    FILL("baseline"),
    OUTLINE("outline"),
    ROUND("round"),
    TWO_TONE("twotone"),
    SHARP("sharp");

    private final String value;

    Theme(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public String getPreviewUrl(String iconName, String version) {
        String themeName = getIconType();
        return String.format("https://fonts.gstatic.com/s/i/%s/%s/v%s/24px.svg", themeName, iconName, version);
    }

    public String getDownloadUrl(String iconName, String version) {
        String themeName = getIconType();
        return String.format("https://fonts.gstatic.com/s/i/%s/%s/v%s/black-android.zip", themeName, iconName, version);
    }

    private String getIconType() {
        switch (this) {
            case OUTLINE:
                return "materialiconsoutlined";
            case ROUND:
                return "materialiconsround";
            case TWO_TONE:
                return "materialiconstwotone";
            case SHARP:
                return "materialiconssharp";
            case FILL:
            default:
                return "materialicons";
        }
    }

    public static Theme getTheme(String theme) {
        switch (theme.toLowerCase()) {
            case "outline":
                return Theme.OUTLINE;
            case "round":
                return Theme.ROUND;
            case "twotone":
                return Theme.TWO_TONE;
            case "sharp":
                return Theme.SHARP;
            case "fill":
            default:
                return Theme.FILL;
        }
    }

}
