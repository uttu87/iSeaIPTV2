package com.iseasoft.iseaiptv.models

import java.io.Serializable

open class M3UItem : Serializable {

    var itemDuration: String? = null

    var itemName: String? = null

    var itemUrl: String? = null

    var itemIcon: String? = null

    var itemGroup: String? = null
}
