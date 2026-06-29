package hl7Viewer.nonGui.config;

import hl7Viewer.nonGui.AbstractFileReaderWriter;

import java.util.HashMap;
import java.util.Map;

public class IniReaderWriter extends AbstractFileReaderWriter {
    private static IniReaderWriter iniReaderWriter = null;
    private final  Map<String, Map<String, String>> contents
            = new HashMap<>();
    private String section;

    private IniReaderWriter() {
        super("config.ini");
    }



    @Override
    protected void onReadLine(String line) {
        line = line.trim();
        var firstChar = line.charAt(0);

        if (firstChar == ';')
            return;

    }


    @Override
    protected void onWriteLine() {
        // do nothing
    }


    /**
     * <p>Used to grab the singleton class instance.
     * If the static field is null, then a new instance is created
     * and the same instance will be returned if called again<p/>
     * @return the singleton IniReaderWriter instance
     */
    public IniReaderWriter getInstance() {
        IniReaderWriter instance;
        if (iniReaderWriter == null) {
            instance = new IniReaderWriter();
            iniReaderWriter = instance;
        }

        return iniReaderWriter;
    }

}
