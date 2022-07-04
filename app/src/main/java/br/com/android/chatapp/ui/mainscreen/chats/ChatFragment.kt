package br.com.android.chatapp.ui.mainscreen.chats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import br.com.android.chatapp.databinding.FragmentChatBinding

class ChatFragment : Fragment() {


    private var _binding : FragmentChatBinding? = null
    private val binding get() = _binding!!

    private var _chatViewModel : ChatViewModel? = null
    private val chatViewModel get() = _chatViewModel!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChatBinding.inflate(inflater,
            container,
            false)

        _chatViewModel = ViewModelProvider(
            this,
            ChatViewModelFactory())[ChatViewModel::class.java]

        return binding.root
    }

}