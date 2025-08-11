package hl7Viewer.gui;

import ca.uhn.hl7v2.model.Message;
import hl7Viewer.nonGui.parser.Hl7Parse;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


public class GuiBase extends JFrame{
    private Hl7Parse parse;
    private Hl7TablePanel hl7TablePanel;


    public GuiBase(){
        super("Hl7 Parser");

        setSize(1000, 600);
        setLocationRelativeTo(null); //loads GUi onto center of screen
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        var iconURL = getClass().getResource("/images/important.jpg");
        var image = new ImageIcon(iconURL);
        setIconImage(image.getImage());
        //adding and setting a menubar to the application
        setJMenuBar(new MenuBar());

        //creates confirmation popup when exiting
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int option = JOptionPane.showConfirmDialog(GuiBase.this,
                        "Are you sure you want to exit","Exit", JOptionPane.YES_NO_OPTION);
                if(option == JOptionPane.YES_OPTION){
                    GuiBase.this.dispose();
                }
            }
        });



        getContentPane().setBackground(Utilities.PRIMARY_COLOR);
        //addGuiComponents();
        addHl7ViewerComponents();
    }


    private void addHl7ViewerComponents() {
        var mainPanel = new JPanel(new GridLayout(1, 2));
        mainPanel.setOpaque(false);

        var messagePanel = createMessagePanel();
        var parsedPanel = createParsedViewPanel();

        mainPanel.add(messagePanel);
        mainPanel.add(parsedPanel);

        add(mainPanel, BorderLayout.CENTER);
    }

    //used to create the textbox
    private JPanel createMessagePanel() {
        var messagePanel = new JPanel(new BorderLayout());
        messagePanel.setOpaque(false);

        var inputField = new JTextArea();
        inputField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if(e.getKeyChar() == KeyEvent.VK_ENTER){
                    String input = inputField.getText();
                    viewProcessedMsg(input);
                    inputField.setText("");
                    e.consume();
                }
            }
        });

        inputField.setLineWrap(true);
        inputField.setWrapStyleWord(true);
        inputField.setOpaque(false);
        inputField.setBackground(Utilities.TRANSPARENT_COLOR);
        inputField.setForeground(Utilities.TEXT_COLOR);
        inputField.setCaretColor(Utilities.TEXT_COLOR);
        inputField.setFont(new Font("Monospaced", Font.PLAIN, 14));
        inputField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Utilities.SECONDARY_COLOR, 2),
                Utilities.addPadding(10, 10, 10, 10)
        ));
        var scrollPane = new JScrollPane(inputField);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        messagePanel.add(scrollPane, BorderLayout.CENTER);
       return messagePanel;
    }

    private JPanel createParsedViewPanel() {
        //creates JTable which is where we can view the parsed Hl7 message
        hl7TablePanel = new Hl7TablePanel();
       return hl7TablePanel;
    }


    private void viewProcessedMsg(String input){
        try {

            var parser = new Hl7Parse(input);
            Message hl7Message =  parser.getParsedMessage();

            hl7TablePanel.updateFromInput(hl7Message);

        }catch (Exception ex){
            javax.swing.SwingUtilities.invokeLater(() -> {
                javax.swing.JOptionPane.showMessageDialog(null,
                        "Failed to parse HL7 message:\n" + ex.getMessage(),
                        "Parsing Error",
                        javax.swing.JOptionPane.ERROR_MESSAGE);
            });
        }
        }
}
