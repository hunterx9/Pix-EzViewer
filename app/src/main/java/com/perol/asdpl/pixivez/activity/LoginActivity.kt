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

package com.perol.asdpl.pixivez.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.perol.asdpl.pixivez.R
import com.perol.asdpl.pixivez.networks.RestClient
import com.perol.asdpl.pixivez.networks.SharedPreferencesServices
import com.perol.asdpl.pixivez.objects.ThemeUtil
import com.perol.asdpl.pixivez.objects.Toasty
import com.perol.asdpl.pixivez.repository.AppDataRepository
import com.perol.asdpl.pixivez.responses.ErrorResponse
import com.perol.asdpl.pixivez.responses.PixivOAuthResponse
import com.perol.asdpl.pixivez.services.OAuthSecureService
import com.perol.asdpl.pixivez.services.Works
import com.perol.asdpl.pixivez.sql.UserEntity
import io.noties.markwon.Markwon
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import java.util.*

class LoginActivity : RinkActivity() {
    private var username: String? = null
    private var password: String? = null
    private var sharedPreferencesServices: SharedPreferencesServices? = null
    val markdownShot = "# TroubleShoot help \\ n \"+\n" +
            "            \"If you are prompted \\ n\" when you log in +\n" +
            "            \"` `` shell \\ n \"+\n" +
            "            \"http 400 bad request \\ n\" +\n" +
            "            \"` `` \\ n \"+\n" +
            "            \"Try the following steps \\ n\" +\n" +
            "            \"1. Make sure the application is up to date, go to google play or github to get the latest version \\ n\" +\n" +
            "            \"2. It is the latest version, the username or password is wrong (104 :), you need to check the account password \\ n\" +\n" +
            "            \"3. The account is not verified or the user account is invalid (103 :), you need to go to the official website for verification and improvement \\ n\" +\n" +
            "            \"ps: account password refers to pixiv account password, not github \\ n\" +\n" +
            "            \"\\ n\" +\n" +
            "            \"\\ n\" +\n" +
            "            \"If prompted after login \\ n\" +\n" +
            "            \"` `` shell \\ n \"+\n" +
            "            \"Program error, exiting soon, xx resourece not found \\ n\" +\n" +
            "            \"` `` \\ n \"+\n" +
            "            \"1. Make sure the application is up to date, go to google play or github to get the latest version \\ n\" +\n" +
            "            \"2. Make sure the application is obtained by google play or github, and not passed by others or even dalao \\ n\" +\n" +
            "            \"3. After installing the latest version to the above channels, clear the app data and try to log in again \\ n\" +\n" +
            "            \"\\ n\" +\n" +
            "            \"## If it doesn't help \\ n\" +\n" +
            "            \"Please feedback the version number, system information, and error message screenshot through the feedback mailbox \\ n\" +\n" +
            "            \"Before feedback, you need to make sure \\ n\" +\n" +
            "            \"1. The application is obtained by google play or github \\ n\" +\n" +
            "            \"2. Not using the early adopter system or using xposed or magisk modules to make magic changes \\ n\" +\n" +
            "            \"3. Read it carefully \\\" Please be sure to read it \\"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ThemeUtil.themeInit(this)
        setContentView(R.layout.activity_login)
        val window = window
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = Color.TRANSPARENT

        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        initbind()

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_login, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                val intent = Intent(this, SettingActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun initbind() {
        sharedPreferencesServices = SharedPreferencesServices.getInstance()
        try {

            if (sharedPreferencesServices!!.getString("password") != null) {
                edit_password!!.setText(sharedPreferencesServices!!.getString("password"))
                edit_username!!.setText(sharedPreferencesServices!!.getString("username"))
            }
            if (!sharedPreferencesServices!!.getBoolean("firstinfo")) {
                val normalDialog = MaterialAlertDialogBuilder(this)
                normalDialog.setMessage(
                    "0. Please make sure to install and update in google play or project address. " +
                            "Installation packages provided by third parties may have problems and " +
                            "are not up-to-date \\ n1. Long press on the picture details page to save" +
                            " the selected picture, long press the avatar to quickly follow the author" +
                            ", Please provide application permissions \\ n2. When browsing the animation," +
                            " click the 0% progress bar in the middle to start downloading. After" +
                            " finishing playing, press and hold to save the composition. The memory" +
                            " overhead of the composition process is quite large, and occasional crashes" +
                            " are unavoidable \\ n3. If the animation cannot be played Play, please " +
                            "exit the page or clear the cache and try again. This will generally work/" +
                            "\\4. This is a personally developed application." +
                            " Please send an email to the mailbox marked on the settings page for feedback." +
                            " Personal energy and ability are limited. Please do not use extreme methods for" +
                            " feedback. Be considerate of developers and welcome joint development and design. " +
                            "\\ N5. If you encounter flashback issues after the update, please try to clear " +
                            "the application data and update to the latest version, and feedback the error " +
                            "message or log to the developer. In most cases, this is valid. \\ N6. " +
                            "Limit the master switch on the official website. The illustrations without" +
                            " permission to access are opened on the website by themselves. The developers " +
                            "do not provide help services.  "
                )
                normalDialog.setTitle("Be sure to read")
                normalDialog.setPositiveButton(
                    "I accept"
                ) { _, _ ->
                    sharedPreferencesServices!!.setBoolean("firstinfo", true)
                }
                normalDialog.show()
            }
        } catch (e: Exception) {

        }
        val restClient = RestClient()
        val oAuthSecureService = restClient.getretrofit_OAuthSecure().create(OAuthSecureService::class.java)
        textview_help.setOnClickListener {
            val builder = MaterialAlertDialogBuilder(this)
            val view = layoutInflater.inflate(R.layout.new_dialog_user_help, null)
            val webView = view.findViewById(R.id.web_user_help) as TextView
            // obtain an instance of Markwon
            val markwon = Markwon.create(this);

            val node = markwon.parse(markdownShot);

            val markdown = markwon.render(node);

            // use it on a TextView
            markwon.setParsedMarkdown(webView, markdown);
            builder.setPositiveButton(android.R.string.ok) { _, _ ->

            }
            builder.setView(view)
            builder.create().show()
        }

        edit_username.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // Ignore.
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Ignore.
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                accountTextInputLayout.isErrorEnabled = false
            }
        })
        edit_password.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // Ignore.
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Ignore.
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                passwordTextInputLayout.isErrorEnabled = false
            }
        })

        loginBtn!!.setOnClickListener {
//            loginBtn.isClickable = false

            username = edit_username!!.text.toString()
            password = edit_password!!.text.toString()

            if (username.isNullOrBlank()) accountTextInputLayout.error = getString(R.string.error_blank_account)

            if (password.isNullOrBlank()) passwordTextInputLayout.error =
                getString(R.string.error_blank_password)

            if (username.isNullOrBlank() || password.isNullOrBlank()) {
                return@setOnClickListener
            }

            loginBtn.isEnabled = false

            sharedPreferencesServices!!.setString("username", username)
            sharedPreferencesServices!!.setString("password", password)
            val map = HashMap<String, Any>()
            map["client_id"] = "MOBrBDS8blbauoSck0ZfDbtuzpyT"
            map["client_secret"] = "lsACyCD94FhDUtGTXi3QzcFE2uU1hqtDaKeqrdwj"
            map["grant_type"] = "password"
            map["username"] = username!!
            map["password"] = password!!
            if (sharedPreferencesServices!!.getString("Device_token") != null) {
                map["device_token"] = sharedPreferencesServices!!.getString("Device_token")
            } else map["device_token"] = "pixiv"

            map["get_secure_url"] = true
            map["include_policy"] = true

            oAuthSecureService.postAuthToken(map).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread()).subscribe(object : Observer<PixivOAuthResponse> {
                        override fun onSubscribe(d: Disposable) {
                            Toast.makeText(applicationContext, getString(R.string.try_to_login), Toast.LENGTH_SHORT).show()
                        }

                        override fun onNext(pixivOAuthResponse: PixivOAuthResponse) {
                            val user = pixivOAuthResponse.response.user
                            GlobalScope.launch {
                                AppDataRepository.insertUser(
                                    UserEntity(
                                        user.profile_image_urls.px_170x170,
                                        user.id.toLong(),
                                        user.name,
                                        user.mail_address,
                                        user.isIs_premium,
                                        pixivOAuthResponse.response.device_token, pixivOAuthResponse.response.refresh_token, "Bearer " + pixivOAuthResponse.response.access_token
                                ))

                                sharedPreferencesServices!!.setBoolean("isnone", false)
                                sharedPreferencesServices!!.setString("username", username)
                                sharedPreferencesServices!!.setString("password", password)

                            }
                        }

                        override fun onError(e: Throwable) {
//                            loginBtn.isClickable = true
                            loginBtn.isEnabled = true

                            textview_help.visibility = View.VISIBLE
                            if (e is HttpException) {
                                try {
                                    val errorBody = e.response()?.errorBody()?.string()
                                    val gson = Gson()
                                    val errorResponse = gson.fromJson<ErrorResponse>(errorBody, ErrorResponse::class.java)

                                    var errMsg = "${e.message}\n${errorResponse.errors.system.message}"

                                    if (errorResponse.has_error && errorResponse.errors.system.message.contains(Regex(""".*103:.*"""))) {
                                        errMsg = getString(R.string.error_invalid_account_password)
                                    }

                                    Toast.makeText(applicationContext, errMsg, Toast.LENGTH_LONG).show()
                                } catch (e1: IOException) {
                                    Toast.makeText(applicationContext, "${e.message}", Toast.LENGTH_LONG).show()
                                }

                            } else if (e.message!!.contains("400") && !e.message!!.contains("oauth")) {
                                Toast.makeText(applicationContext, "${e.message}", Toast.LENGTH_LONG).show()
                            } else {
                                Toast.makeText(applicationContext, "${e.message}", Toast.LENGTH_LONG).show()
                            }
                        }

                        override fun onComplete() {
//                            loginBtn.isClickable = true
//                            loginBtn.isEnabled = true // Avoid double logins.

                            Toast.makeText(applicationContext, "Login success", Toast.LENGTH_LONG)
                                .show()
                            val intent = Intent(this@LoginActivity, HelloMActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    })
        }
        Works.checkUpdate(this)
    }

    fun showHelp() {
//        val intent = Intent(this@LoginActivity, NewUserActivity::class.java)
//        startActivity(intent)
        Toasty.info(this, this.resources.getString(R.string.registerclose), Toast.LENGTH_LONG).show()
    }
}
