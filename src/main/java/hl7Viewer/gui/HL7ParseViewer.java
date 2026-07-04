package hl7Viewer.gui;

import hl7Viewer.AppInfo;
import hl7Viewer.nonGui.Logger;
import hl7Viewer.nonGui.config.IniConfig;
import hl7Viewer.nonGui.hl7Parser.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class HL7ParseViewer implements IView {
    private final IHL7Parser parser;

    private final HL7TableViewer hl7TableViewer;

    public HL7ParseViewer(final IHL7Parser parser,
                          final IniConfig config) {
        this.parser = parser;
        this.hl7TableViewer = new HL7TableViewer(config);
    }


    @Override
    public JPanel createPanel() {
        final var outerPanel = createAndSetHl7ViewerPanel();
        final var mainPanel = createAndSetInputAndOutputPanel();

        outerPanel.add(mainPanel, BorderLayout.CENTER);

        return outerPanel;
    }


    private static JPanel createAndSetHl7ViewerPanel() {
        final var hl7ViewerPanel = new JPanel(new BorderLayout());
        hl7ViewerPanel.setOpaque(false);
        Utilities.createAndSetTitle(hl7ViewerPanel, "HL7 Viewer");

        return hl7ViewerPanel;
    }


    private static void showErrorMessage(final String exceptionMessage) {
        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(
                null,
                "Failed to parse HL7 message:\n" + exceptionMessage,
                "Parsing Error",
                JOptionPane.ERROR_MESSAGE));
    }


    private static boolean clrTextIfNotEmpty(JTextArea messageTextBox) {
        if (!messageTextBox.getText().trim().isEmpty()) {
            messageTextBox.setText("");
            return true;
        }
        return false;
    }


    private static boolean pressedCtrlAndEnter(KeyEvent e) {
        final var buttonHeld = AppInfo.IS_MAC_OS
                ? e.isMetaDown()
                : e.isControlDown();
        return buttonHeld && e.getKeyCode() == KeyEvent.VK_ENTER;
    }


    private JPanel createAndSetInputAndOutputPanel() {
        final var inputAndOutputPanel = new JPanel(new GridLayout(1, 2));
        inputAndOutputPanel.setOpaque(false);
        final var messagePanel = createMessageInputPanel();

        inputAndOutputPanel.add(messagePanel);
        inputAndOutputPanel.add(hl7TableViewer);

        return inputAndOutputPanel;
    }


    private JPanel createMessageInputPanel() {
        final var messagePanel = new JPanel(new BorderLayout());
        messagePanel.setOpaque(false);
        Utilities.setTitledBorder(messagePanel, "HL7 message to Parse");

        final var messageTextBox = createAndSetMessageTextBox();
        addCtrlEnterKeyListener(messageTextBox);
        Utilities.createAndSetScrollPane(messageTextBox, messagePanel);

        final var btnPanel = new JPanel();
        btnPanel.setLayout(new BoxLayout(btnPanel, BoxLayout.Y_AXIS));
        btnPanel.setOpaque(false);

        final var topBtns = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 2));
        topBtns.setOpaque(false);
        topBtns.add(new Button("Parse HL7", () -> parseAndDisplay(messageTextBox.getText())));
        topBtns.add(new Button("Copy Table", hl7TableViewer::copyTableToClipboard));
        btnPanel.add(topBtns);

        final var botBtns = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 2));
        botBtns.setOpaque(false);
        botBtns.add(new Button("Clear Text", () -> {
            if (!clrTextIfNotEmpty(messageTextBox))
                JOptionPane.showMessageDialog(hl7TableViewer, "Textbox is already empty.");
        }));
        botBtns.add(new Button("Clear Table", hl7TableViewer::clearTable));
        btnPanel.add(botBtns);

        messagePanel.add(btnPanel, BorderLayout.SOUTH);
        return messagePanel;
    }


    private JTextArea createAndSetMessageTextBox() {
        var messageTextBox = new JTextArea();
        Utilities.setTextBox(messageTextBox, true, false);

        messageTextBox.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(Theme.CONTROL_COLOR, 2),
                        Utilities.addPadding(10, 10, 10, 10)
        ));

        return messageTextBox;
    }


    private void addCtrlEnterKeyListener(final JTextArea messageTextBox) {
        messageTextBox.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (pressedCtrlAndEnter(e)) {
                    parseAndDisplay(messageTextBox.getText());
                    e.consume();
                }
            }
        });
    }


    private void parseAndDisplay(final String input) {
        try {
            Logger.getInstance().logDebug("Parse triggered, input length: " + input.length());
            hl7TableViewer.displayMessage(parser.parse(input, new HL7Message()));

        } catch (IllegalArgumentException | NullPointerException ex) {
            showErrorMessage(ex.getMessage());
            Logger.getInstance().logError(ex.getMessage());
        }
    }
}
