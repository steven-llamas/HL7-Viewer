package hl7Viewer.gui;

import hl7Viewer.nonGui.parser.*;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class HL7ParseViewer {
    private HL7TableViewer hl7TableViewer;


    public JPanel createPanel() {
        var outerPanel = createAndSetHl7ViewerPanel();
        var mainPanel = createAndSetInputAndOutputPanel();

        outerPanel.add(mainPanel, BorderLayout.CENTER);

        return outerPanel;
    }


    private static JPanel createAndSetHl7ViewerPanel() {
        var hl7ViewerPanel = new JPanel(new BorderLayout());
        hl7ViewerPanel.setOpaque(false);
        Utilities.createAndSetTitle(hl7ViewerPanel, "HL7 Viewer");

        return hl7ViewerPanel;
    }


    private JPanel createAndSetInputAndOutputPanel() {
        var inputAndOutputPanel = new JPanel(new GridLayout(1, 2));
        inputAndOutputPanel.setOpaque(false);

        hl7TableViewer = new HL7TableViewer();
        var messagePanel = createMessageInputPanel();

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

        final var parseButton = createParseBtn(messageTextBox);
        final var clearButton = createClearBtn(messageTextBox);

        final var buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        buttonPanel.setOpaque(false);
        buttonPanel.add(clearButton);
        buttonPanel.add(parseButton);

        messagePanel.add(buttonPanel,BorderLayout.SOUTH);

        return messagePanel;
    }


    private JTextArea  createAndSetMessageTextBox() {
        var messageTextBox = new JTextArea();
        Utilities.setTextBox(messageTextBox, true, false);

        messageTextBox.setBorder(
                BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Utilities.SECONDARY_COLOR, 2),
                    Utilities.addPadding(10, 10, 10, 10)
        ));

        return messageTextBox;
    }


    private JButton createParseBtn(final JTextArea messageTextBox) {
        final JButton parseButton = new JButton("Parse Message");

        parseButton.setOpaque(true);
        parseButton.setBorderPainted(false);
        Utilities.setButtonColors(parseButton);
        parseButton.addActionListener(e -> {
            handleMessage(messageTextBox);
        } );

        return parseButton;
    }


    private JButton createClearBtn(final JTextArea messageTextBox) {
        final JButton clearButton = new JButton("Clear Text");

        clearButton.setOpaque(true);
        clearButton.setBorderPainted(false);
        Utilities.setButtonColors(clearButton);
        clearButton.addActionListener(e -> {
            messageTextBox.setText("");
        });

        return clearButton;
    }


    private void addCtrlEnterKeyListener(final JTextArea messageTextBox) {
        messageTextBox.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (pressedCtrlAndEnter(e)) {
                    handleMessage(messageTextBox);
                    e.consume();
                }
            }
        });
    }


    private void handleMessage(JTextArea textBox) {
        parseAndDisplay(textBox.getText());
        //textBox.setText("");
    }


    private static boolean pressedCtrlAndEnter(KeyEvent e) {
        return e.isControlDown() && e.getKeyCode() == KeyEvent.VK_ENTER;
    }


    private void parseAndDisplay(String input) {
        try {

            IMessageParser parser = new BasicMessageParser();
            var parsedMsg = new HL7Message();
            parsedMsg = parser.parse(input);
            hl7TableViewer.displayMessage(parsedMsg);
        } catch (Exception ex) {
            showErrorMessage(ex.getMessage());
        }
    }


    private static void showErrorMessage(String exceptionMessage) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(
                    null,
                    "Failed to parse HL7 message:\n" + exceptionMessage,
                    "Parsing Error",
                    JOptionPane.ERROR_MESSAGE);
        });
    }
}


class HL7TableViewer extends JPanel {
    private JTable jTable;
    private DefaultTableModel hl7TableData;


    public HL7TableViewer() {
        setLayout(new BorderLayout());
        setOpaque(false);
        initializeTable();
    }


