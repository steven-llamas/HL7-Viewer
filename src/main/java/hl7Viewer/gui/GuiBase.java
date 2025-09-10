package hl7Viewer.gui;


import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


public class GuiBase extends JFrame {
    private final JPanel contentPanel;
    
    public GuiBase() {

        setTitle("Hl7 Viewer");
        setSize(1000, 600);
        setAppOnCenterOfScreen();
        setImageIcon();
        setWarningOnExit();

        var menuBar1 = new MenuBar(this);
        menuBar1.createMenuWithItem("View", "HL7 Parser", this::showHl7Viewer);
        menuBar1.createMenuWithItem("View", "HL7 Builder", this::showMessageBuilderView);
        setJMenuBar(menuBar1);

        setBackground();
        contentPanel = setupContentPanel();
        showHl7Viewer();
    }

    private void setAppOnCenterOfScreen() {
        setLocationRelativeTo(null);
    }

    private void setImageIcon() {
        final var iconURL = getClass().getResource("/images/important.jpg");
        assert iconURL != null;
        final var image = new ImageIcon(iconURL);
        setIconImage(image.getImage());
    }

    private void setWarningOnExit() {
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                final int option = JOptionPane.showConfirmDialog(GuiBase.this,
                        "Are you sure you want to exit",
                        "Exit", JOptionPane.YES_NO_OPTION);
                if (clickedYes(option)) {
                    GuiBase.this.dispose();
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

    public void showHl7Viewer() {
        JPanel parserPanel =  (new HL7ParseViewer().createPanel());
        var TablePanel = new HL7TableViewer();
        panelRefresher(parserPanel);

    }
    //used to display the HL7 message builder
    public void showMessageBuilderView() {
        var builderPanel = new HL7MessageBuilder();
        panelRefresher(new HL7MessageBuilder().createMessageBuilderPanel());
    }
    //removes panels and adds new panels
    private void panelRefresher(JPanel mainPanel) {
        contentPanel.removeAll();
        contentPanel.add(mainPanel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    private boolean clickedYes(int option) {
        return option == JOptionPane.YES_OPTION;
    }
}
