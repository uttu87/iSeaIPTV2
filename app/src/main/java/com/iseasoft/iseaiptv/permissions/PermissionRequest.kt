/*
 * The MIT License (MIT)

 * Copyright (c) 2015 Michal Tajchert

 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.iseasoft.iseaiptv.permissions

import java.util.*

class PermissionRequest {
    var permissions: MutableList<String> = arrayListOf()
    var requestCode: Int = 0
        private set
    var permissionCallback: PermissionCallback? = null

    constructor(requestCode: Int) {
        this.requestCode = requestCode
    }

    constructor(permissions: ArrayList<String>, permissionCallback: PermissionCallback) {
        this.permissions = permissions
        this.permissionCallback = permissionCallback
        if (random == null) {
            random = Random()
        }
        this.requestCode = random!!.nextInt(32768)
    }

    override fun equals(`object`: Any?): Boolean {
        if (`object` == null) {
            return false
        }
        return if (`object` is PermissionRequest) {
            `object`.requestCode == this.requestCode
        } else false
    }

    override fun hashCode(): Int {
        return requestCode
    }

    companion object {
        private var random: Random? = null
    }
}