    private void initializeTable() {

        final String[] columnNames = {"Index", "Value"};

        hl7TableData = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        jTable = new JTable(hl7TableData);
        jTable.setOpaque(false);
        jTable.setBackground(Utilities.TRANSPARENT_COLOR);
        jTable.setForeground(Utilities.TEXT_COLOR);
        jTable.setGridColor(Utilities.TERCIARY_COLOR);
        jTable.setFont(new Font("SansSerif", Font.PLAIN, 12));
        jTable.setRowHeight(22);
        jTable.setFillsViewportHeight(true);
        setupHeaderRenderer(jTable);

        final var header = jTable.getTableHeader();
        header.setReorderingAllowed(false);
        header.setResizingAllowed(false);
        header.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Utilities.TERCIARY_COLOR));
        Utilities.setPanelColors(header);

        final var scrollPane = getJScrollPane();
        add(scrollPane, BorderLayout.CENTER);
    }


    private JScrollPane getJScrollPane() {
        var scrollPane = new JScrollPane(jTable);
        scrollPane.setOpaque(false);
        scrollPane.setBackground(Utilities.TRANSPARENT_COLOR);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getViewport().setBackground(Utilities.TRANSPARENT_COLOR);
        scrollPane.setViewportBorder(null);
        return scrollPane;
    }


    public void displayMessage(final HL7Message hl7Message) {
        hl7TableData.setRowCount(0);

        if (!displayEachRow(hl7Message)) {
            JOptionPane.showMessageDialog(this,
                    "Failed to display HL7 fields.",
                    "Parsing Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }


    private boolean displayEachRow(final HL7Message hl7Message) {
        for (var i = 0; i < hl7Message.getSegments().size(); i++ ) {
            final var segment = hl7Message.getSegments().get(i);

            for (var j = 0; j < segment.getFieldList().size(); j++) {
                final var field = segment.getFieldList().get(j);

                for (var k = 0; k < field.getRepetitionList().size(); k++ ) {
                    final var repetition = field.getRepetitionList().get(k);

                    for (var l = 0; l < repetition.getComponentList().size(); l++) {
                        final var comp = repetition.getComponentList().get(l);

                        for (var m = 0; m < comp.getSubcomponentList().size(); m++ ) {
                            final String value          = comp.getSubcomponentList().get(m);
                            final String segHeader      = segment.getSegmentName();
                            final StringBuilder index = calculateRowIndex(
                                    segHeader,
                                    j, field,
                                    k, repetition,
                                    l, comp, m);
                            
                            if (!value.trim().isEmpty())
                                hl7TableData.addRow(new Object[]{
                                    index, value
                                } );
                        }
                    }
                }
            }
        }

        return hl7TableData.getRowCount() != 0;
    }


    private static StringBuilder calculateRowIndex(
            final String segHeader,
            final int _fieldIndex,
            final HL7Field field,
            final int _repIndex,
            final HL7Repetition repetition,
            final int _compIndex,
            final HL7Component comp,
            final int _subcomponentIndex) {
        final StringBuilder index   = new StringBuilder(segHeader);

        final int fieldIndex = (segHeader.equals("MSH") && _fieldIndex != 0)
                ? _fieldIndex + 1 : _fieldIndex;

        index.append("-").append(fieldIndex);

        if(field.hasRepetition())
            index.append(".").append(_repIndex + 1);

        if(repetition.hasComponent())
            index.append(".").append(_compIndex + 1);

        if(comp.hasSubcomponent())
            index.append(".").append(_subcomponentIndex + 1);

        return index;
    }


    private void setupHeaderRenderer(JTable table) {
        table.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                Utilities.setPanelColors(label);
                label.setHorizontalAlignment(SwingConstants.CENTER);

                if (column < table.getColumnCount() - 1)
                    label.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Utilities.TERCIARY_COLOR));
                else
                    label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

                return label;
            }
        });
    }
}
