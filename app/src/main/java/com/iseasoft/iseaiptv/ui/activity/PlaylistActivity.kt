package com.iseasoft.iseaiptv.ui.activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.FragmentTransaction
import com.iseasoft.iseaiptv.R
import com.iseasoft.iseaiptv.dialogs.AddUrlDialog
import com.iseasoft.iseaiptv.helpers.Router
import com.iseasoft.iseaiptv.ui.fragment.PlaylistFragment

class PlaylistActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        initStartAppSdk()
        setContentView(R.layout.activity_playlist)
        super.onCreate(savedInstanceState)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        toolbar.setNavigationOnClickListener { v -> onBackPressed() }

        setupPlaylist()
        setupStartAppBanner()
    }

    private fun setupPlaylist() {
        val fm = supportFragmentManager
        val ft = fm.beginTransaction()

        val playlistFragment = PlaylistFragment.newInstance()
        ft.replace(R.id.playlist_container, playlistFragment)
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        ft.commit()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_playlist, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId


        if (id == R.id.action_add_url) {
            showAddUrlDialog()
            return true
        }

        if (id == R.id.action_select_file) {
            navigateToSelectFile()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    private fun showAddUrlDialog() {
        AddUrlDialog.newInstance(this).show(supportFragmentManager, AddUrlDialog.TAG)
    }

    private fun navigateToSelectFile() {
        Router.navigateTo(this, Router.Screens.SELECT_FILE)
    }

}
