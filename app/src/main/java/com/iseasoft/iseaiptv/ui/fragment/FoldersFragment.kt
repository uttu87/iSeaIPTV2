package com.iseasoft.iseaiptv.ui.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.os.AsyncTask
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.iseasoft.iseaiptv.R
import com.iseasoft.iseaiptv.adapters.FolderAdapter
import com.iseasoft.iseaiptv.dialogs.StorageSelectDialog
import com.iseasoft.iseaiptv.listeners.FolderListener
import com.iseasoft.iseaiptv.permissions.IseaSoft
import com.iseasoft.iseaiptv.permissions.PermissionCallback
import com.iseasoft.iseaiptv.utils.PreferencesUtility
import com.iseasoft.iseaiptv.utils.Utils
import com.iseasoft.iseaiptv.widgets.DividerItemDecoration
import java.io.File

/**
 * Created by nv95 on 10.11.16.
 */

class FoldersFragment : Fragment(), StorageSelectDialog.OnDirSelectListener {

    private val permissionReadstorageCallback = object : PermissionCallback {
        override fun permissionGranted() {
            loadFolders()
        }

        override fun permissionRefused() {
            requestStoragePermission()
        }
    }
    private var panelLayout: RelativeLayout? = null
    private var mAdapter: FolderAdapter? = null
    private var recyclerView: RecyclerView? = null
    private var mProgressBar: ProgressBar? = null

    var listener: FolderListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(
                R.layout.fragment_folders, container, false)

        panelLayout = rootView.findViewById<View>(R.id.panel_layout) as RelativeLayout
        recyclerView = rootView.findViewById<View>(R.id.recyclerview) as RecyclerView
        mProgressBar = rootView.findViewById<View>(R.id.progressBar) as ProgressBar

        recyclerView!!.layoutManager = LinearLayoutManager(activity)
        return rootView
    }

    private fun loadFolders() {
        if (activity != null) {
            LoadFolder().execute("")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (Utils.isMarshmallow) {
            requestStoragePermission()
        } else {
            loadFolders()
        }
    }

    private fun setItemDecoration() {
        recyclerView!!.addItemDecoration(DividerItemDecoration(activity!!, DividerItemDecoration.VERTICAL_LIST))
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    fun updateTheme() {
        val context = activity
        if (context != null) {
            val dark = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("dark_theme", false)
            mAdapter!!.applyTheme(dark)
        }
    }

    override fun onDirSelected(dir: File) {
        mAdapter!!.updateDataSetAsync(dir)
    }

    @SuppressLint("StringFormatInvalid")
    private fun requestStoragePermission() {
        if (activity == null) {
            return
        }
        if (IseaSoft.checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE) && IseaSoft.checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            loadFolders()
        } else {
            if (IseaSoft.shouldShowRequestPermissionRationale(activity!!, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Snackbar.make(panelLayout!!, getString(R.string.request_storage_permission_message, getString(R.string.app_name)),
                        Snackbar.LENGTH_INDEFINITE)
                        .setAction("OK") { IseaSoft.askForPermission(activity!!, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE), permissionReadstorageCallback) }.show()
            } else {
                IseaSoft.askForPermission(activity!!, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE), permissionReadstorageCallback)
            }
        }
    }

    fun onBackPressed(): Boolean {
        return if (mAdapter == null) {
            false
        } else mAdapter!!.goUpAsync()
    }

    @SuppressLint("StaticFieldLeak")
    private inner class LoadFolder : AsyncTask<String, Void, String>() {

        override fun doInBackground(vararg params: String): String {
            val activity = activity
            if (activity != null) {
                mAdapter = FolderAdapter(activity, File(PreferencesUtility.getInstance(activity).lastFolder))
                mAdapter!!.folderListener = listener
                updateTheme()
            }
            return "Executed"
        }

        override fun onPostExecute(result: String) {
            recyclerView!!.adapter = mAdapter
            mAdapter!!.notifyDataSetChanged()
            mProgressBar!!.visibility = View.GONE
        }

        override fun onPreExecute() {}
    }

    companion object {
        val TAG = FoldersFragment::class.java.simpleName

        fun newInstance(): FoldersFragment {
            val fragment = FoldersFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }
}
