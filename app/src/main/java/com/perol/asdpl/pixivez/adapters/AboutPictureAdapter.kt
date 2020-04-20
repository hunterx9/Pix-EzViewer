/*
 * MIT License
 *
 * Copyright (c) 2019 Perol_Notsfsssf
 *
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
 * SOFTWARE
 */

package com.perol.asdpl.pixivez.adapters

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.bumptech.glide.request.RequestOptions
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.perol.asdpl.pixivez.R
import com.perol.asdpl.pixivez.repository.RetrofitRepository
import com.perol.asdpl.pixivez.services.GlideApp
import com.perol.asdpl.pixivez.sql.entity.RecommendIllust

class AboutPictureAdapter(layoutResId: Int) :
    BaseQuickAdapter<RecommendIllust, BaseViewHolder>(layoutResId) {

    override fun convert(helper: BaseViewHolder, item: RecommendIllust) {
        val imageView = helper.getView<ImageView>(R.id.imageview_aboutpic)
        val imageCount = helper.getView<TextView>(R.id.tv_image_count)
        val likeImage = helper.getView<TextView>(R.id.tv_img_like)
        likeImage.text = item.bookmark_count
        if (item.is_bookmarked)
            likeImage.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_heart_color_red,0,0,0)
        else
            likeImage.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_heart_red,0,0,0)

        likeImage.setOnClickListener {
            val retrofit = RetrofitRepository.getInstance()
            if (item.is_bookmarked) {
                retrofit.postUnlikeIllust(item.id).subscribe({
                    likeImage.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_heart_red,0,0,0)

                }, {}, {})
            } else {
                retrofit.postLikeIllust(item.id)!!.subscribe({
                    likeImage.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_action_heart_color_red,0,0,0)
                }, {}, {})
            }
        }
        if (item.type == "") {
            imageCount.visibility = View.GONE
        } else {
            imageCount.text = item.type
        }
        if (helper.layoutPosition % 2 != 0)
            GlideApp.with(imageView.context).load(item.illust).placeholder(R.color.white)
                .transition(withCrossFade()).centerInside().into(imageView).clearOnDetach()
        else
            GlideApp.with(imageView.context).load(item.illust).placeholder(R.color.gray)
                .transition(withCrossFade()).centerInside().into(imageView).clearOnDetach()
    }
}

