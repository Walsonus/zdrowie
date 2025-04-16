package pack.zdrowie

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputLayout
import pack.zdrowie.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // View binding initialization
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Edge-to-edge handling
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupViews()
        registerButton()
    }

    private fun setupViews() {
        // Login button click listener
        binding.loginButton.setOnClickListener {
            if (validateForm()) {
                performLogin()
            }
        }
    }

    // validateForm checks if user gave correct login data
    private fun validateForm(): Boolean {
        return true
    }

    // performLogin makes user logged in and changes activity
    private fun performLogin() {
        val userEmail = binding.emailLayout.editText?.text.toString().trim()
        val userPassword = binding.passwordLayout.editText?.text.toString().trim()

        // Launch a coroutine for asynchronous database operations
        lifecycleScope.launch {
            val user = userDAO.getUserByMail(userEmail)
            if (user != null) {
                if (user.userPassword == userPassword) {
                    // TODO: Change the target Activity to the correct one when ready
                    /*
                        val intent = Intent(this@LoginActivity, MainPage::class.java).apply {
                            putExtra("UserID", user.userId)
                        }
                        startActivity(intent)
                        finish()
                    */
                    // TODO: Consider removing this Toast after implementing the proper transition
                    Toast.makeText(this@LoginActivity, getString(R.string.user_logged), Toast.LENGTH_SHORT).show()
                } else {
                    // Inform the user about the incorrect password
                    Toast.makeText(this@LoginActivity, getString(R.string.wrong_password), Toast.LENGTH_SHORT).show()
                    return@launch
                }
            } else {
                // Inform the user that the provided email does not exist
                Toast.makeText(this@LoginActivity, getString(R.string.wrong_email), Toast.LENGTH_SHORT).show()
                return@launch
            }
        }
    }
    // function needed to get private variable to use in tests
    fun getBinding(): ActivityLoginBinding {
        return binding
    }

    private fun registerButton() {
        binding.registerText.setOnClickListener {
            window.setBackgroundDrawableResource(R.color.background_navy)
            val intent = Intent(this, RegisterActivity::class.java) // Should go to Home, not RegisterActivity


            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this).toBundle()

            startActivity(intent, options)
        }

    }
}

