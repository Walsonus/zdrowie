package pack.zdrowie

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import pack.zdrowie.databinding.DialogAddSupplementBinding
import pack.zdrowie.databinding.FragmentSupplementsBinding

class SupplementsFragment : Fragment() {
    private var _binding: FragmentSupplementsBinding? = null
    private val binding get() = _binding!!
    private var userId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            userId = it.getInt("UserID", -1)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSupplementsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.addSupplementFab.setOnClickListener {
            showAddSupplementDialog()
        }
    }

    private fun showAddSupplementDialog() {
        val dialogBinding = DialogAddSupplementBinding.inflate(layoutInflater)

        // Budowanie dialogu
        AlertDialog.Builder(requireContext())
            .setTitle("Dodaj suplement")
            .setView(dialogBinding.root)
            .setPositiveButton("Dodaj") { _, _ ->
                // Tutaj będzie obsługa dodawania
                val name = dialogBinding.supplementNameEditText.text.toString()
                val amount = dialogBinding.supplementAmountEditText.text.toString()
                //val frequency = dialogBinding.supplementFrequencyAutoComplete.text.toString()

                // TODO: Zapisz dane suplementu
            }
            .setNegativeButton("Anuluj", null)
            .show()
    }

}