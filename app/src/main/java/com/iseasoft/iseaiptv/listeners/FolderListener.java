package com.iseasoft.iseaiptv.listeners;

import java.io.File;

public interface FolderListener {
    void onFileSelected(File file);

    void onDirChanged(File dir);
}
