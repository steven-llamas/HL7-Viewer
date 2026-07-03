package hl7Viewer.gui;

import hl7Viewer.nonGui.config.ConfigKey;
import hl7Viewer.nonGui.config.IniConfig;

import javax.swing.*;
import java.awt.*;

public class OptionsView implements IView {

    private boolean boldHL7Index;
    private boolean ignoreMshCheck;
    private Color   primaryColor;
    private Color   secondaryColor;
    private Color   tertiaryColor;
    private Color   textColor;

    private final IniConfig config;

    private JCheckBox  boldHL7IndexBox;
    private JCheckBox  ignoreMshCheckBox;
    private JTextField primaryColorField;
    private JTextField secondaryColorField;
    private JTextField tertiaryColorField;
    private JTextField textColorField;


    public OptionsView(final IniConfig config) {
        this.config = config;
        loadFromConfig();
    }


    private void loadFromConfig() {
        boldHL7Index   = config.get(ConfigKey.BOLD_HL7_INDEX,   true);
        ignoreMshCheck = config.get(ConfigKey.IGNORE_MSH_CHECK, false);
        primaryColor   = config.get(ConfigKey.BACKGROUND_COLOR, Theme.BACKGROUND_COLOR);
        secondaryColor = config.get(ConfigKey.CONTROL_COLOR,   Theme.CONTROL_COLOR);
        tertiaryColor  = config.get(ConfigKey.GRID_COLOR,      Theme.GRID_COLOR);
        textColor      = config.get(ConfigKey.TEXT_COLOR,       Theme.TEXT_COLOR);
    }


    @Override
    public JPanel createPanel() {
        final var panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        Utilities.createAndSetTitle(panel, "Options");
        panel.add(buildFormPanel(),  BorderLayout.CENTER);
        panel.add(buildButtonPanel(), BorderLayout.SOUTH);
        return panel;
    }


    private JPanel buildFormPanel() {
        final var form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        Utilities.setPanelColors(form);
        form.setBorder(Utilities.addPadding(10, 24, 10, 24));

        addSectionLabel(form, "HL7 Settings");
        boldHL7IndexBox   = addCheckRow(form, ConfigKey.BOLD_HL7_INDEX,   boldHL7Index);
        ignoreMshCheckBox = addCheckRow(form, ConfigKey.IGNORE_MSH_CHECK, ignoreMshCheck);

        addSectionLabel(form, "Theme Colors");
        primaryColorField   = addTextRow(form, ConfigKey.BACKGROUND_COLOR,   Theme.toHex(primaryColor));
        secondaryColorField = addTextRow(form, ConfigKey.CONTROL_COLOR, Theme.toHex(secondaryColor));
        tertiaryColorField  = addTextRow(form, ConfigKey.GRID_COLOR,  Theme.toHex(tertiaryColor));
        textColorField      = addTextRow(form, ConfigKey.TEXT_COLOR,       Theme.toHex(textColor));

        return form;
    }


    private JPanel buildButtonPanel() {
        final var panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panel.setOpaque(false);
        panel.add(new Button("Save",   this::onSave));
        panel.add(new Button("Cancel", this::onCancel));
        return panel;
    }


