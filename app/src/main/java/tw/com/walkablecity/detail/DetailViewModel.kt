package tw.com.walkablecity.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import tw.com.walkablecity.R
import tw.com.walkablecity.UserManager
import tw.com.walkablecity.Util.getString
import tw.com.walkablecity.data.*
import tw.com.walkablecity.data.source.WalkableRepository

class DetailViewModel(val walkableRepository: WalkableRepository, val route: Route) : ViewModel() {

    val colorId = R.color.primaryColor

    private val _natigateToRanking = MutableLiveData<Boolean>(false)
    val navigatingToRanking: LiveData<Boolean> get() = _natigateToRanking

    private val _status = MutableLiveData<LoadStatus>()
    val status: LiveData<LoadStatus> get() = _status

    private val _favStatus = MutableLiveData<LoadStatus>()
    val favStatus: LiveData<LoadStatus> get() = _favStatus

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    private val _commentList = MutableLiveData<List<Comment>>()
    val commentList: LiveData<List<Comment>> get() = _commentList

    private val _favoriteAdded = MutableLiveData<Boolean>().apply{
        value = route.followers.contains(requireNotNull(UserManager.user?.id))
    }
    val favoriteAdded: LiveData<Boolean> get() = _favoriteAdded

    private val _photoPoints = MutableLiveData<List<PhotoPoint>>()
    val photoPoints: LiveData<List<PhotoPoint>> get() = _photoPoints

    private val _displayPhotos = MutableLiveData<List<String>>()
    val displayPhotos: LiveData<List<String>> get() = _displayPhotos

    private val viewModelJob = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + viewModelJob)


    init{
        route.id?.let{

            getComment(it)
            downloadPhotoPoints(it)
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    fun navigateToRanking(){
        _natigateToRanking.value = true
    }

    fun navigationComplete(){
        _natigateToRanking.value = false
    }

    fun switchState(){
        when(favoriteAdded.value){
            true -> removeUserFromFollowers(requireNotNull(UserManager.user?.id), route)
            false -> addUserToFollowers(requireNotNull(UserManager.user?.id), route)
            else -> addUserToFollowers(requireNotNull(UserManager.user?.id), route)
        }
    }

    fun addUserToFollowers(userId: String, route: Route){

        coroutineScope.launch {
            _favStatus.value = LoadStatus.LOADING

            _favoriteAdded.value = when(val result = walkableRepository.addUserToFollowers(userId,route)){
                is Result.Success ->{
                    _error.value = null
                    _favStatus.value = LoadStatus.DONE
                    result.data
                }
                is Result.Fail ->{
                    _error.value = result.error
                    _favStatus.value = LoadStatus.ERROR
                    null
                }
                is Result.Error ->{
                    _error.value = result.exception.toString()
                    _favStatus.value = LoadStatus.ERROR
                    null
                }
                else ->{
                    _error.value = getString(R.string.not_here)
                    _favStatus.value = LoadStatus.ERROR
                    null
                }
            }

        }

    }

    fun removeUserFromFollowers(userId: String, route: Route){
        coroutineScope.launch {
            _favStatus.value = LoadStatus.LOADING

            _favoriteAdded.value = when(val result = walkableRepository.removeUserFromFollowers(userId, route)){
                is Result.Success ->{
                    _error.value = null
                    _favStatus.value = LoadStatus.DONE
                    result.data
                }
                is Result.Fail ->{
                    _error.value = result.error
                    _favStatus.value = LoadStatus.ERROR
                    null
                }
                is Result.Error ->{
                    _error.value = result.exception.toString()
                    _favStatus.value = LoadStatus.ERROR
                    null
                }
                else ->{
                    _error.value = getString(R.string.not_here)
                    _favStatus.value = LoadStatus.ERROR
                    null
                }
            }

        }
    }

    private fun getComment(routeId : String){

        coroutineScope.launch {
            _status.value = LoadStatus.LOADING

            _commentList.value = when(val result = walkableRepository.getRouteComments(routeId)){
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

    fun addMaptodisplayPhotos(list: List<PhotoPoint>){
        _displayPhotos.value = listOf(requireNotNull(route.mapImage)) + list.map{ requireNotNull(it.photo) }
    }

    fun downloadPhotoPoints(routeId: String){

        coroutineScope.launch {

            _status.value = LoadStatus.LOADING

            val result = walkableRepository.downloadPhotoPoints(routeId)

            _photoPoints.value = when(result){

                is Result.Success ->{
                    _error.value = null
                    _status.value = LoadStatus.ERROR
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
