package com.iseasoft.iseaiptv.ui.activity

import android.os.Bundle
import android.text.TextUtils
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.FragmentTransaction
import com.iseasoft.iseaiptv.R
import com.iseasoft.iseaiptv.ui.fragment.ChannelFragment

class ChannelActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        initStartAppSdk()
        setContentView(R.layout.activity_channel)
        super.onCreate(savedInstanceState)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        toolbar.setNavigationOnClickListener { v -> onBackPressed() }


        var catalog: String? = getString(R.string.all_channels)
        if (intent.extras != null) {
            val extraValue = intent.extras!!.getString(CATALOG_KEY)
            if (!TextUtils.isEmpty(extraValue)) {
                catalog = extraValue
            }
        }

        supportActionBar!!.setTitle(catalog)

        catalog?.let { setupPlaylist(it) }
        setupStartAppBanner()
    }

    private fun setupPlaylist(catalog: String) {
        val fm = supportFragmentManager
        val ft = fm.beginTransaction()

        val playlistFragment = ChannelFragment.newInstance(catalog)
        ft.replace(R.id.playlist_container, playlistFragment)
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        ft.commit()
    }

    companion object {
        val TAG = ChannelActivity::class.java.simpleName
        val CATALOG_KEY = "catalog"
    }
}
