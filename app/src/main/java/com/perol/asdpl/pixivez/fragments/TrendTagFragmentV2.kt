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

import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.perol.asdpl.pixivez.R
import com.perol.asdpl.pixivez.activity.SearchResultActivity
import com.perol.asdpl.pixivez.sql.SearchHistoryEntity
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.trend_tag_fragment.*

private const val ARG_PARAM1 = "word"
class TrendTagFragmentV2 : BottomSheetDialogFragment() {
    private var tags: String = ""
    private val mDisposable = CompositeDisposable()

    companion object {
        fun newInstance(tag: String) = TrendTagFragmentV2().apply {

            arguments = Bundle().apply {  putString(ARG_PARAM1, tag)}
        }
    }

    private lateinit var viewModel: TrendTagViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.trend_tag_fragmentv2, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        textView_clearn.setOnClickListener {

            viewModel.sethis()
        }
        arguments?.let {
            tags = it.getString(ARG_PARAM1)!!

        }

        textView_clearn.visibility = View.GONE
    }

    override fun onStop() {
        super.onStop()

        // clear all the subscriptions
        mDisposable.clear()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(TrendTagViewModel::class.java)

        viewModel.searchhistroy.observe(viewLifecycleOwner, Observer { it ->

            val arrayList = ArrayList<String>()
            chipgroup.removeAllViews()
            it.forEach {
                arrayList.add(it.word)
                chipgroup.addView(getChip(it))
            }

            if (arrayList.isNotEmpty()) textView_clearn.visibility = View.VISIBLE
            else textView_clearn.visibility = View.GONE


        })
    }

    private fun getChip(searchHistoryEntity: SearchHistoryEntity): Chip {
        val chip = Chip(activity)
        val paddingDp = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 5f,
            resources.displayMetrics
        ).toInt()
        chip.text = searchHistoryEntity.word
        chip.setOnClickListener {
            upToPage(searchHistoryEntity.word)
        }
        chip.setOnLongClickListener {
            viewModel.deleteHistoryEntity(searchHistoryEntity).subscribe({
                viewModel.resethistory()
            }, {

            }, {

            })
            true
        }
        return chip
    }

    private fun upToPage(query: String) {
        val bundle = Bundle()
        var nameQuery = query
        if (query.contains('|')) {
            val splits = query.split('|')
            nameQuery = splits[0]
        }
        bundle.putString("searchword", "$tags $nameQuery")
        val intent = Intent(activity!!, SearchResultActivity::class.java)
        intent.putExtras(bundle)
        startActivityForResult(intent, 775)
    }
}
