package tw.com.walkablecity.addfriend

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import tw.com.walkablecity.data.LoadStatus
import tw.com.walkablecity.data.source.WalkableRepository

class AddFriendViewModel(val walkableRepository: WalkableRepository) : ViewModel() {


    private val _status = MutableLiveData<LoadStatus>()
    val status: LiveData<LoadStatus> get() = _status

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    val idSearch = MutableLiveData<String>()


    private val viewModelJob = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }


}
