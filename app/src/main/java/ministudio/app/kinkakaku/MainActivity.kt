package ministudio.app.kinkakaku

import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import ministudio.app.kinkakaku.localization.LanguageManager
import ministudio.app.kinkakaku.navigation.KinKakakuNavigation
import ministudio.app.kinkakaku.ui.theme.KinKakakuTheme

class MainActivity : AppCompatActivity() {
    private var lastBackPressedAt = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        LanguageManager.applySavedLanguage(this)
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val now = System.currentTimeMillis()
                if (now - lastBackPressedAt < 2000L) {
                    finishAffinity()
                } else {
                    lastBackPressedAt = now
                    Toast.makeText(
                        this@MainActivity,
                        getString(R.string.press_back_again_to_exit),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })

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