package hl7Viewer;

public enum OsType {
    WINDOWS,
    MAC,
    UNIX,
    UNKNOWN;

    private static final String OS = System.getProperty("os.name").toLowerCase();

    public static final OsType TYPE = getType();


    private static OsType getType() {

        if (OS.contains("win"))
            return WINDOWS;
        if(OS.contains("mac"))
            return MAC;
        if (OS.contains("nix") || OS.contains("nux") || OS.contains("aix"))
            return UNIX;

        return UNKNOWN;
    }
}
