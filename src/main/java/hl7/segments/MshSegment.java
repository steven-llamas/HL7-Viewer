package hl7.segments;

public class MshSegment extends Hl7segment {
    //listing all fields in MSH
    // MSH.1 declares field separator to be used( aka "|")
    // which we use in Hl7Parse as "|" is the standard
    private String encodingCharacters;
    private String sendingApplication;
    private String sendingFacility;
    private String receivingApplication;
    private String receivingFacility;
    private String dateTimeOfMessage;
    private String security;
    private String messageType;
    private String messageControlId;
    private String processingId;
    private String versionId;
    private String sequenceNumber;
    private String continuationPointer;
    private String acceptAckType;
    private String appAckType;
    private String countryCode;
    private String characterSet;
    private String principalLangOfMessage;

    public MshSegment(String[] fields) {

        this.encodingCharacters = get(fields, 1);
        this.sendingApplication = get(fields, 2);
        this.sendingFacility = get(fields, 3);
        this.receivingApplication = get(fields, 4);
        this.receivingFacility = get(fields, 5);
        this.dateTimeOfMessage = get(fields, 6);
        this.security = get(fields, 7);
        this.messageType = get(fields, 8);
        this.messageControlId = get(fields, 9);
        this.processingId = get(fields, 10);
        this.versionId = get(fields, 11);
        this.sequenceNumber = get(fields, 12);
        this.continuationPointer = get(fields, 13);
        this.acceptAckType = get(fields, 14);
        this.appAckType = get(fields, 15);
        this.countryCode = get(fields, 16);
        this.characterSet = get(fields, 17);
        this.principalLangOfMessage = get(fields, 18);
    }

    public String getEncodingCharacters() {
        return encodingCharacters;
    }

    public String getSendingApplication() {
        return sendingApplication;
    }

    public String getSendingFacility() {
        return sendingFacility;
    }

    public String getReceivingApplication() {
        return receivingApplication;
    }

    public String getReceivingFacility() {
        return receivingFacility;
    }

    public String getDateTimeOfMessage() {
        return dateTimeOfMessage;
    }

    public String getSecurity() {
        return security;
    }

    public String getMessageType() {
        return messageType;
    }

    public String getMessageControlId() {
        return messageControlId;
    }

    public String getProcessingId() {
        return processingId;
    }

    public String getVersionId() {
        return versionId;
    }

    public String getSequenceNumber() {
        return sequenceNumber;
    }

    public String getContinuationPointer() {
        return continuationPointer;
    }

    public String getAcceptAckType() {
        return acceptAckType;
    }

    public String getAppAckType() {
        return appAckType;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public String getCharacterSet() {
        return characterSet;
    }

    public String getPrincipalLangOfMessage() {
        return principalLangOfMessage;
    }
}