package hl7Viewer.gui;

import javax.swing.JPanel;
import java.util.function.Consumer;

public class ViewNavigator {
    private final Consumer<JPanel> panelSwapper;

    public ViewNavigator(final Consumer<JPanel> panelSwapper) {
        this.panelSwapper = panelSwapper;
    }

    public void show(final IView view) {
        panelSwapper.accept(view.createPanel());
    }
}
