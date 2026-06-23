package hl7Viewer.nonGui.hl7Parser;

import hl7Viewer.utils.Pair;
import java.util.ArrayList;
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


    public List<Pair<String, String>> flatten() {
        final List<Pair<String, String>> rows = new ArrayList<>();

        for (final var segment : segments) {
            final String segName = segment.getSegmentName();

            for (var j = 0; j < segment.getFieldList().size(); ++j) {
                final var field = segment.getFieldList().get(j);

                final int fieldIndex = (segName.equals("MSH") && j != 0) ? j + 1 : j;

                for (var k = 0; k < field.getRepetitionList().size(); ++k) {
                    final var repetition = field.getRepetitionList().get(k);

                    for (var l = 0; l < repetition.getComponentList().size(); ++l) {
                        final var comp = repetition.getComponentList().get(l);

                        for (var m = 0; m < comp.getSubcomponentList().size(); ++m) {
                            final String value = comp.getSubcomponentList().get(m);

                            if (value.trim().isEmpty())
                                continue;

                            final StringBuilder index = new StringBuilder(segName);
                            index.append("-").append(fieldIndex);

                            if (field.hasRepetitions())
                                index.append(".").append(k + 1);
                            if (repetition.hasComponents())
                                index.append(".").append(l + 1);
                            if (comp.hasSubcomponents())
                                index.append(".").append(m + 1);

                            rows.add(new Pair<>(index.toString(), value));
                        }
                    }
                }
            }
        }
        return rows;
    }


    public static String sanitizeEnterChar(String message) {
        return message.replaceAll("\\R","\r");
    }
}
