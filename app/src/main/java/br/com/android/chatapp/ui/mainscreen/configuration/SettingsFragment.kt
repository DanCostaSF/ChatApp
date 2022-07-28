package br.com.android.chatapp.ui.mainscreen.configuration

import android.os.Bundle
import android.text.Editable
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import br.com.android.chatapp.R
import br.com.android.chatapp.databinding.FragmentSettingsBinding
import br.com.android.chatapp.ui.mainscreen.configuration.bottomsheet.BottomSheetConfigFragment
import br.com.android.chatapp.ui.util.navBack
import br.com.android.chatapp.ui.util.toast
import com.squareup.picasso.Picasso
import java.lang.Exception

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private var _settingsViewModel: SettingsViewModel? = null
    private val settingsViewModel get() = _settingsViewModel!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        _binding = FragmentSettingsBinding.inflate(inflater, container, false)

        _settingsViewModel = ViewModelProvider(
            this,
            SettingsViewModelFactory()
        )[SettingsViewModel::class.java]

        binding.buttonUpdate.visibility = View.VISIBLE
        settingsViewModel.setDataUser()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbarConfiguration)

        val bottomSheetFragment = BottomSheetConfigFragment()

        binding.buttonUpdate.setOnClickListener {
            buttonUpdateClick()
        }

        binding.buttonSave.setOnClickListener {
            buttonSaveClick()
        }

        binding.addImage.setOnClickListener {
            bottomSheetFragment.show(requireActivity().supportFragmentManager, "BottomSheetDialog")
        }

        binding.btnBack.setOnClickListener {
            navBack()
        }

        settingsViewModel.userConf.observe(viewLifecycleOwner) { user ->
            user?.let {
                binding.textViewName.text = it[0].profileName
                binding.textViewStatus.text = it[0].profileStatus
                binding.textViewEmail.text = it[0].profileEmail

                try {
                    Picasso.get()
                        .load(it[0].profilePhoto)
                        .error(R.drawable.padrao)
                        .into(binding.profileImage)
                } catch (e: Exception) {
                    toast("Erro ao atualizar a foto, tente novamente.")
                }
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.settings_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }


    private fun buttonSaveClick() {
        binding.textViewName.visibility = View.VISIBLE
        binding.textViewEmail.visibility = View.VISIBLE
        binding.textViewStatus.visibility = View.VISIBLE

        binding.edtLayoutName.visibility = View.GONE
        binding.edtLayoutStatus.visibility = View.GONE

        binding.buttonSave.visibility = View.GONE
        binding.buttonUpdate.visibility = View.VISIBLE



        settingsViewModel.buttonSave(
            binding.edtProfileName.text.toString(),
            binding.textViewEmail.text.toString(),
            binding.edtProfileStatus.text.toString()
        )

        binding.textViewName.text = binding.edtProfileName.text
        binding.textViewStatus.text = binding.edtProfileStatus.text
    }

    private fun buttonUpdateClick() {
        binding.textViewName.visibility = View.GONE
        binding.textViewEmail.visibility = View.GONE
        binding.textViewStatus.visibility = View.GONE

        binding.edtLayoutName.visibility = View.VISIBLE
        binding.edtLayoutStatus.visibility = View.VISIBLE

        binding.buttonSave.visibility = View.VISIBLE
        binding.buttonUpdate.visibility = View.GONE

        binding.edtProfileName.text = Editable.Factory.getInstance().newEditable(
            binding.textViewName.text.toString()
        )

        binding.edtProfileStatus.text = Editable.Factory.getInstance().newEditable(
            binding.textViewStatus.text.toString()
        )
    }

}