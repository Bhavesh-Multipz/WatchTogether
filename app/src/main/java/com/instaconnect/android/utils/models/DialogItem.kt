package com.instaconnect.android.utils.models

class DialogItem {
    var image: String? = null
    var name: String
    var txtColor = -1
    var isDivider = false
    var isUnderLine = false
    var resId = -1
    var isSelected = false

    constructor(image: String?, name: String, resId: Int) {
        this.image = image
        this.name = name
        this.resId = resId
    }

    constructor(image: String?, name: String) {
        this.image = image
        this.name = name
    }

    constructor(name: String, txtColor: Int) {
        this.name = name
        this.txtColor = txtColor
    }

    constructor(name: String) {
        this.name = name
    }

    constructor(name: String, divider: Boolean) {
        this.name = name
        isDivider = divider
    }

    constructor(name: String, txtColor: Int, divider: Boolean) {
        this.name = name
        this.txtColor = txtColor
        isDivider = divider
    }

    constructor(isSelected: Boolean, name: String, txtColor: Int, divider: Boolean) {
        this.name = name
        this.txtColor = txtColor
        isDivider = divider
        this.isSelected = isSelected
    }

    constructor(name: String, txtColor: Int, underLine: Boolean, divider: Boolean) {
        this.name = name
        this.txtColor = txtColor
        isDivider = divider
        isUnderLine = underLine
    }
}