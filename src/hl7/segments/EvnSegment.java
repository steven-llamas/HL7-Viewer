package hl7.segments;

public class EvnSegment extends Hl7segment {

    private String eventTypeCode;
    private String recordedDateTime;
    private String dateTimePlannedEvent;
    private String eventReasonCode;
    private String operatorID;
    private String eventOccurred;


    public EvnSegment(String[] fields){
        this.eventTypeCode = get(fields, 1);
        this.recordedDateTime = get(fields, 2);
        this.dateTimePlannedEvent = get(fields, 3);
        this.eventReasonCode = get(fields, 4);
        this.operatorID = get(fields, 5);
        this.eventOccurred = get(fields, 6);
    }
}
