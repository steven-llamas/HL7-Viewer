package hl7Viewer.nonGui.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/** Test utility for creating IniConfig instances from temp directories. */
public class IniConfigTestHelper {

    public static IniConfig emptyConfig(Path dir) throws IOException {
        return configWithContent(dir, "; empty");
    }

    public static IniConfig configWithContent(Path dir, String iniContent) throws IOException {
        final var file = dir.resolve("config.ini");
        Files.writeString(file, iniContent);
        final var rw = new IniIO(file.toString());
        rw.read();
        return new IniConfig(rw);
    }
}