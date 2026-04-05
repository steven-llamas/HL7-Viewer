package hl7Viewer.nonGui.parser;

import java.util.List;

public class HL7Component {
    final List<String> subcomponentList;


    public HL7Component(List<String> subcomponentList) {
        this.subcomponentList = subcomponentList;
    }
    

    public List<String> getSubcomponentList() {
        return subcomponentList;
    }


    public boolean hasSubcomponent() {
        return getSubcomponentList().size() > 1;
    }
}
