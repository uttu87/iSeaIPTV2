package com.iseasoft.iseaiptv.ui.fragment

import androidx.annotation.IntDef

object PlayerType {

    const val PLAYER_ERROR_VIDEO_NOT_FOUND = -100
    const val PLAYER_ERROR_LOAD_FAILED = -101
    const val PLAYER_ERROR_BEACON_CONFIG_ERROR = -102
    const val PLAYER_ERROR_OTHER_ERROR = -103
    const val PLAYER_STATE_LOAD_FINISHED = 200
    const val PLAYER_STATE_PLAY_FINISHED = 201
    const val PLAYER_STATE_PLAY = 202
    const val PLAYER_STATE_PAUSE = 203

    @IntDef(PLAYER_ERROR_VIDEO_NOT_FOUND, PLAYER_ERROR_LOAD_FAILED, PLAYER_ERROR_BEACON_CONFIG_ERROR, PLAYER_ERROR_OTHER_ERROR)
    annotation class PlayerErrorType

    @IntDef(PLAYER_STATE_LOAD_FINISHED, PLAYER_STATE_PAUSE, PLAYER_STATE_PLAY_FINISHED, PLAYER_STATE_PLAY)
    annotation class PlayerStatusType


}
