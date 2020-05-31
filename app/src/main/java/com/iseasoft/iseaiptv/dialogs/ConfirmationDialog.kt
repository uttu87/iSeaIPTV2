package com.iseasoft.iseaiptv.dialogs

import android.content.res.Resources
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import butterknife.Unbinder
import com.iseasoft.iseaiptv.R
import com.iseasoft.iseaiptv.listeners.OnConfirmationDialogListener

open class ConfirmationDialog : DialogFragment() {
    lateinit var title: String
    var description: String? = null
    lateinit var okText: String
    var cancelText: String? = null
    protected var onConfirmationDialogListener: OnConfirmationDialogListener? = null
    protected var moduleLayout: Int = 0
    protected var isQuitPopup: Boolean = false

    @BindView(R.id.popup_title)
    @JvmField
    var popupTitle: TextView? = null

    @BindView(R.id.popup_description)
    @JvmField
    var popupDescription: TextView? = null

    @BindView(R.id.module_container)
    @JvmField
    var moduleContainer: FrameLayout? = null

    @BindView(R.id.two_buttons_container)
    @JvmField
    var twoButtonsContainer: LinearLayout? = null

    @BindView(R.id.btn_ok)
    @JvmField
    var btnOk: TextView? = null

    @BindView(R.id.btn_cancel)
    @JvmField
    var btnCancel: TextView? = null

    @BindView(R.id.one_button_container)
    @JvmField
    var oneButtonContainer: LinearLayout? = null

    @BindView(R.id.btn_dismiss)
    @JvmField
    var btnDismiss: TextView? = null
    internal lateinit var unbinder: Unbinder

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog!!.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        val view = inflater.inflate(R.layout.fragment_confirmation_popup, container, false)
        unbinder = ButterKnife.bind(this, view)
        popupTitle!!.text = title
        if (description != null) {
            popupDescription!!.text = description
            popupDescription!!.visibility = View.VISIBLE
        }

        if (moduleLayout != NO_MODULE) {
            try {
                val moduleView = inflater.inflate(moduleLayout, container, false)
                moduleContainer!!.addView(moduleView)
                moduleContainer!!.visibility = View.VISIBLE
            } catch (e: Resources.NotFoundException) {
                Log.e(TAG, "", e)
            }

        }

        btnOk!!.text = okText

        if (cancelText != null) {
            btnCancel!!.text = cancelText
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        unbinder.unbind()
    }

    @OnClick(R.id.btn_cancel, R.id.btn_ok, R.id.btn_dismiss)
    fun onViewClicked(view: View) {
        when (view.id) {
            R.id.btn_cancel -> cancelled()
            R.id.btn_ok -> confirmed()
            R.id.btn_dismiss -> cancelled()
        }
    }

    protected fun confirmed() {
        Handler(Looper.getMainLooper()).post {
            setPopupTitle(getString(R.string.rating))
            okText(getString(R.string.common_dialog_ok))
            cancelText(getString(R.string.common_dialog_cancel))
        }
        if (isQuitPopup) {
            if (onConfirmationDialogListener != null) {
                onConfirmationDialogListener!!.onConfirmed()
            }

            dismiss()
        }

        isQuitPopup = true
    }

    private fun cancelled() {
        Handler(Looper.getMainLooper()).post {
            setPopupTitle(getString(R.string.feedback))
            okText(getString(R.string.common_dialog_ok))
            cancelText(getString(R.string.common_dialog_cancel))
        }

        if (isQuitPopup) {
            if (onConfirmationDialogListener != null) {
                onConfirmationDialogListener!!.onCanceled()
            }
            dismiss()
        }
        isQuitPopup = true
    }

    protected fun setPopupTitle(title: String?) {
        if (title == null || TextUtils.isEmpty(title)) {
            return
        }
        this.title = title
        popupTitle!!.text = title
    }

    fun okText(text: String?) {
        if (text == null || TextUtils.isEmpty(text)) {
            return
        }
        this.okText = text
        btnOk!!.text = text
    }

    fun cancelText(cancelText: String?) {
        if (cancelText == null || TextUtils.isEmpty(cancelText)) {
            return
        }
        btnCancel!!.text = cancelText
    }

    protected fun setOkEnable(enable: Boolean) {
        btnOk!!.alpha = if (enable) 1.0f else 0.3f
        btnOk!!.isEnabled = enable
    }

    protected fun setEnableOneButton(enable: Boolean) {
        Handler(Looper.getMainLooper()).post {
            oneButtonContainer!!.visibility = if (enable) View.VISIBLE else View.GONE
            twoButtonsContainer!!.visibility = if (enable) View.GONE else View.VISIBLE
        }

    }

    protected fun setOneButtonText(text: String?) {
        if (text == null || TextUtils.isEmpty(text)) {
            return
        }
        btnDismiss!!.text = text
    }

    override fun onResume() {
        super.onResume()
        val window = dialog!!.window
        if (window != null) {
            val widthDialog = LinearLayout.LayoutParams.MATCH_PARENT
            val heightDialog = LinearLayout.LayoutParams.WRAP_CONTENT
            window.setLayout(widthDialog, heightDialog)
            window.setGravity(Gravity.CENTER)
        }
    }

    companion object {

        val TAG = ConfirmationDialog::class.java.simpleName
        private val NO_MODULE = -1

        fun newInstance(title: String, description: String, okText: String, listener: OnConfirmationDialogListener): ConfirmationDialog {
            return newInstance(title, description, okText, NO_MODULE, listener)
        }

        fun newInstance(title: String, okText: String, moduleLayout: Int, listener: OnConfirmationDialogListener): ConfirmationDialog {
            return newInstance(title, null, okText, moduleLayout, listener)
        }

        fun newInstance(title: String, description: String?, okText: String, moduleLayout: Int, listener: OnConfirmationDialogListener): ConfirmationDialog {
            val fragment = ConfirmationDialog()
            fragment.title = title
            fragment.description = description
            fragment.okText = okText
            fragment.moduleLayout = moduleLayout
            fragment.onConfirmationDialogListener = listener
            fragment.isQuitPopup = false
            return fragment
        }
    }
}
