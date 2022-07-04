package br.com.android.chatapp.ui.authscreen.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import br.com.android.chatapp.data.models.UserLoginModel
import br.com.android.chatapp.databinding.FragmentLoginBinding
import br.com.android.chatapp.ui.authscreen.AuthFragmentDirections
import br.com.android.chatapp.ui.util.navTo
import br.com.android.chatapp.ui.util.toast
import com.google.firebase.auth.FirebaseAuth

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private var _loginViewModel: LoginViewModel? = null
    private val loginViewModel get() = _loginViewModel!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        val viewModelFactory = LoginViewModelFactory()

        _loginViewModel = ViewModelProvider(
            this, viewModelFactory)[LoginViewModel::class.java]


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val edtEmail = binding.edtTextLogin
        val edtPassword = binding.edtTextPassword

        binding.buttonLogin.setOnClickListener {
            if ( !edtEmail.text.isNullOrEmpty()) {
                if ( !edtPassword.text.isNullOrEmpty()) {

                    val user = UserLoginModel(
                        edtEmail.text.toString(),
                        edtPassword.text.toString(),
                    )
                    loginViewModel.loginUser(user)
                } else {
                    toast("Preencha a senha!")
                }
            } else {
                binding.edtTextLogin.error = "Preencha o e-mail!"
            }
        }
        loginViewModel.navigateToMainScreen.observe(viewLifecycleOwner, Observer {
            if(it == true) {
                navTo(AuthFragmentDirections.actionAuthFragmentToMainScreenFragment())
                loginViewModel.doneNavigatingMS()
            }
        })

        loginViewModel.savedExceptLogin.observe(viewLifecycleOwner, Observer {
            if(!it.isNullOrEmpty()) {
                toast(it)
                loginViewModel.doneSaveExceptLogin()
            }
        })
    }
}