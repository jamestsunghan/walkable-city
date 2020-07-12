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
import tw.com.walkablecity.Util.getString
import tw.com.walkablecity.data.LoadStatus
import tw.com.walkablecity.data.Result
import tw.com.walkablecity.data.User
import tw.com.walkablecity.data.source.WalkableRepository
import tw.com.walkablecity.ext.toSignInUser

class LoginViewModel(private val walkableRepository: WalkableRepository) : ViewModel() {

    private val _status = MutableLiveData<LoadStatus>()
    val status: LiveData<LoadStatus> get() = _status

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    private val _firebaseUser = MutableLiveData<FirebaseUser>()
    val firebaseUser: LiveData<FirebaseUser> get() = _firebaseUser

    private val _user = MutableLiveData<User>()
    val user: LiveData<User> get() = _user


    private val viewModelJob = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + viewModelJob)


    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    init{
        //check user already login or not
    }

    fun navigateComplete(){
        _user.value = null
    }


    fun signInWithGoogle(idToken: String?){

        coroutineScope.launch {

            _status.value = LoadStatus.LOADING

            _firebaseUser.value = when(val result = walkableRepository.firebaseAuthWithGoogle(idToken)){
                is Result.Success ->{
                    _error.value = null
                    _status.value = LoadStatus.DONE
                    result.data
                }
                is Result.Fail ->{
                    _error.value = result.error
                    _status.value = LoadStatus.ERROR
                    null
                }
                is Result.Error ->{
                    _error.value = result.exception.toString()
                    _status.value = LoadStatus.ERROR
                    null
                }
                else ->{
                    _error.value = getString(R.string.not_here)
                    _status.value = LoadStatus.ERROR
                    null
                }
            }

        }
    }

    fun addUser(user: User){
        coroutineScope.launch {
            _status.value = LoadStatus.LOADING

            _user.value = when(val result = walkableRepository.signUpUser(user)){
                is Result.Success ->{
                    _error.value = null
                    _status.value = LoadStatus.DONE
                    result.data
                }
                is Result.Fail ->{
                    _error.value = result.error
                    _status.value = LoadStatus.ERROR
                    null
                }
                is Result.Error ->{
                    _error.value = result.exception.toString()
                    _status.value = LoadStatus.ERROR
                    null
                }
                else ->{
                    _error.value = getString(R.string.not_here)
                    _status.value = LoadStatus.ERROR
                    null
                }
            }
        }
    }
}
