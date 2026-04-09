package hl7Viewer.nonGui.parser;

import javafx.util.Pair;
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

    public static Pair<Boolean,String> isValidHl7Message(final String message) {
        var isValid = false;
        String errorMsg = "Invalid Message: ";

        if (message.isEmpty())
                return new Pair<>(isValid, errorMsg + "message cannot be empty");

        if(!message.substring(0,3).toUpperCase().contains("MSH"))
                return new Pair<>(isValid, errorMsg +
                        "Message doesn't contain MSH as first segment");

        if((message.length() < 4))
                 return new Pair<>(isValid, errorMsg +
                         "Message length is too short to parse");

        isValid = true;
        errorMsg = "";
        return new Pair<>(isValid, errorMsg);
    }
}
