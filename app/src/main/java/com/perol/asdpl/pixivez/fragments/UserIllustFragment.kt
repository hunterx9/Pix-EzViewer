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
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.perol.asdpl.pixivez.R
import com.perol.asdpl.pixivez.adapters.RecommendAdapter
import com.perol.asdpl.pixivez.objects.LazyV4Fragment
import com.perol.asdpl.pixivez.responses.Illust
import com.perol.asdpl.pixivez.viewmodel.UserMillustViewModel
import kotlinx.android.synthetic.main.fragment_user_illust.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [UserIllustFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class UserIllustFragment : LazyV4Fragment() {

    override fun loadData() {
        viewmodel!!.first(param1!!, param2!!)
    }

    fun lazyLoad() {
        recommendAdapter.setOnLoadMoreListener({
            viewmodel!!.OnLoadMoreListener()
        }, mrecyclerview)
        mrefreshlayout.setOnRefreshListener {
            viewmodel!!.OnRefreshListener(param1!!, param2!!)
        }
        mrecyclerview.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        mrecyclerview.adapter = recommendAdapter
    }

    // TODO: Rename and change types of parameters
    private var param1: Long? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getLong(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        initvoid()

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lazyLoad()
    }

    var viewmodel: UserMillustViewModel? = null
    private fun initvoid() {
        viewmodel = ViewModelProviders.of(this).get(UserMillustViewModel::class.java)

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
                recommendAdapter.setNewData(filterIllust(it))
            }

        })
        viewmodel!!.adddata.observe(this, Observer {
            if (it != null) {


                recommendAdapter.addData(filterIllust(it))
                recommendAdapter.loadMoreComplete()
            }
        })


    }

    private fun filterIllust(it: List<Illust>): List<Illust> {
        var filteredIllusts = listOf<Illust>()
        try {

            val isR18all =
                PreferenceManager.getDefaultSharedPreferences(context).getBoolean("r18all", false)
            val isR18no =
                PreferenceManager.getDefaultSharedPreferences(context).getBoolean("r18no", false)
            if (isR18all) {
                filteredIllusts = it
                    .filter { it.tags.all { tag -> tag.name != "shota" } }
                    .filter { it.tags.all { tag -> tag.name != "yaoi" } }
                    .filter { it.tags.all { tag -> tag.name != "BL" } }
                    .filter { it.tags.all { tag -> tag.name != "腐向け" } }
                    .filter { it.tags.all { tag -> tag.name != "やおい" } }
                    .filter { it.tags.all { tag -> tag.name != "腐" } }
                    .filter { it.tags.all { tag -> tag.name != "ホモ" } }
                    .filter { it.tags.all { tag -> tag.name != "ふたなり" } }
                    .filter { it.tags.all { tag -> tag.name != "futanari" } }
                    .filter { it.tags.all { tag -> tag.name != "gay" } }
                    .filter { it.tags.all { tag -> tag.name != "ゲイ" } }
                    .filter { it.tags.all { tag -> tag.name != "ケモノ" } }
                    .filter { it.tags.all { tag -> tag.name != "獸人" } }
                    .filter { it.tags.all { tag -> tag.name != "furry" } }
                    .filter { it.tags.all { tag -> !tag.name.contains("【腐】") } }
                    .filter { it.tags.get(0).name.equals("R-18") }
            } else if (isR18no) {
                filteredIllusts = it
                    .filter { it.tags.all { tag -> tag.name != "shota" } }
                    .filter { it.tags.all { tag -> tag.name != "yaoi" } }
                    .filter { it.tags.all { tag -> tag.name != "BL" } }
                    .filter { it.tags.all { tag -> tag.name != "腐向け" } }
                    .filter { it.tags.all { tag -> tag.name != "やおい" } }
                    .filter { it.tags.all { tag -> tag.name != "腐" } }
                    .filter { it.tags.all { tag -> tag.name != "ホモ" } }
                    .filter { it.tags.all { tag -> tag.name != "ふたなり" } }
                    .filter { it.tags.all { tag -> tag.name != "futanari" } }
                    .filter { it.tags.all { tag -> tag.name != "gay" } }
                    .filter { it.tags.all { tag -> tag.name != "ゲイ" } }
                    .filter { it.tags.all { tag -> tag.name != "ケモノ" } }
                    .filter { it.tags.all { tag -> tag.name != "獸人" } }
                    .filter { it.tags.all { tag -> tag.name != "furry" } }
                    .filter { it.tags.all { tag -> !tag.name.contains("【腐】") } }
                    .filter { !it.tags.get(0).name.equals("R-18") }
            } else {
                filteredIllusts = it
            }

        } catch (e: Exception) {

        }


//        val illust = arrayListOf<Illust>()
//        illust.addAll(filteredIllusts)
        return filteredIllusts
    }


    lateinit var recommendAdapter: RecommendAdapter
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        recommendAdapter = RecommendAdapter(R.layout.view_recommand_item, null, PreferenceManager.getDefaultSharedPreferences(activity).getBoolean("r18on", false))

        return inflater.inflate(R.layout.fragment_user_illust, container, false)
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment UserIllustFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: Long, param2: String) =
                UserIllustFragment().apply {
                    arguments = Bundle().apply {
                        putLong(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}
