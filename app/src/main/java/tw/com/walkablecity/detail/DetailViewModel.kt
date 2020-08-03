package tw.com.walkablecity.detail

import android.graphics.Rect
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import tw.com.walkablecity.R
import tw.com.walkablecity.UserManager
import tw.com.walkablecity.util.Util.getString
import tw.com.walkablecity.util.Util.setDp
import tw.com.walkablecity.data.*
import tw.com.walkablecity.data.source.WalkableRepository

class DetailViewModel(val walkableRepository: WalkableRepository, val route: Route) : ViewModel() {

    val colorId = R.color.primaryColor

    private val _natigateToRanking = MutableLiveData<Boolean>(false)
    val navigatingToRanking: LiveData<Boolean> get() = _natigateToRanking

    private val _navigateToHome = MutableLiveData<Route>()
    val navigateToHome: LiveData<Route> get() = _navigateToHome

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

    private val _snapPosition = MutableLiveData<Int>()
    val snapPosition: LiveData<Int> get() = _snapPosition

    private val viewModelJob = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    val decoration = object : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
            super.getItemOffsets(outRect, view, parent, state)

            // Add top margin only for the first item to avoid double space between items
            if (parent.getChildLayoutPosition(view) == 0) {
                outRect.left = 0
            } else {
                outRect.left = setDp(16f).toInt()
            }
        }
    }


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

    fun navigateToHome(route: Route){
        _navigateToHome.value = route
    }

    fun navigateToHomeComplete(){
        _navigateToHome.value = null
    }

    fun navigateToRanking(){
        _natigateToRanking.value = true
    }

    fun navigationComplete(){
        _natigateToRanking.value = false
    }

    fun onGalleryScrollChange(layoutManager: RecyclerView.LayoutManager?, linearSnapHelper: LinearSnapHelper){
        val snapView = linearSnapHelper.findSnapView(layoutManager)
        snapView?.let{
            layoutManager?.getPosition(snapView)?.let{
                if(it != snapPosition.value){
                    _snapPosition.value = it
                }
            }
        }
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

            val result = walkableRepository.addUserToFollowers(userId,route)

            _favoriteAdded.value = result.setLiveData(_error, _status)

        }

    }

    fun removeUserFromFollowers(userId: String, route: Route){
        coroutineScope.launch {
            _favStatus.value = LoadStatus.LOADING

            val result = walkableRepository.removeUserFromFollowers(userId, route)

            _favoriteAdded.value = result.setLiveData(_error, _status)

        }
    }

    private fun getComment(routeId : String){

        coroutineScope.launch {
            _status.value = LoadStatus.LOADING

            val result = walkableRepository.getRouteComments(routeId)

            _commentList.value = result.setLiveData(_error, _status)

        }

    }

    fun addMaptodisplayPhotos(list: List<PhotoPoint>){
        _displayPhotos.value = listOf(requireNotNull(route.mapImage)) + list.map{ requireNotNull(it.photo) }
    }

    fun downloadPhotoPoints(routeId: String){

        coroutineScope.launch {

            _status.value = LoadStatus.LOADING

            val result = walkableRepository.downloadPhotoPoints(routeId)

            _photoPoints.value = result.setLiveData(_error, _status)


        }


    }

}
