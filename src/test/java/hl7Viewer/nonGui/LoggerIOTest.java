package hl7Viewer.nonGui;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LoggerIOTest {

    private static Path createStaleFile(Path dir, String name) throws IOException {
        final var file = dir.resolve(name);
        Files.writeString(file, "content");
        final var yesterday = LocalDate.now().minusDays(1)
                .atStartOfDay(ZoneId.systemDefault()).toInstant();
        Files.setLastModifiedTime(file, FileTime.from(yesterday));
        return file;
    }

    private static Path createFreshFile(Path dir, String name) throws IOException {
        final var file = dir.resolve(name);
        Files.writeString(file, "content");
        return file;
    }

    private static LoggerIO makeIO(Path logsDir, int maxFileCount) {
        return new LoggerIO(logsDir.resolve("log.txt").toString(), maxFileCount);
    }


    @Nested
    @DisplayName("When archiving files")
    class ArchiveFilesTests {

        @Test
        @DisplayName("should move stale file into Archive subdirectory")
        void archiveFiles_MovesStaleFileToArchive(@TempDir Path dir) throws IOException {
            final var logsDir = dir.resolve("logs");
            Files.createDirectories(logsDir);
            createStaleFile(logsDir, "log.txt");

            makeIO(logsDir, 31);

            final var archiveDir = logsDir.resolve("Archive");
            assertTrue(Files.exists(archiveDir));

            final var archived = archiveDir.toFile().listFiles();
            assertNotNull(archived);
            assertEquals(1, archived.length);
        }

        @Test
        @DisplayName("archived file name should contain the original date stamp")
        void archiveFiles_RenamesFileWithDateStamp(@TempDir Path dir) throws IOException {
            final var logsDir = dir.resolve("logs");
            Files.createDirectories(logsDir);
            createStaleFile(logsDir, "log.txt");

            makeIO(logsDir, 31);

            final var archiveDir = logsDir.resolve("Archive");
            final var archived = archiveDir.toFile().listFiles();
            assertNotNull(archived);

            final var expectedStamp = LocalDate.now().minusDays(1)
                    .format(DateTimeFormatter.ofPattern("yyyy_MM_dd"));

            assertTrue(archived[0].getName().contains(expectedStamp),
                    "Archived name should contain date stamp: " + expectedStamp);
        }

        @Test
        @DisplayName("should not move a fresh (today's) file to archive")
        void archiveFiles_DoesNotMoveFreshFile(@TempDir Path dir) throws IOException {
            final var logsDir = dir.resolve("logs");
            Files.createDirectories(logsDir);
            createFreshFile(logsDir, "log.txt");

            makeIO(logsDir, 31);

            final var archiveDir = logsDir.resolve("Archive");
            if (Files.exists(archiveDir)) {
                final var files = archiveDir.toFile().listFiles();
                assertTrue(files == null || files.length == 0,
                        "Fresh file should not have been archived");
            }
        }

        @Test
        @DisplayName("should do nothing when logs directory is empty")
        void archiveFiles_DoesNothingWhenDirEmpty(@TempDir Path dir) throws IOException {
            final var logsDir = dir.resolve("logs");
            Files.createDirectories(logsDir);

            assertDoesNotThrow(() -> makeIO(logsDir, 31));
        }

        @Test
        @DisplayName("should not throw when logs directory does not exist")
        void archiveFiles_DoesNotThrowWhenDirMissing(@TempDir Path dir) {
            final var missing = dir.resolve("nologs").resolve("log.txt");
            assertDoesNotThrow(() -> new LoggerIO(missing.toString(), 31));
        }
    }


    @Nested
    @DisplayName("When reducing the archive folder")
    class ReduceArchiveFolderTests {

        @Test
        @DisplayName("should delete files when archive count exceeds maxFileCount")
        void reduceArchiveFolder_DeletesWhenOverLimit(@TempDir Path dir) throws IOException {
            final var logsDir = dir.resolve("logs");
            final var archiveDir = logsDir.resolve("Archive");
            Files.createDirectories(archiveDir);

            for (int i = 1; i <= 5; i++)
                Files.writeString(archiveDir.resolve("log_2024_01_0" + i + ".txt"), "old");

            createStaleFile(logsDir, "log.txt");

            makeIO(logsDir, 3);

            final var remaining = archiveDir.toFile().listFiles();
            assertNotNull(remaining);
            assertTrue(remaining.length <= 4,
                    "Archive should have been reduced, found: " + remaining.length);
        }

        @Test
        @DisplayName("should not delete files when archive count is within limit")
        void reduceArchiveFolder_DoesNotDeleteWhenUnderLimit(@TempDir Path dir) throws IOException {
            final var logsDir = dir.resolve("logs");
            final var archiveDir = logsDir.resolve("Archive");
            Files.createDirectories(archiveDir);

            for (int i = 1; i <= 3; i++)
                Files.writeString(archiveDir.resolve("log_2024_01_0" + i + ".txt"), "old");

            createStaleFile(logsDir, "log.txt");

            makeIO(logsDir, 10);

            final var remaining = archiveDir.toFile().listFiles();
            assertNotNull(remaining);
            assertEquals(4, remaining.length, "No files should have been deleted");
        }

        @Test
        @DisplayName("should delete the alphabetically earliest file first")
        void reduceArchiveFolder_DeletesAlphabeticallyEarliestFirst(@TempDir Path dir) throws IOException {
            final var logsDir = dir.resolve("logs");
            final var archiveDir = logsDir.resolve("Archive");
            Files.createDirectories(archiveDir);

            Files.writeString(archiveDir.resolve("log_2024_01_01.txt"), "oldest");
            Files.writeString(archiveDir.resolve("log_2024_06_01.txt"), "middle");
            Files.writeString(archiveDir.resolve("log_2024_12_01.txt"), "newest");

            createStaleFile(logsDir, "log.txt");

            makeIO(logsDir, 2);

            final var remaining = archiveDir.toFile().listFiles();
            assertNotNull(remaining);
            assertFalse(
                    Arrays.stream(remaining).anyMatch(f -> f.getName().contains("2024_01_01")),
                    "Oldest file should have been deleted first"
            );
        }
    }


    @Nested
    @DisplayName("When writing")
    class WriteTests {

        @Test
        @DisplayName("should return true on successful write")
        void write_ReturnsTrueOnSuccess(@TempDir Path dir) throws IOException {
            final var logsDir = dir.resolve("logs");
            Files.createDirectories(logsDir);
            final var io = makeIO(logsDir, 31);

            assertTrue(io.write(List.of("line1", "line2"), true));
        }

        @Test
        @DisplayName("should append content to existing file")
        void write_AppendsContentToFile(@TempDir Path dir) throws IOException {
            final var logsDir = dir.resolve("logs");
            Files.createDirectories(logsDir);
            final var logFile = logsDir.resolve("log.txt");
            Files.writeString(logFile, "existing");

            final var io = makeIO(logsDir, 31);
            io.write(List.of("new line"), true);

            final var content = Files.readString(logFile);
            assertTrue(content.contains("existing"));
            assertTrue(content.contains("new line"));
        }
    }
}