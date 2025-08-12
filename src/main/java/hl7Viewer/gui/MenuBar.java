package hl7Viewer.gui;

import javax.swing.*;

public class MenuBar extends JMenuBar {
    private final GuiBase guiBase;
    //constructor that creates and configures MenuBar
    public MenuBar(GuiBase guiBase) {
        this.guiBase = guiBase;

        Utilities.setPanelColors(this);
        this.setBorder(BorderFactory.createLineBorder(Utilities.SECONDARY_COLOR, 2));

        createMenuWithItem("View", "HL7 Parser", () -> guiBase.showHl7Viewer());
        createMenuWithItem("View", "HL7 Builder", () -> guiBase.showMessageBuilderView());
    }
    //allows you to specify menu name, item name and what action to do in menu bar
    private void createMenuWithItem(String menuName, String itemName, Runnable action) {
        JMenu menu = getOrCreateMenu(menuName);

        if (!menuContainsItem(menu, itemName)) {
            JMenuItem item = new JMenuItem(itemName);
            item.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
            Utilities.setPanelColors(item);
            item.addActionListener(e -> action.run());
            menu.add(item);
        }
    }
    //Menu name validation within menu bar
    private JMenu getOrCreateMenu(String menuName) {
        for (int i = 0; i < getMenuCount(); i++) {
            JMenu menu = getMenu(i);
            if (menuName.equals(menu.getText())) {
                return menu;
            }
        }
        JMenu newMenu = new JMenu(menuName);
        newMenu.setForeground(Utilities.TEXT_COLOR);
        newMenu.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        // Fix the white borders on dropdown
        JPopupMenu popup = newMenu.getPopupMenu();
        popup.setBorder(BorderFactory.createEmptyBorder());
        popup.setBackground(Utilities.SECONDARY_COLOR);
        popup.setOpaque(true);

        add(newMenu);
        return newMenu;
    }
    //menu item validation in menu bar
    private boolean menuContainsItem(JMenu menu, String itemName) {
        for (int i = 0; i < menu.getItemCount(); i++) {
            JMenuItem item = menu.getItem(i);
            if (item != null && itemName.equals(item.getText())) {
                return true;
            }
        }
        return false;
    }
}