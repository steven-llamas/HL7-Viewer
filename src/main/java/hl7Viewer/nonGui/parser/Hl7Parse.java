package hl7Viewer.nonGui.parser;

import ca.uhn.hl7v2.DefaultHapiContext;
import ca.uhn.hl7v2.model.Message;


public class Hl7Parse {

     private final Message parsedMessage;
    //called HAPI library to parse message
    public Hl7Parse(String message) throws Exception{
            var context = new DefaultHapiContext();
            //converts \n or \r\n to \r
            String normalizedHL7 = message.replace("\r\n", "\r").replace("\n", "\r");
            //context.setValidationContext(ValidationContextFactory.defaultValidation());
            var parser = context.getPipeParser();
            parsedMessage = parser.parse(normalizedHL7);
    }
    //returns parsed message
    public Message getParsedMessage(){
        return parsedMessage;
    }
}