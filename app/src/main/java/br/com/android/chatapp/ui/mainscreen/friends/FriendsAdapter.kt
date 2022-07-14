package br.com.android.chatapp.ui.mainscreen.friends

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import br.com.android.chatapp.R
import br.com.android.chatapp.data.models.UserModel
import br.com.android.chatapp.databinding.RecyclerviewFriendsBinding
import br.com.android.chatapp.ui.mainscreen.MainScreenFragmentDirections
import com.squareup.picasso.Picasso

class FriendsAdapter : RecyclerView.Adapter<FriendsAdapter.FriendsViewHolder>() {

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
        val list = data[position]
        holder.name.text = list.profileName
        holder.email.text = list.profileEmail
        holder.status.text = list.profileStatus
        Picasso.get().load(list.profilePhoto).error(R.drawable.padrao).into(holder.photo)


        holder.userContact.setOnClickListener {
            val sendData = MainScreenFragmentDirections.actionMainScreenFragmentToMessageFragment(
                list.profileUid,
                list.profileName,
                list.profilePhoto
            )
            Navigation.findNavController(holder.itemView).navigate(sendData)

        }
    }

    override fun getItemCount(): Int {
        return data.size
    }
}