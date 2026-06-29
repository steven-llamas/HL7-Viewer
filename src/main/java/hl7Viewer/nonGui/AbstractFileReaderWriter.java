package hl7Viewer.nonGui;

import hl7Viewer.nonGui.config.IniItems;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public abstract class AbstractFileReaderWriter implements IFileReader, IFileWriter{
    private String fileName;
    private String line;

    public AbstractFileReaderWriter(final String fileName) {
        this.fileName = fileName;
    }

    @Override
    public boolean read() {
        var success = false;

        try (final var bufferedReader =
                     new BufferedReader(new java.io.FileReader(fileName))
        ) {
            String line = null;
            while ((line = bufferedReader.readLine()) != null)
                onReadLine(line);

            success = true;
        } catch (IOException e) {
            System.out.println("Error reading file." + e.getMessage());
            return success;
        }

        return success;
    }

    @Override
    public boolean write(List<String> items, final boolean isAppended) {
        var success = false;

        try (final var bufferedWriter
                     = new BufferedWriter(new FileWriter(fileName, isAppended))
        ) {
            if (isAppended)
                bufferedWriter.newLine();

            for (final var item : items) {
                line = item;
                onWriteLine();
                bufferedWriter.write(line);
                System.out.println("Wrote:\t" + line + " to file ");
            }

            success = true;
        } catch (IOException e) {
            System.out.println("Error writing file." + e.getMessage());
            return success;
        }

        return success;
    }

    protected abstract void onReadLine(final String line);

    protected abstract void onWriteLine();

//    protected String getFileName() {
//        return fileName;
//    }
//
//    protected void setFileName(String fileName) {
//        this.fileName = fileName;
//    }

    protected String getLine() {
        return line;
    }

    protected void setLine(String line) {
        this.line = line;
    }
}
