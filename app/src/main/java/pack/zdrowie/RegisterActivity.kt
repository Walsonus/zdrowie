package pack.zdrowie

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.ViewCompat

import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import pack.zdrowie.database.AppDatabase
import pack.zdrowie.database.DatabaseProvider
import pack.zdrowie.database.entities.User
import pack.zdrowie.databinding.ActivityLoginBinding
import pack.zdrowie.databinding.ActivityRegisterBinding
import java.time.LocalDate

/**
 * Activity responsible for registering new users.
 * Handles validation of input data and storing users in the database.
 */
class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    lateinit var database: AppDatabase

    /**
     * Called when the activity is created.
     * Initializes components, visual configurations, and the database connection.
     *
     * @param savedInstanceState The state bundle provided by Android during Activity creation.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configure modern edge-to-edge UI for API 30+
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Legacy insets handling for devices with API <30
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize the database connection
        database = DatabaseProvider.getDatabase(this)

        // Set up the register button action
        binding.loginButton.setOnClickListener { registerUser() }
    }

    /**
     * Handles the process of registering a new user.
     * Validates the input form, checks if the email is unique, and stores the user in the database.
     */
    private fun registerUser() {
        lifecycleScope.launch {
            val email = binding.emailLayout.editText?.text?.toString().orEmpty()
            val pass = binding.passwordLayout.editText?.text?.toString().orEmpty()
            val pass2 = binding.password2Layout.editText?.text?.toString().orEmpty()

            if (!isFormValid(email, pass, pass2)) return@launch
            if (isEmailUsed(email)) {
                Toast.makeText(this@RegisterActivity, getString(R.string.email_used), Toast.LENGTH_SHORT).show()
                return@launch
            }

            val userCount = database.userDao().getCount()
            val newUser = User(
                userId = 0,
                userName = "user$userCount",
                userMail = email,
                userPassword = pass,
                userWeight = 0.0f,
                userHeight = 0.0f,
                userDateOfBirth = LocalDate.of(2000, 1, 1)
            )

            try {
                database.userDao().insert(newUser)
                Toast.makeText(this@RegisterActivity, getString(R.string.user_registered), Toast.LENGTH_SHORT).show()
                finish() // Close the Activity after successful registration
            } catch (e: Exception) {
                Toast.makeText(this@RegisterActivity, getString(R.string.error_adding_user), Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Validates the input data provided by the user in the registration form.
     *
     * @param email The user's email.
     * @param pass The user's password.
     * @param pass2 The confirmation of the user's password.
     * @return true if the form data is valid; false otherwise.
     */
    private fun isFormValid(email: String, pass: String, pass2: String): Boolean {
        val emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$".toRegex()

        if (email.isBlank()) {
            Toast.makeText(this, getString(R.string.no_email), Toast.LENGTH_SHORT).show()
            return false
        }
        if (!emailRegex.matches(email)) {
            Toast.makeText(this, getString(R.string.wrong_email), Toast.LENGTH_SHORT).show()
            return false
        }
        if (pass.isBlank()) {
            Toast.makeText(this, getString(R.string.no_password), Toast.LENGTH_SHORT).show()
            return false
        }
        if (pass2.isBlank() || pass2 != pass) {
            Toast.makeText(this, getString(R.string.wrong_password), Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    /**
     * Checks if the user's email is already used in the database.
     *
     * @param email The email to check.
     * @return true if the email is already used; false otherwise.
     */
    private suspend fun isEmailUsed(email: String): Boolean {
        return database.userDao().getEmailCount(email) > 0
    }
}
    // function needed to get private variable to use in tests
    fun getBinding(): ActivityRegisterBinding {
        return binding
    }
}
