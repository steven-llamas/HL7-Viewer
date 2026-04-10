package hl7Viewer.nonGui.parser;

public interface IHL7Parser {

    HL7Message parse(String message, HL7Message hl7Msg) throws IllegalArgumentException, NullPointerException;
}
