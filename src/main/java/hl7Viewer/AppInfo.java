package hl7Viewer;

import java.io.File;
import java.net.URISyntaxException;

public final class AppInfo {
    public static final String VERSION = "1.0.4";

    public static final String BUILD_TYPE = isJar() ? "Release" : "Debug";

    public static final boolean IS_MAC_OS =
            System.getProperty("os.name").toLowerCase().contains("mac");

    public static final String CONFIG_PATH = resolvePath("config.ini");

    public static String resolvePath(final String filename) {
        if (!isJar())
            return filename;

        try {
            final var binDir = new File(AppInfo.class.getProtectionDomain()
                    .getCodeSource().getLocation().toURI()).getParent();
            return binDir + File.separator + filename;
        } catch (URISyntaxException e) {
            return filename;
        }
    }

    private static boolean isJar() {
        return AppInfo.class.getProtectionDomain()
                .getCodeSource().getLocation().toString().endsWith(".jar");
    }

    private AppInfo() {}
}
