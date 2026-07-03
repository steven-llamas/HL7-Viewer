package hl7Viewer.gui;


import hl7Viewer.AppInfo;
import hl7Viewer.nonGui.config.ConfigKey;
import hl7Viewer.nonGui.config.IniConfig;
import hl7Viewer.nonGui.hl7Parser.BasicMessageParser;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


public class MainForm extends JFrame {

    private final JPanel contentPanel;

    private final IniConfig config;

    private Image appIcon;

    public MainForm(final IniConfig config) {
        this.config = config;

        setTitle("HL7 Viewer");
        int screenWidth  = config.get(ConfigKey.SCREEN_WIDTH,  1000);
        int screenHeight = config.get(ConfigKey.SCREEN_HEIGHT, 600);

        setSize(screenWidth, screenHeight);
        setAppOnCenterOfScreen();
        setImageIcon();
        setWarningOnExit();

        if (AppInfo.IS_MAC_OS) {
            System.setProperty("apple.awt.application.name", "HL7 Viewer");
            System.setProperty("apple.laf.useScreenMenuBar", "true");
        }

        setBackground();
        contentPanel = setupContentPanel();

        final var navigator = new ViewNavigator(this::panelRefresher);

        final var menuBar = new MenuBar();

        menuBar.createMenuWithItem("File",
                "About",
                () -> new AboutDialog(this, appIcon).display());

        menuBar.createMenuWithItem("File",
                "Options",
                () -> navigator.show(new OptionsView(config)));


        final var hl7ParseViewer = new HL7ParseViewer(new BasicMessageParser(config), config);
        menuBar.createMenuWithItem("View", "HL7 Parser", () -> navigator.show(hl7ParseViewer));
        setJMenuBar(menuBar);

        navigator.show(hl7ParseViewer);
    }


    private void setAppOnCenterOfScreen() {
        setLocationRelativeTo(null);
    }


    private void setImageIcon() {
        final var iconURL = getClass().getResource("/images/icon.png");
        assert iconURL != null;
        final var image = new ImageIcon(iconURL);
        appIcon = image.getImage();
        setIconImage(appIcon);

        if (AppInfo.IS_MAC_OS){
            try {
                Taskbar.getTaskbar().setIconImage(appIcon);
            } catch (UnsupportedOperationException e) {
                // do nothing
            }
        }
    }


    private void setWarningOnExit() {
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                final int option = JOptionPane.showConfirmDialog(MainForm.this,
                        "Are you sure you want to exit",
                        "Exit", JOptionPane.YES_NO_OPTION);
                if (ifClickedYes(option)) {
                    onShutdown();
                    MainForm.this.dispose();
                }
            }
        });
    }


    private void setBackground() {
        getContentPane().setBackground(Theme.BACKGROUND_COLOR);
    }


    private JPanel setupContentPanel() {
        final JPanel contentPanel;
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);
        add(contentPanel, BorderLayout.CENTER);
        return contentPanel;
    }


    //removes panels and adds new panels
    private void panelRefresher(JPanel mainPanel) {
        getContentPane().setBackground(Theme.BACKGROUND_COLOR);
        contentPanel.removeAll();
        contentPanel.add(mainPanel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }


    private void onShutdown() {
        config.set(ConfigKey.SCREEN_WIDTH,  getWidth());
        config.set(ConfigKey.SCREEN_HEIGHT, getHeight());
        config.save();
    }


    private boolean ifClickedYes(final int option) {
        return option == JOptionPane.YES_OPTION;
    }
}
