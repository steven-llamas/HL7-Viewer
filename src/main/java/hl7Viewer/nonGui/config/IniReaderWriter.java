package hl7Viewer.nonGui.config;

import hl7Viewer.nonGui.AbstractFileReaderWriter;


public class IniReaderWriter extends AbstractFileReaderWriter {

    public enum IniTokens {
        COMMENT(';'),
        SECTION_BEGIN('['),
        SECTION_END(']'),
        PAIR('=');

        public final char value;

        IniTokens(char value) {
            this.value = value;
        }

    }


    private static IniReaderWriter iniReaderWriter = null;

    private String sectionHeader;

    final private IniConfig config;

    /**
     * <p>Used to grab the singleton class instance.
     * If the static field is null, then a new instance is created
     * and the same instance will be returned if called again<p/>
     * @param config configuration class that values will be stored in memory to.
     * @return the singleton IniReaderWriter instance
     */
    public IniReaderWriter getInstance(final IniConfig config) {
        IniReaderWriter instance;
        if (iniReaderWriter == null) {
            instance = new IniReaderWriter(config);
            iniReaderWriter = instance;
        }

        return iniReaderWriter;
    }

    /**
     * <p>Used to grab the singleton class instance.
     * Overloaded method that makes it easier
     * <p/>
     * @return the singleton IniReaderWriter instance
     * @throws IllegalArgumentException If iniReaderWriter has not been instantiated.
     * This class requires a config object to be set first
     */
    public IniReaderWriter getInstance() throws IllegalArgumentException {
        if (iniReaderWriter == null)
            throw new IllegalArgumentException("Cannot use this overloaded getInstance method." +
                            " You'll need to use the method that expects a config argument");

        return iniReaderWriter;
    }


    public IniConfig getConfig() {
        return config;
    }


    @Override
    protected void onReadLine(final String line) {
        final var firstChar = line.charAt(0);

        if (firstChar == IniTokens.COMMENT.value)
            return;

        if(firstChar == IniTokens.SECTION_BEGIN.value) {
            if (line.charAt(line.length() - 1) == IniTokens.SECTION_END.value)
                sectionHeader = line.substring( 1, line.length() - 2).trim();

            return;
        }

        final var token = String.valueOf(IniTokens.PAIR.value);
        if(line.contains(token)) {
            final var pair = line.split(token, 2);
            final var key = pair[0].trim();
            final var value = pair[1].trim();

            config.assignValue(sectionHeader, key, value);
        }
    }


    @Override
    protected void onWriteLine() {
        // do nothing
    }


    private IniReaderWriter(final IniConfig config) {
        super("config.ini");
        this.config = config;
    }
}
