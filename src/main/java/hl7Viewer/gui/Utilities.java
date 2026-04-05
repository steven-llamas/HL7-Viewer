package hl7Viewer.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class Utilities {
    public static final Color TRANSPARENT_COLOR = new Color(0,0,0,0);
    public static final Color PRIMARY_COLOR = Color.decode("#2F2D2D");
    public static final Color SECONDARY_COLOR = Color.decode("#484444");
    public static final Color TERCIARY_COLOR = Color.decode("#616161");
    public static final Color TEXT_COLOR = Color.decode("#1aab00");


    //method that allows you to specify how much padding you want
    public static EmptyBorder addPadding(int top, int left, int bottom, int right ){
        return new EmptyBorder(top, left, bottom, right);
    }

    //preconfigured Title boarder, just need panel obj and Title name
    public static void setTitledBorder(JComponent panel, String titleText) {
        TitledBorder border = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(SECONDARY_COLOR, 1),
                titleText,
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("SansSerif", Font.ITALIC, 12),
                TEXT_COLOR
        );
        panel.setBorder(border);
        panel.setBackground(PRIMARY_COLOR);
        panel.setOpaque(true);
    }

    //used to set the background and text color for any panel jtext obj
    public static void setPanelColors(JComponent component) {
        component.setBackground(PRIMARY_COLOR);
        component.setOpaque(true);
        component.setForeground(TEXT_COLOR);
    }

    //Sets Title for message builder
    public static void createAndSetTitle(JPanel mainPanel, String titleText) {
        var titleLabel = new JLabel(titleText, SwingConstants.CENTER);
        setPanelColors(titleLabel);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 16f));
        titleLabel.setBorder(addPadding(8, 0, 8, 0));
        mainPanel.add(titleLabel, BorderLayout.NORTH);
    }

    //creates scroll pane and customizes it
    public static void createAndSetScrollPane(JTextArea JTextArea, JPanel JPanel) {
        var scrollPane = new JScrollPane(JTextArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setOpaque(false);
        scrollPane.setBackground(TRANSPARENT_COLOR);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getViewport().setBackground(TRANSPARENT_COLOR);
        scrollPane.setViewportBorder(null);

        JPanel.add(scrollPane, BorderLayout.CENTER);
    }

    //used to set color of buttons
    public static void setButtonColors(JButton useJsonBtn) {
        useJsonBtn.setBackground(SECONDARY_COLOR);
        useJsonBtn.setForeground(TEXT_COLOR);
    }

    //sets TextBox colors
    public static void setTextBox(JTextArea inputField, boolean isTransparentBackground, boolean isOpaque)  {
        inputField.setLineWrap(true);
        inputField.setWrapStyleWord(true);

        inputField.setOpaque(isOpaque);

        inputField.setBackground(isTransparentBackground ? TRANSPARENT_COLOR : PRIMARY_COLOR);

        inputField.setForeground(TEXT_COLOR);
        inputField.setCaretColor(TEXT_COLOR);

        inputField.setFont(new Font("Monospaced", Font.PLAIN, 12));
    }
}
