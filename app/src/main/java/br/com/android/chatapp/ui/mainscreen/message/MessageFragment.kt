package br.com.android.chatapp.ui.mainscreen.message

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.android.chatapp.R
import br.com.android.chatapp.data.util.UiState
import br.com.android.chatapp.databinding.FragmentMessageBinding
import br.com.android.chatapp.ui.util.navBack
import com.squareup.picasso.Picasso

class MessageFragment : Fragment() {

    private var _binding: FragmentMessageBinding? = null
    private val binding get() = _binding!!

    private var _messageViewModel: MessageViewModel? = null
    private val messageViewModel get() = _messageViewModel!!

    private lateinit var messageAdapter: MessageAdapter
    private val args: MessageFragmentArgs by navArgs()

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMessageBinding.inflate(inflater, container, false)

        val viewModelFactory = MessageViewModelFactory()

        _messageViewModel = ViewModelProvider(
            this, viewModelFactory
        )[MessageViewModel::class.java]

        initialization()
        messageViewModel.fetchMessage(args.friendUID)

        messageViewModel.errorEdtSend.observe(viewLifecycleOwner) {
            if (it == true) {
                binding.edtSend.error = "Digite algo para enviar!"
                messageViewModel.doneErrorEdtSend()
            }
        }

        messageViewModel.messagesList.observe(viewLifecycleOwner) { messages ->

                when(messages) {
                    is UiState.Success -> {
                        messageAdapter.setData(messages.data.toMutableList())
                        binding.recycler.scrollToPosition(messages.data.size - 1)
                    }
                    is UiState.Failure -> UiState.Loading
                    UiState.Loading -> UiState.Loading
                }

        }

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnBack.setOnClickListener {
            navBack()
        }

        binding.fab.setOnClickListener {
            val message = binding.edtSend.text.toString()

            messageViewModel.fabClick(message, args.friendUID)

            binding.edtSend.text!!.clear()
        }

    }

    private fun initialization() {
        binding.namePerson.text = args.friendName
        Picasso.get().load(args.profilePhoto).error(R.drawable.padrao).into(binding.profileImage)
        messageAdapter = MessageAdapter()
        binding.recycler.layoutManager = LinearLayoutManager(requireContext())
        binding.recycler.adapter = messageAdapter
    }
}