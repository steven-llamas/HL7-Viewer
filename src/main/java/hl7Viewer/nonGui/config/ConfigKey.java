package hl7Viewer.nonGui.config;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Central registry of all INI config section/key pairs used by the application.
 * contains {@link #section}, {@link #key}
 */
public enum ConfigKey {
    SCREEN_WIDTH ("Application", "screen_width",""),
    SCREEN_HEIGHT("Application", "screen_height",""),

    BOLD_HL7_INDEX  ("HL7Setting", "bold_index",        "Whether the HL7 segment index column is displayed in bold."),
    IGNORE_MSH_CHECK("HL7Setting", "ignore_msh_check", "Skips the MSH segment presence check during parsing."),

    BACKGROUND_COLOR("Theme", "primary_color",   "Background color for panels and dialogs."),
    CONTROL_COLOR  ("Theme", "secondary_color", "Border and button background color."),
    GRID_COLOR     ("Theme", "tertiary_color",  "Grid and separator color."),
    TEXT_COLOR     ("Theme", "text_color",      "Foreground text and accent color."),

    LOG_LEVEL      ("Logging", "log_level",    "Minimum log level threshold for output (TRACE, DEBUG, ERROR, FATAL).");



    public final String section;
    public final String key;
    public final String description;

    ConfigKey(final String section,
              final String key,
              final String description) {
        this.section = section;
        this.key = key;
        this.description = description;
    }

    public String configName() {
        return Arrays.stream(name().split("_"))
                .map(s -> Character.toUpperCase(s.charAt(0)) + s.substring(1).toLowerCase())
                .collect(Collectors.joining(" "));
    }

}
