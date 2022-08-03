package br.com.android.chatapp.ui.mainscreen.friends

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.android.chatapp.data.models.UserModel
import br.com.android.chatapp.data.repository.FirebaseContactsRepository
import br.com.android.chatapp.data.util.UiIntent
import kotlinx.coroutines.launch

class FriendsViewModel(private val repository: FirebaseContactsRepository) : ViewModel(){

    private val _friends = MutableLiveData<UiIntent<List<UserModel>>>()
    val friends: LiveData<UiIntent<List<UserModel>>>
        get() = _friends

      fun getFriends() {
          viewModelScope.launch {
              repository.getFriendsData {
                  _friends.postValue(it)
              }

          }
      }
}