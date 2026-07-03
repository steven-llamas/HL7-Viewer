package hl7Viewer.gui;

import javax.swing.*;
import java.awt.*;

public class TextRenderer extends JTextArea implements javax.swing.table.TableCellRenderer {

    public TextRenderer() {
        setLineWrap(true);
        setWrapStyleWord(true);
        setOpaque(true);
    }

    @Override
    public Component getTableCellRendererComponent(
            JTable table,
            Object value,
            boolean isSelected,
            boolean hasFocus,
            int row,
            int column) {

        setText((value == null) ? "" : value.toString());

        if (isSelected) {
            setBackground(table.getSelectionBackground());
            setForeground(table.getSelectionForeground());
        } else {
            setBackground(Theme.TRANSPARENT_COLOR);
            setForeground(Theme.TEXT_COLOR);
        }

        setBorder(Utilities.addPadding(2,2,2,2));

        setFont(table.getFont());
        setSize(table.getColumnModel().getColumn(column).getWidth(), Short.MAX_VALUE);

        final int prefHeight = getPreferredSize().height;
        if (table.getRowHeight(row) != prefHeight)
            table.setRowHeight(row, prefHeight);

        return this;
    }
}
