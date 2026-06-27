package hl7Viewer.nonGui.hl7Parser;

import hl7Viewer.utils.Pair;

public class HL7Validator {

    public static Pair<Boolean, String> validateStructure(final String message)  {
        var isValid = true;
        final StringBuilder errorMsg = new StringBuilder("Invalid Message: ");

        if (message.isEmpty()) {
            errorMsg.append("Message cannot be empty");
            isValid = false;
        }
        else if((message.length() < 4)) {
            errorMsg.append("Message length is too short");
            isValid = false;
        }
        else if (!message.substring(0,3).toUpperCase().contains("MSH")) {
            errorMsg.append("Message doesn't contain MSH as first segment");
            isValid = false;
        }

        return new Pair<>(isValid, errorMsg.toString());
    }
}
