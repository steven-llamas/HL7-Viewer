package hl7Viewer.nonGui.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class IniReaderWriterTest {

    private IniReaderWriter readerWriterFrom(Path dir, String content) throws IOException {
        final var file = dir.resolve("config.ini");
        Files.writeString(file, content);
        final var rw = new IniReaderWriter(file.toString());
        rw.read();
        return rw;
    }


    @Nested
    @DisplayName("When parsing comments")
    class CommentTests {

        @Test
        @DisplayName("should ignore lines starting with semicolon")
        void read_IgnoresCommentLines(@TempDir Path dir) throws IOException {
            final var rw = readerWriterFrom(dir, "; this is a comment");

            assertFalse(rw.outConfigMapHasItems());
        }

        @Test
        @DisplayName("should ignore comment lines within a section")
        void read_IgnoresCommentLinesInsideSection(@TempDir Path dir) throws IOException {
            final var rw = readerWriterFrom(dir, """
                    [section]
                    ; comment
                    key=value
                    """);

            assertEquals("value", rw.getOutConfigMap().get("section.key"));
        }
    }


    @Nested
    @DisplayName("When parsing section headers")
    class SectionHeaderTests {

        @Test
        @DisplayName("should parse section name correctly")
        void read_ParsesSectionName(@TempDir Path dir) throws IOException {
            final var rw = readerWriterFrom(dir, """
                    [owner]
                    name=John
                    """);

            assertTrue(rw.getOutConfigMap().containsKey("owner.name"));
        }

        @Test
        @DisplayName("should trim whitespace from section name")
        void read_TrimsSectionName(@TempDir Path dir) throws IOException {
            final var rw = readerWriterFrom(dir, """
                    [  owner  ]
                    name=John
                    """);

            assertTrue(rw.getOutConfigMap().containsKey("owner.name"));
        }

        @Test
        @DisplayName("should ignore malformed section header missing closing bracket")
        void read_IgnoresMalformedSectionHeader(@TempDir Path dir) throws IOException {
            final var rw = readerWriterFrom(dir, """
                    [owner
                    name=John
                    """);

            assertFalse(rw.getOutConfigMap().containsKey("owner.name"));
        }
    }


    @Nested
    @DisplayName("When parsing key=value pairs")
    class KeyValueTests {

        @Test
        @DisplayName("should store key=value under the current section")
        void read_StoresKeyValueUnderSection(@TempDir Path dir) throws IOException {
            final var rw = readerWriterFrom(dir, """
                    [database]
                    port=143
                    """);

            assertEquals("143", rw.getOutConfigMap().get("database.port"));
        }

        @Test
        @DisplayName("should trim spaces around key and value")
        void read_TrimsSpacesAroundKeyAndValue(@TempDir Path dir) throws IOException {
            final var rw = readerWriterFrom(dir, """
                    [owner]
                    name = John Doe
                    """);

            assertEquals("John Doe", rw.getOutConfigMap().get("owner.name"));
        }

        @Test
        @DisplayName("should handle multiple sections")
        void read_HandlesMultipleSections(@TempDir Path dir) throws IOException {
            final var rw = readerWriterFrom(dir, """
                    [owner]
                    name=John
                    [database]
                    port=143
                    """);

            assertEquals("John", rw.getOutConfigMap().get("owner.name"));
            assertEquals("143", rw.getOutConfigMap().get("database.port"));
        }

        @Test
        @DisplayName("should skip pairs with empty key")
        void read_SkipsPairWithEmptyKey(@TempDir Path dir) throws IOException {
            final var rw = readerWriterFrom(dir, """
                    [section]
                    =value
                    """);

            assertFalse(rw.outConfigMapHasItems());
        }

        @Test
        @DisplayName("should skip pairs with empty value")
        void read_SkipsPairWithEmptyValue(@TempDir Path dir) throws IOException {
            final var rw = readerWriterFrom(dir, """
                    [section]
                    key=
                    """);

            assertFalse(rw.outConfigMapHasItems());
        }

        @Test
        @DisplayName("should not overwrite existing key with putIfAbsent")
        void read_DoesNotOverwriteExistingKey(@TempDir Path dir) throws IOException {
            final var rw = readerWriterFrom(dir, """
                    [section]
                    key=first
                    key=second
                    """);

            assertEquals("first", rw.getOutConfigMap().get("section.key"));
        }

        @Test
        @DisplayName("should ignore lines without = that are not sections or comments")
        void read_IgnoresLinesWithoutPairToken(@TempDir Path dir) throws IOException {
            final var rw = readerWriterFrom(dir, """
                    [section]
                    justAWord
                    """);

            assertFalse(rw.outConfigMapHasItems());
        }
    }


    @Nested
    @DisplayName("When managing the config map")
    class ConfigMapTests {

        @Test
        @DisplayName("outConfigMapHasItems should return false on empty map")
        void configMapHasItems_ReturnsFalseWhenEmpty(@TempDir Path dir) throws IOException {
            final var rw = readerWriterFrom(dir, "; only a comment");

            assertFalse(rw.outConfigMapHasItems());
        }

        @Test
        @DisplayName("outConfigMapHasItems should return true when map has entries")
        void configMapHasItems_ReturnsTrueWhenPopulated(@TempDir Path dir) throws IOException {
            final var rw = readerWriterFrom(dir, "[s]\nk=v");

            assertTrue(rw.outConfigMapHasItems());
        }

        @Test
        @DisplayName("clearOutConfigMap should empty the map")
        void clearConfigMap_EmptiesMap(@TempDir Path dir) throws IOException {
            final var rw = readerWriterFrom(dir, "[s]\nk=v");
            rw.clearOutConfigMap();

            assertFalse(rw.outConfigMapHasItems());
        }
    }
}
