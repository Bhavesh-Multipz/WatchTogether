package com.instaconnect.android.ui.login

import android.app.Activity
import android.content.Context
import androidx.viewpager.widget.PagerAdapter
import android.view.LayoutInflater
import android.view.ViewGroup
import com.instaconnect.android.R
import android.os.Parcelable
import android.view.View
import android.widget.ImageView

class IntoScreenAdapter(
    private val context: Context,
    var sliderImages: IntArray,
    activity: Activity
) : PagerAdapter() {
    private val inflater: LayoutInflater
    var activity: Activity
    fun replaceSlider(IMAGES: IntArray) {
        sliderImages = IMAGES
        notifyDataSetChanged()
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun getCount(): Int {
        return sliderImages.size
    }

    override fun instantiateItem(view: ViewGroup, pos: Int): Any {
        val imageLayout: View = inflater.inflate(R.layout.item_intro_screen, view, false)!!
        val imageView = imageLayout.findViewById<View>(R.id.img_intro) as ImageView
        imageView.setImageResource(sliderImages[pos])
        imageView.setOnClickListener { }
        view.addView(imageLayout, 0)
        return imageLayout
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun restoreState(state: Parcelable?, loader: ClassLoader?) {}
    override fun saveState(): Parcelable? {
        return null
    }

    init {
        inflater = LayoutInflater.from(context)
        this.activity = activity
    }
}