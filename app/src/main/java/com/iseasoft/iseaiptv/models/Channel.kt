package com.iseasoft.iseaiptv.models

import java.io.Serializable

class Channel : M3UItem(), Serializable {
    var id: Int = 0
    var name: String? = null
    var description: String? = null
    var streamUrl: String? = null
    var thumbnailUrl: String? = null
    var type: String? = null
    var catalog: String? = null
    var isLive: Boolean = false
    var isYoutube: Boolean = false
    var isHidden: Boolean = false
}
