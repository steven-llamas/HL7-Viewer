package hl7.segments;

public class Al1Segment extends Hl7segment {
    //listing all fields in AL1
    private String setID;
    private String allergyType;
    private String allergyCodeMnemonicDesc;
    private String allergySeverity;
    private String allergyReaction;
    private String identificationDate;


    public Al1Segment(String[] fields){
        this.setID = get(fields, 1);
        this.allergyType = get(fields, 2);
        this.allergyCodeMnemonicDesc = get(fields, 3);
        this.allergySeverity = get(fields, 4);
        this.allergyReaction = get(fields, 5);
        this.identificationDate = get(fields, 6);
    }
}
