package pack.zdrowie

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import pack.zdrowie.databinding.DialogAddSupplementBinding
import pack.zdrowie.databinding.FragmentSupplementsBinding

/**
 * Fragment responsible for managing user's supplements.
 *
 * <p>This fragment provides:
 * <ul>
 *   <li>Display of user's supplement list</li>
 *   <li>Add new supplement functionality</li>
 *   <li>Supplement management interface</li>
 * </ul>
 *
 * <p>Requires user ID to be passed via arguments bundle under key "UserID".
 */
class SupplementsFragment : Fragment() {

    /**
     * View binding instance. Should be nullified in onDestroyView() to prevent memory leaks.
     */
    private var _binding: FragmentSupplementsBinding? = null

    /**
     * Non-null accessor for view binding.
     * @throws IllegalStateException if accessed when binding is null
     */
    private val binding get() = _binding!!

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
        _binding = FragmentSupplementsBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * Called immediately after onCreateView().
     * Configures UI components and sets up event listeners.
     * @param view The created view
     * @param savedInstanceState Saved state from previous instance
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAddSupplementButton()
    }

    /**
     * Sets up the floating action button for adding new supplements.
     */
    private fun setupAddSupplementButton() {
        binding.addSupplementFab.setOnClickListener {
            showAddSupplementDialog()
        }
    }

    /**
     * Displays dialog for adding new supplement.
     *
     * <p>The dialog contains:
     * <ul>
     *   <li>Supplement name input field</li>
     *   <li>Amount input field</li>
     *   <li>Add/Cancel buttons</li>
     * </ul>
     */
    private fun showAddSupplementDialog() {
        val dialogBinding = DialogAddSupplementBinding.inflate(layoutInflater)

        AlertDialog.Builder(requireContext())
            .setTitle("Dodaj suplement")
            .setView(dialogBinding.root)
            .setPositiveButton("Dodaj") { _, _ ->
                handleSupplementAddition(
                    dialogBinding.supplementNameEditText.text.toString(),
                    dialogBinding.supplementAmountEditText.text.toString()
                )
            }
            .setNegativeButton("Anuluj", null)
            .show()
    }

    /**
     * Handles the addition of new supplement.
     * @param name Name of the supplement
     * @param amount Amount/dosage of the supplement
     */
    private fun handleSupplementAddition(name: String, amount: String) {
        // TODO: Implement supplement persistence
        // Will need to:
        // 1. Validate inputs
        // 2. Create supplement entity
        // 3. Save to database
        // 4. Update UI
    }

    /**
     * Cleans up view binding references when view is destroyed.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        /**
         * Factory method to create new SupplementsFragment instance with user ID.
         * @param userId The ID of the user
         * @return New SupplementsFragment instance with arguments set
         */
        fun newInstance(userId: Int): SupplementsFragment {
            return SupplementsFragment().apply {
                arguments = Bundle().apply {
                    putInt("UserID", userId)
                }
            }
        }
    }
}