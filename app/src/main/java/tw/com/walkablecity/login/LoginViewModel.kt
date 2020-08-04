package tw.com.walkablecity.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import tw.com.walkablecity.R
import tw.com.walkablecity.util.Util.getString
import tw.com.walkablecity.data.LoadStatus
import tw.com.walkablecity.data.Result
import tw.com.walkablecity.data.User
import tw.com.walkablecity.data.source.WalkableRepository

class LoginViewModel(private val walkableRepository: WalkableRepository) : ViewModel() {

    private val _status = MutableLiveData<LoadStatus>()
    val status: LiveData<LoadStatus>
        get() = _status

    private val _error = MutableLiveData<String>()
    val error: LiveData<String>
        get() = _error

    val idCustom = MutableLiveData<String>()

    private val _firebaseUser = MutableLiveData<FirebaseUser>()
    val firebaseUser: LiveData<FirebaseUser>
        get() = _firebaseUser

    private val _user = MutableLiveData<User>()
    val user: LiveData<User>
        get() = _user

    private val _isCustomIdUsable = MutableLiveData<Boolean>()
    val isCustomIdUsable: LiveData<Boolean>
        get() = _isCustomIdUsable

    private val viewModelJob = Job()

    private val coroutineScope = CoroutineScope(Dispatchers.Main + viewModelJob)


    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    fun navigateComplete() {
        _user.value = null
    }

    fun resetCustomIdCheck() {
        _isCustomIdUsable.value = null
    }

    fun getUser(userId: String) {

        coroutineScope.launch {
            _status.value = LoadStatus.LOADING

            val result = walkableRepository.getUser(userId)

            _user.value = result.handleResultWith(_error, _status)
        }
    }


    fun signInWithGoogle(idToken: String?) {

        coroutineScope.launch {

            _status.value = LoadStatus.LOADING

            val result = walkableRepository.firebaseAuthWithGoogle(idToken)

            _firebaseUser.value = result.handleResultWith(_error, _status)

        }
    }

    fun checkUserCustomId(idCustom: String) {

        coroutineScope.launch {

            _status.value = LoadStatus.LOADING

            val result = walkableRepository.checkIdCustomBeenUsed(idCustom)

            _isCustomIdUsable.value = result.handleResultWith(_error, _status)

        }
    }

    fun addUser(user: User) {
        coroutineScope.launch {
            _status.value = LoadStatus.LOADING

            val result = walkableRepository.signUpUser(user)

            _user.value = result.handleResultWith(_error, _status)

        }
    }
}
