package hl7Viewer;

public final class AppInfo {
    public static final String VERSION = "0.2";

    public static final String BUILD_TYPE = isJar() ? "Release" : "Debug";

    public static final boolean IS_MAC_OS =
            System.getProperty("os.name").toLowerCase().contains("mac");

    private static boolean isJar() {
        return AppInfo.class.getProtectionDomain()
                .getCodeSource().getLocation().toString().endsWith(".jar");
    }


    private AppInfo() {}
}
