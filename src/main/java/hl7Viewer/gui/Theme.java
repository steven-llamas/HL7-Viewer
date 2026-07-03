package hl7Viewer.gui;

import hl7Viewer.nonGui.config.ConfigKey;
import hl7Viewer.nonGui.config.IniConfig;

import java.awt.Color;

public final class Theme {
    public static final Color TRANSPARENT_COLOR = new Color(0, 0, 0, 0);

    public static Color BACKGROUND_COLOR = Color.decode("#2F2D2D");

    public static Color CONTROL_COLOR   = Color.decode("#484444");

    public static Color GRID_COLOR      = Color.decode("#616161");

    public static Color TEXT_COLOR      = Color.decode("#1aab00");

    private static final String HEX_FORMAT = "#%02x%02x%02x";


    public static void loadFromConfig(final IniConfig config) {
        BACKGROUND_COLOR = config.get(ConfigKey.BACKGROUND_COLOR, BACKGROUND_COLOR);
        CONTROL_COLOR    = config.get(ConfigKey.CONTROL_COLOR,    CONTROL_COLOR);
        GRID_COLOR       = config.get(ConfigKey.GRID_COLOR,       GRID_COLOR);
        TEXT_COLOR       = config.get(ConfigKey.TEXT_COLOR,        TEXT_COLOR);
    }

    public static boolean isValidHex(final String hex) {
        if (hex == null || hex.isBlank())
            return false;

        try {
            Color.decode(hex);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }


    public static String toHex(final Color color) {
        return String.format(HEX_FORMAT, color.getRed(), color.getGreen(), color.getBlue());
    }

    public static Color parseColor(final String hex, final Color fallback) {
        return isValidHex(hex) ? Color.decode(hex) : fallback;
    }

    private Theme() {}
}
