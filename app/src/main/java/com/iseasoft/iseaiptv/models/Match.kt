package com.iseasoft.iseaiptv.models

import androidx.lifecycle.ViewModel
import java.io.Serializable
import java.util.*

class Match : ViewModel(), Serializable, Comparable<Any> {
    var id: Int = 0
    var name: String? = null
    var description: String? = null
    var streamUrl: String? = null
    var streamUrls: ArrayList<String>? = null
        set(streamUrls) {
            field = streamUrls
            streamUrl = streamUrls?.get(0)
        }
    var thumbnailUrl: String? = null
    var type: String? = null
    var league: String? = null
    var time: Date? = null
    var isLive: Boolean = false
    var isYoutube: Boolean = false
    var isHidden: Boolean = false
        get() {
            if (time != null) {
                val beginTime = Calendar.getInstance()
                beginTime.time = time
                beginTime.add(Calendar.MINUTE, -30)

                val endTime = Calendar.getInstance()
                endTime.time = time
                endTime.add(Calendar.HOUR, 2)

                val now = Date()

                return now.before(beginTime.time) || now.after(endTime.time)
            }

            return field
        }
    var isFullMatch: Boolean = false

    override operator fun compareTo(o: Any): Int {
        val match = o as Match

        return if (this.time == null || match.time == null) {
            0
        } else (this.time!!.time - match.time!!.time).toInt()

    }
}
