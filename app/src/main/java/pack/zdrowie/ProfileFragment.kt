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

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var sharedPreferences: SharedPreferences
    private var userId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = requireContext().getSharedPreferences("AppPreferences", 0)
        arguments?.let {
            userId = it.getInt("UserID", -1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

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

    private fun updateThemeButtonText() {
        val currentNightMode = sharedPreferences.getInt("NightMode", AppCompatDelegate.MODE_NIGHT_NO)
        binding.themeToggleButton.text = if (currentNightMode == AppCompatDelegate.MODE_NIGHT_YES) {
            "Zmień tryb na jasny"
        } else {
            "Zmień tryb na ciemny"
        }
    }

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(userId: Int): ProfileFragment {
            return ProfileFragment().apply {
                arguments = Bundle().apply {
                    putInt("UserID", userId)
                }
            }
        }
    }
}