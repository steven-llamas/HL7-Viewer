package hl7Viewer.nonGui.parser;

import java.util.List;

public class HL7Component {
    private final List<String> subcomponentList;


    public HL7Component(List<String> subcomponentList) {
        this.subcomponentList = subcomponentList;
    }
    

    public List<String> getSubcomponentList() {
        return subcomponentList;
    }


    public boolean hasSubcomponents() {
        return getSubcomponentList().size() > 1;
    }
}
