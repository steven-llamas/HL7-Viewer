package hl7Viewer.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class Utilities {

    //method that allows you to specify how much padding you want
    public static EmptyBorder addPadding(int top, int left, int bottom, int right) {
        return new EmptyBorder(top, left, bottom, right);
    }

    //preconfigured Title border, just need panel obj and Title name
    public static void setTitledBorder(JComponent panel, String titleText) {
        TitledBorder border = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Theme.CONTROL_COLOR, 1),
                titleText,
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("SansSerif", Font.ITALIC, 12),
                Theme.TEXT_COLOR
        );
        panel.setBorder(border);
        panel.setBackground(Theme.BACKGROUND_COLOR);
        panel.setOpaque(true);
    }

    //used to set the background and text color for any panel jtext obj
    public static void setPanelColors(JComponent component) {
        component.setBackground(Theme.BACKGROUND_COLOR);
        component.setOpaque(true);
        component.setForeground(Theme.TEXT_COLOR);
    }

    //Sets Title for a panel
    public static void createAndSetTitle(JPanel mainPanel, String titleText) {
        var titleLabel = new JLabel(titleText, SwingConstants.CENTER);
        setPanelColors(titleLabel);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        titleLabel.setBorder(addPadding(8, 0, 8, 0));
        mainPanel.add(titleLabel, BorderLayout.NORTH);
    }

    //creates scroll pane and customizes it
    public static void createAndSetScrollPane(JTextArea textArea, JPanel panel) {
        var scrollPane = new JScrollPane(textArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setOpaque(false);
        scrollPane.setBackground(Theme.TRANSPARENT_COLOR);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getViewport().setBackground(Theme.TRANSPARENT_COLOR);
        scrollPane.setViewportBorder(null);
        panel.add(scrollPane, BorderLayout.CENTER);
    }

    //sets TextBox colors
    public static void setTextBox(JTextArea inputField, boolean isTransparentBackground, boolean isOpaque) {
        inputField.setLineWrap(true);
        inputField.setWrapStyleWord(true);
        inputField.setOpaque(isOpaque);
        inputField.setBackground(isTransparentBackground ? Theme.TRANSPARENT_COLOR : Theme.BACKGROUND_COLOR);
        inputField.setForeground(Theme.TEXT_COLOR);
        inputField.setCaretColor(Theme.TEXT_COLOR);
        inputField.setFont(new Font("Monospaced", Font.PLAIN, 12));
    }

    private Utilities() {}
}
