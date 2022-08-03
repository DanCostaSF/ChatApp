package br.com.android.chatapp.ui.mainscreen.chats

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.android.chatapp.data.models.ChatModel
import br.com.android.chatapp.data.repository.FirebaseChatRepository
import br.com.android.chatapp.data.util.UiIntent

import kotlinx.coroutines.launch

class ChatViewModel(private val repository: FirebaseChatRepository) : ViewModel() {

    private val _chatsList= MutableLiveData<UiIntent<List<ChatModel>>>()
    val chatsList: LiveData<UiIntent<List<ChatModel>>>
        get() = _chatsList

    fun findChat() {
        viewModelScope.launch {
            repository.findChats {
                _chatsList.postValue(it)
            }
        }
    }
}