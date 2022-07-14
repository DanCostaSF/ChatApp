package br.com.android.chatapp.ui.mainscreen.contacts

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.android.chatapp.R
import br.com.android.chatapp.data.models.UserModel
import br.com.android.chatapp.databinding.SearchAdapterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import java.util.*

class SearchAdapter : RecyclerView.Adapter<SearchAdapter.SearchViewHolder>() {

    private val data = mutableListOf<UserModel>()

    fun setData(list: List<UserModel>) {
        this.data.clear()
        this.data.addAll(list)
        notifyDataSetChanged()
    }

    class SearchViewHolder(binding: SearchAdapterBinding)
        : RecyclerView.ViewHolder(binding.root) {

        val name = binding.textViewNameRec
        val email = binding.textViewEmailRec
        val status = binding.textViewStatusRec
        val photo = binding.imgProfileImage
        val buttonFriend = binding.buttonAddFriend

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        return SearchViewHolder(
            SearchAdapterBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val list = data[position]
        holder.name.text = list.profileName
        holder.email.text = list.profileEmail
        holder.status.text = list.profileStatus
        Picasso.get().load(list.profilePhoto).error(R.drawable.padrao).into(holder.photo)

        FirebaseFirestore.getInstance()
            .collection("users").document(FirebaseAuth.getInstance().currentUser!!.uid)
            .collection("friends")
            .whereEqualTo(FieldPath.documentId(), list.profileUid)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    Log.e("onError", "ErrorSearchContact")
                } else {
                    if (!snapshot?.isEmpty!!) {
                        holder.buttonFriend.visibility = View.GONE
                    } else {
                        holder.buttonFriend.visibility = View.VISIBLE
                    }
                }
            }

        holder.buttonFriend.setOnClickListener {

            val c = Calendar.getInstance(Locale.getDefault())
            val hour = c.get(Calendar.HOUR_OF_DAY)
            val minute = c.get(Calendar.MINUTE)
            val timeNow = "$hour:$minute"

            val uid1 = FirebaseAuth.getInstance().currentUser?.uid.toString()
            val uid2 = list.profileUid

            val obj = mutableMapOf<String, String>().also {
                it["time"] = timeNow
            }

            FirebaseFirestore.getInstance().collection("users").document(uid1)
                .collection("friends").document(uid2).set(obj)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }



}