package com.iseasoft.iseaiptv.ui.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.fragment.app.FragmentTransaction
import androidx.viewpager.widget.ViewPager
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.iseasoft.iseaiptv.App
import com.iseasoft.iseaiptv.Constants
import com.iseasoft.iseaiptv.R
import com.iseasoft.iseaiptv.helpers.Router
import com.iseasoft.iseaiptv.http.HttpHandler
import com.iseasoft.iseaiptv.models.M3UPlaylist
import com.iseasoft.iseaiptv.models.Playlist
import com.iseasoft.iseaiptv.parsers.M3UParser
import com.iseasoft.iseaiptv.permissions.IseaSoft
import com.iseasoft.iseaiptv.permissions.PermissionCallback
import com.iseasoft.iseaiptv.ui.fragment.ChannelFragment
import com.iseasoft.iseaiptv.ui.fragment.HomeFragment
import com.iseasoft.iseaiptv.utils.PreferencesUtility
import com.startapp.sdk.adsbase.Ad
import com.startapp.sdk.adsbase.StartAppAd
import com.startapp.sdk.adsbase.adlisteners.AdEventListener
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.InputStream
import java.util.*

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var allChannelTabIndex = 1
    private var panelLayout: CoordinatorLayout? = null
    private var viewPager: ViewPager? = null
    private var tabLayout: TabLayout? = null
    private var placeholderContainer: ConstraintLayout? = null
    private var progressBar: ProgressBar? = null

    var playlist: M3UPlaylist? = null
        private set

    private val permissionReadstorageCallback = object : PermissionCallback {
        override fun permissionGranted() {
            loadChannels()
        }

        override fun permissionRefused() {
            requestStoragePermission()
        }
    }
    private var adapter: GroupChannelAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        initStartAppSdk()
        setContentView(R.layout.activity_main)
        super.onCreate(savedInstanceState)

        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        val drawer = findViewById<View>(R.id.drawer_layout) as DrawerLayout
        val toggle = ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()

        val navigationView = findViewById<View>(R.id.nav_view) as NavigationView
        navigationView.setNavigationItemSelectedListener(this)

        panelLayout = findViewById(R.id.panel_layout)
        viewPager = findViewById<View>(R.id.viewpager) as ViewPager
        tabLayout = findViewById<View>(R.id.tabs) as TabLayout
        tabLayout!!.setupWithViewPager(viewPager)
        placeholderContainer = findViewById(R.id.placeholder_container)
        progressBar = findViewById(R.id.progressBar)

        checkToPlayFromPushNotification()
        loadChannels()
        setupStartAppBanner()
    }

    private fun checkToPlayFromPushNotification() {
        val intent = intent
        if (intent != null) {
            val url = intent.getStringExtra(Constants.PUSH_URL_KEY)
            val message = intent.getStringExtra(Constants.PUSH_MESSAGE)

            if (!TextUtils.isEmpty(url)) {
                if (checkPlayableUrl(url)) {
                    val playerIntent = Intent(this, PlayerActivity::class.java)
                    playerIntent.putExtra(Constants.PUSH_URL_KEY, url)
                    playerIntent.putExtra(Constants.PUSH_MESSAGE, message)
                    startActivity(playerIntent)
                } else {
                    val playlist = Playlist()
                    playlist.name = message
                    playlist.link = url
                    PreferencesUtility.getInstance(this).savePlaylist(playlist)
                }
            }
        }
    }

    private fun checkPlayableUrl(url: String): Boolean {
        return true
    }

    override fun onResume() {
        super.onResume()
    }

    private fun loadChannels() {
        val lastPlaylist = PreferencesUtility.getInstance(this).lastPlaylist
        if (lastPlaylist != null) {
            displayPlaylistInfo(lastPlaylist)
            if (lastPlaylist.link!!.trim { it <= ' ' }.startsWith("http")) {
                loadServer(lastPlaylist.link)
            } else {
                try {
                    val file = File(lastPlaylist.link)
                    val inputStream = FileInputStream(file)
                    parsePlaylist(inputStream)
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                    updateUI()
                }

            }
        } else {
            updateUI()
        }

    }

    private fun displayPlaylistInfo(lastPlaylist: Playlist) {
        val navigationView = findViewById<View>(R.id.nav_view) as NavigationView
        val header = navigationView.getHeaderView(0)
        val playlistName = header.findViewById<TextView>(R.id.nav_header_title)
        val playlistLink = header.findViewById<TextView>(R.id.nav_header_description)
        playlistName.text = lastPlaylist.name
        //playlistLink.setText(lastPlaylist.getLink());
        supportActionBar!!.title = lastPlaylist.name
    }

    private fun showChannelPlaceholder() {
        tabLayout!!.visibility = View.GONE
        progressBar!!.visibility = View.GONE
        placeholderContainer!!.visibility = View.VISIBLE
        val btnAdd = findViewById<Button>(R.id.btn_add_playlist)
        btnAdd.setOnClickListener { navigateToPlaylist() }
    }

    private fun setupViewPager(viewPager: ViewPager) {
        if (adapter == null) {
            adapter = GroupChannelAdapter(supportFragmentManager)
        }
        adapter!!.addFragment(getString(R.string.favorites))
        if (!PreferencesUtility.getInstance(this).hasNoHistoryWatching()) {
            adapter!!.addFragment(getString(R.string.history_watching))
            allChannelTabIndex = 2
        }

        val groupList = LinkedList<String>()
        if (playlist != null) {
            for (i in 0 until playlist!!.playlistItems!!.size) {
                val m3UItem = playlist!!.playlistItems!![i]
                if (groupList.contains(m3UItem.itemGroup)) {
                    continue
                }
                m3UItem.itemGroup?.let { groupList.add(it) }
            }
            adapter!!.addFragment(getString(R.string.all_channels))
            for (i in groupList.indices) {
                val groupTitle = groupList[i]
                if (!TextUtils.isEmpty(groupTitle)) {
                    adapter!!.addFragment(groupTitle)
                }
            }
        }

        viewPager.adapter = adapter
        tabLayout!!.visibility = View.VISIBLE
        placeholderContainer!!.visibility = View.GONE
        progressBar!!.visibility = View.GONE
    }

    private fun loadServer(url: String?) {
        //mProgressBar.setVisibility(View.VISIBLE);
        LoadServer().execute(url)
    }

    override fun onBackPressed() {
        val drawer = findViewById<View>(R.id.drawer_layout) as DrawerLayout
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        val id = item.itemId

        when (id) {
            //            case R.id.nav_playlist:
            //                navigateToPlaylist();
            //                break;
            R.id.nav_trending_app -> openTrendingApps()
            R.id.nav_share -> shareApp()
            R.id.nav_rate -> launchMarket()
            R.id.nav_about -> showAbout()
            else -> {
            }
        }

        val drawer = findViewById<View>(R.id.drawer_layout) as DrawerLayout
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    private fun openTrendingApps() {
        val offerWallAds = StartAppAd(this)
        offerWallAds.loadAd(StartAppAd.AdMode.OFFERWALL, object : AdEventListener {
            override fun onReceiveAd(ad: Ad) {
                offerWallAds.showAd()
            }

            override fun onFailedToReceiveAd(ad: Ad) {
                openTrendingApps()
            }
        })
    }

    private fun navigateToPlaylist() {
        Router.navigateTo(this, Router.Screens.PLAYLIST)
    }

    @SuppressLint("StringFormatInvalid")
    private fun requestStoragePermission() {
        if (IseaSoft.checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE) && IseaSoft.checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            loadChannels()
        } else {
            if (IseaSoft.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Snackbar.make(panelLayout!!, getString(R.string.request_storage_permission_message_load,
                        getString(R.string.app_name)),
                        Snackbar.LENGTH_INDEFINITE)
                        .setAction("OK") { IseaSoft.askForPermission(this@MainActivity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE), permissionReadstorageCallback) }.show()
            } else {
                IseaSoft.askForPermission(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE), permissionReadstorageCallback)
            }
        }
    }

    private fun parsePlaylist(inputStream: InputStream?) {

        val m3UParser = M3UParser()
        try {
            playlist = inputStream?.let { m3UParser.parseFile(it) }
            playlist?.playlistItems?.let {
                App.channelList = playlist?.playlistItems!!
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }

    }

    private fun updateUI() {
        //            if (viewPager != null) {
        //                setupViewPager(viewPager);
        //                viewPager.setCurrentItem(allChannelTabIndex, true);//Set All channels tab
        //            }
        Handler(Looper.getMainLooper()).post { this.setupHomeView() }
    }

    private fun setupHomeView() {
        val fm = supportFragmentManager
        val ft = fm.beginTransaction()

        val homeFragment = HomeFragment()
        ft.replace(R.id.home_content, homeFragment, HomeFragment.TAG)
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        ft.commitAllowingStateLoss()
        placeholderContainer!!.visibility = View.GONE
        progressBar!!.visibility = View.GONE
    }

    override fun onRequestPermissionsResult(
            requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        IseaSoft.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    internal class GroupChannelAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
        private val mFragmentTitles = ArrayList<String>()

        fun addFragment(title: String) {
            mFragmentTitles.add(title)
        }

        override fun getItem(position: Int): Fragment {
            return ChannelFragment.newInstance(mFragmentTitles[position])
        }

        override fun getCount(): Int {
            return mFragmentTitles.size
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return mFragmentTitles[position]
        }
    }

    @SuppressLint("StaticFieldLeak")
    private inner class LoadServer : AsyncTask<String, Void, Void?>() {

        override fun doInBackground(vararg urls: String): Void? {

            val hh = HttpHandler()
            val inputStream = hh.makeServiceCall(urls[0])

            parsePlaylist(inputStream)
            return null
        }

        override fun onPostExecute(aVoid: Void?) {
            super.onPostExecute(aVoid)
            updateUI()
        }
    }

}
