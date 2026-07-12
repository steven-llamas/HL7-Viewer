package hl7Viewer.nonGui;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static hl7Viewer.nonGui.config.IniConfigTestHelper.configWithContent;
import static hl7Viewer.nonGui.config.IniConfigTestHelper.emptyConfig;
import static org.junit.jupiter.api.Assertions.*;

class LoggerTest {

    private static final int SLEEP_MS = 300;

    private static void resetSingleton() throws ReflectiveOperationException {
        final var field = Logger.class.getDeclaredField("loggerInstance");
        field.setAccessible(true);
        field.set(null, null);
    }

    /** LoggerIO that captures written lines in memory instead of disk. */
    private static class CapturingLoggerIO extends LoggerIO {
        final List<String> written = new CopyOnWriteArrayList<>();

        CapturingLoggerIO(Path dir) {
            super(dir.resolve("log.txt").toString(), 5);
        }

        @Override
        public boolean write(List<String> items, boolean isAppended) {
            written.addAll(items);
            return true;
        }
    }


    @BeforeEach
    void reset() throws ReflectiveOperationException {
        resetSingleton();
    }

    @AfterEach
    void cleanup() throws ReflectiveOperationException {
        resetSingleton();
    }


    @Nested
    @DisplayName("When starting the LogWriter thread")
    class LogWriterThreadTests {

        @Test
        @DisplayName("LogWriter should run on a virtual thread")
        void logWriter_IsVirtualThread(@TempDir Path dir) throws IOException {
            Logger.configure(new CapturingLoggerIO(dir), emptyConfig(dir), "Test");

            final var thread = Logger.getInstance().logWriterThread;

            assertNotNull(thread, "LogWriter thread should exist");
            assertTrue(thread.isVirtual(), "LogWriter should be a virtual thread");
        }
    }


    @Nested
    @DisplayName("When checking configuration state")
    class IsConfiguredTests {

        @Test
        @DisplayName("should return false before configure is called")
        void isConfigured_ReturnsFalseBeforeConfigure() {
            assertFalse(Logger.isConfigured());
        }

        @Test
        @DisplayName("should return true after configure is called")
        void isConfigured_ReturnsTrueAfterConfigure(@TempDir Path dir) throws IOException {
            Logger.configure(new CapturingLoggerIO(dir), emptyConfig(dir), "Test");

            assertTrue(Logger.isConfigured());
        }
    }


    @Nested
    @DisplayName("When configuring the singleton")
    class ConfigureTests {

        @Test
        @DisplayName("should return true on first call")
        void configure_ReturnsTrueFirstTime(@TempDir Path dir) throws IOException {
            assertTrue(Logger.configure(new CapturingLoggerIO(dir), emptyConfig(dir), "Test"));
        }

        @Test
        @DisplayName("should return false on subsequent calls")
        void configure_ReturnsFalseOnSubsequentCall(@TempDir Path dir) throws IOException {
            Logger.configure(new CapturingLoggerIO(dir), emptyConfig(dir), "Test");

            assertFalse(Logger.configure(new CapturingLoggerIO(dir), emptyConfig(dir), "Test"));
        }

        @Test
        @DisplayName("getInstance should throw before configure is called")
        void getInstance_ThrowsWhenNotConfigured() {
            assertThrows(IllegalStateException.class, Logger::getInstance);
        }

        @Test
        @DisplayName("getInstance should return the same instance on repeated calls")
        void getInstance_ReturnsSameInstance(@TempDir Path dir) throws IOException {
            Logger.configure(new CapturingLoggerIO(dir), emptyConfig(dir), "Test");

            assertSame(Logger.getInstance(), Logger.getInstance());
        }
    }


    @Nested
    @DisplayName("When filtering log levels")
    class LogLevelFilteringTests {

