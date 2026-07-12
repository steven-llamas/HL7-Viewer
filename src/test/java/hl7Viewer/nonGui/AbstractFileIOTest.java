package hl7Viewer.nonGui;

import hl7Viewer.AppInfo;
import hl7Viewer.OsType;
import hl7Viewer.nonGui.config.IniConfigTestHelper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

class AbstractFileIOTest {

    private static class ConcreteIO extends AbstractFileIO {
        final List<String> linesRead = new ArrayList<>();

        ConcreteIO(String fileName) {
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

            final var rw = new ConcreteIO(file.toString());
            rw.read();

            assertEquals(List.of("line1", "line2", "line3"), rw.linesRead);
        }

        @Test
        @DisplayName("should skip empty and blank lines")
        void read_SkipsEmptyLines(@TempDir Path dir) throws IOException {
            final var file = dir.resolve("test.ini");
            Files.writeString(file, "line1\n\n   \nline2");

            final var rw = new ConcreteIO(file.toString());
            rw.read();

            assertEquals(List.of("line1", "line2"), rw.linesRead);
        }

        @Test
        @DisplayName("should trim leading and trailing whitespace from lines")
        void read_TrimsLines(@TempDir Path dir) throws IOException {
            final var file = dir.resolve("test.ini");
            Files.writeString(file, "  line1  \n  line2  ");

            final var rw = new ConcreteIO(file.toString());
            rw.read();

            assertEquals(List.of("line1", "line2"), rw.linesRead);
        }

        @Test
        @DisplayName("should return true on success")
        void read_ReturnsTrueOnSuccess(@TempDir Path dir) throws IOException {
            final var file = dir.resolve("test.ini");
            Files.writeString(file, "line1");

            final var rw = new ConcreteIO(file.toString());

            assertTrue(rw.read());
        }

        @Test
        @DisplayName("should return false when reading a file that does not exist")
        void read_ReturnsFalseWhenFileNotFound(@TempDir Path dir) {
            final var rw = new ConcreteIO(dir.resolve("nonexistent_file.ini").toString());

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
            final var rw = new ConcreteIO(file.toString());

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
            final var rw = new ConcreteIO(file.toString());

            assertTrue(rw.write(List.of("line1"), false));
        }

        @Test
        @DisplayName("should return false when write fails")
        void write_ReturnsFalseOnFailure(@TempDir Path dir) throws IOException {
            final var blockingFile = dir.resolve("notadir");
            Files.writeString(blockingFile, "blocking");
            final var rw = new ConcreteIO(blockingFile.resolve("test.ini").toString());

            assertFalse(rw.write(List.of("line1"), false));
        }

        @Test
        @DisplayName("should write each item on its own line")
        void write_WritesEachItemOnSeparateLine(@TempDir Path dir) throws IOException {
            final var file = dir.resolve("test.ini");
            final var rw = new ConcreteIO(file.toString());

            rw.write(List.of("line1", "line2", "line3"), false);

            assertEquals(List.of("line1", "line2", "line3"), Files.readAllLines(file));
        }
    }


    @Nested
    @DisplayName("When using pending logs")
    class PendingLogTests {

        private void resetLoggerSingleton() throws ReflectiveOperationException {
            final var field = Logger.class.getDeclaredField("loggerInstance");
            field.setAccessible(true);
            field.set(null, null);
        }

        @BeforeEach
        void reset(@TempDir Path dir) throws ReflectiveOperationException, IOException {
            resetLoggerSingleton();
            Logger.configure(new LoggerIO(dir.resolve("log.txt").toString(), 5),
                    IniConfigTestHelper.emptyConfig(dir), "Test");
        }

        @AfterEach
        void cleanup() throws ReflectiveOperationException {
            resetLoggerSingleton();
        }

        @Test
        @DisplayName("addPending should queue message when Logger is not configured")
        void addPending_QueuesWhenLoggerNotConfigured(@TempDir Path dir) throws IOException, ReflectiveOperationException {
            resetLoggerSingleton();
            final var file = dir.resolve("test.ini");
            Files.writeString(file, "line1");
            final var rw = new ConcreteIO(file.toString());

            final var received = new ArrayList<String>();
            rw.addPending("queued", received::add);

            assertTrue(received.isEmpty(), "Consumer should not fire before Logger is configured");
        }

