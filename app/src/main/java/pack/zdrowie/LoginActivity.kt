package pack.zdrowie

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.sadadnan.customtoastlib.CustomToast
import kotlinx.coroutines.launch
import pack.zdrowie.database.AppDatabase
import pack.zdrowie.database.DatabaseProvider
import pack.zdrowie.database.dao.UserDAO
import pack.zdrowie.databinding.ActivityLoginBinding

/**
 * Handles user authentication and login flow.
 *
 * This activity provides:
 * - Email/password validation
 * - Database authentication via Room
 * - Secure credential handling
 * - Theme configuration (dark/light mode)
 * - Smooth activity transitions
 * - Edge-to-edge display support
 *
 * Flow:
 * 1. User enters credentials
 * 2. System validates input format
 * 3. Credentials are checked against database
 * 4. On success: User is redirected to MainAppActivity
 * 5. On failure: Appropriate error messages are shown
 */
class LoginActivity : AppCompatActivity() {

    private lateinit var userDAO: UserDAO
    private lateinit var dataBase: AppDatabase
    private lateinit var binding: ActivityLoginBinding

    /**
     * Initializes activity components and sets up UI.
     *
     * @param savedInstanceState Persisted state from previous instance
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        configureWindow()
        initializeBinding()
        setupDatabase()
        configureUI()
    }

    /** Configures edge-to-edge display and theme settings */
    private fun configureWindow() {
        enableEdgeToEdge()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
    }

    /** Initializes view binding and sets content view */
    private fun initializeBinding() {
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    /** Initializes database components */
    private fun setupDatabase() {
        dataBase = DatabaseProvider.getDatabase(this)
        userDAO = dataBase.userDao()
    }

    /** Configures all UI elements and event listeners */
    private fun configureUI() {
        setupLoginButton()
        setupRegisterNavigation()
    }

    /** Sets up login button click handler */
    private fun setupLoginButton() {
        binding.loginButton.setOnClickListener {
            if (validateCredentials()) {
                authenticateUser()
            }
        }
    }

    /** Sets up register text click handler */
    private fun setupRegisterNavigation() {
        binding.registerText.setOnClickListener {
            navigateToRegister()
        }
    }

    /**
     * Validates user input credentials.
     *
     * @return true if both email and password are non-empty, false otherwise
     */
    private fun validateCredentials(): Boolean {
        val email = binding.emailLayout.editText?.text.toString().trim()
        val password = binding.passwordLayout.editText?.text.toString().trim()

        return when {
            email.isBlank() || password.isBlank() -> {
                showErrorToast(getString(R.string.no_login_data))
                false
            }
            else -> true
        }
    }

    /**
     * Authenticates user against database credentials.
     *
     * Uses coroutine for database operation to avoid UI freezing.
     * Shows appropriate feedback for success/failure cases.
     */
    private fun authenticateUser() {
        val email = binding.emailLayout.editText?.text.toString().trim()
        val password = binding.passwordLayout.editText?.text.toString().trim()

        lifecycleScope.launch {
            val user = userDAO.getUserByMail(email)

            when {
                user == null -> showErrorToast(getString(R.string.wrong_email))
                user.userPassword != password -> showErrorToast(getString(R.string.wrong_password), true)
                else -> handleSuccessfulLogin(user.userId)
            }
        }
    }

    /**
     * Handles successful authentication.
     *
     * @param userId ID of the authenticated user
     */
    private fun handleSuccessfulLogin(userId: Int) {
        showSuccessToast(getString(R.string.user_logged))
        startActivity(
            Intent(this, MainAppActivity::class.java).apply {
                putExtra("UserID", userId)
            }
        )
        finish()
    }

    /** Navigates to registration screen with transition animation */
    private fun navigateToRegister() {
        window.setBackgroundDrawableResource(R.color.background_navy)
        startActivity(
            Intent(this, RegisterActivity::class.java),
            ActivityOptionsCompat.makeSceneTransitionAnimation(this).toBundle()
        )
    }

    /**
     * Displays an error toast message.
     *
     * @param message The error message to display
     * @param longDuration Whether to show the toast for a long duration
     */
    private fun showErrorToast(message: String, longDuration: Boolean = false) {
        CustomToast.ShowErrorToast(this, longDuration, message)
    }

    /**
     * Displays a success toast message.
     *
     * @param message The success message to display
     * @param longDuration Whether to show the toast for a long duration
     */
    private fun showSuccessToast(message: String, longDuration: Boolean = false) {
        CustomToast.ShowSuccessToast(this, longDuration, message)
    }

    /**
     * Provides access to view binding for testing purposes.
     *
     * @return The activity's view binding instance
     */
    fun getBinding(): ActivityLoginBinding = binding
}