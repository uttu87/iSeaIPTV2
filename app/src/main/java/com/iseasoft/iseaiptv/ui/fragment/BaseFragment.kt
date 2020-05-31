package com.iseasoft.iseaiptv.ui.fragment

import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle

abstract class BaseFragment : Fragment() {
    protected val isStateSafe: Boolean
        get() = lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)
}
