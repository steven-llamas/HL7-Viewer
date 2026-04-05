package hl7Viewer.nonGui.parser;

public interface IMessageParser {

    HL7Message parse(final String message) throws IllegalArgumentException;
}
