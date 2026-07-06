package hl7Viewer.nonGui;


import hl7Viewer.AppInfo;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;

/**
 * Base class for file-backed readers and writers.
 * Handles file I/O and delegates per-line processing to subclasses via
 * {@link #onReadLine} and {@link #onWriteLine}.
 */
public abstract class AbstractFileReaderWriter implements IFileReader, IFileWriter {
    private Path filepath;

    private boolean isPathExists;

    private boolean isFilePathChanged;


    /**
     * Constructs a new {@link  AbstractFileReaderWriter} and sets
     * its filepath for later methods to use
     *
     * @param filePath filepath of where the file is located
     */
    public AbstractFileReaderWriter(final String filePath) {
        this.filepath = Paths.get(filePath);
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
        if (!pathExists())  {
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
            onError("Error reading file: " + e.getMessage());
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
            onError("Error writing file: " + e.getMessage());
            return success;
        }

        return success;
    }


//    protected Instant getFileLastModifiedDate() {
//        try {
//            return Files.getLastModifiedTime(filepath).toInstant();
//        } catch (java.nio.file.NoSuchFileException e) {
//            onError("File does not exist. No reason to check modified date");
//            return null;
//        } catch (IOException e) {
//            onError("Error grabbing file's Last Modified Date: " + e.getMessage());
//            return null;
//        }
//    }


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

        try {
            Logger.getInstance().logError(message);
        } catch (IllegalStateException e) {
            if (AppInfo.IS_DEBUG)
                System.err.println(message);
        }
    }


    /** Called for each item just before it is written during {@link #write}.
     *  Child classes can override to add functionality if needed
     * @param line the string item from the list
     * @return String base method returns the string
     */
    protected String onWriteLine(final String line) {
        return line;
    }


    /** Called for each non-empty trimmed line during {@link #read}. */
    protected abstract void onReadLine(final String line);

    protected void setFilePathChanged() {
        this.isFilePathChanged = true;
    }


    private void createPath() {
        try {
            Files.createFile(filepath);
            if (AppInfo.IS_DEBUG)
                System.out.println("File: " + filepath + "Created");
        } catch (IOException e) {
            onError("An Error occurred when creating: " + filepath  + e.getMessage());
        }
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
            onError("Error creating directory '" + parent + "': " + e.getMessage());
        }
    }
}
