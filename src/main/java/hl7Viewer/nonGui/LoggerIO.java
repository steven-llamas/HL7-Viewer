package hl7Viewer.nonGui;

import hl7Viewer.AppInfo;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

public class LoggerIO extends AbstractFileReaderWriter {
    private static final String ARCHIVE_FOLDER = "Archive";

    private static final LocalDate CURRENT_LOCAL_DATE = LocalDate.now();

    private static final String FINAL_LOG_PATH = AppInfo.LOG_PATH + "_" + CURRENT_LOCAL_DATE + ".txt";

    private final Instant archiveCutoffDate;

    public LoggerIO() {
        super(FINAL_LOG_PATH);
        archiveCutoffDate = CURRENT_LOCAL_DATE.atStartOfDay(ZoneId.systemDefault()).toInstant();
        archiveFiles();
    }

    public LoggerIO(String filePath) {
        super(filePath);
        archiveCutoffDate = CURRENT_LOCAL_DATE.atStartOfDay(ZoneId.systemDefault()).toInstant();
        archiveFiles();
    }

    @Override
    protected void onReadLine(String line) {
        // do nothing
    }


    @Override
    protected void onError(final String message) {
        if(AppInfo.IS_DEBUG)
            System.err.println(message);
    }

//    @Override
//    protected String onWriteLine(String line) {
//    }

    private String getArchivePath() {
        final var parent = getFilepath().getParent();
        final var archiveDir = (parent != null)
                ? parent.resolve(ARCHIVE_FOLDER)
                : Path.of(ARCHIVE_FOLDER);

        return archiveDir.toString();
    }

    private void archiveFiles() {
        final var logsDir = getFilepath().getParent();
        if (logsDir == null || !Files.exists(logsDir))
            return;

        final var archiveFolder = Paths.get(getArchivePath());
        final var logFiles = logsDir.toFile().listFiles(File::isFile);
        if (logFiles == null)
            return;

        try {
            if (!Files.exists(archiveFolder))
                Files.createDirectories(archiveFolder);

            for (final var file : logFiles) {
                final var lastModified = Instant.ofEpochMilli(file.lastModified());
                if (!lastModified.isBefore(archiveCutoffDate))
                    continue;

                Files.move(file.toPath(),
                        archiveFolder.resolve(file.getName()),
                        StandardCopyOption.ATOMIC_MOVE);
            }
        } catch (IOException e) {
            onError("Error archiving files to: " + archiveFolder  + e.getMessage());
        }
    }

}
