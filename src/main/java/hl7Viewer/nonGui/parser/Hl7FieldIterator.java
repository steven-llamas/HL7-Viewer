package hl7Viewer.nonGui.parser;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Composite;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.Primitive;
import ca.uhn.hl7v2.model.Segment;
import ca.uhn.hl7v2.model.Type;
import ca.uhn.hl7v2.model.Varies;

import java.util.ArrayList;
import java.util.List;

public class Hl7FieldIterator {

    public static Object[][] getSegmentFieldTableData(Message msg) {
        List<Object[]> rows = new ArrayList<>();

        try {
            for (String segmentName : msg.getNames()) {
                var segments = msg.getAll(segmentName);

                for (int segIndex = 0; segIndex < segments.length; segIndex++) {
                    Segment segment = (Segment) segments[segIndex];
                    int numFields = segment.numFields();

                    for (int fieldNum = 1; fieldNum <= numFields; fieldNum++) {
                        Type[] fieldReps = segment.getField(fieldNum);

                        for (int repIndex = 0; repIndex < fieldReps.length; repIndex++) {
                            String value = safeEncode(fieldReps[repIndex]);
                            String label = segmentName + "-" + fieldNum;
                            if (fieldReps.length > 1) {
                                label += "[" + (repIndex + 1) + "]";
                            }
                            rows.add(new Object[]{label, value});
                        }
                    }
                }
            }
        } catch (HL7Exception e) {
            e.printStackTrace();
        }
        return rows.toArray(new Object[0][0]);
    }

    private static String safeEncode(Type t) {
        if (t == null) return ""; // Return empty string instead of "null"

        try {
            if (t instanceof Varies) {
                Type data = ((Varies) t).getData();
                return data != null ? safeEncode(data) : "";
            }
            else if (t instanceof Composite) {
                Composite comp = (Composite) t;
                int n = comp.getComponents().length;
                StringBuilder sb = new StringBuilder();

                for (int i = 0; i < n; i++) {
                    if (i > 0) sb.append("^");

                    Type subComponent = comp.getComponent(i);
                    String encoded = safeEncode(subComponent);
                    //  Prevent "null" string from being appended
                    sb.append(encoded != null ? encoded : "");
                }
                return sb.toString();
            }
            else if (t instanceof Primitive) {
                String val = t.encode();
                return val != null ? val : "";  //  Ensure encoded primitive is not null
            }
            else {
                String val = t.encode();
                return val != null ? val : "";  //Fallback, safeguard
            }
        } catch (Exception e) {
            System.err.println("Error encoding field: " + t.getClass().getSimpleName() + " - " + e);
            try {
                return t.toString() != null ? t.toString() : "";
            } catch (Exception e2) {
                System.err.println("Error calling toString on field: " + e2);
                return "[Error]";
            }
        }
    }
}