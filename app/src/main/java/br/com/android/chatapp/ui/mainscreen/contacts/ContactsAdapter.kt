package br.com.android.chatapp.ui.mainscreen.contacts


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.android.chatapp.R
import br.com.android.chatapp.data.models.ContactModel
import br.com.android.chatapp.databinding.RecyclerviewContactsBinding
import br.com.android.chatapp.ui.OnClickItemListener

import com.squareup.picasso.Picasso

class ContactsAdapter(var listener: OnClickItemListener) :
    RecyclerView.Adapter<ContactsAdapter.ContactsViewHolder>() {

    private val data = mutableListOf<ContactModel>()

    fun setData(list: List<ContactModel>) {
        this.data.clear()
        val users = list.reversed().distinctBy {
            it.profileUid
        }.reversed()
        this.data.addAll(users)
        notifyDataSetChanged()
    }

    class ContactsViewHolder(binding: RecyclerviewContactsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val name = binding.textViewNameRec
        val email = binding.textViewEmailRec
        val status = binding.textViewStatusRec
        private val photo = binding.imgProfileImage
        private val buttonFriend = binding.buttonAddFriend
        private val openChat = binding.messagesend

        fun bind(item: ContactModel, listener: OnClickItemListener) {
            name.text = item.profileName
            email.text = item.profileEmail
            status.text = item.profileStatus
            Picasso.get()
                .load(item.profilePhoto)
                .error(R.drawable.padrao)
                .into(photo)

            if (item.isFriend) {
                buttonFriend.visibility = View.GONE
                openChat.visibility = View.VISIBLE
            } else {
                openChat.visibility = View.GONE
                buttonFriend.visibility = View.VISIBLE
            }

            buttonFriend.setOnClickListener {
                listener.onItemClick(
                    item, IntentContact.goToAddFriend(
                        item.profileUid
                    )
                )
            }

            openChat.setOnClickListener {
                listener.onItemClick(
                    item, IntentContact.goToMessage(
                        item.profileUid,
                        item.profileName,
                        item.profilePhoto
                    )
                )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactsViewHolder {
        return ContactsViewHolder(
            RecyclerviewContactsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ContactsViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item, listener)
    }

    override fun getItemCount(): Int {
        return data.size
    }
}