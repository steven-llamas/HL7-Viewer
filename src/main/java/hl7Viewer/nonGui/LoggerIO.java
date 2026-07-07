package hl7Viewer.nonGui;

import hl7Viewer.AppInfo;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.atomic.AtomicInteger;

public class LoggerIO extends AbstractFileIO {
    private static final String ARCHIVE_FOLDER = "Archive";

    private static final LocalDate CURRENT_LOCAL_DATE = LocalDate.now();

    private static final String FILE_EXTENSION = ".txt";

    private static final String DEFAULT_LOG_PATH = "logs" + File.separator + "log" + FILE_EXTENSION;


    private final Instant archiveCutoffDate;

    private final int maxFileCount;

    public LoggerIO() {
        super(DEFAULT_LOG_PATH);
        archiveCutoffDate = CURRENT_LOCAL_DATE.atStartOfDay(ZoneId.systemDefault()).toInstant();
        this.maxFileCount = 31;
        archiveFiles();
    }

    public LoggerIO(final String fileName, final int maxFileCount) {
        super(fileName);
        this.maxFileCount = maxFileCount;
        archiveCutoffDate = CURRENT_LOCAL_DATE.atStartOfDay(ZoneId.systemDefault()).toInstant();
        archiveFiles();
    }

    @Override
    protected void onReadLine(String line) {
        // do nothing
    }

    @Override
    protected void onError(final String message) {
        addPending(message, m -> Logger.getInstance().logError(m));
        if (AppInfo.IS_DEBUG)
            System.err.println(message);
    }

//    @Override
//    protected String onWriteLine(String line) {
//    }

    private void archiveFiles() {
        final var logsDir = getFilepath().getParent();

        if (!Files.exists(logsDir))
            return;

        final var archiveFolder = logsDir.resolve(ARCHIVE_FOLDER);
        final var logFiles = logsDir.toFile().listFiles(File::isFile);

        if (logFiles == null || logFiles.length == 0)
            return;

        final boolean hasFilesToArchive = Arrays.stream(logFiles)
                .anyMatch(file -> Instant.ofEpochMilli(file.lastModified()).isBefore(archiveCutoffDate));

        if (!hasFilesToArchive)
            return;

        addPending("Archive started in dir: " + logsDir, m -> Logger.getInstance().logDebug(m));

        boolean isArchiveDirNew = false;
        try {
            if (!Files.exists(archiveFolder)) {
                isArchiveDirNew = true;
                Files.createDirectories(archiveFolder);
                addPending("Archive directory created: " +
                        archiveFolder, m -> Logger.getInstance().logDebug(m));
            }

            if (!isArchiveDirNew)
                reduceArchiveFolder(archiveFolder);

            final var timeZone = ZoneId.systemDefault();
            var movedCount = 0;

            for (final var file : logFiles) {
                final var lastModified = Instant.ofEpochMilli(file.lastModified());

                if (!lastModified.isBefore(archiveCutoffDate))
                    continue;

                final var modifiedDateStamp = Files.getLastModifiedTime(file.toPath())
                        .toInstant()
                        .atZone(timeZone)
                        .toLocalDate()
                        .format(DateTimeFormatter.ofPattern("yyyy_MM_dd"));

                final var newFileName =
                        file.getName().replace(FILE_EXTENSION, "_" + modifiedDateStamp + FILE_EXTENSION);

                Files.move(file.toPath(),
                        archiveFolder.resolve(newFileName),
                        StandardCopyOption.ATOMIC_MOVE);

                addPending("Moved: " + file.getName() + " -> " +
                        newFileName, m -> Logger.getInstance().logTrace(m));
                movedCount++;
            }

            if (movedCount > 0) {
                addPending("Archive complete, files moved: " +
                        movedCount, m -> Logger.getInstance().logInfo(m));
            }

        } catch (IOException e) {
            onError("Error archiving files to: " + archiveFolder + " message: " + e.getMessage());
        }
    }

    private void reduceArchiveFolder(final Path archiveFolder) {
        var archiveFiles = archiveFolder.toFile().listFiles(File::isFile);
        var count = new AtomicInteger();

        if (archiveFiles != null && archiveFiles.length <= maxFileCount)
            return;

        final var msg = "Reducing archive, current count: " +
                (archiveFiles != null ? archiveFiles.length : 0) + ", max: " + maxFileCount;
        addPending(msg, m -> Logger.getInstance().logDebug(m));

        while (archiveFiles != null && archiveFiles.length > maxFileCount) {
            Arrays.stream(archiveFiles)
                    .min(Comparator.comparing(File::getName))
                    .ifPresent(file -> {
                        final var filename = file.getName();

                        if (file.delete()) {
                            count.incrementAndGet();
                            addPending("Deleted: " + filename, m -> Logger.getInstance().logTrace(m));

                            if (AppInfo.IS_DEBUG)
                                System.out.println("Deleted: " + filename);
                        } else {
                            final var delMsg = "Failed to delete: " + filename;
                            addPending(delMsg, m -> Logger.getInstance().logError(m));
                        }
                    });

            archiveFiles = archiveFolder.toFile().listFiles(File::isFile);
        }

        addPending("Reduction complete, deleted: " + count.get(), m -> Logger.getInstance().logInfo(m));
    }
}