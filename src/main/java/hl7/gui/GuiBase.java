package hl7.gui;

import hl7.parser.Hl7Parse;
import hl7.segments.MshSegment;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


public class GuiBase extends JFrame{
    private JPanel messagePanel;
    private Hl7Parse parse;
    private JTable parsedTable;
    private JPanel parsedViewPanel;
    private DefaultTableModel tableModel;

    public GuiBase(){
        super("Hl7 Parser");

        setSize(1000, 600);
        setLocationRelativeTo(null); //loads GUi onto center of screen
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        var iconURL = getClass().getResource("/images/important.jpg");
        var image = new ImageIcon(iconURL);
        setIconImage(image.getImage());
        addMenuBar();

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

    private void addMenuBar(){
        var menuBar = new JMenuBar();
        menuBar.setBackground(Utilities.SECONDARY_COLOR);
        menuBar.setBorder(BorderFactory.createEmptyBorder());
        var menuFile = new JMenu("Shh ");
        var menuItemExit = new JMenuItem("Exit");
        menuBar.add(menuFile);
        setJMenuBar(menuBar);


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
        parsedViewPanel = new JPanel(new BorderLayout());
        parsedViewPanel.setOpaque(false);

        String[] columnNames = {"Segment", "Value"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        parsedTable = new JTable(tableModel);
        parsedTable.setOpaque(false);
        parsedTable.setBackground(Utilities.TRANSPARENT_COLOR);
        parsedTable.setForeground(Utilities.TEXT_COLOR);
        parsedTable.setGridColor(Utilities.TERCIARY_COLOR);
        parsedTable.setFont(new Font("SansSerif", Font.PLAIN, 12));
        parsedTable.setRowHeight(22);
        parsedTable.setFillsViewportHeight(true);
        parsedTable.getTableHeader().setReorderingAllowed(false);
        parsedTable.getTableHeader().setResizingAllowed(false);

        var header = parsedTable.getTableHeader();
        header.setOpaque(true);
        header.setBackground(Utilities.PRIMARY_COLOR);
        header.setForeground(Utilities.TEXT_COLOR);
        header.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Utilities.TERCIARY_COLOR));

        var scrollPane = new JScrollPane(parsedTable);
        scrollPane.setOpaque(false);
        scrollPane.setBackground(Utilities.TRANSPARENT_COLOR);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getViewport().setBackground(Utilities.TRANSPARENT_COLOR);
        scrollPane.setViewportBorder(null);

        parsedViewPanel.add(scrollPane, BorderLayout.CENTER);
        return parsedViewPanel;
    }


    private void viewProcessedMsg(String input){
        try{
            this.parse = new Hl7Parse(input);
            MshSegment msh = (MshSegment) parse.getMshSegment();
            tableModel.setRowCount(0);


            Object[][] tableData = {
                    {"Encoding Characters", msh.getEncodingCharacters()},
                    {"Sending Application", msh.getSendingApplication()},
                    {"Sending Facility", msh.getSendingFacility()},
                    {"Receiving Application", msh.getReceivingApplication()},
                    {"Receiving Facility", msh.getReceivingFacility()},
                    {"Date/Time of Message", msh.getDateTimeOfMessage()},
                    {"Security", msh.getSecurity()},
                    {"Message Type", msh.getMessageType()},
                    {"Message Control ID", msh.getMessageControlId()},
                    {"Processing ID", msh.getProcessingId()},
                    {"Version ID", msh.getVersionId()},
                    {"Sequence Number", msh.getSequenceNumber()},
                    {"Continuation Pointer", msh.getContinuationPointer()},
                    {"Accept Ack Type", msh.getAcceptAckType()},
                    {"App Ack Type", msh.getAppAckType()},
                    {"Country Code", msh.getCountryCode()},
                    {"Character Set", msh.getCharacterSet()},
                    {"Principal Language", msh.getPrincipalLangOfMessage()}
            };
            for (Object[] row : tableData){
                tableModel.addRow(row);
            }

        } catch (IllegalArgumentException e ){
            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage(),
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }


}
