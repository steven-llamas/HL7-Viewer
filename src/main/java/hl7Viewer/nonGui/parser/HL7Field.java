package hl7Viewer.nonGui.parser;

import java.util.List;

public class HL7Field {
    private final List<HL7Repetition> repetitionList;


    public HL7Field(List<HL7Repetition> repetitionList) {
        this.repetitionList = repetitionList;
    }


    public void addRepetition(HL7Repetition repetition) {
        if (repetition != null)
            repetitionList.addLast(repetition);
    }


    public List<HL7Repetition> getRepetitionList() {
        return repetitionList;
    }


    public boolean hasRepetitions() {
        return getRepetitionList().size() > 1;
    }
}
