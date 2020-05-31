package com.iseasoft.iseaiptv.ui.activity

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.FragmentTransaction
import com.iseasoft.iseaiptv.R
import com.iseasoft.iseaiptv.listeners.FolderListener
import com.iseasoft.iseaiptv.parsers.M3UParser
import com.iseasoft.iseaiptv.permissions.IseaSoft
import com.iseasoft.iseaiptv.ui.fragment.FoldersFragment
import com.iseasoft.iseaiptv.utils.PreferencesUtility
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.InputStream

class SelectFileActivity : BaseActivity(), FolderListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        initStartAppSdk()
        setContentView(R.layout.activity_select_file)
        super.onCreate(savedInstanceState)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        updateTitle(PreferencesUtility.getInstance(this).lastFolder)

        toolbar.setNavigationOnClickListener { v -> onBackPressed() }

        setupSelectFileView()
        setupStartAppBanner()
    }

    private fun updateTitle(title: String?) {
        supportActionBar!!.title = title
    }

    private fun setupSelectFileView() {
        val fm = supportFragmentManager
        val ft = fm.beginTransaction()

        val foldersFragment = FoldersFragment.newInstance()
        foldersFragment.listener = this
        ft.replace(R.id.select_file_container, foldersFragment, FoldersFragment.TAG)
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        ft.commit()
    }

    override fun onBackPressed() {
        val fragment = supportFragmentManager.findFragmentByTag(FoldersFragment.TAG) as FoldersFragment?
        if (fragment != null) {
            if (fragment.onBackPressed()) {
                return
            }
        }
        super.onBackPressed()
    }

    override fun onFileSelected(file: File) {
        try {
            val inputStream = FileInputStream(file)
            parseAndUpdateUI(inputStream)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }

    }

    override fun onDirChanged(dir: File) {
        updateTitle(dir.path)
    }

    private fun parseAndUpdateUI(inputStream: InputStream) {

        val m3UParser = M3UParser()
        try {
            val playlist = m3UParser.parseFile(inputStream)
            Handler(Looper.getMainLooper()).post {
                //                if (playlistAdapter == null) {
                //                    playlistAdapter = new ChannelAdapter(getActivity());
                //                }
                //                playlistAdapter.update(playlist.getPlaylistItems());
                //                recyclerView.setAdapter(playlistAdapter);
                //                int columnWidthInDp = COLUMN_WIDTH;
                //                int spanCount = Utils.getOptimalSpanCount(recyclerView, columnWidthInDp);
                //                Utils.modifyRecylerViewForGridView(recyclerView, spanCount, columnWidthInDp);
                //                mProgressBar.setVisibility(View.GONE);
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }

    }

    override fun onRequestPermissionsResult(
            requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        IseaSoft.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

}
