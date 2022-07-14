package br.com.android.chatapp.ui.mainscreen.chats

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import br.com.android.chatapp.R
import br.com.android.chatapp.data.models.ChatModel
import br.com.android.chatapp.databinding.AdapterChatsBinding
import br.com.android.chatapp.ui.mainscreen.MainScreenFragmentDirections
import com.squareup.picasso.Picasso

class ChatAdapter :
    RecyclerView.Adapter<ChatAdapter.ChatsViewHolder>() {

    private val data = mutableListOf<ChatModel>()

    fun setData(list: List<ChatModel>) {
        this.data.clear()
        this.data.addAll(list)
        notifyDataSetChanged()
    }

    class ChatsViewHolder(binding: AdapterChatsBinding) : RecyclerView.ViewHolder(binding.root) {
        val name = binding.textReceiver
        val message = binding.textMessage
        val photo = binding.imgProfileImage
        val content = binding.content
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
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
        val list = data[position]

        holder.name.text = list.receiver
        holder.message.text = list.message

        Picasso
            .get()
            .load(list.receiverImage)
            .error(R.drawable.padrao)
            .into(holder.photo)

        holder.content.setOnClickListener {
            val sendData = MainScreenFragmentDirections.actionMainScreenFragmentToMessageFragment(
                list.docId,
                list.receiver,
                list.receiverImage
            )
            Navigation.findNavController(holder.itemView).navigate(sendData)

        }
    }

    override fun getItemCount(): Int {
        return data.size
    }


}