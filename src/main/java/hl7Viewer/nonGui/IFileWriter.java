package hl7Viewer.nonGui;

import java.util.List;

public interface IFileWriter {
    boolean write(final List<String> items, boolean isAppended);
}
