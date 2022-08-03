package br.com.android.chatapp.ui.mainscreen.chats

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.android.chatapp.R
import br.com.android.chatapp.data.models.ChatModel
import br.com.android.chatapp.databinding.AdapterChatsBinding
import br.com.android.chatapp.ui.OnClickItemListener
import com.squareup.picasso.Picasso

class ChatAdapter(var listener: OnClickItemListener) :
    RecyclerView.Adapter<ChatAdapter.ChatsViewHolder>() {

    private val data = mutableListOf<ChatModel>()

    fun setData(list: List<ChatModel>) {
        this.data.clear()
        this.data.addAll(list)
        notifyDataSetChanged()
    }

    class ChatsViewHolder(binding: AdapterChatsBinding) : RecyclerView.ViewHolder(binding.root) {
        val name = binding.textReceiver
        val email = binding.textEmail
        private val photo = binding.imgProfileImage
        private val content = binding.content

        fun bind(item: ChatModel, listener: OnClickItemListener) {
            name.text = item.receiver
            email.text = item.email

            Picasso.get()
                .load(item.receiverImage)
                .error(R.drawable.padrao)
                .into(photo)

            content.setOnClickListener {
                listener.onItemClick(
                    item, null
                )
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ChatsViewHolder {
        return ChatsViewHolder(
            AdapterChatsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ChatsViewHolder, position: Int) {
        val chat = data[position]
        holder.bind(chat, listener)
    }

    override fun getItemCount(): Int {
        return data.size
    }
}