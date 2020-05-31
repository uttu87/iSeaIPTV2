package com.iseasoft.iseaiptv.listeners

import java.io.File

interface FolderListener {
    fun onFileSelected(file: File)

    fun onDirChanged(dir: File)
}
