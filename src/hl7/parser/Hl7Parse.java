package hl7.parser;

import hl7.segments.*;

public class Hl7Parse {
    private String message;


    public Hl7Parse(String message)  {
        if (message == null|| message.trim().isEmpty()) { //simple input validation to check if message is empty
            throw new IllegalArgumentException("HL7 message cannot be null");
        }
        this.message = message;
        parse();
    }


    public void parse() {
        String[] segments = getSegments();

        for (String segment : segments) {
            //splitting the string array of segments into fields w "|"
            String[] fields = getFields(segment);
            //segment type "MSH" for ex is always first item in array
            //using Switch statements to check each segment
            //and create new object  for their type
            //constructor sets each element in array to
            String segmentType = fields[0].toUpperCase();

            switch (segmentType) {
                case "MSH":
                    var msh = new MshSegment(fields);
                    break;

                case "PID":
                    var pid = new PidSegment(fields);
                    break;

                case "PV1":
                    var pv1 = new Pv1Segment(fields);
                    break;

                case "ORC":
                    var orc = new OrcSegment(fields);
                    break;

                case "RXO":
                    var rxo = new RxoSegment(fields);
                    break;

                case "RXE":
                    var rxe = new RxeSegment(fields);
                    break;

                case "RXR":
                    var rxr = new RxrSegment(fields);
                    break;

                case "AL1":
                    var al1 = new Al1Segment(fields);
                    break;

                default:
                    System.out.println("Unknown segment: " + segmentType);
            }
        }
    }

    private String[] getSegments() {
        //Splitting Hl7 message into segments by carriage return \ new line
        // returns segments into array of strings
        return this.message.split("[\\r\\n]");
    }

    private String[] getFields(String segments) {
        return segments.split("\\|");

    }


}
