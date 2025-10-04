package hl7Viewer.gui;

import hl7Viewer.nonGui.parser.HL7Node;
import hl7Viewer.nonGui.parser.HL7Parser;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

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
        var messagePanel = new JPanel(new BorderLayout());
        messagePanel.setOpaque(false);
        Utilities.setTitledBorder(messagePanel, "HL7 message to Parse");

        var messageTextBox = createAndSetMessageTextBox();
        addCtrlEnterKeyListener(messageTextBox);

        Utilities.createAndSetScrollPane(messageTextBox, messagePanel);
        
        return messagePanel;
    }

    private JTextArea  createAndSetMessageTextBox() {
        var messageTextBox = new JTextArea();
        Utilities.setTextBox(messageTextBox, true, false);
        messageTextBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Utilities.SECONDARY_COLOR, 2),
                Utilities.addPadding(10, 10, 10, 10)
        ));

        return messageTextBox;
    }

    private void addCtrlEnterKeyListener(JTextArea messageTextBox) {
        messageTextBox.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (pressedCtrlAndEnter(e)) {
                    handleMessageAndClearTextbox(messageTextBox);
                    e.consume();
                }
            }
        });
    }

    private void handleMessageAndClearTextbox(JTextArea textBox) {
        parseAndDisplay(textBox.getText());

        textBox.setText("");
    }

    private static boolean pressedCtrlAndEnter(KeyEvent e) {
        return e.isControlDown() && e.getKeyCode() == KeyEvent.VK_ENTER ;
    }

    private void parseAndDisplay(String input) {
        try {
            HL7Parser parser = new HL7Parser();
            HL7Node root = parser.parse(input);

            List<String[]> tableData = new ArrayList<>();
            root.flatten(tableData);
            hl7TableViewer.displayParsedHl7((ArrayList<String[]>) tableData);

        } catch (Exception ex) {
            showErrorMessage(ex.getMessage());
        }
    }

    private static void showErrorMessage(String exceptionMessage) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(null,
                    "Failed to parse HL7 message:\n" + exceptionMessage,
                    "Parsing Error",
                    JOptionPane.ERROR_MESSAGE);
        });
    }
}



class HL7TableViewer extends JPanel {
    private JTable hl7Tablecomponent;
    private DefaultTableModel hl7TableData;

    public HL7TableViewer() {
        setLayout(new BorderLayout());
        setOpaque(false);
        initializeTable();
    }

    private void initializeTable() {
        String[] columnNames = {"Segment-Field", "Value"};
        hl7TableData = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        hl7Tablecomponent = new JTable(hl7TableData);
        hl7Tablecomponent.setOpaque(false);
        hl7Tablecomponent.setBackground(Utilities.TRANSPARENT_COLOR);
        hl7Tablecomponent.setForeground(Utilities.TEXT_COLOR);
        hl7Tablecomponent.setGridColor(Utilities.TERCIARY_COLOR);
        hl7Tablecomponent.setFont(new Font("SansSerif", Font.PLAIN, 12));
        hl7Tablecomponent.setRowHeight(22);
        hl7Tablecomponent.setFillsViewportHeight(true);
        setupHeaderRenderer(hl7Tablecomponent);

        var header = hl7Tablecomponent.getTableHeader();
        header.setReorderingAllowed(false);
        header.setResizingAllowed(false);
        header.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Utilities.TERCIARY_COLOR));
        Utilities.setPanelColors(header);

        var scrollPane = getJScrollPane();
        add(scrollPane, BorderLayout.CENTER);
    }

    private JScrollPane getJScrollPane() {
        var scrollPane = new JScrollPane(hl7Tablecomponent);
        scrollPane.setOpaque(false);
        scrollPane.setBackground(Utilities.TRANSPARENT_COLOR);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getViewport().setBackground(Utilities.TRANSPARENT_COLOR);
        scrollPane.setViewportBorder(null);
        return scrollPane;
    }

    public void displayParsedHl7(ArrayList<String[]> tableData) {
        hl7TableData.setRowCount(0);
        if (!displayEachRow(tableData)) {
            JOptionPane.showMessageDialog(this,
                    "Failed to display HL7 fields.",
                    "Parsing Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean displayEachRow(ArrayList<String[]> tableData) {
        for (String[] row : tableData) {
            hl7TableData.addRow(row);
        }
        return hl7TableData.getRowCount() != 0;
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

                if (column < table.getColumnCount() - 1) {
                    label.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Utilities.TERCIARY_COLOR));
                } else {
                    label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
                }
                return label;
            }
        });
    }
}
