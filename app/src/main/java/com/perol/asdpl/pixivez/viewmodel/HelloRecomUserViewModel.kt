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
import com.perol.asdpl.pixivez.responses.SearchUserResponse

class HelloRecomUserViewModel : BaseViewModel() {
    val retrofit = RetrofitRespository.getInstance()
    val nexturl = MutableLiveData<String>()
    val adddata = MutableLiveData<List<SearchUserResponse.UserPreviewsBean>>()
    val data = MutableLiveData<List<SearchUserResponse.UserPreviewsBean>>()
    fun reData() {
        retrofit.getUserRecommanded().subscribe({
            nexturl.value = it.next_url
            data.value = it.user_previews

            retrofit.getUserRecommandedUrl(nexturl.value!!).subscribe({
                nexturl.value = it.next_url
                adddata.value = it.user_previews
            }, {}, {})
        }, {

        }, {}).add()
    }

    fun getNext() {
        retrofit.getUserRecommandedUrl(nexturl.value!!).subscribe({
            nexturl.value = it.next_url
            adddata.value = it.user_previews
        }, {}, {}).add()
    }
}