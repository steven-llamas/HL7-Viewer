package hl7Viewer.nonGui.config;

/**
 * Central registry of all INI config section/key pairs used by the application.
 * contains {@link #section}, {@link #key}
 */
public enum ConfigKey {
    SCREEN_WIDTH ("Application", "screen_width","Width of the main application window in pixels."),
    SCREEN_HEIGHT("Application", "screen_height","Height of the main application window in pixels."),

    BOLD_HL7_INDEX  ("HL7Setting", "bold_index", "Whether the HL7 segment index column is displayed in bold."),
    IGNORE_MSH_CHECK("HL7Setting", "ignore_msh_check","Skips the MSH segment presence check during parsing.");


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

}
