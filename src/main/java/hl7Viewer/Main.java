package hl7Viewer;

import hl7Viewer.gui.MainForm;
import hl7Viewer.gui.Theme;
import hl7Viewer.nonGui.config.IniConfig;
import hl7Viewer.nonGui.config.IniReaderWriter;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {

        try {
            runSwingGuiOnEdt();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void runSwingGuiOnEdt() {
        final var rw = new IniReaderWriter();
        rw.read();

        final var config = new IniConfig(rw);
        Theme.loadFromConfig(config);
        final var program = new MainForm(config);
        SwingUtilities.invokeLater(() -> program.setVisible(true));
    }
}
