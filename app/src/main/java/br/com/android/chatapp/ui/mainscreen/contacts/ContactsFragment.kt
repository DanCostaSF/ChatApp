package br.com.android.chatapp.ui.mainscreen.contacts

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.android.chatapp.R
import br.com.android.chatapp.data.models.UserModel
import br.com.android.chatapp.data.util.UiState
import br.com.android.chatapp.databinding.FragmentContactsBinding
import br.com.android.chatapp.ui.util.navBack

class ContactsFragment : Fragment(), androidx.appcompat.widget.SearchView.OnQueryTextListener {

    private var _binding: FragmentContactsBinding? = null
    private val binding get() = _binding!!

    private var _contactViewModel: ContactsViewModel? = null
    private val contactViewModel get() = _contactViewModel!!

    private lateinit var searchRecyclerView: RecyclerView
    private lateinit var searchLayoutManager: RecyclerView.LayoutManager
    private lateinit var searchAdapter: SearchAdapter

    private lateinit var contactsAdapter: ContactsAdapter

    private val searchInfo = arrayListOf<UserModel>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentContactsBinding.inflate(inflater, container, false)
        val viewModelFactory = ContactsViewModelFactory()

        _contactViewModel = ViewModelProvider(
            this, viewModelFactory
        )[ContactsViewModel::class.java]

        initialization()
        contactViewModel.getUsers()

        contactViewModel.users.observe(viewLifecycleOwner) { users ->
            when(users) {
                is UiState.Success -> {
                    contactsAdapter.setData(users.data.toMutableList())
                }
                is UiState.Failure -> UiState.Loading
                UiState.Loading -> UiState.Loading
            }
        }

        contactViewModel.searchInfo.observe(viewLifecycleOwner) { search ->
            when(search) {
                is UiState.Success -> {
                    contactsAdapter.setData(search.data.toMutableList())
                }
                is UiState.Failure -> UiState.Loading
                UiState.Loading -> UiState.Loading
            }
        }

        return binding.root
    }

    private fun initialization() {
        searchRecyclerView = binding.recyclerViewSearch
        searchLayoutManager = LinearLayoutManager(requireContext())
        searchRecyclerView.layoutManager = searchLayoutManager
        searchAdapter = SearchAdapter()
        searchRecyclerView.adapter = searchAdapter
        binding.recycler.layoutManager = LinearLayoutManager(requireContext())
        contactsAdapter = ContactsAdapter()
        binding.recycler.adapter = contactsAdapter


    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
        binding.btnBack.setOnClickListener {
            navBack()
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
                searchInfo.clear()
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
                searchInfo.clear()
                contactViewModel.searchUsers(p0)
                binding.recyclerViewSearch.visibility = View.VISIBLE
            } else {
                binding.recyclerViewSearch.visibility = View.GONE
            }
        }
        return true
    }


}