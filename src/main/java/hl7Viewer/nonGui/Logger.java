package hl7Viewer.nonGui;

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
        ERROR(2),
        FATAL(3);

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

    private static volatile Logger loggerInstance;

    private final LoggerIO logIO;
    
    private final IniConfig config;
    
    private final BlockingQueue<String> logQueue = new LinkedBlockingQueue<>();


    public static void configure(final LoggerIO logIO, final IniConfig config) {
        if (loggerInstance == null) {

            synchronized (Logger.class) {
                if (loggerInstance == null) {
                    loggerInstance = new Logger(logIO, config);
                }
            }
        }
    }


    public static Logger getInstance() throws IllegalStateException {
        final var instance = loggerInstance;

        if (instance == null)
            throw new IllegalStateException("Logger not configured. Call Logger.configure() first.");

        return instance;
    }

    /** Logs a message at {@link LogLevel#TRACE} */
    public void logTrace(final String message) {
        log(message, LogLevel.TRACE);
    }

    /** Logs a message at {@link LogLevel#DEBUG} */
    public void logDebug(final String message) {
        log(message, LogLevel.DEBUG);
    }

    /** Logs a message at {@link LogLevel#ERROR} */
    public void logError(final String message) {
        log(message, LogLevel.ERROR);
    }

    /** Logs a message at {@link LogLevel#FATAL} */
    public void logFatal(final String message) {
        log(message, LogLevel.FATAL);
    }


    private static String caller() {
        return StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE)
                .walk(frames -> frames
                        .skip(3) // skip caller(), log(), and the public logX() method
                        .findFirst()
                        .map(f -> f.getDeclaringClass().getSimpleName() + "." + f.getMethodName() +"()")
                        .orElse("unknown"));
    }


    private void log(final String message, final LogLevel logLevel) {
        if (logLevel.level >= getConfigLogLevel().level) {
            final var timestamp = LocalDateTime.now().format(DATE_TIME_FORMAT);
            final var output = timestamp + " [" + logLevel + "] " + caller() + " - " + message;

            try {
                logQueue.put(output);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

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
                    Thread.currentThread().interrupt();
                }
            }
        }, "LogWriter").start();
    }

    private Logger(final LoggerIO logIO, final IniConfig config) {
        this.logIO = logIO;
        this.config = config;
        startLogWriter();
    }

    private LogLevel getConfigLogLevel() {
        return LogLevel.toLogLevel(config.get(ConfigKey.LOG_LEVEL, LogLevel.ERROR.level));
    }
}
