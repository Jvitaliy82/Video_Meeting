package ru.jdeveloperapps.videomeeting.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.jdeveloperapps.videomeeting.R
import ru.jdeveloperapps.videomeeting.databinding.ItemContainerUserBinding
import ru.jdeveloperapps.videomeeting.listeners.UsersListeners
import ru.jdeveloperapps.videomeeting.models.User

class UsersAdapter : RecyclerView.Adapter<UsersAdapter.UsersHolder>() {

    private var userListener: UsersListeners? = null

    fun setListener(listener: UsersListeners) {
        userListener = listener
    }

    class UsersHolder(val binding: ItemContainerUserBinding) :
        RecyclerView.ViewHolder(binding.root)

    private val differCallBack = object : DiffUtil.ItemCallback<User>() {

        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.token == newItem.token
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallBack)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding: ItemContainerUserBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.item_container_user, parent, false)
        return UsersHolder(binding)
    }

    override fun onBindViewHolder(holder: UsersHolder, position: Int) {
        val currentUser = differ.currentList[position]
        holder.binding.apply {
            imageAudioMeeting.setOnClickListener {
                userListener?.initiateAudioMeeting(currentUser)
            }
            imageVideoMeeting.setOnClickListener {
                userListener?.initiateVideoMeeting(currentUser)
            }
            user = currentUser
        }
    }

    override fun getItemCount() = differ.currentList.size

}