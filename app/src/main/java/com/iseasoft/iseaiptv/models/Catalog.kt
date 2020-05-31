package com.iseasoft.iseaiptv.models

import androidx.lifecycle.ViewModel
import java.io.Serializable
import java.util.*

class Catalog : ViewModel(), Serializable {
    var id: Int = 0
    var name: String? = null
    var description: String? = null
    var channels: ArrayList<M3UItem>? = null
}
