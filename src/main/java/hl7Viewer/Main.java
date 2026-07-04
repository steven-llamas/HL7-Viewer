package hl7Viewer;

import hl7Viewer.gui.MainForm;
import hl7Viewer.gui.Theme;
import hl7Viewer.nonGui.Logger;
import hl7Viewer.nonGui.LoggerIO;
import hl7Viewer.nonGui.config.ConfigKey;
import hl7Viewer.nonGui.config.IniConfig;
import hl7Viewer.nonGui.config.IniReaderWriter;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {

        try {
            initialize();
        } catch (Exception ex) {
            if (AppInfo.IS_DEBUG) {
                System.err.println(ex);
                ex.printStackTrace();
            }
        }
    }

    private static void initialize() {
        final var rw = new IniReaderWriter();
        rw.read();

        final var config = new IniConfig(rw);
        Theme.loadFromConfig(config);

        Logger.configure(new LoggerIO(), config);
        Logger.getInstance().logDebug("Logger configured, log level: " +
                Logger.LogLevel.toLogLevel(config.get(ConfigKey.LOG_LEVEL, Logger.LogLevel.ERROR.level)).toString());

        final var program = new MainForm(config);
        SwingUtilities.invokeLater(() -> program.setVisible(true));
    }
}
