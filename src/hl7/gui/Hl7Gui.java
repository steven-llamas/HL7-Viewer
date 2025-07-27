package hl7.gui;

import javax.swing.*;

public class Hl7Gui {

    public Hl7Gui(){
        var frame = new JFrame("HL7 Parser");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);

        var label = new JLabel("Wellcome to Hl7 Viewer!");
        frame.add(label);
        frame.setVisible(true);

    }
}
