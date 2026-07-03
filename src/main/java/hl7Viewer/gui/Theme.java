package hl7Viewer.gui;

import hl7Viewer.nonGui.config.ConfigKey;
import hl7Viewer.nonGui.config.IniConfig;

import java.awt.Color;

public final class Theme {

    public static final Color TRANSPARENT_COLOR = new Color(0, 0, 0, 0);

    public static Color PRIMARY_COLOR   = Color.decode("#2F2D2D");
    public static Color SECONDARY_COLOR = Color.decode("#484444");
    public static Color TERTIARY_COLOR  = Color.decode("#616161");
    public static Color TEXT_COLOR      = Color.decode("#1aab00");

    public static void loadFromConfig(final IniConfig config) {
        PRIMARY_COLOR   = decode(config.getString(ConfigKey.COLOR_PRIMARY,   "#2F2D2D"), PRIMARY_COLOR);
        SECONDARY_COLOR = decode(config.getString(ConfigKey.COLOR_SECONDARY, "#484444"), SECONDARY_COLOR);
        TERTIARY_COLOR  = decode(config.getString(ConfigKey.COLOR_TERTIARY,  "#616161"), TERTIARY_COLOR);
        TEXT_COLOR      = decode(config.getString(ConfigKey.COLOR_TEXT,      "#1aab00"), TEXT_COLOR);
    }

    public static boolean isValidHex(final String hex) {
        if (hex == null || hex.isBlank()) return false;
        try {
            Color.decode(hex);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static Color decode(final String hex, final Color fallback) {
        return isValidHex(hex) ? Color.decode(hex) : fallback;
    }

    private Theme() {}
}
