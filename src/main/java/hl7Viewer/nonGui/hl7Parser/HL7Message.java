package hl7Viewer.nonGui.hl7Parser;

import hl7Viewer.utils.Pair;
import java.util.ArrayList;
import java.util.List;

public class HL7Message extends HL7Base<HL7Segment> {
    public static final String NORMAL_ENCODING = "|^~\\&";

    public List<Pair<String, String>> flatten() {
        final List<Pair<String, String>> rows = new ArrayList<>();

        for (final var segment : items) {
            final String segName = segment.getSegmentName();

            for (var j = 0; j < segment.getItems().size(); ++j) {
                final var field = segment.getItems().get(j);

                final int fieldIndex =
                        (segName.equals("MSH") && j != 0)
                                ? j + 1
                                : j;

                for (var k = 0; k < field.getItems().size(); ++k) {
                    final var repetition = field.getItems().get(k);

                    for (var l = 0; l < repetition.getItems().size(); ++l) {
                        final var comp = repetition.getItems().get(l);

                        for (var m = 0; m < comp.getItems().size(); ++m) {
                            final String value = comp.getItems().get(m);

                            if (value.trim().isEmpty())
                                continue;

                            final StringBuilder index = new StringBuilder(segName);
                            index.append("-").append(fieldIndex);

                            if (field.hasItems())
                                index.append(".").append(k + 1);
                            if (repetition.hasItems())
                                index.append(".").append(l + 1);
                            if (comp.hasItems())
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
        return message.replaceAll("\\R", "\r");
    }
}
