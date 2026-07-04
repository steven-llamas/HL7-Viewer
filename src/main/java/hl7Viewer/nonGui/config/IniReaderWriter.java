package hl7Viewer.nonGui.config;

import hl7Viewer.AppInfo;
import hl7Viewer.nonGui.AbstractFileReaderWriter;
import hl7Viewer.nonGui.Logger;

import java.util.LinkedHashMap;
import java.util.Map;


/**
 * Reads and writes INI files. Parsed key-value pairs are stored in {@code outConfigMap}
 * keyed as {@code section + IniTokens.KEY_SEPARATOR.value + key}
 * and are intended to be transferred to {@link IniConfig}.
 * Comments and blank lines are discarded on read and are not written back on save.
 */
public class IniReaderWriter extends AbstractFileReaderWriter {

    /**
     * Character tokens that define the INI file format.
     * Used during both parsing and output generation.
     */
    public enum IniTokens {
        COMMENT(';'),
        SECTION_BEGIN('['),
        SECTION_END(']'),
        PAIR('='),
        KEY_SEPARATOR('.');

        public final char value;

        IniTokens(char value) {
            this.value = value;
        }
    }



    private String sectionHeader;

    final private Map<String, String> outConfigMap = new LinkedHashMap<>();


    /**
     * Constructs a new {@link  IniReaderWriter} and sets
     * its parent class filepath as the default {@code config.ini} for later methods to use.
     *
     */
    public IniReaderWriter() {
        super(AppInfo.CONFIG_PATH);
    }

    /**
     * Constructs a new {@link  IniReaderWriter} and sets
     * parent class filepath for later methods to use.
     *
     * @param filePath filepath of where the file is located
     */
    IniReaderWriter(final String filePath) {
        super(filePath);
    }


    /** Returns the parsed key-value pairs keyed
     * as {@code section + IniTokens.KEY_SEPARATOR.value + key}. */
    public Map<String, String> getOutConfigMap() {
        return outConfigMap;
    }


    /** Clears all parsed entries from the map. */
    public void clearOutConfigMap() {
        outConfigMap.clear();
    }


    /** Returns {@code true} if the map contains at least one parsed entry. */
    public boolean outConfigMapHasItems() {
        return !outConfigMap.isEmpty();
    }


    /**
     * Parses a single line from the INI file.
     * Called from {@link #read()}. Ignores comments, extracts section headers,
     * and stores valid key=value pairs into {@code outConfigMap}.
     * @param line String line that is passed into by {@link #read()}
     */
    @Override
    protected void onReadLine(final String line) {
        final var firstChar = line.charAt(0);

        if (firstChar == IniTokens.COMMENT.value)
            return;

        if(firstChar == IniTokens.SECTION_BEGIN.value) {
            final var lineEnd = line.length() - 1;

            if (line.charAt(lineEnd) == IniTokens.SECTION_END.value)
                sectionHeader = line.substring( 1, lineEnd).trim();

            return;
        }


        final var token = String.valueOf(IniTokens.PAIR.value);
        if(line.contains(token)) {
            final var pair = line.split(token, 2);
            final var key = pair[0].trim();
            final var value = pair[1].trim();

            if (!key.isEmpty() && !value.isEmpty()) {
                final var configKey = IniConfig.makeMapKey(sectionHeader, key);
                outConfigMap.putIfAbsent(configKey, value);

                final var msg = "Config loaded: " + configKey + " = " + value;
                try {
                    Logger.getInstance().logTrace(msg);
                } catch (IllegalStateException ignored) {
                    System.out.println("Config loaded: " + configKey + " = " + value);
                }
            }
        }
    }
}
