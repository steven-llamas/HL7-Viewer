package hl7Viewer.gui;

import javax.swing.*;

public class MenuBar extends JMenuBar {

    public MenuBar() {
        this.setBackground(Utilities.SECONDARY_COLOR);
        this.setOpaque(true);
        this.setBorder(BorderFactory.createEmptyBorder());

        createMenuWithItem("File", "Exit", () -> System.exit(0));
        createMenuWithItem("File", "Test", () -> System.out.println("Test clicked"));
    }

    private void createMenuWithItem(String menuName, String itemName, Runnable action) {
        JMenu menu = null;

        if (!menuBarContainsMenu(this, menuName)) {
            menu = new JMenu(menuName);
            menu.setForeground(Utilities.TEXT_COLOR);
            this.add(menu);
        } else {
            menu = getMenuByName(this, menuName);
        }

        if (menu != null && !menuContainsItem(menu, itemName)) {
            JMenuItem item = new JMenuItem(itemName);
            item.setForeground(Utilities.TEXT_COLOR);
            item.setBackground(Utilities.SECONDARY_COLOR);
            item.setOpaque(true);

            if (action != null) {
                item.addActionListener(e -> action.run());
            }

            menu.add(item);
        }
    }

    private boolean menuBarContainsMenu(JMenuBar menuBar, String menuName) {
        for (int i = 0; i < menuBar.getMenuCount(); i++) {
            JMenu menu = menuBar.getMenu(i);
            if (menu != null && menuName.equals(menu.getText())) {
                return true;
            }
        }
        return false;
    }

    private boolean menuContainsItem(JMenu menu, String itemName) {
        for (int i = 0; i < menu.getItemCount(); i++) {
            JMenuItem item = menu.getItem(i);
            if (item != null && itemName.equals(item.getText())) {
                return true;
            }
        }
        return false;
    }

    private JMenu getMenuByName(JMenuBar menuBar, String menuName) {
        for (int i = 0; i < menuBar.getMenuCount(); i++) {
            JMenu menu = menuBar.getMenu(i);
            if (menu != null && menuName.equals(menu.getText())) {
                return menu;
            }
        }
        return null;
    }
}
