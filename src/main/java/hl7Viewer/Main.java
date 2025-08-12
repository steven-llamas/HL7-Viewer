package hl7Viewer;

import hl7Viewer.gui.GuiBase;

import javax.swing.*;


public class Main {
    public static void main(String[] args) {
        //used to call the GUI
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                var GuiBase = new GuiBase();
                GuiBase.setVisible(true);
            }
        });
    }
}