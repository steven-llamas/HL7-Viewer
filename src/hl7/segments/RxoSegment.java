package hl7.segments;

public class RxoSegment extends Hl7segment{
    // listing all fields in RXO
    private String requestedGiveCode;
    private String requestedGiveAmtMin;
    private String requestedGiveAmtMax;
    private String requestedGiveUnits;
    private String requestedDosageForm;
    private String providersPharmInstructions;
    private String providersAdminInstructions;
    private String deliverToLocation;
    private String allowSubstitutions;
    private String requestedDispenseCode;
    private String requestedDispenseAmt;
    private String requestedDispenseUnits;
    private String numOfRefills;
    private String orderingProviderDeaNum;
    private String pharmacistTreatmentSupplierVerifyID;
    private String needsHumanReview;
    private String requestedGivePer;
    private String requestedGiveStrength;
    private String requestedGiveStrengthUnits;
    private String indication;
    private String requestedGiveRateAmt;
    private String requestedGiveRateUnits;


    public RxoSegment(String[] fields){
        this.requestedGiveCode = get(fields, 1);
        this.requestedGiveAmtMin = get(fields, 2);
        this.requestedGiveAmtMax = get(fields, 3);
        this.requestedGiveUnits = get(fields, 4);
        this.requestedDosageForm = get(fields, 5);
        this.providersPharmInstructions = get(fields, 6);
        this.providersAdminInstructions = get(fields, 7);
        this.deliverToLocation = get(fields, 8);
        this.allowSubstitutions = get(fields, 9);
        this.requestedDispenseCode = get(fields, 10);
        this.requestedDispenseAmt = get(fields, 11);
        this.requestedDispenseUnits = get(fields, 12);
        this.numOfRefills = get(fields, 13);
        this.orderingProviderDeaNum = get(fields, 14);
        this.pharmacistTreatmentSupplierVerifyID = get(fields, 15);
        this.needsHumanReview = get(fields, 16);
        this.requestedGivePer = get(fields, 17);
        this.requestedGiveStrength = get(fields, 18);
        this.requestedGiveStrengthUnits = get(fields, 19);
        this.indication = get(fields, 20);
        this.requestedGiveRateAmt = get(fields, 21);
        this.requestedGiveRateUnits = get(fields, 22);
    }
}
