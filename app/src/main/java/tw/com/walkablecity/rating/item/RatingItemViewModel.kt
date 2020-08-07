package tw.com.walkablecity.rating.item

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import tw.com.walkablecity.R
import tw.com.walkablecity.UserManager
import tw.com.walkablecity.util.Util.makeShortToast
import tw.com.walkablecity.data.*
import tw.com.walkablecity.data.source.WalkableRepository
import tw.com.walkablecity.ext.toComment
import tw.com.walkablecity.ext.toGeoPoint
import tw.com.walkablecity.ext.toLatLngPoints
import tw.com.walkablecity.rating.RatingType

class RatingItemViewModel(
    val walkableRepository: WalkableRepository, val selectedRoute: Route?
    , val walk: Walk, val type: RatingType?, val photoPoints: List<PhotoPoint>?
) : ViewModel() {

    val duration = MutableLiveData<Float>().apply {
        value = requireNotNull(walk.duration).toFloat().div(60)
    }

    val walkCreatePoints = MutableLiveData<List<LatLng>>().apply {
        value = walk.waypoints.toLatLngPoints()
    }

    val photos = MutableLiveData<List<PhotoPoint>>().apply {
        value = when (type) {
            RatingType.WALK -> photoPoints
            else -> null
        }
    }

    private val _sendRating = MutableLiveData<Boolean>(false)
    val sendRating: LiveData<Boolean>
        get() = _sendRating

    private val _imageUrl = MutableLiveData<String>()
    val imageUrl: LiveData<String>
        get() = _imageUrl

    private val _uploadPointsSuccess = MutableLiveData<Boolean>()
    val uploadPointsSuccess: LiveData<Boolean>
        get() = _uploadPointsSuccess


    val ratingCoverage = MutableLiveData<Int>().apply {
        value = selectedRoute?.ratingAvr?.coverage?.toInt() ?: 4
    }
    val ratingTranquility = MutableLiveData<Int>().apply {
        value = selectedRoute?.ratingAvr?.tranquility?.toInt() ?: 3
    }
    val ratingScenery = MutableLiveData<Int>().apply {
        value = selectedRoute?.ratingAvr?.scenery?.toInt() ?: 4
    }
    val ratingRest = MutableLiveData<Int>().apply {
        value = selectedRoute?.ratingAvr?.rest?.toInt() ?: 3
    }
    val ratingSnack = MutableLiveData<Int>().apply {
        value = selectedRoute?.ratingAvr?.snack?.toInt() ?: 4
    }
    val ratingVibe = MutableLiveData<Int>().apply {
        value = selectedRoute?.ratingAvr?.vibe?.toInt() ?: 3
    }

    val routeCommentContent = MutableLiveData<String>()

    val routeTitle = MutableLiveData<String>()

    val routeDescription = MutableLiveData<String>()

    private val _status = MutableLiveData<LoadStatus>()
    val status: LiveData<LoadStatus>
        get() = _status

    private val _error = MutableLiveData<String>()
    val error: LiveData<String>
        get() = _error

    private val viewModelJob = Job()

    private val coroutineScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    init {

        when (type) {
            RatingType.WALK -> _sendRating.value = !type.willComment

            RatingType.ROUTE -> {
                _sendRating.value = selectedRoute == null
                if (selectedRoute != null) {
                    downloadPhotoPoints(requireNotNull(selectedRoute.id))
                }
            }
        }
    }

    fun setCreateFilter(range: List<Float>) {
        val min = range.min()?.toInt()
        val max = range.max()?.toInt()?.plus(1)
        walkCreatePoints.value = walk.waypoints.subList(min ?: 0, max ?: walk.waypoints.size)
            .toLatLngPoints()
    }

    fun sendRouteRating() {
        val ratingUpdate = RouteRating(
            tranquility = ratingTranquility.value?.toFloat() as Float,
            vibe = ratingVibe.value?.toFloat() as Float,
            scenery = ratingScenery.value?.toFloat() as Float,
            snack = ratingSnack.value?.toFloat() as Float,
            coverage = ratingCoverage.value?.toFloat() as Float,
            rest = ratingRest.value?.toFloat() as Float
        )

        when (type) {
            RatingType.ROUTE -> {
                updateRouteRating(
                    ratingUpdate,
                    routeCommentContent.value,
                    requireNotNull(UserManager.user?.id)
                )
            }
            RatingType.WALK -> {
                if (routeTitle.value == null || routeDescription.value == null
                    || routeCommentContent.value == null || walkCreatePoints.value.isNullOrEmpty()
                ) {
                    makeShortToast(R.string.not_complete_yet)
                } else {
                    createRoute(
                        requireNotNull(routeTitle.value)
                        , requireNotNull(routeDescription.value)
                        , requireNotNull(imageUrl.value)
                        , walk, ratingUpdate, requireNotNull(UserManager.user?.id)
                        , requireNotNull(routeCommentContent.value)
                        , requireNotNull(walkCreatePoints.value)
                    )
                }
            }
            else -> {
            }
        }

    }

    private fun updateRouteRating(
        ratingUpdate: RouteRating,
        commentContent: String?,
        userId: String
    ) {

        coroutineScope.launch {

            val comment = commentContent?.toComment(4, userId)

            _status.value = LoadStatus.LOADING

            val result = walkableRepository.updateRouteRating(
                ratingUpdate, selectedRoute as Route
                , requireNotNull(UserManager.user?.id), comment
            )

            _sendRating.value = result.handleBooleanResultWith(_error, _status)

        }

    }


    fun getImageUrl(walk: Walk, userIdCustom: String, bitmap: Bitmap) {

        coroutineScope.launch {

            _status.value = LoadStatus.LOADING

            val result =
                walkableRepository.getRouteMapImageUrl(walk.toRouteId(userIdCustom), bitmap)

            _imageUrl.value = result.handleResultWith(_error, _status)

        }

    }

    fun uploadPhotoPoints(list: List<PhotoPoint>?, walk: Walk, userId: String) {
        if (list.isNullOrEmpty()) {
            sendRouteRating()
        } else
            coroutineScope.launch {

                _status.value = LoadStatus.LOADING

                val result = walkableRepository.uploadPhotoPoints(walk.toRouteId(userId), list)

                _uploadPointsSuccess.value = result.handleResultWith(_error, _status)


            }
    }

    private fun downloadPhotoPoints(routeId: String) {

        coroutineScope.launch {

            _status.value = LoadStatus.LOADING

            val result = walkableRepository.downloadPhotoPoints(routeId)

            setPhotosValue(result.handleResultWith(_error, _status))

        }
    }

    private fun setPhotosValue(list: List<PhotoPoint>?) {
        photos.value = when (type) {
            RatingType.ROUTE -> list
            RatingType.WALK -> photoPoints
            null -> null
        }
    }

    private fun createRoute(
        title: String, description: String, imageUrl: String
        , walk: Walk, rating: RouteRating, userId: String
        , commentContent: String, createPoints: List<LatLng>
    ) {

        val waypoints = createPoints.filterIndexed { index, _ ->
            when {
                createPoints.size <= ALL_KEEP  -> index == index
                createPoints.size <= KEEP_HALF -> index % 2 == 1
                else -> {
                    val factor = createPoints.size / ONE_NINTH
                    index % factor == 1
                }
            }
        }
        val route = Route(
            id = walk.toRouteId(userId),
            title = title,
            mapImage = imageUrl,
            description = description,
            length = requireNotNull(walk.distance),
            minutes = requireNotNull(walk.duration).toFloat().div(MINUTES)
                .times(createPoints.size).div(walk.waypoints.size),
            ratingAvr = rating,
            waypoints = waypoints.map { it.toGeoPoint() },
            walkers = listOf(userId),
            comments = listOf(commentContent.toComment(4, userId))
        )

        coroutineScope.launch {
            _status.value = LoadStatus.LOADING

            val result = walkableRepository.createRouteByUser(route)

            _sendRating.value = result.handleBooleanResultWith(_error, _status)


        }

    }

    companion object {
        private const val ALL_KEEP = 10
        private const val KEEP_HALF = 18
        private const val ONE_NINTH = 9
        private const val MINUTES = 60
    }

}
