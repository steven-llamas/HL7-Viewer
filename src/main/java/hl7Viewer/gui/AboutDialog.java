package hl7Viewer.gui;

import hl7Viewer.AppInfo;

import javax.swing.*;
import java.awt.*;

public class AboutDialog extends JDialog {

    public AboutDialog(final Frame owner, final Image icon) {
        super(owner, "About HL7 Viewer", true);
        buildUI(icon);
        pack();
        setResizable(false);
        setLocationRelativeTo(owner);
    }

    public void display() {
        setVisible(true);
    }

    private void buildUI(final Image icon) {
        final var panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        Utilities.setPanelColors(panel);
        panel.setBorder(Utilities.addPadding(20, 30, 20, 30));

        panel.add(centered(scaledIcon(icon)));
        panel.add(Box.createVerticalStrut(12));

        panel.add(centered(styledLabel("HL7 Viewer", 18, Font.BOLD)));
        panel.add(Box.createVerticalStrut(6));

        panel.add(centered(styledLabel("Version " + AppInfo.VERSION, 13, Font.PLAIN)));
        panel.add(Box.createVerticalStrut(4));

        panel.add(centered(styledLabel(AppInfo.BUILD_TYPE, 12, Font.ITALIC)));
        panel.add(Box.createVerticalStrut(16));

        panel.add(centered(closeButton()));

        Utilities.setPanelColors((JComponent) getContentPane());
        getContentPane().add(panel);
    }

    private static JLabel scaledIcon(final Image icon) {
        final var scaled = icon.getScaledInstance(64, 64, Image.SCALE_SMOOTH);
        return new JLabel(new ImageIcon(scaled));
    }

    private static JLabel styledLabel(final String text, final int size, final int style) {
        final var label = new JLabel(text);
        Utilities.setPanelColors(label);
        label.setFont(label.getFont().deriveFont(style, (float) size));
        return label;
    }

    private static JPanel centered(final JComponent component) {
        final var wrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        wrapper.setOpaque(false);
        wrapper.add(component);
        return wrapper;
    }

    private JButton closeButton() {
        final var btn = new Button("OK", this::dispose);
        btn.setPreferredSize(new Dimension(80, 30));
        return btn;
    }
}
