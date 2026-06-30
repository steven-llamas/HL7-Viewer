package hl7Viewer.nonGui.config;

public class IniShowEmptyVals extends AbstractIniItem<Boolean> {

    public IniShowEmptyVals() {
        super("HL7Options", "ShowEmptyValues", false);
    }


    @Override
    public void convertAndSetValue(final String rawVal) {
        final Boolean val = rawVal.equalsIgnoreCase("true");
        setValue(val);
    }
}
