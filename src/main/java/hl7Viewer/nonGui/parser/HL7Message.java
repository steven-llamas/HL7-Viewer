package hl7Viewer.nonGui.parser;

import java.util.List;

public class HL7Message {
    private List<HL7Segment>  segments;
    public static final String NORMAL_ENCODING = "|^~\\&";

    public void setSegments(List<HL7Segment> segments) {
        this.segments = segments;
    }


    public void addSegment(HL7Segment segment) {
        if (segment != null)
            segments.addLast(segment);
    }


    public List<HL7Segment> getSegments() {
        return segments;
    }


    public static String sanitizeEnterChar(String message) {
        return message.replaceAll("\\R","\r");
    }
}
