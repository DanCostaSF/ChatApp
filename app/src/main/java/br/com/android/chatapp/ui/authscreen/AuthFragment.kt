package br.com.android.chatapp.ui.authscreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import br.com.android.chatapp.databinding.FragmentAuthBinding
import br.com.android.chatapp.ui.authscreen.login.LoginFragment
import br.com.android.chatapp.ui.authscreen.register.RegisterFragment
import br.com.android.chatapp.ui.util.navTo
import br.com.android.chatapp.ui.viewpager.ViewPagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class AuthFragment : Fragment() {

    private var _binding: FragmentAuthBinding? = null
    private val binding get() = _binding!!

    private var _authViewModel: AuthViewModel? = null
    private val authViewModel get() = _authViewModel!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAuthBinding.inflate(inflater, container, false)

        _authViewModel = ViewModelProvider(
            this, AuthViewModelFactory())[AuthViewModel::class.java]

        configTabLayout()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        authViewModel.navigateToMainScreen.observe(viewLifecycleOwner) {
            if ( it == true ) {
                navTo(
                    AuthFragmentDirections.
                    actionAuthFragmentToMainScreenFragment()
                )
                authViewModel.doneNavigation()
            }
        }
    }

    private fun configTabLayout() {
        val adapter = ViewPagerAdapter(this)
        binding.viewPager.adapter = adapter

        adapter.addFragment(LoginFragment(), "Login")
        adapter.addFragment(RegisterFragment(), "Register")

        binding.viewPager.offscreenPageLimit = adapter.itemCount

        val mediator = TabLayoutMediator(
            binding.tabLayout, binding.viewPager
        ) { tab: TabLayout.Tab, position: Int ->
            tab.text = adapter.getTitle(
                position
            )
        }
        mediator.attach()
    }

    override fun onStart() {
        super.onStart()
        authViewModel.userIsOn()
    }
}