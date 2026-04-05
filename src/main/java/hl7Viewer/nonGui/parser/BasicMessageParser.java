package hl7Viewer.nonGui.parser;

import java.util.ArrayList;
import java.util.Collections;

public class BasicMessageParser implements IMessageParser {
    @Override
    public HL7Message parse(String message) throws IllegalArgumentException {
        java.util.Objects.requireNonNull(message, "Invalid Message: Message cannot be null.");

        message = message.trim();
        if (message.isEmpty())
            throw new IllegalArgumentException("Invalid Message: message cannot be empty");

        if(!message.toUpperCase().contains("MSH"))
            throw new IllegalArgumentException("Invalid Message: message doesn't contain MSH");

        message = HL7Message.sanitizeEnterChar(message);

        final var hl7Msg = new HL7Message();
        hl7Msg.setSegments(new ArrayList<>());
        final var segments = message.split("\n");
        char fieldSeparator         = '|';
        char repSeparator           = '~';
        char componentSeparator     = '^';
        char subcomponentSeparator  = '&';

        for (var segment : segments) {
            final var fields = segment.split("\\" + fieldSeparator);
            final var hl7Seg = new HL7Segment(fields[0], new ArrayList<>());
            var fieldIndex = 0;

            if (isMshSeg(hl7Seg)) {
                final var encoding         = fields[1];
                componentSeparator      = encoding.charAt(0);
                repSeparator            = encoding.charAt(1);
                subcomponentSeparator   = encoding.charAt(3);
            }

            for (var field : fields) {

              final var hl7field = new HL7Field(new ArrayList<>());
                if (isMshEncodingField(hl7Seg, fieldIndex)) {
                    parseEncodingField(field, hl7Seg, hl7field);
                    fieldIndex++;
                    continue;
                }
                final var repetitions = field.split("\\" + repSeparator);

                for (var repetition : repetitions ) {

                    final var hl7Repetition = new HL7Repetition(new ArrayList<>());
                    final var components = repetition.split("\\" + componentSeparator);

                    for (var component : components) {

                        final var hl7Comp = new HL7Component(new ArrayList<>());
                        final var subcomponents = component.split("\\" + subcomponentSeparator);
                        Collections.addAll(hl7Comp.getSubcomponentList(), subcomponents);
                        hl7Repetition.addComponent(hl7Comp);
                    }
                    hl7field.addRepetition(hl7Repetition);
                }
                hl7Seg.addField(hl7field);
                fieldIndex++;
            }
            hl7Msg.addSegment(hl7Seg);
        }
        return hl7Msg;
    }


    private static boolean isMshSeg(final HL7Segment hl7Seg) {
        return hl7Seg.getSegmentName().equals("MSH");
    }


    private static boolean isMshEncodingField(
            final HL7Segment hl7Seg,
            final int fieldIndex ) {
        return (isMshSeg(hl7Seg) && fieldIndex == 1);
    }


    private static void parseEncodingField(
            final String field,
            final HL7Segment hl7Seg,
            final HL7Field hl7field) {

        final var hl7Repetition = new HL7Repetition(new ArrayList<>());
        final var component = new HL7Component(new ArrayList<>());

        component.getSubcomponentList().add(field);

        hl7Repetition.addComponent(component);
        hl7field.addRepetition(hl7Repetition);
        hl7Seg.addField(hl7field);
    }
}
