package hl7Viewer.gui;

import hl7Viewer.nonGui.config.IniConfig;
import hl7Viewer.nonGui.hl7Parser.HL7Message;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

import static hl7Viewer.nonGui.config.ConfigKey.BOLD_HL7_INDEX;

public class HL7TableViewer extends JPanel {
    private JTable jTable;

    private DefaultTableModel hl7TableData;

    private JViewport viewport;

    private final IniConfig config;


    public HL7TableViewer(final IniConfig config) {
        this.config = config;

        setLayout(new BorderLayout());
        setOpaque(false);
        initializeTable();
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


    public void clearTable() {
        if (hl7TableData.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Table is already Empty.");
            return;
        }
        hl7TableData.setRowCount(0);
    }


    public void copyTableToClipboard() {
        if (jTable.getRowCount() == 0) {
            JOptionPane.showMessageDialog(
                    this, "Table is empty. Please press after parsing message");
            return;
        }

        final var contents = new StringBuilder();

        for (var i = 0; i < jTable.getColumnCount(); ++i)
            contents.append(jTable.getColumnName(i)).append("\t");

        contents.append("\n");

        for (var i = 0; i < jTable.getRowCount(); ++i) {
            for (var j = 0; j < jTable.getColumnCount(); ++j) {
                var value = jTable.getValueAt(i, j)
                        .toString();

                if (config.get(BOLD_HL7_INDEX, true))
                    value = value.replaceAll("<[^>]*>", "");

                contents.append(value).append("\t");
            }
            contents.append("\n");
        }

        Toolkit.getDefaultToolkit()
                .getSystemClipboard()
                .setContents(
                        new java.awt.datatransfer.StringSelection(
                                contents.toString()), null);
    }


    @Override
    public void addNotify() {
        super.addNotify();
        applyTheme();
    }


    private void applyTheme() {
        jTable.setForeground(Theme.TEXT_COLOR);
        jTable.setGridColor(Theme.GRID_COLOR);

        viewport.setBackground(Theme.BACKGROUND_COLOR);

        final var header = jTable.getTableHeader();
        header.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Theme.GRID_COLOR));
        Utilities.setPanelColors(header);

        repaint();
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
        jTable.getColumnModel().getColumn(1).setCellRenderer(new TextRenderer());
        jTable.setOpaque(false);
        jTable.setBackground(Theme.TRANSPARENT_COLOR);
        jTable.setFont(new Font("SansSerif", Font.PLAIN, 12));
        jTable.setRowHeight(22);
        jTable.setFillsViewportHeight(true);
        setupHeaderRenderer(jTable);

        final var header = jTable.getTableHeader();
        header.setReorderingAllowed(false);
        header.setResizingAllowed(false);

        add(getJScrollPane(), BorderLayout.CENTER);
        applyTheme();
    }


    private JScrollPane getJScrollPane() {
        final var scrollPane = new JScrollPane(jTable);
        scrollPane.setOpaque(false);
        scrollPane.setBackground(Theme.TRANSPARENT_COLOR);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setViewportBorder(null);

        viewport = scrollPane.getViewport();
        viewport.setOpaque(true);
        viewport.setBackground(Theme.BACKGROUND_COLOR);

        return scrollPane;
    }


    private boolean displayEachRow(final HL7Message hl7Message) {
        for (final var row : hl7Message.flatten()) {
            final String plainIndex = row.first();
            final int dash = plainIndex.indexOf('-');

            final String finalIndex =
                    (config.get(BOLD_HL7_INDEX, true))
                    ? "<html><b>" + plainIndex.substring(0, dash) + "</b>" + plainIndex.substring(dash) + "</html>"
                    : plainIndex;

            hl7TableData.addRow(new Object[]{finalIndex, row.second()});
        }
        return hl7TableData.getRowCount() != 0;
    }


    private void setupHeaderRenderer(JTable table) {
        table.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);
                Utilities.setPanelColors(label);
                label.setHorizontalAlignment(SwingConstants.CENTER);

                if (column < table.getColumnCount() - 1)
                    label.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Theme.GRID_COLOR));
                else
                    label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

                return label;
            }
        });
    }
}
