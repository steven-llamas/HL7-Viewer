package hl7.segments;

public class PidSegment extends Hl7segment {
    //listing all segments in PID
    private String setID;
    private String patIdExternal;
    private String patIdInternal;
    private String patName;
    private String mothersMaidenName;
    private String dateOfBirth;
    private String gender;
    private String patAlias;
    private String race;
    private String patAddress;
    private String countryCode;
    private String phoneNumHome;
    private String phoneNumBiz;
    private String primaryLang;
    private String maritalStatus;
    private String religion;
    private String patAccountNum;
    private String ssnNum;
    private String dlNum;
    private String mothersIdentifier;
    private String ethnicGroup;
    private String birthPlace;
    private String multipleBirthIndicator;
    private String birthOrder;
    private String citizenship;
    private String veteranStatus;
    private String nationalityCode;
    private String patDeathDateTime;
    private String patDeathIndicator;

    public PidSegment(String[] fields) {
        this.setID = get(fields, 1);
        this.patIdExternal = get(fields, 2);
        this.patIdInternal = get(fields, 3);
        this.patName = get(fields, 5);
        this.mothersMaidenName = get(fields, 6);
        this.dateOfBirth = get(fields, 7);
        this.gender = get(fields, 8);
        this.patAlias = get(fields, 9);
        this.race = get(fields, 10);
        this.patAddress = get(fields, 11);
        this.countryCode = get(fields, 12);
        this.phoneNumHome = get(fields, 13);
        this.phoneNumBiz = get(fields, 14);
        this.primaryLang = get(fields, 15);
        this.maritalStatus = get(fields, 16);
        this.religion = get(fields, 17);
        this.patAccountNum = get(fields, 18);
        this.ssnNum = get(fields, 19);
        this.dlNum = get(fields, 20);
        this.mothersIdentifier = get(fields, 21);
        this.ethnicGroup = get(fields, 22);
        this.birthPlace = get(fields, 23);
        this.multipleBirthIndicator = get(fields, 24);
        this.birthOrder = get(fields, 25);
        this.citizenship = get(fields, 26);
        this.veteranStatus = get(fields, 27);
        this.nationalityCode = get(fields, 28);
        this.patDeathDateTime = get(fields, 29);
        this.patDeathIndicator = get(fields, 30);
    }

}
