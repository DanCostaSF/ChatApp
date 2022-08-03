package br.com.android.chatapp.ui.mainscreen.contacts

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.android.chatapp.R
import br.com.android.chatapp.data.util.UiIntent
import br.com.android.chatapp.databinding.FragmentContactsBinding
import br.com.android.chatapp.ui.OnClickItemListener
import br.com.android.chatapp.ui.util.navBack


class ContactsFragment
    : Fragment(),
    androidx.appcompat.widget.SearchView.OnQueryTextListener,
    OnClickItemListener{

    private var _binding: FragmentContactsBinding? = null
    private val binding get() = _binding!!

    private var _contactViewModel: ContactsViewModel? = null
    private val contactViewModel get() = _contactViewModel!!

    private lateinit var searchRecyclerView: RecyclerView
    private lateinit var searchLayoutManager: RecyclerView.LayoutManager
    private lateinit var searchAdapter: SearchAdapter
    private lateinit var contactsAdapter: ContactsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentContactsBinding.inflate(inflater, container, false)
        val viewModelFactory = ContactsViewModelFactory()
        _contactViewModel = ViewModelProvider(
            this, viewModelFactory)[ContactsViewModel::class.java]

        initialization()
        contactViewModel.getUsers()
        return binding.root
    }

    private fun initialization() {
        searchRecyclerView = binding.recyclerViewSearch
        searchLayoutManager = LinearLayoutManager(requireContext())
        searchRecyclerView.layoutManager = searchLayoutManager
        searchAdapter = SearchAdapter(this)
        searchRecyclerView.adapter = searchAdapter
        binding.recycler.layoutManager = LinearLayoutManager(requireContext())
        contactsAdapter = ContactsAdapter(this)
        binding.recycler.adapter = contactsAdapter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)

        setObservers()

        binding.btnBack.setOnClickListener {
            navBack()
        }
    }

    private fun setObservers() {
        contactViewModel.users.observe(viewLifecycleOwner) { chat ->
            when(chat) {
                is UiIntent.Success -> {
                    contactsAdapter.setData(chat.data.toMutableList())
                }
                is UiIntent.Failure -> UiIntent.Loading
                UiIntent.Loading -> UiIntent.Loading
            }
        }

        contactViewModel.searchInfo.observe(viewLifecycleOwner) { search ->
            when(search) {
                is UiIntent.Success -> {
                    searchAdapter.setData(search.data.toMutableList())
                }
                is UiIntent.Failure -> UiIntent.Loading
                UiIntent.Loading -> UiIntent.Loading
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.contacts_menu, menu)
        val searchView =
            menu.findItem(R.id.action_search_contacts).actionView as androidx.appcompat.widget.SearchView
        searchView.isSubmitButtonEnabled = true
        searchView.setOnQueryTextListener(this)
        super.onCreateOptionsMenu(menu, inflater)
    }


    override fun onQueryTextSubmit(p0: String?): Boolean {
        if (p0 != null) {
            if (p0.isNotEmpty()) {
                contactViewModel.searchUsers(p0)
                binding.recyclerViewSearch.visibility = View.VISIBLE

            } else {
                binding.recyclerViewSearch.visibility = View.GONE
            }
        }
        return true
    }

    override fun onQueryTextChange(p0: String?): Boolean {
        if (p0 != null) {
            if (p0.isNotEmpty()) {
                contactViewModel.searchUsers(p0)
                binding.recyclerViewSearch.visibility = View.VISIBLE
            } else {
                binding.recyclerViewSearch.visibility = View.GONE
            }
        }
        return true
    }

    override fun <T, I> onItemClick(item: T, intent: I?) {
        val intentContact = intent as IntentContact
        when(intentContact) {
            is IntentContact.goToMessage -> {
                val sendData = ContactsFragmentDirections.actionContactsFragmentToMessageFragment(
                    intentContact.profileUid,
                    intentContact.profileName,
                    intentContact.profilePhoto
                )
                requireView().findNavController().navigate(sendData)
            }
            is IntentContact.goToAddFriend -> {
                contactViewModel.addFriend(intentContact.profileUid)
            }
        }
    }
}