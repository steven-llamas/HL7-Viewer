package hl7Viewer.nonGui;


import hl7Viewer.AppInfo;
import hl7Viewer.OsType;
import hl7Viewer.utils.Pair;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Base class for file-backed readers and writers.
 * Handles file I/O and delegates per-line processing to subclasses via
 * {@link #onReadLine} and {@link #onWriteLine}.
 */
public abstract class AbstractFileIO implements IFileReader, IFileWriter {
    private static final String BASE_PATH = File.separator + AppInfo.APP_NAME + File.separator;

    private static final String WINDOWS_APPDATA = System.getenv("APPDATA");

    private static final String MAC_APP_SUPPORT = System.getProperty("user.home") + "/Library/Application Support";


    private final Path filepath;

    private boolean isPathExists;

    private boolean isFilePathChanged;

    private final List<Pair<String, Consumer<String>>> pendingLogs = new ArrayList<>();


    /**
     * Constructs a new {@link AbstractFileIO}.
     * Resolves {@code filePath} to an absolute path via {@code resolvePath},
     * checks whether the file already exists, and calls {@link #ensureParentDirectory()}
     * to create any missing parent directories.
     *
     * @param filePath filepath of where the file is located
     * @throws NullPointerException if {@code filePath} is null
     */
    protected AbstractFileIO(final String filePath) throws NullPointerException {
        java.util.Objects.requireNonNull(filePath, getClass().getName() + " filePath cannot be null");

        this.filepath = resolvePath(filePath);
        this.isPathExists = Files.exists(filepath);
        this.isFilePathChanged = false;
        ensureParentDirectory();
    }

    /**
     * Reads the file line by line. Empty and blank lines are skipped.
     * Each non-empty trimmed line is passed to {@link #onReadLine}.
     *
     * @return {@code true} on success, {@code false} if the file cannot be read.
     */
    @Override
    public boolean read() {
        var success = false;

        if (!pathExists()) {
            createPath();
            return success;
        }

        try (final var bufferedReader = Files.newBufferedReader(filepath)) {
            String fileLine = null;
            while ((fileLine = bufferedReader.readLine()) != null) {
                fileLine = fileLine.trim();

                if (fileLine.isEmpty())
                    continue;

                onReadLine(fileLine);
            }

            success = true;
        } catch (IOException e) {
            onError("Error reading file to: '" + filepath + "' message: " + e.getMessage());
            return success;
        }

        return success;
    }


    /**
     * Writes {@code items} to the file. Each item is passed through {@link #onWriteLine}
     * before being written, allowing subclasses to transform it.
     *
     * @param items      lines to write.
     * @param isAppended {@code true} to append to an existing file, {@code false} to overwrite.
     * @return {@code true} on success, {@code false} if the file cannot be written.
     */
    @Override
    public boolean write(List<String> items, final boolean isAppended) {
        var success = false;

        try (final BufferedWriter bufferedWriter = isAppended
                ? Files.newBufferedWriter(filepath, StandardOpenOption.CREATE, StandardOpenOption.APPEND)
                : Files.newBufferedWriter(filepath)) {
            if (isAppended)
                bufferedWriter.newLine();

            for (final var item : items) {
                final String writeLine = onWriteLine(item);
                bufferedWriter.write(writeLine);
                bufferedWriter.newLine();
            }

            success = true;
        } catch (IOException e) {
            onError("Error writing file to: '" + filepath + "' message: " + e.getMessage());
            return success;
        }

        return success;
    }


    /**
     * Flushes all pending log messages through their associated log methods and clears the queue.
     */
    public void logPending() {
        String msg = "";
        for (final var entry : pendingLogs) {
            msg = entry.first();
            entry.second().accept(msg);
        }
        pendingLogs.clear();
    }


    protected Path getFilepath() {
        return filepath;
    }


    protected boolean pathExists() {
        if (!isFilePathChanged)
            return isPathExists;

        isFilePathChanged = false;
        return isPathExists = Files.exists(filepath);
    }


    /**
     *
     * Called when a read or write operation fails. Defaults to logging via {@link Logger}.
     * Subclasses that cannot safely call {@link Logger} should override this
     *
     * @param message error description
     */
    protected void onError(final String message) {
        if (Logger.isConfigured())
            Logger.getInstance().logError(message);
        else if (AppInfo.IS_DEBUG)
            System.err.println(message);
    }


    /**
     * Queues a message to be logged once {@link Logger} is configured.
     * Use when logging is needed before {@link Logger#configure} has been called.
     *
     * @param msg      the message to log
     * @param consumer the log method to invoke (e.g. {@code Logger.getInstance()::logTrace})
     */
    protected void addPending(final String msg, final Consumer<String> consumer) {
        if (Logger.isConfigured())
            consumer.accept(msg);
        else
            pendingLogs.add(new Pair<>(msg, consumer));
    }


    /**
     * Called for each item just before it is written during {@link #write}.
     * Child classes can override to add functionality if needed
     *
     * @param line the string item from the list
     * @return String base method returns the string
     */
    protected String onWriteLine(final String line) {
        return line;
    }


    /**
     * Called for each non-empty trimmed line during {@link #read}.
     */
    protected abstract void onReadLine(final String line);


    protected void setFilePathChanged() {
        this.isFilePathChanged = true;
    }


    /**
     * Resolves {@code filename} to its absolute path. In debug mode returns the path as-is;
     * otherwise maps it under the OS-specific user data directory ({@code %APPDATA%} on Windows,
     * {@code ~/Library/Application Support} on Mac, or next to the JAR on other platforms).
     *
     * @param filename relative filename, optionally including a subdirectory (e.g. {@code logs/app.log})
     * @return the resolved absolute {@link Path}
     */
    private Path resolvePath(final String filename) {
        final var path = Paths.get(filename);
        final Path resolved;

        if (AppInfo.IS_DEBUG) {
            resolved = path;
        } else {
            final var parent = path.getParent();
            final var basePath = (parent != null)
                    ? BASE_PATH + parent + File.separator
                    : BASE_PATH;

            File dir;
            switch (OsType.TYPE) {
                case WINDOWS -> dir = new File(WINDOWS_APPDATA + basePath);
                case MAC     -> dir = new File(MAC_APP_SUPPORT + basePath);
                default      -> {
                    try {
                        dir = new File(AppInfo.class.getProtectionDomain()
                                .getCodeSource().getLocation().toURI()).getParentFile();
                        dir = new File(dir + basePath);
                    } catch (URISyntaxException _) {
                        return path;
                    }
                }
            }
            resolved = dir.toPath().resolve(path.getFileName());
        }
        addPending("Final path: '" + resolved + "'", m -> Logger.getInstance().logDebug(m));
        return resolved;
    }


    private void createPath() {
        try {
            Files.createFile(filepath);
        } catch (IOException e) {
            final var msg = "An Error occurred when creating: '" + filepath + "' message: " + e.getMessage();
            onError(msg);
        }

        addPending("File: '" + filepath + "' Created", m -> Logger.getInstance().logInfo(m));
    }

    /**
     * Creates the parent directory of {@link #filepath} if it does not already exist.
     */
    private void ensureParentDirectory() {
        final var parent = filepath.getParent();
        if (parent == null)
            return;

        try {
            Files.createDirectories(parent);
        } catch (IOException e) {
            final var msg = "Error occurred when creating directory: '" + parent + "' message: " + e.getMessage();
            onError(msg);
        }
    }
}
