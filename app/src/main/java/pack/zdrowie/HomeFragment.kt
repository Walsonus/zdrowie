package pack.zdrowie

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import pack.zdrowie.database.DatabaseProvider

class HomeFragment : Fragment() {
    private var userId: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Odczytujemy przekazany userID przez Bundle
        arguments?.let {
            userId = it.getInt("UserID", -1)
        }
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Znajdujemy TextView, w którym wyświetlimy powitanie
        val welcomeTextView = view.findViewById<TextView>(R.id.welcomeText)

        if (userId != -1) {
            val appDatabase = DatabaseProvider.getDatabase(requireContext())
            val userDAO = appDatabase.userDao()

            viewLifecycleOwner.lifecycleScope.launch {
                val user = userDAO.getUserById(userId)
                if (user != null) {
                    val welcomeMessage = getString(R.string.welcome, user.userMail)
                    welcomeTextView.text = welcomeMessage
                } else {
                    welcomeTextView.text = getString(R.string.welcome, "User1")
                }
            }
        } else {
            welcomeTextView.text = getString(R.string.welcome, "User")
        }
    }
}
