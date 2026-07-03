package hl7Viewer.nonGui.hl7Parser;

import java.util.List;

public class HL7Segment extends HL7Base<HL7Field> {
    private final String segmentName;

    public HL7Segment(String segmentName, List<HL7Field> list) {
        super(list);
        this.segmentName = segmentName;
    }

    public String getSegmentName() {
        return segmentName;
    }
}
