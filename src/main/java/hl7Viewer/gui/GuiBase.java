package hl7Viewer.gui;

import ca.uhn.hl7v2.model.Message;
import hl7Viewer.nonGui.parser.Hl7Parse;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class GuiBase extends JFrame {
    private Hl7Parse parse;
    private Hl7TablePanel hl7TablePanel;
    private JPanel contentPanel;
    //Constructor for the Swing window
    public GuiBase() {
        super("HL7 Viewer");

        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        setImageIcon();
        //used for MenuBar
        setJMenuBar(new MenuBar(this));

        setWarningOnExit();

        getContentPane().setBackground(Utilities.PRIMARY_COLOR);

        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);
        add(contentPanel, BorderLayout.CENTER);

        showHl7Viewer(); // Show the parser view initially
    }
    //sets Icon for GUI
    private void setImageIcon() {
        var iconURL = getClass().getResource("/images/important.jpg");
        var image = new ImageIcon(iconURL);
        setIconImage(image.getImage());
    }
    //creates popup on Exit
    private void setWarningOnExit() {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int option = JOptionPane.showConfirmDialog(GuiBase.this,
                        "Are you sure you want to exit", "Exit", JOptionPane.YES_NO_OPTION);
                if (option == JOptionPane.YES_OPTION) {
                    GuiBase.this.dispose();
                }
            }
        });
    }
    //used to display the HL7 message builder
    public void showMessageBuilderView() {
        var builderPanel = new MessageBuilderViewer();

        panelRefresher(new MessageBuilderViewer().createMessageBuilderPanel());
    }
    //Swappable HL7 Viewer Panel
    public void showHl7Viewer() {
        var outerPanel = new JPanel(new BorderLayout());
        outerPanel.setOpaque(false);
        Utilities.setTitle(outerPanel,"HL7 Viewer");

        var mainPanel = new JPanel(new GridLayout(1, 2));
        mainPanel.setOpaque(false);
        var messagePanel = createMessagePanel();
        var parsedPanel = createParsedViewPanel();

        mainPanel.add(messagePanel);
        mainPanel.add(parsedPanel);
        outerPanel.add(mainPanel, BorderLayout.CENTER);
        panelRefresher(outerPanel);
    }
    //text panel that is configured and includes key listener that sends text to be parsed
    private JPanel createMessagePanel() {
        var messagePanel = new JPanel(new BorderLayout());
        Utilities.setTitledBorder(messagePanel, "HL7 message to Parse");
        messagePanel.setOpaque(false);
        //textbox and listener
        var inputField = new JTextArea();
        inputField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (e.getKeyChar() == KeyEvent.VK_ENTER) {
                    String input = inputField.getText();
                    viewProcessedMsg(input);
                    inputField.setText("");
                    e.consume();
                }
            }
        });
        Utilities.setTextBox(inputField, true,false);
        inputField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Utilities.SECONDARY_COLOR, 2),
                Utilities.addPadding(10, 10, 10, 10)
        ));
        Utilities.createScrollPane(inputField, messagePanel);
        return messagePanel;
    }
    //method that calls the HL7 panel to display parsed message
    private JPanel createParsedViewPanel() {
        hl7TablePanel = new Hl7TablePanel();
        return hl7TablePanel;
    }
    //where the message is thrown to the parser in a try-catch
    private void viewProcessedMsg(String input) {
        try {
            var parser = new Hl7Parse(input);
            Message hl7Message = parser.getParsedMessage();
            hl7TablePanel.updateFromInput(hl7Message);
        } catch (Exception ex) {
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(null,
                        "Failed to parse HL7 message:\n" + ex.getMessage(),
                        "Parsing Error",
                        JOptionPane.ERROR_MESSAGE);
            });
        }
    }
    //removes panels and adds new panels
    private void panelRefresher(JPanel mainPanel) {
        contentPanel.removeAll();
        contentPanel.add(mainPanel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
}
