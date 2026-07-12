package hl7Viewer.nonGui;

import hl7Viewer.AppInfo;
import hl7Viewer.nonGui.config.ConfigKey;
import hl7Viewer.nonGui.config.IniConfig;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Logger {
    public enum LogLevel {
        TRACE(0),
        DEBUG(1),
        INFO(2),
        ERROR(3),
        FATAL(4);

        public final int level;


        LogLevel(int level) {
            this.level = level;
        }

        /**
         * Returns the {@link LogLevel} matching the given int value.
         * Defaults to {@link #ERROR} if no match is found.
         *
         * @param value numeric log level read from config
         * @return corresponding {@link LogLevel}, or {@link #ERROR} if unrecognized
         */
        public static LogLevel toLogLevel(final int value) {
            for (final var l : values()) {
                if (l.level == value)
                    return l;
            }
            return ERROR;
        }
    }


    private static DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static int LOG_DEFAULT = (!AppInfo.IS_DEBUG) ? LogLevel.ERROR.level : LogLevel.TRACE.level;

    private static volatile Logger loggerInstance;

    private final LoggerIO logIO;
    
    private IniConfig config;

    private final String appName;

    private final BlockingQueue<String> logQueue = new LinkedBlockingQueue<>();


    /**
     * Initializes the singleton logger. Must be called once before {@link #getInstance()}.
     * Subsequent calls are ignored, as the instance is only created once.
     *
     * @param logIO   the file writer the logger will append to
     * @param config  app config used to read the active {@link LogLevel}
     * @return true if {@link #loggerInstance} was set, false otherwise
     */
    public static boolean configure(final LoggerIO logIO, final IniConfig config, final String appName) {
        if (loggerInstance == null) {

            synchronized (Logger.class) {
                if (loggerInstance == null) {
                    loggerInstance = new Logger(logIO, config, appName);
                    return true;
                }

            }
        }
        return false;
    }


    /**
     * Returns the singleton logger instance.
     *
     * @return the configured {@link Logger}
     * @throws IllegalStateException if {@link #configure} has not been called yet
     */
    public static Logger getInstance() throws IllegalStateException {
        final var instance = loggerInstance;

        if (instance == null)
            throw new IllegalStateException("Logger not configured. Call Logger.configure() first.");

        return instance;
    }


    /** Returns {@code true} if {@link #configure} has been called. */
    public static boolean isConfigured() {
        return loggerInstance != null;
    }


    /**
     * Logs at the given level only if the logger is configured.
     *
     * @param level   the log level
     * @param message the text to log
     */
//    public static void log(final LogLevel level, final String message) {
//        final var instance = loggerInstance;
//        if (instance != null)
//            instance.log(message, level);
//    }


    /**
     * Logs a message at {@link LogLevel#INFO}.
     *
     * @param message the text to log
     */
    public void logInfo(final String message) {
        log(message, LogLevel.INFO);
    }


    /**
     * Logs a message at {@link LogLevel#TRACE}.
     *
     * @param message the text to log
     */
    public void logTrace(final String message) {
        log(message, LogLevel.TRACE);
    }


    /**
     * Logs a message at {@link LogLevel#DEBUG}.
     *
     * @param message the text to log
     */
    public void logDebug(final String message) {
        log(message, LogLevel.DEBUG);
    }


    /**
     * Logs a message at {@link LogLevel#ERROR}.
     *
     * @param message the text to log
     */
    public void logError(final String message) {
        log(message, LogLevel.ERROR);
    }


    /**
     * Logs a message at {@link LogLevel#FATAL}.
     *
     * @param message the text to log
     */
    public void logFatal(final String message) {
        log(message, LogLevel.FATAL);
    }


    /** Returns the calling class and method name using {@link StackWalker}. */
    private static String getCaller() {
        return StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE)
                .walk(frames -> frames
                        .skip(3) // skip caller(), log(), and the public logX() method
                        .findFirst()
                        .map(f -> f.getDeclaringClass().getSimpleName() + "." + f.getMethodName() +"()")
                        .orElse("unknown"));
    }


    private void log(final String message, final LogLevel logLevel) {
        if (logLevel.level >= getConfigLogLevel()) {
            final var dateTimeStamp = LocalDateTime.now().format(DATE_TIME_FORMAT);
            final var output = dateTimeStamp + "|" + logLevel + "|" + appName + "| " + getCaller() + ": " + message;

            try {
                logQueue.put(output);
            } catch (InterruptedException e) {
                if(AppInfo.IS_DEBUG)
                    System.out.println("Exception when logging, message: " + e.getMessage());
                Thread.currentThread().interrupt();
            }
        }
    }


    /**
     * Starts the background {@code LogWriter} thread.
     * Blocks on the queue until at least one
     * message is available, then drains the rest and writes the batch in one file append.
     */
    private void startLogWriter() {
        new Thread(() -> {
            final List<String> batch = new ArrayList<>();
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    batch.add(logQueue.take());
                    logQueue.drainTo(batch);
                    logIO.write(batch, true);
                    batch.clear();
                } catch (InterruptedException e) {
                    if(AppInfo.IS_DEBUG)
                        System.out.println("Exception in startLogWriter(), message: " + e.getMessage());
                    Thread.currentThread().interrupt();
                }
            }
        }, "LogWriter").start();
    }


    private Logger(final LoggerIO logIO, final IniConfig config, final String appName) {
        this.logIO = logIO;
        this.config = config;
        this.appName = appName;
        startLogWriter();
    }


    private int getConfigLogLevel() {
        return config.get(ConfigKey.LOG_LEVEL, LOG_DEFAULT);
    }
}
