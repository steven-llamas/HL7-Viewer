package hl7Viewer;

import java.io.File;
import java.net.URISyntaxException;

public final class AppInfo {
    public static final String VERSION = "0.2";

    public static final String BUILD_TYPE = isJar() ? "Release" : "Debug";

    public static final boolean IS_MAC_OS =
            System.getProperty("os.name").toLowerCase().contains("mac");

    public static final String CONFIG_PATH = resolveConfigPath();

    private static String resolveConfigPath() {
        try {
            final var binDir = new File(AppInfo.class.getProtectionDomain()
                    .getCodeSource().getLocation().toURI()).getParent();
            return binDir + File.separator + "config.ini";
        } catch (URISyntaxException e) {
            return "config.ini";
        }
    }

    private static boolean isJar() {
        return AppInfo.class.getProtectionDomain()
                .getCodeSource().getLocation().toString().endsWith(".jar");
    }

    private AppInfo() {}
}
