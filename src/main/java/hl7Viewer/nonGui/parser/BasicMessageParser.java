package hl7Viewer.nonGui.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;

import static hl7Viewer.nonGui.parser.HL7Message.NORMAL_ENCODING;
import static hl7Viewer.nonGui.parser.HL7Message.isValidHl7Message;

public class BasicMessageParser implements IHL7Parser {
    @Override
    public HL7Message parse(String message, HL7Message hl7Msg) throws IllegalArgumentException, NullPointerException {
        java.util.Objects.requireNonNull(message, "Message cannot be null.");
        java.util.Objects.requireNonNull(hl7Msg, "HL7 message Object cannot be null");

        message = message.trim();

        var isError = isValidHl7Message(message);
        if (!isError.getKey()) {
            var ErrorMsg = isError.getValue();
            throw new IllegalArgumentException(ErrorMsg);
        }

        message = HL7Message.sanitizeEnterChar(message);

        hl7Msg.setSegments(new ArrayList<>());
        final var segments = message.split("\r");
        final char fieldSeparator   = segments[0].charAt(3);
        char componentSeparator     = NORMAL_ENCODING.charAt(1);
        char repSeparator           = NORMAL_ENCODING.charAt(2);
        char subcomponentSeparator  = NORMAL_ENCODING.charAt(4);

        for (var segment : segments) {
            final var fields = segment.split(
                    Pattern.quote(String.valueOf(fieldSeparator)));

            final var segHeader = fields[0].toUpperCase();
            final var hl7Seg = new HL7Segment(segHeader, new ArrayList<>());
            var fieldIndex = 0;

            if (isMshSeg(hl7Seg)) {
                final var encoding = (fields.length > 1) ? fields[1] : "";

                if (validEncodingCharField(encoding)) {
                    componentSeparator      = encoding.charAt(0);
                    repSeparator            = encoding.charAt(1);
                    subcomponentSeparator   = encoding.charAt(3);
                }
            }

            for (var field : fields) {

              final var hl7field = new HL7Field(new ArrayList<>());
                if (isMshEncodingField(hl7Seg, fieldIndex)) {
                    parseEncodingField(field, hl7Seg, hl7field);
                    fieldIndex++;
                    continue;
                }
                final var repetitions = field.split(
                        Pattern.quote(String.valueOf(repSeparator)));

                for (var repetition : repetitions ) {

                    final var hl7Repetition = new HL7Repetition(new ArrayList<>());
                    final var components = repetition.split(
                            Pattern.quote(String.valueOf(componentSeparator)));

                    for (var component : components) {

                        final var hl7Comp = new HL7Component(new ArrayList<>());
                        Arrays.stream(
                                    component.split(Pattern.quote(String.valueOf(subcomponentSeparator))))
                                .map(String::toUpperCase)
                                .map(String::trim)
                                .forEach(hl7Comp.getSubcomponentList()::add);

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


    private static boolean validEncodingCharField(final String encoding) {
        return !encoding.trim().isEmpty() &&
                encoding.length() <= 4;
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
