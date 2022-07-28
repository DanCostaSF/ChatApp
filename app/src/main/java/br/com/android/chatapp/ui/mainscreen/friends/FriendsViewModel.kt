package br.com.android.chatapp.ui.mainscreen.friends

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.android.chatapp.data.models.UserModel
import br.com.android.chatapp.data.repository.FirebaseContactsRepository
import br.com.android.chatapp.data.util.UiState
import kotlinx.coroutines.launch

class FriendsViewModel(private val repository: FirebaseContactsRepository) : ViewModel(){

    private val _friends = MutableLiveData<UiState<List<UserModel>>>()
    val friends: LiveData<UiState<List<UserModel>>>
        get() = _friends

      fun getFriends() {
          viewModelScope.launch {
              repository.getFriendsData {
                  _friends.postValue(it)
              }

          }
      }
}