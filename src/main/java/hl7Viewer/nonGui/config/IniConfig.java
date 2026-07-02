package hl7Viewer.nonGui.config;

import hl7Viewer.nonGui.AbstractFileReaderWriter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;


public class IniConfig {


    private final Map<String, String> configMap;

    private final IniReaderWriter readerWriter;

    private final ArrayList<String> outputList = new ArrayList<>();



    public IniConfig(final IniReaderWriter readerWriter) {
        this.readerWriter = readerWriter;

        configMap = (readerWriter.outConfigMapHasItems())
                ? new LinkedHashMap<>(readerWriter.getOutConfigMap())
                : new LinkedHashMap<>();

        if(readerWriter.outConfigMapHasItems())
            readerWriter.clearOutConfigMap();
    }


    /** Builds the internal map key used to store and look up a section/key pair. */
    public static String makeMapKey(final String section, final String key) {
        return section + IniReaderWriter.IniTokens.KEY_SEPARATOR.value + key;
    }

    /** Returns the boolean value for the given section/key,
     *  or {@code defaultValue} if absent. */
    public boolean getBoolean(final String section,
                              final String key,
                              final boolean defaultValue) {
        final var value = configMap.get(makeMapKey(section, key));
        return (value == null || value.isEmpty())
                ? defaultValue
                : Boolean.parseBoolean(value);
    }

    /** Returns the int value for the given section/key, or {@code defaultValue} if absent or unparseable. */
    public int getInt(final String section, final String key, final int defaultValue) {
        final var value = configMap.get(makeMapKey(section, key));
        if (value == null || value.isEmpty())
            return defaultValue;

        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /** Returns the string value for the given section/key, or {@code defaultValue} if absent. */
    public String getString(final String section, final String key, final String defaultValue) {
        final var value = configMap.get(makeMapKey(section, key));
        return (value == null || value.isEmpty())
                ? defaultValue
                : value;
    }


    /** Sets a value in memory. Changes are not persisted until {@link #save()} is called. */
    public void set(final String section, final String key, final Object newValue) {
        final var mapKey = makeMapKey(section, key);
        configMap.put(mapKey, (newValue != null) ? String.valueOf(newValue) : "");
    }


    /** Writes the current config back to the INI file, as long as {@link #configMap} is not empty */
    public void save() {
        if(configMap.isEmpty())
            return;

        buildOutputList();
        readerWriter.write(outputList, false);
    }

    // formats configMap into INI lines grouped by section, with a blank line between sections
    private void buildOutputList() {
        outputList.clear();

        final var keySepValue       = IniReaderWriter.IniTokens.KEY_SEPARATOR.value;
        final var pairSepValue      = IniReaderWriter.IniTokens.PAIR.value;
        final var sectionBeginValue = IniReaderWriter.IniTokens.SECTION_BEGIN.value;
        final var sectionEndValue   = IniReaderWriter.IniTokens.SECTION_END.value;

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
