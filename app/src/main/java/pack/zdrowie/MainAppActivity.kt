package pack.zdrowie

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import pack.zdrowie.databinding.ActivityMainAppBinding

class MainAppActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        var binding = ActivityMainAppBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setContentView(R.layout.activity_main_app)
        ViewCompat.setOnApplyWindowInsetsListener(binding.mainAppActivity) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}