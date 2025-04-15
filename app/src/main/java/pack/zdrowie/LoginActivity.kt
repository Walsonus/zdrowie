package pack.zdrowie

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
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
 * LoginActivity handles the user login functionality.
 *
 * This Activity validates the login form inputs, communicates with the database
 * (using Room via the UserDAO), and triggers a login process. Upon successful login,
 * the user is navigated to the next screen (the target Activity name is subject to change).
 */
class LoginActivity : AppCompatActivity() {

    /** DAO instance for user operations */
    private lateinit var userDAO: UserDAO

    /** Instance of the Room database */
    private lateinit var dataBase: AppDatabase

    /** View binding instance for the activity layout */
    private lateinit var binding: ActivityLoginBinding

    /**
     * Called when the activity is starting.
     *
     * Initializes view binding, sets up edge-to-edge display, and initializes
     * the database and its DAO. It also sets up view listeners.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously
     *                           being shut down, then this Bundle contains the data it most
     *                           recently supplied.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Enable edge-to-edge mode for immersive layout
        enableEdgeToEdge()

        // Initialize view binding
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Apply padding to accommodate system windows (edge-to-edge handling)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize the database and obtain the UserDAO instance
        dataBase = DatabaseProvider.getDatabase(this)
        userDAO = dataBase.userDao()

        // Set up view-related configurations
        setupViews()
        registerButton()
    }

    /**
     * Sets up listeners for view elements.
     *
     * In particular, this function sets a click listener on the login button
     * which validates the login form and then performs the login operation if valid.
     */
    private fun setupViews() {
        // Set a click listener on the login button
        binding.loginButton.setOnClickListener {
            if (validateForm()) {
                performLogin()
            }
        }
    }

    /**
     * Validates the login form.
     *
     * This function checks if the user has entered both an email and a password.
     *
     * @return true if both email and password fields contain data; false otherwise.
     */
    private fun validateForm(): Boolean {
        val userEmail = binding.emailLayout.editText?.text.toString().trim()
        val userPassword = binding.passwordLayout.editText?.text.toString().trim()
        if (userEmail.isBlank() || userPassword.isBlank()) {
            // Inform the user that login data is missing
            Toast.makeText(this, getString(R.string.no_login_data), Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    /**
     * Performs the user login process.
     *
     * This function retrieves the user based on the email provided,
     * compares the retrieved password with the entered password, and if they match,
     * proceeds to log in the user. On success, it is intended to navigate the user
     * to the next Activity. (Currently, the navigation code is commented out.)
     */
    private fun performLogin() {
        val userEmail = binding.emailLayout.editText?.text.toString().trim()
        val userPassword = binding.passwordLayout.editText?.text.toString().trim()

        // Launch a coroutine for asynchronous database operations
        lifecycleScope.launch {
            val user = userDAO.getUserByMail(userEmail)
            if (user != null) {
                if (user.userPassword == userPassword) {
                    // TODO: Change the target Activity to the correct one when ready

                    val intent = Intent(this@LoginActivity, MainAppActivity::class.java).apply {
                        putExtra("UserID", user.userId)
                    }
                    startActivity(intent)
                    finish()

                    // TODO: Consider removing this Toast after implementing the proper transition
                    //Toast.makeText(this@LoginActivity, getString(R.string.user_logged), Toast.LENGTH_SHORT).show()
                    CustomToast.ShowSuccessToast(this@LoginActivity, false, getString(R.string.user_logged));
                } else {
                    // Inform the user about the incorrect password
                    //Toast.makeText(this@LoginActivity, getString(R.string.wrong_password), Toast.LENGTH_SHORT).show()
                    CustomToast.ShowErrorToast(this@LoginActivity,true,getString(R.string.wrong_password));
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

    /**
     * Registers the click listener for the register text.
     *
     * When the user clicks on the register text, the Activity transitions to the
     * RegisterActivity (or Home Activity as per future changes) with a scene transition animation.
     */
    private fun registerButton() {
        binding.registerText.setOnClickListener {
            // Set a background resource for the window during the transition
            window.setBackgroundDrawableResource(R.color.background_navy)
            // Create an intent for RegisterActivity (adjust as needed)
            val intent = Intent(this, RegisterActivity::class.java)
            // Create transition options for a smoother scene transition
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this).toBundle()
            // Start the RegisterActivity using the defined options
            startActivity(intent, options)
        }
    }
}