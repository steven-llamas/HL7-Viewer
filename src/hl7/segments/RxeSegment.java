package hl7.segments;

public class RxeSegment extends Hl7segment{
    //listing all fields in RXE
    private String QuantityTiming;
    private String GiveCode;
    private String GiveAmountMin;
    private String GiveAmountMax;
    private String GiveUnits;
    private String GiveDosageForm;
    private String ProvidersAdminInstructions;
    private String DeliverToLocation;
    private String SubstitutionStatus;
    private String DispenseAmount;
    private String DispenseUnits;
    private String NumberOfRefills;
    private String OrderingProvidersDeaNum;
    private String PharmacistTreatSupplierVerifyID;
    private String ScriptNum;
    private String NumOfRefillsRemaining;
    private String NumOfRefillsDosesDispensed;
    private String TotalDailyDose;
    private String NeedsHumanReview;
    private String PharmacyTreatmentSupplierSpecialInstructions;
    private String GivePer;
    private String GiveRateAmount;
    private String GiveRateUnits;
    private String GiveStrength;
    private String GiveStrengthUnits;
    private String GiveIndications;
    private String DispensePackageSize;
    private String DispensePackageSizeUnit;
    private String DispensePackageSizeMethod;


    public RxeSegment(String[] fields) {
        this.QuantityTiming = get(fields, 1);
        this.GiveCode = get(fields, 2);
        this.GiveAmountMin = get(fields, 3);
        this.GiveAmountMax = get(fields, 4);
        this.GiveUnits = get(fields, 5);
        this.GiveDosageForm = get(fields, 6);
        this.ProvidersAdminInstructions = get(fields, 7);
        this.DeliverToLocation = get(fields, 8);
        this.SubstitutionStatus = get(fields, 9);
        this.DispenseAmount = get(fields, 10);
        this.DispenseUnits = get(fields, 11);
        this.NumberOfRefills = get(fields, 12);
        this.OrderingProvidersDeaNum = get(fields, 13);
        this.PharmacistTreatSupplierVerifyID = get(fields, 14);
        this.ScriptNum = get(fields, 15);
        this.NumOfRefillsRemaining = get(fields, 16);
        this.NumOfRefillsDosesDispensed = get(fields, 17);
        this.TotalDailyDose = get(fields, 18);
        this.NeedsHumanReview = get(fields, 19);
        this.PharmacyTreatmentSupplierSpecialInstructions = get(fields, 20);
        this.GivePer = get(fields, 21);
        this.GiveRateAmount = get(fields, 22);
        this.GiveRateUnits = get(fields, 23);
        this.GiveStrength = get(fields, 24);
        this.GiveStrengthUnits = get(fields, 25);
        this.GiveIndications = get(fields, 26);
        this.DispensePackageSize = get(fields, 27);
        this.DispensePackageSizeUnit = get(fields, 28);
        this.DispensePackageSizeMethod = get(fields, 29);
    }

}
