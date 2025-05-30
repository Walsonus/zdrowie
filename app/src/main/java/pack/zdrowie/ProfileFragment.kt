package pack.zdrowie

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import pack.zdrowie.database.DatabaseProvider
import pack.zdrowie.databinding.FragmentProfileBinding
import androidx.core.content.edit

/**
 * Fragment responsible for displaying user profile and theme settings.
 *
 * <p>Key responsibilities include:
 * <ul>
 *   <li>Displaying authenticated user's email</li>
 *   <li>Providing theme switching functionality (dark/light mode)</li>
 *   <li>Persisting theme preferences across app sessions</li>
 * </ul>
 *
 * <p>Requires user ID to be passed via arguments bundle under key "UserID".
 */
class ProfileFragment : Fragment() {

    /**
     * View binding instance. Should be nullified in onDestroyView() to prevent memory leaks.
     * @see onDestroyView
     */
    private var _binding: FragmentProfileBinding? = null

    /**
     * Non-null accessor for view binding.
     * @throws IllegalStateException if accessed when binding is null
     */
    private val binding get() = _binding!!

    /**
     * SharedPreferences instance for persisting theme preferences.
     */
    private lateinit var sharedPreferences: SharedPreferences

    /**
     * Current user ID loaded from fragment arguments.
     * Value of -1 indicates no user ID was provided.
     */
    private var userId: Int = -1

    /**
     * Initializes fragment components.
     * @param savedInstanceState If non-null, fragment is being re-created from saved state.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = requireContext().getSharedPreferences("AppPreferences", 0)
        arguments?.let {
            userId = it.getInt("UserID", -1)
        }
    }

    /**
     * Creates and returns the view hierarchy for this fragment.
     * @param inflater LayoutInflater to inflate views
     * @param container Parent view group (may be null)
     * @param savedInstanceState Saved state from previous instance
     * @return Root view of the fragment
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * Called immediately after onCreateView().
     * Configures UI components and loads user data.
     * @param view The created view
     * @param savedInstanceState Saved state from previous instance
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (userId != -1) {
            loadUserData()
        } else {
            binding.userEmail.text = "Nieznany użytkownik"
            Toast.makeText(requireContext(), "Błąd: brak ID użytkownika", Toast.LENGTH_SHORT).show()
        }

        updateThemeButtonText()
        binding.themeToggleButton.setOnClickListener { toggleTheme() }
    }

    /**
     * Loads user data from database asynchronously.
     * Uses viewLifecycleOwner to ensure safe coroutine lifecycle.
     */
    private fun loadUserData() {
        viewLifecycleOwner.lifecycleScope.launch {
            val appDatabase = DatabaseProvider.getDatabase(requireContext())
            val userDAO = appDatabase.userDao()
            val user = userDAO.getUserById(userId)

            user?.let {
                binding.userEmail.text = it.userMail
            }
        }
    }

    /**
     * Updates theme toggle button text based on current theme.
     * Reads preference from SharedPreferences.
     */
    private fun updateThemeButtonText() {
        val currentNightMode = sharedPreferences.getInt("NightMode", AppCompatDelegate.MODE_NIGHT_NO)
        binding.themeToggleButton.text = if (currentNightMode == AppCompatDelegate.MODE_NIGHT_YES) {
            "Zmień tryb na jasny"
        } else {
            "Zmień tryb na ciemny"
        }
    }

    /**
     * Toggles between dark and light theme modes.
     * Updates SharedPreferences and applies new theme globally.
     */
    private fun toggleTheme() {
        val currentNightMode = sharedPreferences.getInt("NightMode", AppCompatDelegate.MODE_NIGHT_NO)
        val newNightMode = if (currentNightMode == AppCompatDelegate.MODE_NIGHT_YES) {
            AppCompatDelegate.MODE_NIGHT_NO
        } else {
            AppCompatDelegate.MODE_NIGHT_YES
        }

        sharedPreferences.edit() { putInt("NightMode", newNightMode) }
        AppCompatDelegate.setDefaultNightMode(newNightMode)
        updateThemeButtonText()
    }

    /**
     * Cleans up view binding references when view is destroyed.
     * Prevents memory leaks.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        /**
         * Factory method to create new ProfileFragment instance with user ID.
         * @param userId The ID of the user to display
         * @return New ProfileFragment instance with arguments set
         */
        fun newInstance(userId: Int): ProfileFragment {
            return ProfileFragment().apply {
                arguments = Bundle().apply {
                    putInt("UserID", userId)
                }
            }
        }
    }
}