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

package com.perol.asdpl.pixivez.viewmodel

import androidx.lifecycle.MutableLiveData
import com.perol.asdpl.pixivez.repository.RetrofitRespository
import com.perol.asdpl.pixivez.responses.Illust
import com.perol.asdpl.pixivez.services.PxEZApp
import java.util.*
import kotlin.collections.ArrayList

fun Calendar?.generateDateString(): String? {
    if (this == null) {
        return this;
    }
    return "${this.get(Calendar.YEAR)}-${this.get(Calendar.MONTH) + 1}-${this.get(Calendar.DATE)}"
}

class IllustfragmentViewModel : BaseViewModel() {
    var sortT = arrayOf("date_desc", "date_asc", "popular_desc")
    var search_targetT =
        arrayOf("partial_match_for_tags", "exact_match_for_tags", "title_and_caption")

    var illusts = MutableLiveData<ArrayList<Illust>>()
    var addIllusts = MutableLiveData<ArrayList<Illust>>()
    var retrofitRespository = RetrofitRespository.getInstance()
    var nexturl = MutableLiveData<String>()
    var bookmarkid = MutableLiveData<Long>()
    var isRefresh = MutableLiveData<Boolean>(false)
    val sort = MutableLiveData<String>(sortT[0])
    val searchTarget = MutableLiveData<String>(search_targetT[0])
    val startDate = MutableLiveData<Calendar>()
    val endDate = MutableLiveData<Calendar>()
    fun setPreview(word: String, sort: String, search_target: String?, duration: String?) {
        isRefresh.value = true
        retrofitRespository.getSearchIllustPreview(word, sort, search_target, null, duration)
            .subscribe({
                illusts.value = ArrayList<Illust>(it.illusts)
                nexturl.value = it.next_url
                isRefresh.value = false
            }, {
                it.printStackTrace()
            }, {}).add()
    }


    fun firstSetData(word: String) {
        isRefresh.value = true
        if ((startDate.value != null || endDate.value != null) && (startDate.value != null && endDate.value != null) && startDate.value!!.timeInMillis >= endDate.value!!.timeInMillis) {
            startDate.value = null
            endDate.value = null
        }

        retrofitRespository.getSearchIllust(
            word,
            sort.value!!,
            searchTarget.value!!,
            startDate.value.generateDateString(),
            endDate.value.generateDateString(),
            null
        )
            .subscribe({
                illusts.value = ArrayList<Illust>(it.illusts)
                nexturl.value = it.next_url
                isRefresh.value = false
                if (PxEZApp.searchMode == 60) {
                    if (nexturl.value != null) {
                        retrofitRespository.getNext(nexturl.value!!).subscribe({
                            addIllusts.value = ArrayList<Illust>(it.illusts)
                            nexturl.value = it.next_url

                            if (nexturl.value != null) {
                                retrofitRespository.getNext(nexturl.value!!).subscribe({
                                    addIllusts.value = ArrayList<Illust>(it.illusts)
                                    nexturl.value = it.next_url
                                }, {}, {}).add()
                            }
                        }, {}, {}).add()
                    }
                }

            }, {
                it.printStackTrace()
            }, {}).add()
    }

    fun onLoadMoreListen() {
        if (nexturl.value != null) {
            retrofitRespository.getNext(nexturl.value!!).subscribe({
                addIllusts.value = ArrayList<Illust>(it.illusts)
                nexturl.value = it.next_url
                if (PxEZApp.searchMode == 60) {

                    if (nexturl.value != null) {
                        retrofitRespository.getNext(nexturl.value!!).subscribe({
                            val lists = ArrayList<Illust>(it.illusts)
                            val listss = addIllusts.value
                            listss!!.addAll(lists)
                            addIllusts.value = listss
                            nexturl.value = it.next_url
                        }, {}, {}).add()
                    }
                }
            }, {}, {}).add()
        }

    }


}

