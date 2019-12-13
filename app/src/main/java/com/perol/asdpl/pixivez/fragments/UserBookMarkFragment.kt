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

package com.perol.asdpl.pixivez.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.perol.asdpl.pixivez.R
import com.perol.asdpl.pixivez.adapters.RecommendAdapterV2
import com.perol.asdpl.pixivez.dialog.TagsShowDialog
import com.perol.asdpl.pixivez.objects.LazyV4Fragment
import com.perol.asdpl.pixivez.responses.Illust
import com.perol.asdpl.pixivez.services.Works
import com.perol.asdpl.pixivez.viewmodel.UserBookMarkViewModel
import kotlinx.android.synthetic.main.fragment_user_book_mark.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [UserBookMarkFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */

class UserBookMarkFragment : LazyV4Fragment(), TagsShowDialog.Callback, MainInterface {
    override fun loadData() {
        viewmodel!!.first(param1!!, pub).doOnSuccess {
            if (it) {
                val view = layoutInflater.inflate(R.layout.header_bookmark, null)
                val imagebutton = view.findViewById<ImageView>(R.id.imagebutton_showtags)
                val image_button_download = view.findViewById<ImageView>(R.id.imagebutton_download)
                recommendAdapter.addHeaderView(view)

                imagebutton.setOnClickListener {
                    showtagdialog()
                }
                image_button_download.setOnClickListener {

                    showDownloadDialog()
                }

                val fab_selected_btn =
                    adapterView!!.findViewById<FloatingActionButton>(R.id.downloadSelected)
                fab_selected_btn_cancel =
                    adapterView!!.findViewById<FloatingActionButton>(R.id.downloadSelectedCancel)
                shouldResetRecyclerView = true
                fab_selected_btn.visibility = View.VISIBLE
//                fab_selected_btn_cancel!!.visibility = View.VISIBLE
                fab_selected_btn.setOnClickListener {


                    val normalDialog = MaterialAlertDialogBuilder(context)
                    normalDialog.setMessage("Do you want to download selected images from your bookmarks?")
                    normalDialog.setPositiveButton(
                        "Yes"
                    ) { _, _ ->
                        Toast.makeText(
                            context,
                            "Downloading selected images from user bookmarks",
                            Toast.LENGTH_SHORT
                        ).show()
                        shouldResetRecyclerView = true
                        isMultiSelectOn = false
                        recommendAdapter.downloadSelectedIllusts()
                        recommendAdapter.notifyDataSetChanged()
                    }
                        .setNegativeButton(
                            "Cancel"
                        ) { _, _ ->

                            shouldResetRecyclerView = true
                            isMultiSelectOn = false
                            recommendAdapter.setIsLongPress(false)
                            recommendAdapter.notifyDataSetChanged()
                        }
                    normalDialog.show()

                    fab_selected_btn_cancel!!.visibility = View.GONE
                }
                fab_selected_btn_cancel!!.setOnClickListener {
                    recommendAdapter.selectedIds.clear()
                    recommendAdapter.setIsLongPress(false)
                    recommendAdapter.notifyDataSetChanged()

                    isMultiSelectOn = false
                    shouldResetRecyclerView = true
                    fab_selected_btn_cancel!!.visibility = View.GONE
                }

            }
        }.doOnError {

        }.subscribe()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isMultiSelectOn = false
        val manager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
//        manager.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_NONE
        mrecyclerview.layoutManager = manager

        mrecyclerview.adapter = recommendAdapter
        recommendAdapter.setOnLoadMoreListener({
            viewmodel!!.onLoadMoreListener()
        }, mrecyclerview)

        mrefreshlayout.setOnRefreshListener {
            viewmodel!!.onRefreshListener(param1!!, pub, null)
        }
    }

    override fun onClick(string: String, public: String) {
        viewmodel!!.onRefreshListener(
            param1!!, public, if (string.isNotBlank()) {
            string
        } else {
            null
        })
    }

    fun lazyLoad() {
        illusts = ArrayList<Illust>()
        viewmodel = ViewModelProviders.of(this).get(UserBookMarkViewModel::class.java)
        viewmodel!!.nexturl.observe(this, Observer {
            if (it.isNullOrEmpty()) {
                recommendAdapter.loadMoreEnd()
            } else {
                recommendAdapter.loadMoreComplete()
            }
        })
        viewmodel!!.data.observe(this, Observer {
            if (it != null) {
                mrefreshlayout.isRefreshing = false
                recommendAdapter.setNewData(it)
                illusts.clear()
                illusts.addAll(recommendAdapter.data)
            }

        })
        viewmodel!!.adddata.observe(this, Observer {
            if (it != null) {
                recommendAdapter.addData(it)
                recommendAdapter.loadMoreComplete()
                illusts.clear()
                illusts.addAll(recommendAdapter.data)
            }
        })
        viewmodel!!.tags.observe(this, Observer {

        })

    }


    private var fab_selected_btn_cancel: FloatingActionButton? = null
    private var adapterView: View? = null
    private var shouldResetRecyclerView: Boolean = false

    private lateinit var illusts: java.util.ArrayList<Illust>
    var first = true

    // TODO: Rename and change types of parameters
    private var param1: Long? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getLong(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        lazyLoad()

    }

    var pub = "public"

    var viewmodel: UserBookMarkViewModel? = null


    private fun showDownloadDialog() {
        val normalDialog = MaterialAlertDialogBuilder(context)
        normalDialog.setMessage("Do you want to download ${illusts.size} images from your bookmarks?")
        normalDialog.setPositiveButton(
            "Yes"
        ) { _, _ ->
            Toast.makeText(
                context,
                "Downloading ${illusts.size} images from user bookmarks",
                Toast.LENGTH_SHORT
            ).show()
            Works.imagesUserBookmarkAll(illusts)
        }
        normalDialog.show()

    }

    fun showtagdialog() {
        val arrayList = ArrayList<String>()
        val arrayList1 = ArrayList<Int>()
        if (viewmodel!!.tags.value != null) {
            val tagsShowDialog = TagsShowDialog()
            tagsShowDialog.callback = this
            for (i in viewmodel!!.tags.value!!.bookmark_tags) {
                arrayList.add(i.name)
                arrayList1.add(i.count)
            }
            val bundle = Bundle()
            bundle.putStringArrayList("tags", arrayList)
            bundle.putIntegerArrayList("counts", arrayList1)
            bundle.putString("nexturl", viewmodel!!.tags.value!!.next_url)
            bundle.putLong("id", param1!!)
            tagsShowDialog.setArguments(bundle)
            tagsShowDialog.show(childFragmentManager)
        }


    }

    lateinit var recommendAdapter: RecommendAdapterV2
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        adapterView = inflater.inflate(R.layout.fragment_user_book_mark, container, false)
        recommendAdapter = RecommendAdapterV2(
            R.layout.view_recommand_item,
            null,
            PreferenceManager.getDefaultSharedPreferences(activity).getBoolean("r18on", false)
            , this
        )
        return adapterView
    }


    companion object {
        var isMultiSelectOn = false
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment UserBookMarkFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: Long, param2: String) =
                UserBookMarkFragment().apply {
                    arguments = Bundle().apply {
                        putLong(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }


    override fun mainInterface(size: Int, index: Int) {
        Toast.makeText(context, "Size: $size", Toast.LENGTH_SHORT).show()
    }

    override fun onLongPress(isPressed: Boolean) {
        fab_selected_btn_cancel!!.visibility = View.VISIBLE
    }

}

interface MainInterface {
    fun mainInterface(size: Int, index: Int)
    fun onLongPress(isPressed: Boolean)
}