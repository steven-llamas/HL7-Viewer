package hl7Viewer.nonGui.hl7Parser;

public interface IHL7Parser {

    HL7Message parse(String message, HL7Message hl7Msg) throws IllegalArgumentException, NullPointerException;
}
