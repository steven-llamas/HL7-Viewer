package hl7Viewer.nonGui;

import hl7Viewer.AppInfo;

public class LoggerIO extends AbstractFileReaderWriter {


    public LoggerIO() {
        super(AppInfo.LOG_PATH);
    }

    public LoggerIO(String filePath) {
        super(filePath);
    }

    @Override
    protected void onReadLine(String line) {

    }


    @Override
    protected void onError(final String message) {
        System.err.println(message);
    }

//    @Override
//    protected String onWriteLine(String line) {
//    }

    // TODO add implementation for moving old log files to archive folder
}
