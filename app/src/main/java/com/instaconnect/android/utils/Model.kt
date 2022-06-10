package com.instaconnect.android.utils

import java.io.Serializable

open class Model : Serializable {
    var modelId: Long = 0

    constructor() {}
    constructor(modelId: Long) {
        this.modelId = modelId
    }
}