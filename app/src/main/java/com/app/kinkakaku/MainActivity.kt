package com.app.kinkakaku

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.app.kinkakaku.localization.LanguageManager
import com.app.kinkakaku.navigation.KinKakakuNavigation
import com.app.kinkakaku.ui.theme.KinKakakuTheme

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        LanguageManager.applySavedLanguage(this)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            KinKakakuTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    KinKakakuNavigation()
                }
            }
        }
    }
}