package br.com.android.chatapp.ui.mainscreen.message

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.android.chatapp.data.models.MessageModel
import br.com.android.chatapp.data.repository.FirebaseMessageRepository
import br.com.android.chatapp.data.util.UiState
import kotlinx.coroutines.launch

class MessageViewModel(private val repository: FirebaseMessageRepository) : ViewModel() {

    private val _messages = MutableLiveData<UiState<List<MessageModel>>>()
    val messagesList: MutableLiveData<UiState<List<MessageModel>>>
        get() = _messages

    private val _errorEdtSend = MutableLiveData<Boolean?>()
    val errorEdtSend: LiveData<Boolean?>
        get() = _errorEdtSend

    fun fetchMessage(receiverid: String) {
        viewModelScope.launch {
            repository.fetchingMessages(receiverid) { list ->
                _messages.postValue(list)
            }
        }
    }


    fun fabClick(message: String, friendId: String) {
        viewModelScope.launch {
            if(message.isEmpty()) {
                _errorEdtSend.value = true
            } else {
                repository.sendMessagesData(message, friendId)
            }
        }
    }

    fun doneErrorEdtSend() {
        _errorEdtSend.value = null
    }
}