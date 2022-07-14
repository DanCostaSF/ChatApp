package br.com.android.chatapp.ui.mainscreen.message

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.com.android.chatapp.R
import br.com.android.chatapp.data.models.MessageModel
import com.google.firebase.auth.FirebaseAuth

class MessageAdapter : RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    private val data = mutableListOf<MessageModel>()

    fun setData(list: List<MessageModel>) {
        this.data.clear()
        this.data.addAll(list)
        notifyDataSetChanged()
    }

    private val left  = 0
    private val right = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        return if (viewType == right) {
            val messageView = LayoutInflater.from(parent.context)
                .inflate(R.layout.recycler_view_sender, parent, false)
            MessageViewHolder(messageView)
        } else {
            val messageView = LayoutInflater.from(parent.context)
                .inflate(R.layout.recycler_view_receiver, parent, false)
            MessageViewHolder(messageView)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (data[position].senderId ==
            FirebaseAuth.getInstance().currentUser?.uid.toString()) {
            right
        } else {
            left
        }
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val list = data[position]
        holder.message.text = list.message
        holder.time.text = list.timeNow
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class MessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val message : TextView = view.findViewById(R.id.txtMessage)
        val time    : TextView = view.findViewById(R.id.txtTime)
    }


}