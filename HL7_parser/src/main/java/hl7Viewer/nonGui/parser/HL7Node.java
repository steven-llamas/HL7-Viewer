package hl7Viewer.nonGui.parser;


import java.util.ArrayList;
import java.util.List;

public class HL7Node {
    String tag;
    int index;
    String value;
    List<HL7Node> children = new ArrayList<>();

    HL7Node(String tag, int index) {
        this.tag = tag;
        this.index = index;
    }

    public ArrayList<String[]> flatten(List<String[]> rows) {
        if (value != null) {
            rows.add(new String[]{tag, value});
        }

        for (HL7Node child : children) {
            child.flatten(rows);
        }
        return null;
    }
}