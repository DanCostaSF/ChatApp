package br.com.android.chatapp.ui.mainscreen.contacts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import br.com.android.chatapp.databinding.FragmentContactsBinding

class ContactsFragment : Fragment() {

    private var _binding : FragmentContactsBinding? = null
    private val binding get() = _binding!!

    private var _contactsViewModel : ContactsViewModel? = null
    private val contactsViewModel get() = _contactsViewModel!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentContactsBinding.inflate(inflater, container, false)

        _contactsViewModel = ViewModelProvider(this,
        ContactsViewModelFactory()
        )[ContactsViewModel::class.java]



        return binding.root


    }

}