package com.iseasoft.iseaiptv.api

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.iseasoft.iseaiptv.Constants.CONFIG_COLLECTION

class IndiaTvAPI {

    fun getConfig(listener: APIListener<Task<QuerySnapshot>>) {
        val firebaseFirestore = FirebaseFirestore.getInstance()
        firebaseFirestore.collection(CONFIG_COLLECTION)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        listener.onRequestCompleted(task, task.result!!.metadata.toString())
                    } else {
                        listener.onError(Error(task.exception))
                    }
                }
    }

    companion object {
        private val TAG = IndiaTvAPI::class.java.simpleName
        private val MATCH_URL_REGEX = "Hosted by <a href=\"(.*?)\" target=\"_blank\""
        private val URL_REGEX = "href=\"(.*?)\">"
        private val IMAGE_URL_REGEX = "poster:\"(.*?)\",name:"
        private val STREAM_URL_REGEX = "hls:\"(.*?)\"\\};settings"
        private val NAME_REGEX = "name:\"(.*?)\",contentTitle:"


        private var instance: IndiaTvAPI? = null

        val baseURLDev: String
            get() = "http://hoofoot.com"

        @Synchronized
        fun getInstance(): IndiaTvAPI {
            if (instance == null) {
                instance = IndiaTvAPI()
            }
            return instance as IndiaTvAPI
        }
    }
}
