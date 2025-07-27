package hl7.segments;

import hl7.segments.subsegments.PatientLocationSub;

public class Pv1Segment extends Hl7segment {
    //listing all fields in PV1
    private String setIdPatVisit;
    private String patClass;
    private String assignedPatLocation;
    private String admissionType;
    private String preadmitNumber;
    private String priorPatLocation;
    private String attendingDoc;
    private String referringDoc;
    private String consultingDoc;
    private String hospitalService;
    private String tempLocation;
    private String preadmitTestIndicator;
    private String readmissionIndicator;
    private String admitSource;
    private String ambulatoryStatus;
    private String vipIndicator;
    private String admittingDoc;
    private String patType;
    private String visitNum;
    private String financialClass;
    private String chargePriceIndicator;
    private String courtesyCode;
    private String creditRating;
    private String contractCode;
    private String contractEffectiveDate;
    private String contractAmount;
    private String contractPeriod;
    private String interestCode;
    private String transferToBadDebtCode;
    private String transferToBadDebtDate;
    private String dischargeDisposition;
    private String dischargedToLocation;
    private String dietType;
    private String servicingFac;
    private String bedStatus;
    private String accountStatus;
    private String pendingLocation;
    private String priorTemporaryLocation;
    private String admitDateTime;
    private String dischargeDateTime;
    private String currentPatBalance;
    private String totalCharges;
    private String totalAdjustments;
    private String totalPayments;
    private String alternateVisitId;
    private String visitIndicator;
    private String otherHealthcareProvider;

    public Pv1Segment(String[] fields) {
        this.setIdPatVisit = get(fields, 1);
        this.patClass = get(fields, 2);
        this.assignedPatLocation = new PatientLocationSub(get(fields, 3)).toString();
        this.admissionType = get(fields, 4);
        this.preadmitNumber = get(fields, 5);
        this.priorPatLocation = get(fields, 6);
        this.attendingDoc = get(fields, 7);
        this.referringDoc = get(fields, 8);
        this.consultingDoc = get(fields, 9);
        this.hospitalService = get(fields, 10);
        this.tempLocation = get(fields, 11);
        this.preadmitTestIndicator = get(fields, 12);
        this.readmissionIndicator = get(fields, 13);
        this.admitSource = get(fields, 14);
        this.ambulatoryStatus = get(fields, 15);
        this.vipIndicator = get(fields, 16);
        this.admittingDoc = get(fields, 17);
        this.patType = get(fields, 18);
        this.visitNum = get(fields, 19);
        this.financialClass = get(fields, 20);
        this.chargePriceIndicator = get(fields, 21);
        this.courtesyCode = get(fields, 22);
        this.creditRating = get(fields, 23);
        this.contractCode = get(fields, 24);
        this.contractEffectiveDate = get(fields, 25);
        this.contractAmount = get(fields, 26);
        this.contractPeriod = get(fields, 27);
        this.interestCode = get(fields, 28);
        this.transferToBadDebtCode = get(fields, 29);
        this.transferToBadDebtDate = get(fields, 30);
        this.dischargeDisposition = get(fields, 31);
        this.dischargedToLocation = get(fields, 32);
        this.dietType = get(fields, 33);
        this.servicingFac = get(fields, 34);
        this.bedStatus = get(fields, 35);
        this.accountStatus = get(fields, 36);
        this.pendingLocation = get(fields, 37);
        this.priorTemporaryLocation = get(fields, 38);
        this.admitDateTime = get(fields, 39);
        this.dischargeDateTime = get(fields, 40);
        this.currentPatBalance = get(fields, 41);
        this.totalCharges = get(fields, 42);
        this.totalAdjustments = get(fields, 43);
        this.totalPayments = get(fields, 44);
        this.alternateVisitId = get(fields, 45);
        this.visitIndicator = get(fields, 46);
        this.otherHealthcareProvider = get(fields, 47);

    }


}


