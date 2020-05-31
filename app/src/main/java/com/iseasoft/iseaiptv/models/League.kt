package com.iseasoft.iseaiptv.models

import androidx.lifecycle.ViewModel
import java.io.Serializable
import java.util.*

class League : ViewModel(), Serializable {
    var id: Int = 0
    var name: String? = null
    var description: String? = null
    var matches: ArrayList<Match>? = null
    var isHidden: Boolean = false
}
