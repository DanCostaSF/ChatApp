package br.com.android.chatapp.ui.mainscreen.chats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.android.chatapp.data.models.ChatModel
import br.com.android.chatapp.data.models.ContactModel
import br.com.android.chatapp.data.util.UiIntent
import br.com.android.chatapp.databinding.FragmentChatBinding
import br.com.android.chatapp.ui.OnClickItemListener
import br.com.android.chatapp.ui.mainscreen.MainScreenFragmentDirections

class ChatFragment : Fragment(), OnClickItemListener {

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!

    private var _chatScreenViewModel: ChatViewModel? = null
    private val chatScreenViewModel get() = _chatScreenViewModel!!

    private lateinit var chatAdapter: ChatAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        val viewModelFactory = ChatViewModelFactory()

        _chatScreenViewModel = ViewModelProvider(
            this, viewModelFactory
        )[ChatViewModel::class.java]
        initialization()
        chatScreenViewModel.findChat()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObservers()
    }

    private fun initialization() {
        binding.recycler.setHasFixedSize(true)
        chatAdapter = ChatAdapter(this)
        binding.recycler.layoutManager = LinearLayoutManager(requireContext())
        binding.recycler.adapter = chatAdapter
    }

    private fun setObservers() {
        chatScreenViewModel.chatsList.observe(viewLifecycleOwner) { chats ->
            when (chats) {
                is UiIntent.Success -> {
                    chatAdapter.setData(chats.data.toMutableList())
                }
                is UiIntent.Failure -> UiIntent.Loading
                UiIntent.Loading -> UiIntent.Loading
            }
        }
    }

    override fun <T, I> onItemClick(item: T, intent: I?) {
        val chatModel = item as ChatModel
        val sendData = MainScreenFragmentDirections.actionMainScreenFragmentToMessageFragment(
            chatModel.docId,
            chatModel.receiver,
            chatModel.receiverImage
        )
        requireView().findNavController().navigate(sendData)
    }
}

