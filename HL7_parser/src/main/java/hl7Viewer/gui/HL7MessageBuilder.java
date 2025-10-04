package hl7Viewer.gui;

import javax.swing.*;
import java.awt.*;

public class HL7MessageBuilder {

    private JTextArea _outputArea;
    //main panel creation
    public JPanel createMessageBuilderPanel() {
        var mainPanel = new JPanel(new BorderLayout());
        // Title at the top
        Utilities.createAndSetTitle(mainPanel, "HL7 Message Builder");
        // Split panel with two columns
        var splitPanel = new JPanel(new GridLayout(1, 2));
        splitPanel.add(messageCustomizationPanel());
        splitPanel.add(generatedMessagePanel());

        mainPanel.add(splitPanel, BorderLayout.CENTER);
        return mainPanel;
    }
    //creates message picker panel
    private JPanel messageCustomizationPanel() {
        var customizationPanel = new JPanel();
        customizationPanel.setLayout(new BoxLayout(customizationPanel, BoxLayout.Y_AXIS));
        customizationPanel.setBorder(BorderFactory.createTitledBorder("Customization"));
        Utilities.setTitledBorder(customizationPanel, "Hl7 Message Picker");

        var useJsonBtn = getJsonBtn(customizationPanel);
        customizationPanel.add(useJsonBtn);

        return customizationPanel;
    }
    //creates JSON button
    private static JButton getJsonBtn(JComponent parent) {
        var useJsonBtn = new JButton("JSON Editor");
        Utilities.setButtonColors(useJsonBtn);
        useJsonBtn.setVisible(true);
        useJsonBtn.setAlignmentX(Component.RIGHT_ALIGNMENT);
        useJsonBtn.addActionListener(e -> jsonEditorDialog(parent));

        return useJsonBtn;
    }
    //textbox for JSON editor
    private static void jsonEditorDialog(JComponent parent) {
        var dialog = new JDialog(SwingUtilities.getWindowAncestor(parent), "JSON Editor",
            Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(700, 600);
        dialog.setLocationRelativeTo(parent);

        var jsonTextArea = new JTextArea(10,30);
        jsonTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        Utilities.setTitledBorder(jsonTextArea, "JSON Message Customizer");
        Utilities.setTextBox(jsonTextArea, false, true);

        JScrollPane scrollPane = new JScrollPane(jsonTextArea);


        var applyBtn = new JButton("Apply");
        Utilities.setButtonColors(applyBtn);
        var cancelBtn = new JButton("Cancel");
        Utilities.setButtonColors(cancelBtn);

        var buttonPanel = new JPanel();
        Utilities.setPanelColors(buttonPanel);
        buttonPanel.add(applyBtn);
        buttonPanel.add(cancelBtn);

        var dialogPanel = new JPanel(new BorderLayout());
        dialogPanel.add(scrollPane, BorderLayout.CENTER);
        dialogPanel.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setContentPane(dialogPanel);

        applyBtn.addActionListener(ev -> {
            String jsonMessage = jsonTextArea.getText();
            // Aquí puedes agregar lógica para procesar el JSON,
            // actualizar el mensaje HL7, etc.
            System.out.println("JSON recibido:\n" + jsonMessage);
            dialog.dispose();
        });

        cancelBtn.addActionListener(ev -> dialog.dispose());

        dialog.setVisible(true);
    }
    //creates panel that will show generated message
    private JPanel generatedMessagePanel() {
        var messagePanel = new JPanel(new BorderLayout());
        Utilities.setTitledBorder(messagePanel, "Message Output");

        _outputArea = new JTextArea();
        Utilities.setPanelColors(_outputArea);
        _outputArea.setEditable(false);
        _outputArea.setFont(new Font("Monospaced", Font.PLAIN, 14));

       Utilities.createAndSetScrollPane(_outputArea, messagePanel);

        return messagePanel;
    }
    //will be used to set the output message
    public void setOutputMessage(String message) {
        _outputArea.setText(message);
    }
}
