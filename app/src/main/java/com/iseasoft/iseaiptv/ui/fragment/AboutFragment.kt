package com.iseasoft.iseaiptv.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.Unbinder
import com.iseasoft.iseaiptv.BuildConfig
import com.iseasoft.iseaiptv.R

class AboutFragment : DialogFragment() {

    internal lateinit var unbinder: Unbinder

    @BindView(R.id.app_version)
    @JvmField
    var appVersion: TextView? = null

    @BindView(R.id.btn_ok)
    @JvmField
    var btnOk: TextView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog!!.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        val view = inflater.inflate(R.layout.fragment_about, container, false)
        unbinder = ButterKnife.bind(this, view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        appVersion!!.text = getString(R.string.app_version, BuildConfig.VERSION_NAME)
        btnOk!!.setOnClickListener { dismiss() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        unbinder.unbind()
    }

    companion object {
        val TAG = AboutFragment::class.java.simpleName
    }
}
