package hl7Viewer.gui;

import javax.swing.JButton;

public class Button extends JButton {

    public Button(String label, Runnable action) {
        super(label);
        setOpaque(true);
        setBorderPainted(false);
        setBackground(Utilities.SECONDARY_COLOR);
        setForeground(Utilities.TEXT_COLOR);
        addActionListener(e -> action.run());
    }
}
