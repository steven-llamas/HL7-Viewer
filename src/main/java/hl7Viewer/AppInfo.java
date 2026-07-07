package hl7Viewer;

import java.io.File;
import java.net.URISyntaxException;

public final class AppInfo {
    public static final String APP_NAME = "HL7 Viewer";

    public static final String VERSION = "1.1.0";

    public static final String BUILD_TYPE = isJar() ? "Release" : "Debug";

    public static final boolean IS_DEBUG = !isJar();

    public static final boolean IS_MAC_OS =
            System.getProperty("os.name").toLowerCase().contains("mac");


    private static boolean isJar() {
        return AppInfo.class.getProtectionDomain()
                .getCodeSource().getLocation().toString().endsWith(".jar");
    }


    private AppInfo() {}
}
