package hl7Viewer;

import hl7Viewer.gui.MainForm;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {

        try {
            runSwingGuiOnEdt();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

//   Runs the GUI in Event Dispatch Thread (EDT)
    private static void runSwingGuiOnEdt() {
        final var program = new MainForm();
        SwingUtilities.invokeLater(
                () ->  program.setVisible(true)
        );
    }
}
