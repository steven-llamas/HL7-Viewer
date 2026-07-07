package hl7Viewer.nonGui.config;


import hl7Viewer.gui.Theme;
import hl7Viewer.nonGui.Logger;

import java.awt.Color;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;


public class IniConfig {
    private final Map<String, String> configMap;

    private final IniIO readerWriter;

    private final ArrayList<String> outputList = new ArrayList<>();


    /**
     * Constructs a new {@link  IniConfig}
     *
     * @param readerWriter {@link IniIO} object that is then assigned to {@link #readerWriter}.
     *          If object has items in its output map we then we construct new map and copy them over,
     *          otherwise the map is empty. then we
     *
     */
    public IniConfig(final IniIO readerWriter) {
        this.readerWriter = readerWriter;

        if (readerWriter.outConfigMapHasItems()) {
            configMap = new LinkedHashMap<>(readerWriter.getOutConfigMap());
            readerWriter.clearOutConfigMap();
        } else {
            configMap = new LinkedHashMap<>();
        }
    }


    /** Builds the internal map key used to store and look up a section/key pair. */
    public static String makeMapKey(final String section, final String key) {
        return section + IniIO.IniTokens.KEY_SEPARATOR.value + key;
    }


    /** Returns the Color for the given key, or {@code defaultValue} if absent or invalid hex. */
    public Color get(final ConfigKey key, final Color defaultValue) {
        final var value = configMap.get(makeMapKey(key.section, key.key));
        return (value == null || value.isEmpty())
                ? defaultValue
                : Theme.parseColor(value, defaultValue);
    }


    /** Returns the boolean value for the given key, or {@code defaultValue} if absent. */
    public boolean get(final ConfigKey key, final boolean defaultValue) {
        final var value = configMap.get(makeMapKey(key.section, key.key));
        return (value == null || value.isEmpty())
                ? defaultValue
                : Boolean.parseBoolean(value);
    }


    /** Returns the int value for the given key, or {@code defaultValue} if absent or unparseable. */
    public int get(final ConfigKey key, final int defaultValue) {
        final var value = configMap.get(makeMapKey(key.section, key.key));
        if (value == null || value.isEmpty())
            return defaultValue;

        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }


    /** Returns the string value for the given key, or {@code defaultValue} if absent. */
    public String get(final ConfigKey key, final String defaultValue) {
        final var value = configMap.get(makeMapKey(key.section, key.key));
        return (value == null || value.isEmpty())
                ? defaultValue
                : value;
    }


    public void set(final ConfigKey key, final int value) {
        set(key.section, key.key, value);
    }


    public void set(final ConfigKey key, final boolean value) {
        set(key.section, key.key, value);
    }


    public void set(final ConfigKey key, final String value) {
        if (value != null && !value.trim().isBlank())
            set(key.section, key.key, value);
    }


    public void set(final ConfigKey key, final Color value) {
        if (value != null)
            set(key.section, key.key, Theme.toHex(value));
    }


    /** Sets a value in memory. Changes are not persisted until {@link #save()} is called. */
    private void set(final String section, final String key, final Object newValue) {
        final var mapKey = makeMapKey(section, key);
        configMap.put(mapKey, (newValue != null) ? String.valueOf(newValue) : "");
    }


    /** Writes the current config back to the INI file, as long as {@link #configMap} is not empty
     * @return bool depending on whether {@code IniReaderWriter.write()} is successful*/
    public boolean save() {
        if(configMap.isEmpty()) {
            if (Logger.isConfigured())
                Logger.getInstance().logDebug("Config save skipped, map empty");
            return true;
        }

        buildOutputList();
        final var success = readerWriter.write(outputList, false);
        if (Logger.isConfigured())
            Logger.getInstance().logInfo(success ? "Config saved" : "Config save failed");
        return success;
    }

    // formats configMap into INI lines grouped by section, with a blank line between sections
    private void buildOutputList() {
        outputList.clear();

        final var keySepValue       = IniIO.IniTokens.KEY_SEPARATOR.value;
        final var pairSepValue      = IniIO.IniTokens.PAIR.value;
        final var sectionBeginValue = IniIO.IniTokens.SECTION_BEGIN.value;
        final var sectionEndValue   = IniIO.IniTokens.SECTION_END.value;

        String currentSection = null;
        for (final var entry : configMap.entrySet()) {
            final var dotIndex  = entry.getKey().indexOf(keySepValue);
            final var section   = entry.getKey().substring(0, dotIndex);
            final var key       = entry.getKey().substring(dotIndex + 1);

            if (!section.equals(currentSection)) {
                final var sectionHeader = (currentSection != null ? "\n" : "")
                        + sectionBeginValue
                        + section
                        + sectionEndValue;
                outputList.add(sectionHeader);
                currentSection = section;
            }

            outputList.add(key + pairSepValue + entry.getValue());
        }
    }
}