        @Test
        @DisplayName("logPending should flush all queued messages")
        void logPending_FlushesAllQueuedMessages(@TempDir Path dir) throws IOException {
            final var file = dir.resolve("test.ini");
            Files.writeString(file, "line1");
            final var rw = new ConcreteIO(file.toString());

            final var received = new ArrayList<String>();
            rw.addPending("msg1", received::add);
            rw.addPending("msg2", received::add);
            rw.addPending("msg3", received::add);

            rw.logPending();

            assertEquals(List.of("msg1", "msg2", "msg3"), received);
        }

        @Test
        @DisplayName("logPending should clear the queue so a second call is a no-op")
        void logPending_ClearsQueueAfterFlush(@TempDir Path dir) throws IOException {
            final var file = dir.resolve("test.ini");
            Files.writeString(file, "line1");
            final var rw = new ConcreteIO(file.toString());

            final var received = new ArrayList<String>();
            rw.addPending("once", received::add);

            rw.logPending();
            rw.logPending();

            assertEquals(1, received.size(), "Message should only be delivered once");
        }

        @Test
        @DisplayName("addPending should call consumer immediately when Logger is already configured")
        void addPending_CallsImmediatelyWhenLoggerConfigured(@TempDir Path dir) throws IOException {
            final var io = new LoggerIO(dir.resolve("log.txt").toString(), 5);
            Logger.configure(io, IniConfigTestHelper.emptyConfig(dir), "Test");

            final var testFile = dir.resolve("test.ini");
            Files.writeString(testFile, "line1");
            final var rw = new ConcreteIO(testFile.toString());

            final var received = new ArrayList<String>();
            rw.addPending("immediate", received::add);

            assertEquals(List.of("immediate"), received,
                    "Consumer should fire immediately when Logger is configured");
        }
    }


    @Nested
    @DisplayName("When creating parent directories")
    class EnsureParentDirectoryTests {

        @Test
        @DisplayName("constructor should create deeply nested parent directories")
        void constructor_CreatesNestedParentDirectories(@TempDir Path dir) {
            final var nested = dir.resolve("a").resolve("b").resolve("c").resolve("file.txt");

            assertDoesNotThrow(() -> new ConcreteIO(nested.toString()));
            assertTrue(Files.exists(nested.getParent()),
                    "Nested parent directories should have been created");
        }

        @Test
        @DisplayName("constructor should not throw when parent directory already exists")
        void constructor_DoesNotThrowWhenParentExists(@TempDir Path dir) throws IOException {
            final var file = dir.resolve("file.txt");
            Files.writeString(file, "existing");

            assertDoesNotThrow(() -> new ConcreteIO(file.toString()));
        }
    }


    @Nested
    @DisplayName("When resolving OS-specific path")
    class OsPathTests {

        private Field isDebugField;

        @BeforeEach
        void disableDebugMode() throws Exception {
            isDebugField = AppInfo.class.getDeclaredField("IS_DEBUG");
            isDebugField.setAccessible(true);
            isDebugField.setBoolean(null, false);
        }

        @AfterEach
        void restoreDebugMode() throws Exception {
            isDebugField.setBoolean(null, true);
        }

        @Test
        @DisplayName("should place file under APP_NAME subdirectory")
        void resolvePath_PlacesFileUnderAppNameDir() {
            final var io = new ConcreteIO("config.ini");
            assertTrue(io.getFilepath().toString().contains(AppInfo.APP_NAME));
        }

        @Test
        @DisplayName("should use filename as the final path component")
        void resolvePath_UsesFilenameAsLastComponent() {
            final var io = new ConcreteIO("config.ini");
            assertEquals("config.ini", io.getFilepath().getFileName().toString());
        }

        @Test
        @DisplayName("should preserve subdirectory in path")
        void resolvePath_PreservesSubdirectory() {
            final var io = new ConcreteIO("logs/app.log");
            assertTrue(io.getFilepath().toString().contains("logs"));
            assertEquals("app.log", io.getFilepath().getFileName().toString());
        }

        @Test
        @DisplayName("should use APPDATA root on Windows")
        void resolvePath_UsesAppDataOnWindows() {
            assumeTrue(OsType.TYPE == OsType.WINDOWS, "Only runs on Windows");
            final var io = new ConcreteIO("config.ini");
            assertTrue(io.getFilepath().toString().startsWith(System.getenv("APPDATA")));
        }

        @Test
        @DisplayName("should use Library/Application Support root on Mac")
        void resolvePath_UsesLibraryOnMac() {
            assumeTrue(OsType.TYPE == OsType.MAC, "Only runs on Mac");
            final var expected = System.getProperty("user.home") + "/Library/Application Support";
            assertTrue(new ConcreteIO("config.ini").getFilepath().toString().startsWith(expected));
        }
    }
}