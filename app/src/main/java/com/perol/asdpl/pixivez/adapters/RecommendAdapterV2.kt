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

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.bumptech.glide.request.target.ImageViewTarget
import com.bumptech.glide.request.transition.Transition
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseQuickAdapter.OnItemClickListener
import com.chad.library.adapter.base.BaseViewHolder
import com.google.android.material.card.MaterialCardView
import com.perol.asdpl.pixivez.R
import com.perol.asdpl.pixivez.activity.PictureActivity
import com.perol.asdpl.pixivez.fragments.MainInterface
import com.perol.asdpl.pixivez.fragments.UserBookMarkFragment
import com.perol.asdpl.pixivez.repository.RetrofitRespository
import com.perol.asdpl.pixivez.responses.Illust
import com.perol.asdpl.pixivez.services.GlideApp
import com.perol.asdpl.pixivez.services.PxEZApp
import com.perol.asdpl.pixivez.services.Works
import java.util.*
import android.util.Pair as UtilPair

class RecommendAdapterV2(
    layoutResId: Int,
    data: List<Illust>?,
    private val R18on: Boolean,
    val mainInterface: MainInterface
) :
    BaseQuickAdapter<Illust, BaseViewHolder>(layoutResId, data) {


    fun onLongTap(index: Int) {
        if (!UserBookMarkFragment.isMultiSelectOn) {
            UserBookMarkFragment.isMultiSelectOn = true
        }
        addIDIntoSelectedIds(index)
        mainInterface.onLongPress(true)
    }

    fun onTap(index: Int) {
        if (UserBookMarkFragment.isMultiSelectOn) {
            addIDIntoSelectedIds(index)
        }
    }

    var modelList: MutableList<Illust> = ArrayList<Illust>()
    val selectedIds: MutableList<String> = ArrayList<String>()


    fun addIDIntoSelectedIds(index: Int) {
        val id = data[index].id.toString()
        if (selectedIds.contains(id))
            selectedIds.remove(id)
        else
            selectedIds.add(id)

        notifyItemChanged(index)
        if (selectedIds.size < 1) UserBookMarkFragment.isMultiSelectOn = false
        mainInterface.mainInterface(selectedIds.size, index)
    }

    val retrofit = RetrofitRespository.getInstance()

    init {
        this.openLoadAnimation(SCALEIN)
        this.onItemClickListener = OnItemClickListener { adapter, view, position ->
            val bundle = Bundle()
            bundle.putLong("illustid", this@RecommendAdapterV2.data[position].id)
            val illustlist = LongArray(this.data.count())
            for (i in this.data.indices) {
                illustlist[i] = this.data[i].id
            }
            bundle.putLongArray("illustlist", illustlist)
            bundle.putParcelable(this.data[position].id.toString(), this.data[position])
            val intent = Intent(mContext, PictureActivity::class.java)
            intent.putExtras(bundle)
            if (PxEZApp.animationEnable) {
                val mainimage = view!!.findViewById<View>(R.id.item_img)
                val title = view.findViewById<View>(R.id.title)

                val options = ActivityOptions.makeSceneTransitionAnimation(
                    mContext as Activity,
                    UtilPair.create(
                        mainimage,
                        "mainimage"
                    ),
                    UtilPair.create(title, "title")
                )
                startActivity(mContext, intent, options.toBundle())
            } else
                startActivity(mContext, intent, null)
        }


    }


    override fun convert(helper: BaseViewHolder, item: Illust) {
        val typedValue = TypedValue()

        mContext.theme.resolveAttribute(R.attr.colorPrimary, typedValue, true)
        val colorPrimary = typedValue.resourceId

        helper.setText(R.id.title, item.title).setTextColor(
            R.id.like, if (item.is_bookmarked) {
                Color.YELLOW
            } else {
                ContextCompat.getColor(mContext, colorPrimary)
            }
        )
            .setOnClickListener(R.id.save) {
                Works.imageDownloadAll(item)
            }
            .setOnClickListener(R.id.like) { v ->
                val textView = v as Button
                if (item.is_bookmarked) {
                    retrofit.postUnlikeIllust(item.id).subscribe({
                        textView.setTextColor(ContextCompat.getColor(mContext, colorPrimary))
                        item.is_bookmarked = false
                    }, {}, {})
                } else {
                    retrofit.postLikeIllust(item.id)!!.subscribe({

                        textView.setTextColor(
                            Color.YELLOW
                        )
                        item.is_bookmarked = true
                    }, {}, {})
                }
            }

        val constraintLayout =
            helper.itemView.findViewById<ConstraintLayout>(R.id.constraintLayout_num)
        val imageView = helper.getView<ImageView>(R.id.item_img)
        val cardview_recommand = helper.getView<MaterialCardView>(R.id.cardview_recommand)
        when (item.type) {
            "illust" -> if (item.meta_pages.isEmpty()) {
                constraintLayout.visibility = View.INVISIBLE
            } else if (item.meta_pages.isNotEmpty()) {
                constraintLayout.visibility = View.VISIBLE
                helper.setText(R.id.textview_num, item.meta_pages.size.toString())
            }
            "ugoira" -> {
                constraintLayout.visibility = View.VISIBLE
                helper.setText(R.id.textview_num, "Gif")
            }
            else -> {
                constraintLayout.visibility = View.VISIBLE
                helper.setText(R.id.textview_num, "CoM")
            }
        }
        imageView.setTag(R.id.tag_first, item.image_urls.medium)

        val needsmall = item.height > 1500 || item.height > 1500
        val loadurl = if (needsmall) {
            item.image_urls.square_medium
        } else {
            item.image_urls.medium
        }
        if (!R18on) {
            var isr18 = false
            for (i in item.tags) {
                if (i.name == "R-18" || i.name == "R-18G") {
                    isr18 = true
                    break
                }
            }
            if (isr18) {
                GlideApp.with(imageView.context)
                    .load(ContextCompat.getDrawable(mContext, R.drawable.h))
                    .placeholder(R.drawable.h).into(imageView)
            } else {
                GlideApp.with(imageView.context).load(loadurl)
                    .transition(withCrossFade()).placeholder(R.color.white)
                    .diskCacheStrategy(DiskCacheStrategy.DATA)
                    .into(object : ImageViewTarget<Drawable>(imageView) {

                        override fun onResourceReady(
                            resource: Drawable,
                            transition: Transition<in Drawable>?
                        ) {

                            if (imageView.getTag(R.id.tag_first) === item.image_urls.medium) {
                                super.onResourceReady(resource, transition)
                            }


                        }

                        override fun setResource(resource: Drawable?) {

                            imageView.setImageDrawable(resource)
                        }
                    }
                    )

            }
        } else {

            GlideApp.with(imageView.context).load(loadurl).transition(withCrossFade())
                .placeholder(R.color.white)
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .error(ContextCompat.getDrawable(imageView.context, R.drawable.ai))
                .into(object : ImageViewTarget<Drawable>(imageView) {
                    override fun setResource(resource: Drawable?) {
                        imageView.setImageDrawable(resource)
                    }

                    override fun onResourceReady(
                        resource: Drawable,
                        transition: Transition<in Drawable>?
                    ) {
                        if (imageView.getTag(R.id.tag_first) === item.image_urls.medium) {
                            super.onResourceReady(resource, transition)
                        }

                    }
                })
        }

        val id = item.id.toString()

        cardview_recommand.setOnLongClickListener {
            val position = helper.adapterPosition
            onLongTap(position)
            true
        }

        cardview_recommand.setOnClickListener {
            val position = helper.adapterPosition
            onTap(position)
        }
        if (selectedIds.equals(id)) {
            //if item is selected then,set foreground color of FrameLayout.
            cardview_recommand?.foreground =
                ColorDrawable(ContextCompat.getColor(mContext, R.color.md_red_600))
        } else {
            //else remove selected item color.
            cardview_recommand?.foreground =
                ColorDrawable(ContextCompat.getColor(mContext, android.R.color.transparent))
        }

    }

    fun downloadSelectedIllusts() {
        if (selectedIds.size < 1) return
        //get selected download links
        val list = ArrayList<Illust>()
        for (ids: String in selectedIds) {

            for (illust: Illust in data) {
                if (illust.id.toString().trim() == ids.trim()) {
                    list.add(illust)
                }
            }
        }
        Works.imagesUserBookmarkAll(list)

        selectedIds.clear()
        UserBookMarkFragment.isMultiSelectOn = false
    }
}



