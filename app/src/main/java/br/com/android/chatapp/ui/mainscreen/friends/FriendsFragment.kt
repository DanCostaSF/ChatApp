package br.com.android.chatapp.ui.mainscreen.friends

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.android.chatapp.data.util.UiState
import br.com.android.chatapp.databinding.FragmentFriendsBinding

class FriendsFragment : Fragment() {

    private var _binding: FragmentFriendsBinding? = null
    private val binding get() = _binding!!

    private var _friendsViewModel: FriendsViewModel? = null
    private val friendsViewModel get() = _friendsViewModel!!

    private lateinit var friendsAdapter: FriendsAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFriendsBinding.inflate(inflater, container, false)

        val viewModelFactory = FriendsViewModelFactory()

        _friendsViewModel = ViewModelProvider(
            this, viewModelFactory
        )[FriendsViewModel::class.java]
        initialization()
        friendsViewModel.getFriends()

        friendsViewModel.friends.observe(viewLifecycleOwner) { friends ->
            when(friends) {
                is UiState.Success -> {
                    friendsAdapter.setData(friends.data.toMutableList())
                }
                is UiState.Failure -> UiState.Loading
                UiState.Loading -> UiState.Loading
            }
        }

        return binding.root
    }

    private fun initialization() {
        binding.recycler.setHasFixedSize(true)
        friendsAdapter = FriendsAdapter()
        binding.recycler.layoutManager = LinearLayoutManager(requireContext())
        binding.recycler.adapter = friendsAdapter

    }

}