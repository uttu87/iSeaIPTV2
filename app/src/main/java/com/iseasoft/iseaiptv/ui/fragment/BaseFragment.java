package com.iseasoft.iseaiptv.ui.fragment;

import android.arch.lifecycle.Lifecycle;
import android.support.v4.app.Fragment;

public abstract class BaseFragment extends Fragment {
    protected boolean isStateSafe() {
        return getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.CREATED);
    }
}
