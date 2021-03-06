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

package com.perol.asdpl.pixivez.services

import android.app.Activity
import android.app.Application
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager
import androidx.work.WorkManager
import com.perol.asdpl.pixivez.BuildConfig
import com.perol.asdpl.pixivez.objects.CrashHandler
import java.io.File


class PxEZApp : Application() {
    //    override fun attachBaseContext(base: Context) {
//        val locale = when (PxEZApp.language) {
//            1 -> {
//                Locale.ENGLISH
//            }
//            2 -> {
//                Locale.TRADITIONAL_CHINESE
//            }
//            else -> {
//                Locale.SIMPLIFIED_CHINESE
//            }
//        }
//        val context = MyContextWrapper.wrap(base, locale)
//        super.attachBaseContext(context)
//    }
    override fun onCreate() {
        super.onCreate()
        instance = this
        val defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        AppCompatDelegate.setDefaultNightMode(defaultSharedPreferences.getString("dark_mode", "-1")!!.toInt())
        animationEnable = defaultSharedPreferences.getBoolean("animation", true)
        language = defaultSharedPreferences.getString("language", "0")?.toInt() ?: 0
        storepath = defaultSharedPreferences.getString("storepath1", Environment.getExternalStorageDirectory().absolutePath + File.separator + "PxEz")
                ?: Environment.getExternalStorageDirectory().absolutePath + File.separator + "PxEz"
        if (defaultSharedPreferences.getBoolean("crashreport", true)) {
            CrashHandler.getInstance().init(this)
        }
        locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            resources.configuration.locales.get(0).language;
        } else {
            resources.configuration.locale.language;
        }

        WorkManager.getInstance(this).pruneWork()

        if (BuildConfig.DEBUG) {
            registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
                override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                    Log.v(TAG, "${activity.simpleName}: onActivityCreated.")
                }

                override fun onActivityStarted(activity: Activity) {
                    Log.v(TAG, "${activity.simpleName}: onActivityStarted.")
                }

                override fun onActivityResumed(activity: Activity) {
                    Log.v(TAG, "${activity.simpleName}: onActivityResumed.")
                }

                override fun onActivityPaused(activity: Activity) {
                    Log.v(TAG, "${activity.simpleName}: onActivityPaused.")
                }

                override fun onActivityStopped(activity: Activity) {
                    Log.v(TAG, "${activity.simpleName}: onActivityStopped.")
                }

                override fun onActivityDestroyed(activity: Activity) {
                    Log.v(TAG, "${activity.simpleName}: onActivityDestroyed.")
                }

                override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
                    //
                }
            })
        }
    }

    private val Activity.simpleName get() = javaClass.simpleName

    companion object {
        @JvmStatic
        var storepath = Environment.getExternalStorageDirectory().absolutePath + File.separator + "PxEz"
        @JvmStatic
        var locale = "zh"
        @JvmStatic
        var language: Int = 0
        @JvmStatic
        var animationEnable: Boolean = false
        lateinit var instance: PxEZApp
        var autochecked = false

        private const val TAG = "PxEZApp"
    }
}
