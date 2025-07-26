package hl7.segments;

public class RxrSegment extends Hl7segment{
    //listing all segments in RXE
    private String route;
    private String site;
    private String administrationDevice;
    private String administrationMethod;

    public RxrSegment(String[] fields){

        this.route = get(fields, 1);
        this.site = get(fields,2);
        this.administrationDevice = get(fields, 3);
        this.administrationMethod = get(fields, 4);
    }

}
