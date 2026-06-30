package hl7Viewer.nonGui.config;

public abstract class AbstractIniItem<T> {
    private String section;

    private String key;

    private T value;


    public AbstractIniItem(String section, String key, T value) {
        this.section = section;
        this.key = key;
        this.value = value;
    }


    public abstract void convertAndSetValue(final String rawVal);


    public T getValue() {
        return value;
    }


    public void setValue(T value) {
        this.value = value;
    }


    public String getKey() {
        return key;
    }


    public void setKey(String key) {
        this.key = key;
    }


    public String getSection() {
        return section;
    }


    public void setSection(String section) {
        this.section = section;
    }


    @Override
    public String toString() {
        return key + "=" +
                (value != null ? String.valueOf(value) : "");
    }
}
