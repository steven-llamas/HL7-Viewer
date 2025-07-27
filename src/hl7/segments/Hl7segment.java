package hl7.segments;

public abstract class Hl7segment {

    protected String get(String[] fields, int index) {
        return index < fields.length ? fields [index] : null;
    }

}