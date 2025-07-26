package hl7.segments;


public class OrcSegment extends Hl7segment{
    //listing all segments in ORC
    private String OrderControl;
    private String PlacerOrderNum;
    private String FillerOrderNum;
    private String PlacerGroupNumber;
    private String OrderStatus;
    private String ResponseFlag;
    private String QuantityTiming;
    private String ParentOrder;
    private String DateTimeTransaction;
    private String EnteredBy;
    private String VerifiedBy;
    private String OrderingProvider;
    private String EnterersLocation;
    private String CallBackPhoneNum;
    private String OrderEffectiveDateTime;
    private String OrderControlCodeRsn;
    private String EnteringOrg;
    private String EnteringDevice;
    private String ActionBy;

    public OrcSegment(String[] fields) {
        this.OrderControl = get(fields, 1);
        this.PlacerOrderNum = get(fields, 2);
        this.FillerOrderNum = get(fields, 3);
        this.PlacerGroupNumber = get(fields, 4);
        this.OrderStatus = get(fields, 5);
        this.ResponseFlag = get(fields, 6);
        this.QuantityTiming = get(fields, 7);
        this.ParentOrder = get(fields, 8);
        this.DateTimeTransaction = get(fields, 9);
        this.EnteredBy = get(fields, 10);
        this.VerifiedBy = get(fields, 11);
        this.OrderingProvider = get(fields, 12);
        this.EnterersLocation = get(fields, 13);
        this.CallBackPhoneNum = get(fields, 14);
        this.OrderEffectiveDateTime = get(fields, 15);
        this.OrderControlCodeRsn = get(fields, 16);
        this.EnteringOrg = get(fields, 17);
        this.EnteringDevice = get(fields, 18);
        this.ActionBy = get(fields, 19);
    }


}
