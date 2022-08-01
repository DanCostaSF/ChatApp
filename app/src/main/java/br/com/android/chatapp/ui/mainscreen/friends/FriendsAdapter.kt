package br.com.android.chatapp.ui.mainscreen.friends

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.android.chatapp.R
import br.com.android.chatapp.data.models.UserModel
import br.com.android.chatapp.databinding.RecyclerviewFriendsBinding
import br.com.android.chatapp.ui.OnClickItemListener
import com.squareup.picasso.Picasso

class FriendsAdapter(var listener: OnClickItemListener) :
    RecyclerView.Adapter<FriendsAdapter.FriendsViewHolder>() {

    private val data = mutableListOf<UserModel>()

    fun setData(list: List<UserModel>) {
        this.data.clear()
        this.data.addAll(list)
        notifyDataSetChanged()
    }


    class FriendsViewHolder(binding: RecyclerviewFriendsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val name = binding.textViewNameRec
        val email = binding.textViewEmailRec
        val status = binding.textViewStatusRec
        val photo = binding.imgProfileImage
        val userContact = binding.userContact

        fun bind(item: UserModel, listener: OnClickItemListener) {
            name.text = item.profileName
            email.text = item.profileEmail
            status.text = item.profileStatus
            Picasso
                .get()
                .load(item.profilePhoto)
                .error(R.drawable.padrao)
                .into(photo)

            userContact.setOnClickListener {
                listener.onItemClick(
                    item, null
                )
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendsViewHolder {
        return FriendsViewHolder(
            RecyclerviewFriendsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: FriendsViewHolder, position: Int) {
        val friends = data[position]
        holder.bind(friends, listener)
    }

    override fun getItemCount(): Int {
        return data.size
    }
}