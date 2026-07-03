package hl7Viewer.nonGui.hl7Parser;

import java.util.List;

public abstract class HL7Base<T> {
    protected List<T> items;


    protected HL7Base(List<T> items) {
        this.items = items;
    }


    protected HL7Base() {

    }


    public void setItems(List<T> items) {
        this.items = items;
    }

    public void add(T item) {
        if (item != null)
            items.addLast(item);
    }


    public List<T> getItems() {
        return items;
    }


    public boolean hasItems() {
        return items.size() > 1;
    }
}