    private void onSave() {
        final String primaryHex   = primaryColorField.getText().trim();
        final String secondaryHex = secondaryColorField.getText().trim();
        final String tertiaryHex  = tertiaryColorField.getText().trim();
        final String textHex      = textColorField.getText().trim();

        if (!Theme.isValidHex(primaryHex)   ||
            !Theme.isValidHex(secondaryHex) ||
            !Theme.isValidHex(tertiaryHex)  ||
            !Theme.isValidHex(textHex)) {
            JOptionPane.showMessageDialog(null,
                    "Invalid hex color. Expected format: #RRGGBB",
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        final Color primary   = Color.decode(primaryHex);
        final Color secondary = Color.decode(secondaryHex);
        final Color tertiary  = Color.decode(tertiaryHex);
        final Color text      = Color.decode(textHex);

        boldHL7Index   = boldHL7IndexBox.isSelected();
        ignoreMshCheck = ignoreMshCheckBox.isSelected();
        primaryColor   = primary;
        secondaryColor = secondary;
        tertiaryColor  = tertiary;
        textColor      = text;

        config.set(ConfigKey.BOLD_HL7_INDEX,   boldHL7Index);
        config.set(ConfigKey.IGNORE_MSH_CHECK, ignoreMshCheck);
        config.set(ConfigKey.BACKGROUND_COLOR,    primaryColor);
        config.set(ConfigKey.CONTROL_COLOR,  secondaryColor);
        config.set(ConfigKey.GRID_COLOR,   tertiaryColor);
        config.set(ConfigKey.TEXT_COLOR,       textColor);

        Theme.BACKGROUND_COLOR   = primaryColor;
        Theme.CONTROL_COLOR = secondaryColor;
        Theme.GRID_COLOR  = tertiaryColor;
        Theme.TEXT_COLOR      = textColor;
    }


    private void onCancel() {
        boldHL7IndexBox.setSelected(    boldHL7Index                );
        ignoreMshCheckBox.setSelected(  ignoreMshCheck              );
        primaryColorField.setText(      Theme.toHex(primaryColor)   );
        secondaryColorField.setText(    Theme.toHex(secondaryColor) );
        tertiaryColorField.setText(     Theme.toHex(tertiaryColor)  );
        textColorField.setText(         Theme.toHex(textColor)      );
    }


    private static void addSectionLabel(final JPanel form, final String text) {
        form.add(Box.createVerticalStrut(8));

        final var label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(label.getFont().deriveFont(Font.BOLD, 13f));
        Utilities.setPanelColors(label);
        label.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.CONTROL_COLOR),
                Utilities.addPadding(6, 0, 6, 0)));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        form.add(label);

        form.add(Box.createVerticalStrut(6));
    }


    private static JTextField addTextRow(final JPanel form, final ConfigKey key, final String value) {
        final var field = styledTextField(value, 10);
        addRow(form, key, field);
        return field;
    }


    private static JCheckBox addCheckRow(final JPanel form, final ConfigKey key, final boolean value) {
        final var box = new JCheckBox();
        box.setSelected(value);
        box.setBackground(Theme.BACKGROUND_COLOR);
        box.setOpaque(true);
        addRow(form, key, box);
        return box;
    }


    private static void addRow(final JPanel form, final ConfigKey key, final JComponent input) {
        final var row = new JPanel(new BorderLayout(12, 0));
        Utilities.setPanelColors(row);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));
        row.setBorder(Utilities.addPadding(4, 0, 4, 0));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.add(buildLabelPanel(key), BorderLayout.CENTER);
        row.add(input,                BorderLayout.EAST);
        form.add(row);
        form.add(Box.createVerticalStrut(2));
    }


    private static JPanel buildLabelPanel(final ConfigKey key) {
        final var panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        final var nameLabel = new JLabel(key.configName());
        Utilities.setPanelColors(nameLabel);
        nameLabel.setFont(nameLabel.getFont().deriveFont(Font.BOLD, 12f));

        final var descLabel = new JLabel(key.description);
        descLabel.setForeground(Theme.GRID_COLOR);
        descLabel.setFont(descLabel.getFont().deriveFont(Font.PLAIN, 11f));
        descLabel.setOpaque(false);

        panel.add(nameLabel);
        panel.add(descLabel);
        return panel;
    }


    private static JTextField styledTextField(final String value, final int cols) {
        final var field = new JTextField(value, cols);
        field.setBackground(Theme.CONTROL_COLOR);
        field.setForeground(Theme.TEXT_COLOR);
        field.setCaretColor(Theme.TEXT_COLOR);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.GRID_COLOR, 1),
                Utilities.addPadding(2, 4, 2, 4)));
        return field;
    }
}
