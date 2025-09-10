package hl7Viewer.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

public class HL7TableViewer extends JPanel {
    private JTable parsedTable;
    private DefaultTableModel tableModel;
    //constructor that provide a few configurations
    public HL7TableViewer() {
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
    public void displayParsedHl7(ArrayList<String[]> tableData) {
        tableModel.setRowCount(0); // Clear previous data

            if (!DisPlayEachRow(tableData))
                JOptionPane.showMessageDialog(this,
                        "Failed to display HL7 fields:\n",
                        "Parsing Error",
                        JOptionPane.ERROR_MESSAGE);

    }
    private boolean DisPlayEachRow(ArrayList<String[]> tableData) {

        for (String[] row : tableData) {
            tableModel.addRow(row);
        }
        return tableModel.getRowCount() != 0;
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