package hl7Viewer;

import hl7Viewer.gui.GuiBase;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        runSwingGuiOnEdt();
    }
//   Runs the GUI in Event Dispatch Thread (EDT)
    private static void runSwingGuiOnEdt() {
        var program = new GuiBase();
        SwingUtilities.invokeLater(
                () ->  program.setVisible(true)
        );
    }
}