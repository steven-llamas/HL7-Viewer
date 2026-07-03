package hl7Viewer.nonGui;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Base class for file-backed readers and writers.
 * Handles file I/O and delegates per-line processing to subclasses via
 * {@link #onReadLine} and {@link #onWriteLine}.
 */
public abstract class AbstractFileReaderWriter implements IFileReader, IFileWriter {
    private String filePath;


    /**
     * Constructs a new {@link  AbstractFileReaderWriter} and sets
     * its filepath for later methods to use
     *
     * @param filePath filepath of where the file is located
     */
    public AbstractFileReaderWriter(final String filePath) {
        this.filePath = filePath;
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

        try (final var bufferedReader =
                     new BufferedReader(new java.io.FileReader(filePath))
        ) {
            String fileLine = null;
            while ((fileLine = bufferedReader.readLine()) != null) {
                fileLine = fileLine.trim();

                if (fileLine.isEmpty())
                    continue;

                onReadLine(fileLine);
            }

            success = true;
        } catch (IOException e) {
            System.out.println("Error reading file." + e.getMessage());
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

        try (final var bufferedWriter
                     = new BufferedWriter(new FileWriter(filePath, isAppended))
        ) {
            if (isAppended)
                bufferedWriter.newLine();

            for (final var item : items) {
                final String writeLine = onWriteLine(item);
                bufferedWriter.write(writeLine);
                bufferedWriter.newLine();
                System.out.println("Wrote:\t" + writeLine + " to file ");
            }

            success = true;
        } catch (IOException e) {
            System.out.println("Error writing file." + e.getMessage());
            return success;
        }

        return success;
    }

    /** Called for each non-empty trimmed line during {@link #read}. */
    protected abstract void onReadLine(final String line);

    /** Called for each item just before it is written during {@link #write}.
     *  Child classes can override to add functionality if needed
     * @param line the string item from the list
     * @return String base method returns the string
     */
    protected String onWriteLine(final String line) {
        return line;
    }

//    protected String getFileName() {
//        return fileName;
//    }
//
//    protected void setFileName(String fileName) {
//        this.fileName = fileName;
//    }
}
