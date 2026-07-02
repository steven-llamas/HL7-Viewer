package hl7Viewer.nonGui;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AbstractFileReaderWriterTest {

    private static class ConcreteReaderWriter extends AbstractFileReaderWriter {
        final List<String> linesRead = new ArrayList<>();

        ConcreteReaderWriter(String fileName) {
            super(fileName);
        }

        @Override
        protected void onReadLine(String line) {
            linesRead.add(line);
        }
    }


    @Nested
    @DisplayName("When reading files")
    class ReadTests {

        @Test
        @DisplayName("should call onReadLine for each non-empty line")
        void read_CallsOnReadLineForEachNonEmptyLine(@TempDir Path dir) throws IOException {
            final var file = dir.resolve("test.ini");
            Files.writeString(file, "line1\nline2\nline3");

            final var rw = new ConcreteReaderWriter(file.toString());
            rw.read();

            assertEquals(List.of("line1", "line2", "line3"), rw.linesRead);
        }

        @Test
        @DisplayName("should skip empty and blank lines")
        void read_SkipsEmptyLines(@TempDir Path dir) throws IOException {
            final var file = dir.resolve("test.ini");
            Files.writeString(file, "line1\n\n   \nline2");

            final var rw = new ConcreteReaderWriter(file.toString());
            rw.read();

            assertEquals(List.of("line1", "line2"), rw.linesRead);
        }

        @Test
        @DisplayName("should trim leading and trailing whitespace from lines")
        void read_TrimsLines(@TempDir Path dir) throws IOException {
            final var file = dir.resolve("test.ini");
            Files.writeString(file, "  line1  \n  line2  ");

            final var rw = new ConcreteReaderWriter(file.toString());
            rw.read();

            assertEquals(List.of("line1", "line2"), rw.linesRead);
        }

        @Test
        @DisplayName("should return true on success")
        void read_ReturnsTrueOnSuccess(@TempDir Path dir) throws IOException {
            final var file = dir.resolve("test.ini");
            Files.writeString(file, "line1");

            final var rw = new ConcreteReaderWriter(file.toString());

            assertTrue(rw.read());
        }

        @Test
        @DisplayName("should return false when reading a file that does not exist")
        void read_ReturnsFalseWhenFileNotFound() {
            final var rw = new ConcreteReaderWriter("nonexistent_file.ini");

            assertFalse(rw.read());
        }
    }


    @Nested
    @DisplayName("When writing files")
    class WriteTests {

        @Test
        @DisplayName("should write all items to file")
        void write_WritesAllItemsToFile(@TempDir Path dir) throws IOException {
            final var file = dir.resolve("test.ini");
            final var rw = new ConcreteReaderWriter(file.toString());

            rw.write(List.of("line1", "line2", "line3"), false);

            final var written = Files.readString(file);
            assertTrue(written.contains("line1"));
            assertTrue(written.contains("line2"));
            assertTrue(written.contains("line3"));
        }

        @Test
        @DisplayName("should return true on successful write")
        void write_ReturnsTrueOnSuccess(@TempDir Path dir) {
            final var file = dir.resolve("test.ini");
            final var rw = new ConcreteReaderWriter(file.toString());

            assertTrue(rw.write(List.of("line1"), false));
        }

        @Test
        @DisplayName("should return false when write fails")
        void write_ReturnsFalseOnFailure() {
            final var rw = new ConcreteReaderWriter("/invalid_path/test.ini");

            assertFalse(rw.write(List.of("line1"), false));
        }
    }
}