        @Test
        @DisplayName("should not write messages below configured level")
        void log_FiltersMessagesBelowConfiguredLevel(@TempDir Path dir) throws IOException, InterruptedException {
            final var io = new CapturingLoggerIO(dir);
            Logger.configure(io, configWithContent(dir, "[Logging]\nlog_level=3"), "Test");

            Logger.getInstance().logTrace("trace");
            Logger.getInstance().logDebug("debug");
            Logger.getInstance().logInfo("info");

            Thread.sleep(SLEEP_MS);

            assertTrue(io.written.isEmpty(), "No messages below ERROR should be written");
        }

        @Test
        @DisplayName("should write messages at or above configured level")
        void log_WritesMessagesAtOrAboveConfiguredLevel(@TempDir Path dir) throws IOException, InterruptedException {
            final var io = new CapturingLoggerIO(dir);
            Logger.configure(io, configWithContent(dir, "[Logging]\nlog_level=3"), "Test");

            Logger.getInstance().logError("error");
            Logger.getInstance().logFatal("fatal");

            Thread.sleep(SLEEP_MS);

            assertEquals(2, io.written.size());
        }

        @Test
        @DisplayName("log output should contain the level label")
        void log_OutputContainsLevelLabel(@TempDir Path dir) throws IOException, InterruptedException {
            final var io = new CapturingLoggerIO(dir);
            Logger.configure(io, configWithContent(dir, "[Logging]\nlog_level=0"), "Test");

            Logger.getInstance().logInfo("hello");

            Thread.sleep(SLEEP_MS);

            assertTrue(io.written.stream().anyMatch(l -> l.contains("|INFO|")));
        }

        @Test
        @DisplayName("log output should contain the app name")
        void log_OutputContainsAppName(@TempDir Path dir) throws IOException, InterruptedException {
            final var io = new CapturingLoggerIO(dir);
            Logger.configure(io, configWithContent(dir, "[Logging]\nlog_level=0"), "MyApp");

            Logger.getInstance().logInfo("hello");

            Thread.sleep(SLEEP_MS);

            assertTrue(io.written.stream().anyMatch(l -> l.contains("MyApp")));
        }

        @Test
        @DisplayName("log output should contain the calling method name")
        void log_OutputContainsCallerMethodName(@TempDir Path dir) throws IOException, InterruptedException {
            final var io = new CapturingLoggerIO(dir);
            Logger.configure(io, configWithContent(dir, "[Logging]\nlog_level=0"), "Test");

            Logger.getInstance().logInfo("check caller");

            Thread.sleep(SLEEP_MS);

            assertTrue(io.written.stream().anyMatch(l -> l.contains("log_OutputContainsCallerMethodName()")));
        }
    }


    @Nested
    @DisplayName("LogLevel enum")
    class LogLevelEnumTests {

        @Test
        @DisplayName("levels should be ordered ascending by numeric value")
        void logLevel_AscendingNumericOrder() {
            final var levels = Logger.LogLevel.values();
            for (int i = 0; i < levels.length - 1; i++) {
                assertTrue(levels[i].level < levels[i + 1].level,
                        levels[i] + " should be less than " + levels[i + 1]);
            }
        }

        @Test
        @DisplayName("toLogLevel should return matching level for each value")
        void toLogLevel_ReturnsMatchingLevel() {
            assertEquals(Logger.LogLevel.TRACE, Logger.LogLevel.toLogLevel(0));
            assertEquals(Logger.LogLevel.DEBUG, Logger.LogLevel.toLogLevel(1));
            assertEquals(Logger.LogLevel.INFO,  Logger.LogLevel.toLogLevel(2));
            assertEquals(Logger.LogLevel.ERROR, Logger.LogLevel.toLogLevel(3));
            assertEquals(Logger.LogLevel.FATAL, Logger.LogLevel.toLogLevel(4));
        }

        @Test
        @DisplayName("toLogLevel should default to ERROR for unrecognized values")
        void toLogLevel_DefaultsToErrorForUnknownValue() {
            assertEquals(Logger.LogLevel.ERROR, Logger.LogLevel.toLogLevel(99));
            assertEquals(Logger.LogLevel.ERROR, Logger.LogLevel.toLogLevel(-1));
        }
    }
}