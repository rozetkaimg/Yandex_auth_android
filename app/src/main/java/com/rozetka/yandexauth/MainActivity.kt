package com.rozetka.yandexauth

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import com.rozetka.yandexauth.ui.theme.YandexAuthTheme
import com.yandex.authsdk.YandexAuthException
import com.yandex.authsdk.YandexAuthLoginOptions
import com.yandex.authsdk.YandexAuthOptions
import com.yandex.authsdk.YandexAuthSdk

class MainActivity : ComponentActivity() {
    val TokenRes: MutableState<String> = mutableStateOf("")
    val REQUEST_LOGIN_SDK = 1
    lateinit var sdk: YandexAuthSdk
    lateinit var context_: Context
    fun CopyToken(context: Context) {
        val clipboardManager =
            context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Token", TokenRes.value)
        clipboardManager.setPrimaryClip(clip)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == REQUEST_LOGIN_SDK) {

            try {
                val yandexAuthToken = sdk.extractToken(resultCode, data)
                yandexAuthToken?.let {
                    if (it.value != null) {
                        TokenRes.value = it.value

                    }
                }

            } catch (e: YandexAuthException) {

            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context_ = this
        setContent {
            sdk = YandexAuthSdk(
                LocalContext.current, YandexAuthOptions(LocalContext.current, true, 0)
            )
            YandexAuthTheme {
                Box(Modifier.fillMaxSize()) {


                    Column(
                        Modifier
                            .align(Alignment.Center)
                            .fillMaxWidth()
                    ) {
                        Text(text = TokenRes.value, Modifier.align(Alignment.CenterHorizontally))

                        Button(onClick = {
                            ActivityCompat.startActivityForResult(
                                this@MainActivity as Activity,
                                sdk.createLoginIntent(YandexAuthLoginOptions.Builder().build()),
                                REQUEST_LOGIN_SDK,
                                null
                            )
                        }, Modifier.align(Alignment.CenterHorizontally)) {
                            Text(text = "Войти")
                        }

                        if (TokenRes.value != "") {
                            Button(
                                onClick = { CopyToken(context = context_) },
                                Modifier.align(Alignment.CenterHorizontally)
                            ) {
                                Text(text = "Скопировать")
                            }
                        }
                    }
                }
            }
        }
    }
}

