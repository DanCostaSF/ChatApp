package br.com.android.chatapp.ui.authscreen.register

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import br.com.android.chatapp.databinding.FragmentRegisterBinding
import br.com.android.chatapp.ui.authscreen.AuthFragmentDirections
import br.com.android.chatapp.ui.util.navTo
import br.com.android.chatapp.ui.util.toast

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private var _registerViewModel: RegisterViewModel? = null
    private val registerViewModel get() = _registerViewModel!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding =  FragmentRegisterBinding.inflate(inflater, container, false)

        val viewModelFactory = RegisterViewModelFactory()

        _registerViewModel = ViewModelProvider(
            this, viewModelFactory)[RegisterViewModel::class.java]

        registerViewModel.initialization()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonRegister.setOnClickListener {
            registerClick()
        }

        registerViewModel.progressBar.observe(viewLifecycleOwner) {
            if(it == true) {
                binding.signUpprogressBar.visibility = View.GONE
                registerViewModel.doneProgressBar()
            }
        }

        registerViewModel.savedExcepRegister.observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty()) {
                toast(it)
                registerViewModel.doneSaveExceptRegister()
            }
        }

        registerViewModel.navigateToMainScreen.observe(viewLifecycleOwner){
            if ( it == true ) {
                navTo(
                    AuthFragmentDirections.
                    actionAuthFragmentToMainScreenFragment()
                )
                registerViewModel.doneNavigation()
            }
        }

    }

    private fun registerClick() {
        val email = binding.edtTextEmail.text.toString()
        val password = binding.edtTextPassword.text.toString()
        val confirmPass = binding.edtTextConfirmPassword.text.toString()
        if(TextUtils.isEmpty(email)) {
            binding.edtTextEmail.error = "Digite um e-mail"
        } else if(TextUtils.isEmpty(password)) {
            binding.edtTextPassword.error = "Digite uma senha"
        }  else if (password != confirmPass) {
            binding.edtTextConfirmPassword.error = "As senhas não batem!"
        } else {
            binding.signUpprogressBar.visibility = View.VISIBLE
            registerViewModel.createAccount(email, password)
        }
    }
}