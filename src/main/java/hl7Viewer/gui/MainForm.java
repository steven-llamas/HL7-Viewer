package hl7Viewer.gui;


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

    public MainForm(final IniConfig config) {
        this.config = config;

        setTitle("HL7 Viewer");
        int screenWidth  = config.getInt(ConfigKey.SCREEN_WIDTH,  1000);
        int screenHeight = config.getInt(ConfigKey.SCREEN_HEIGHT, 600);

        setSize(screenWidth, screenHeight);
        setAppOnCenterOfScreen();
        setImageIcon();
        setWarningOnExit();

        if (Utilities.IS_MAC_OS) {
            System.setProperty("apple.awt.application.name", "HL7 Viewer");
            System.setProperty("apple.laf.useScreenMenuBar", "true");
        }

        var menuBar = new MenuBar(this);
//        menuBar.createMenuWithItem("File", "Settings", this::showMessageBuilderView);
        menuBar.createMenuWithItem("View", "HL7 Parser", this::showHl7Viewer);
//        menuBar.createMenuWithItem("View", "HL7 Builder", this::showMessageBuilderView);
        setJMenuBar(menuBar);

        setBackground();
        contentPanel = setupContentPanel();
        showHl7Viewer();
    }


    public void showHl7Viewer() {
        final JPanel parserPanel =
                new HL7ParseViewer(new BasicMessageParser(config), new HL7TableViewer(config))
                .createPanel();
        panelRefresher(parserPanel);

    }


    private void setAppOnCenterOfScreen() {
        setLocationRelativeTo(null);
    }


    private void setImageIcon() {
        final var iconURL = getClass().getResource("/images/icon.png");
        assert iconURL != null;
        final var image = new ImageIcon(iconURL);
        setIconImage(image.getImage());

        if (Utilities.IS_MAC_OS){
            try {
                Taskbar.getTaskbar().setIconImage(image.getImage());
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
                    MainForm.this.dispose();
                }
            }
        });
    }


    private void setBackground() {
        getContentPane().setBackground(Utilities.PRIMARY_COLOR);
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
        contentPanel.removeAll();
        contentPanel.add(mainPanel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }


    private boolean ifClickedYes(final int option) {
        return option == JOptionPane.YES_OPTION;
    }
}
