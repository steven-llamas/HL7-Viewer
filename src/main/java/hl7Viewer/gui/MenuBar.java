package hl7Viewer.gui;

import javax.swing.*;

public class MenuBar extends JMenuBar {

    public MenuBar(GuiBase guiBase) {
        Utilities.setPanelColors(this);
        this.setBorder(BorderFactory.createLineBorder(Utilities.SECONDARY_COLOR, 2));
    }

    public void createMenuWithItem(String menuName, String itemName, Runnable action) {
        JMenu menu = getOrCreateMenu(menuName);

        if (!menuContainsItem(menu, itemName)) {
            var item = new JMenuItem(itemName);
            item.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
            Utilities.setPanelColors(item);
            item.addActionListener(e -> action.run());
            menu.add(item);
        }
    }

    private JMenu getOrCreateMenu(String menuName) {
        for (int i = 0; i < getMenuCount(); i++) {
            JMenu menu = getMenu(i);
            if (menuName.equals(menu.getText()))
                return menu;
        }
        var newMenu = new JMenu(menuName);
        newMenu.setForeground(Utilities.TEXT_COLOR);
        newMenu.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        JPopupMenu popup = newMenu.getPopupMenu();
        popup.setBorder(BorderFactory.createEmptyBorder());
        Utilities.setPanelColors(popup);

        add(newMenu);
        return newMenu;
    }

    private boolean menuContainsItem(JMenu menu, String itemName) {
        for (int i = 0; i < menu.getItemCount(); i++) {
            JMenuItem item = menu.getItem(i);
            if (item != null && itemName.equals(item.getText()))
                return true;
        }
        return false;
    }
}