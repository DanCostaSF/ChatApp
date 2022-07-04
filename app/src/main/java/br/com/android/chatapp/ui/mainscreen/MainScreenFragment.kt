package br.com.android.chatapp.ui.mainscreen

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import br.com.android.chatapp.R
import br.com.android.chatapp.databinding.FragmentMainScreenBinding
import br.com.android.chatapp.ui.mainscreen.chats.ChatFragment
import br.com.android.chatapp.ui.mainscreen.contacts.ContactsFragment
import br.com.android.chatapp.ui.util.navTo
import br.com.android.chatapp.ui.util.toast
import br.com.android.chatapp.ui.viewpager.ViewPagerAdapter
import com.example.portifliozap.ui.mainscreen.MainScreenViewModel
import com.example.portifliozap.ui.mainscreen.MainScreenViewModelFactory
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MainScreenFragment : Fragment() {

    private var _binding: FragmentMainScreenBinding? = null
    private val binding get() = _binding!!

    private var _mainScreenViewModel: MainScreenViewModel? = null
    private val mainScreenViewModel get() = _mainScreenViewModel!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainScreenBinding.inflate(inflater, container, false)
        val viewModelFactory = MainScreenViewModelFactory()

        _mainScreenViewModel = ViewModelProvider(
            this, viewModelFactory)[MainScreenViewModel::class.java]

        configTabLayout()

//        binding.fabContacts.setOnClickListener {}


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_search -> {

                true
            }
            R.id.action_settings -> {
                navTo(MainScreenFragmentDirections.actionMainScreenFragmentToSettingsFragment())

                true
            }
            R.id.action_quit -> {
                mainScreenViewModel.logoutUser()
                toast("Usuário deslogado!")
                navTo(MainScreenFragmentDirections.actionMainScreenFragmentToAuthFragment())
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun configTabLayout() {
        val adapter = ViewPagerAdapter(this)
        binding.viewPager.adapter = adapter

        adapter.addFragment(ChatFragment(), "Conversas")
        adapter.addFragment(ContactsFragment(), "Amigos")

        binding.viewPager.offscreenPageLimit = adapter.itemCount

        val mediator = TabLayoutMediator(
            binding.tabs, binding.viewPager
        ) { tab: TabLayout.Tab, position: Int ->
            tab.text = adapter.getTitle(
                position
            )
        }
        mediator.attach()
    }


}