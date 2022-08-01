package br.com.android.chatapp.ui.mainscreen.friends

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.android.chatapp.data.models.UserModel
import br.com.android.chatapp.data.util.UiState
import br.com.android.chatapp.databinding.FragmentFriendsBinding
import br.com.android.chatapp.ui.OnClickItemListener
import br.com.android.chatapp.ui.mainscreen.MainScreenFragmentDirections

class FriendsFragment : Fragment(), OnClickItemListener {

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
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObservers()
    }

    private fun initialization() {
        binding.recycler.setHasFixedSize(true)
        friendsAdapter = FriendsAdapter(this)
        binding.recycler.layoutManager = LinearLayoutManager(requireContext())
        binding.recycler.adapter = friendsAdapter

    }

    private fun setObservers() {
        friendsViewModel.friends.observe(viewLifecycleOwner) { friends ->
            when(friends) {
                is UiState.Success -> {
                    friendsAdapter.setData(friends.data.toMutableList())
                }
                is UiState.Failure -> UiState.Loading
                UiState.Loading -> UiState.Loading
            }
        }
    }

    override fun <T, I> onItemClick(item: T, intent: I?) {
        val userModel = item as UserModel
        val sendData = MainScreenFragmentDirections.actionMainScreenFragmentToMessageFragment(
            userModel.profileUid,
            userModel.profileName,
            userModel.profilePhoto
        )
        requireView().findNavController().navigate(sendData)
    }

}