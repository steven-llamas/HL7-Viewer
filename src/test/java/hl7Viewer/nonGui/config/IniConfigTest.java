package hl7Viewer.nonGui.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class IniConfigTest {

    private IniConfig configFrom(Path dir, String content) throws IOException {
        final var file = dir.resolve("config.ini");
        Files.writeString(file, content);
        final var rw = new IniReaderWriter(file.toString());
        rw.read();
        return new IniConfig(rw);
    }


    @Nested
    @DisplayName("When reading values")
    class GetValueTests {

        @Test
        @DisplayName("getBoolean should return parsed value")
        void getBoolean_ReturnsParsedValue(@TempDir Path dir) throws IOException {
            final var config = configFrom(dir, "[section]\nshowEmpty=true");

            assertTrue(config.getBoolean("section", "showEmpty", false));
        }

        @Test
        @DisplayName("getBoolean should return default when key is absent")
        void getBoolean_ReturnsDefaultWhenAbsent(@TempDir Path dir) throws IOException {
            final var config = configFrom(dir, "[section]\nother=true");

            assertTrue(config.getBoolean("section", "missing", true));
        }

        @Test
        @DisplayName("getInt should return parsed value")
        void getInt_ReturnsParsedValue(@TempDir Path dir) throws IOException {
            final var config = configFrom(dir, "[database]\nport=143");

            assertEquals(143, config.getInt("database", "port", 0));
        }

        @Test
        @DisplayName("getInt should return default when key is absent")
        void getInt_ReturnsDefaultWhenAbsent(@TempDir Path dir) throws IOException {
            final var config = configFrom(dir, "[database]\nother=143");

            assertEquals(99, config.getInt("database", "missing", 99));
        }

        @Test
        @DisplayName("getInt should return default when value is not a number")
        void getInt_ReturnsDefaultWhenUnparseable(@TempDir Path dir) throws IOException {
            final var config = configFrom(dir, "[database]\nport=notanumber");

            assertEquals(99, config.getInt("database", "port", 99));
        }

        @Test
        @DisplayName("getString should return parsed value")
        void getString_ReturnsParsedValue(@TempDir Path dir) throws IOException {
            final var config = configFrom(dir, "[owner]\nname=John Doe");

            assertEquals("John Doe", config.getString("owner", "name", ""));
        }

        @Test
        @DisplayName("getString should return default when key is absent")
        void getString_ReturnsDefaultWhenAbsent(@TempDir Path dir) throws IOException {
            final var config = configFrom(dir, "[owner]\nother=John");

            assertEquals("default", config.getString("owner", "missing", "default"));
        }
    }


    @Nested
    @DisplayName("When setting and saving values")
    class SetAndSaveTests {

        @Test
        @DisplayName("set should update the value in memory")
        void set_UpdatesValueInMemory(@TempDir Path dir) throws IOException {
            final var config = configFrom(dir, "[owner]\nname=John");
            config.set("owner", "name", "Jane");

            assertEquals("Jane", config.getString("owner", "name", ""));
        }

        @Test
        @DisplayName("save should write content to file")
        void save_WritesContentToFile(@TempDir Path dir) throws IOException {
            final var file = dir.resolve("config.ini");
            Files.writeString(file, "[owner]\nname=John");
            final var rw = new IniReaderWriter(file.toString());
            rw.read();
            final var config = new IniConfig(rw);

            config.set("owner", "name", "Jane");
            config.save();

            final var saved = Files.readString(file);
            assertTrue(saved.contains("name=Jane"));
        }

        @Test
        @DisplayName("save should not write when config is empty")
        void save_DoesNothingWhenEmpty(@TempDir Path dir) throws IOException {
            final var file = dir.resolve("config.ini");
            Files.writeString(file, "; only a comment");
            final var rw = new IniReaderWriter(file.toString());
            rw.read();
            final var config = new IniConfig(rw);

            config.save();

            assertEquals("; only a comment", Files.readString(file));
        }
    }


    @Nested
    @DisplayName("When building output for multiple sections")
    class SectionOutputTests {

        @Test
        @DisplayName("first section should have no leading blank line")
        void save_FirstSectionHasNoLeadingBlankLine(@TempDir Path dir) throws IOException {
            final var file = dir.resolve("config.ini");
            Files.writeString(file, "[owner]\nname=John");
            final var rw = new IniReaderWriter(file.toString());
            rw.read();
            final var config = new IniConfig(rw);

            config.save();

            assertFalse(Files.readString(file).startsWith("\n"));
        }

        @Test
        @DisplayName("second section should be preceded by a blank line")
        void save_SecondSectionHasLeadingBlankLine(@TempDir Path dir) throws IOException {
            final var file = dir.resolve("config.ini");
            Files.writeString(file, "[owner]\nname=John\n[database]\nport=143");
            final var rw = new IniReaderWriter(file.toString());
            rw.read();
            final var config = new IniConfig(rw);

            config.save();

            assertTrue(Files.readString(file).contains("\n[database]"));
        }
    }
}
