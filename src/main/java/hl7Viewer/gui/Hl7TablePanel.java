package hl7Viewer.gui;

import ca.uhn.hl7v2.model.Message;
import hl7Viewer.nonGui.parser.Hl7FieldIterator;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class Hl7TablePanel extends JPanel {
    private JTable parsedTable;
    private DefaultTableModel tableModel;
    //constructor that provide a few configurations
    public Hl7TablePanel() {
        setLayout(new BorderLayout());
        setOpaque(false);
        initializeTable();
    }
    //where Jtable is created and configured
    private void initializeTable() {
        String[] columnNames = {"Segment-Field", "Value"};
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
        setupHeaderRenderer(parsedTable);

        var header = parsedTable.getTableHeader();
        header.setReorderingAllowed(false);
        header.setResizingAllowed(false);
        header.setBorder(BorderFactory.createMatteBorder(1,1,1,1, Utilities.TERCIARY_COLOR));
        Utilities.setPanelColors(header);


        var scrollPane = getJScrollPane();

        add(scrollPane, BorderLayout.CENTER);
    }
    //setting scroll pane
    private JScrollPane getJScrollPane() {
        var scrollPane = new JScrollPane(parsedTable);
        scrollPane.setOpaque(false);
        scrollPane.setBackground(Utilities.TRANSPARENT_COLOR);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getViewport().setBackground(Utilities.TRANSPARENT_COLOR);
        scrollPane.setViewportBorder(null);
        return scrollPane;
    }
    //updates the table when new message is to be parsed
    public void updateFromInput(Message hl7Message) {
        tableModel.setRowCount(0); // Clear previous data

        try {
            Object[][] tableData = Hl7FieldIterator.getSegmentFieldTableData(hl7Message);

            for (Object[] row : tableData) {
                tableModel.addRow(row);
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Failed to extract HL7 fields:\n" + e.getMessage(),
                    "Parsing Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
    //fixes color bleed on table header by adding border on right side of first column
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
