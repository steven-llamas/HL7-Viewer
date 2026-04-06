package hl7Viewer.nonGui.parser;

import java.util.List;

public class HL7Segment {
    private final String segmentName;
    private final List<HL7Field> fieldList;


    public HL7Segment(
            String segmentName,
            List<HL7Field> fieldList) {
        this.segmentName = segmentName;
        this.fieldList = fieldList;
    }


    public void addField(HL7Field field) {
        if (field != null)
            fieldList.addLast(field);
    }


    public String getSegmentName() {
        return segmentName;
    }


    public List<HL7Field> getFieldList() {
        return fieldList;
    }

}
