package hl7Viewer.nonGui.parser;

public interface IHL7Parser {

    HL7Message parse(final String message, HL7Message hl7Msg) throws IllegalArgumentException;
}
