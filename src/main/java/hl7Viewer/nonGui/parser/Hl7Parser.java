package hl7Viewer.nonGui.parser;


class HL7Parser {

    private static final char SEGMENT_DELIM = '\r';
    private static final char FIELD_DELIM = '|';
    private static final char COMPONENT_DELIM = '^';

    public MessageParsed parseMessage(String hl7Msg) {
        if (hl7Msg.isEmpty())
            throw new IllegalArgumentException();

        var message = new MessageParsed();
        String[] segments = hl7Msg.split(String.valueOf(SEGMENT_DELIM));

        for (String segText : segments) {
            SegmentParsed segmentParsed = parseSegment(segText);
            message.addSegment(segmentParsed);
        }
        return message;
    }

    private SegmentParsed parseSegment(String segmentText) {
        String[] fields = segmentText.split("\\" + FIELD_DELIM);  // escape if needed
        SegmentParsed segmentParsed = new SegmentParsed(fields[0]);  // first field is segment name, e.g. PV1
        for (int i = 1; i < fields.length; i++) {
            FieldParsed fieldParsed = parseField(fields[i]);
            segmentParsed.addField(fieldParsed);
        }
        return segmentParsed;
    }

    private FieldParsed parseField(String fieldText) {
        if (!fieldText.contains("\\^"))
            return null;

        FieldParsed fieldParsed = new FieldParsed();
        String[] components = fieldText.split("\\" + COMPONENT_DELIM);
        for (String comp : components) {
            fieldParsed.addComponent(new ComponentParsed(comp));
        }
        return fieldParsed;
    }
}


