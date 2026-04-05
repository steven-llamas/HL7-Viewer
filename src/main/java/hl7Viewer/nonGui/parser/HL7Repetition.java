package hl7Viewer.nonGui.parser;

import java.util.List;

public class HL7Repetition {
    final List<HL7Component> componentList;


    public HL7Repetition(List<HL7Component> componentList) {
        this.componentList = componentList;
    }


    public void addComponent(HL7Component component) {
        if (component != null)
            componentList.addLast(component);
    }


    public List<HL7Component> getComponentList() {
        return componentList;
    }


    public boolean hasComponent() {
        return getComponentList().size() > 1;
    }
}
