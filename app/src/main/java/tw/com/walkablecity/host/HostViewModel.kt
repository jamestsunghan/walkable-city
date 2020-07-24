package tw.com.walkablecity.host

import android.util.Log
import androidx.lifecycle.*
import com.google.firebase.Timestamp
import com.google.firebase.Timestamp.now
import kotlinx.coroutines.*
import tw.com.walkablecity.R
import tw.com.walkablecity.UserManager
import tw.com.walkablecity.Util.dateToTimeStamp
import tw.com.walkablecity.Util.getString
import tw.com.walkablecity.Util.makeShortToast
import tw.com.walkablecity.data.*
import tw.com.walkablecity.data.source.WalkableRepository
import tw.com.walkablecity.ext.toDateLong
import tw.com.walkablecity.ext.toFriend

class HostViewModel(private val walkableRepository: WalkableRepository) : ViewModel() {


    val title = MutableLiveData<String>()
    val description = MutableLiveData<String>()
    val isPublic = MutableLiveData<Boolean>(false)

    private val _friendList = MutableLiveData<List<Friend>>()
    val friendList: LiveData<List<Friend>> get() = _friendList

    val searchId = MutableLiveData<String>()

    val friendListed = MediatorLiveData<List<Friend>?>().apply {
        addSource(searchId){search ->
            value = when(search){
                null -> friendList.value
                else -> {
                    when(val friendList = friendList.value){
                        null -> friendList
                        else -> friendList.filter{friend->
                            friend.name!!.startsWith(search)
                        }
                    }
                }
            }
        }

        addSource(friendList){list->
            value = when(list){
                null -> null
                else -> {
                    when(val search = searchId.value){
                        null -> list
                        else -> list.filter{friend->
                            friend.name!!.startsWith(search)
                        }
                    }
                }
            }
        }
    }

    private val _navigateToHost = MutableLiveData<List<Friend>>()
    val navigateToHost: LiveData<List<Friend>> get() = _navigateToHost

    val selectFQPosition = MutableLiveData<Int>()

    val targetAmount = MutableLiveData<String>()

    val frequencyType = Transformations.map(selectFQPosition){
        if(it != null && it > 0 ){
            FrequencyType.values()[it-1]
        }
        else null
    }

    val target = MediatorLiveData<EventTarget>().apply{

        addSource(frequencyType) {
            it?.let{
                if(value == null) value = EventTarget()
                this.value?.frequencyType = it
                value = this.value

            }
        }
        addSource(targetAmount){
            it?.let{
                if(value == null) value = EventTarget()
                when(selectTypePosition.value){
                    1 -> this.value?.distance = if(it == "") null else it.toFloat()
                    2 -> this.value?.hour     = if(it == "") null else it.toFloat()
                    3 -> this.value?.distance = if(it == "") null else it.toFloat()
                    4 -> this.value?.distance = if(it == "") null else it.toFloat()
                    5 -> this.value?.hour     = if(it == "") null else it.toFloat()
                    6 -> this.value?.hour     = if(it == "") null else it.toFloat()
                    else -> {}
                }
                value = this.value


            }
        }
    }

    val selectTypePosition = MutableLiveData<Int>()
    val type = Transformations.map(selectTypePosition){
        if(it>0){
            if(it ==1 || it == 2){
                EventType.FREQUENCY
            }else
            EventType.values()[it-2]
        }
        else null
    }
    val startDateDisplay = MutableLiveData<String>()
    val startDate = Transformations.map(startDateDisplay){

        when(it){
            null -> {
                Log.d("JJ", "date null")
                null
            }
            else -> {
                Log.d("JJ", "date $it")
                dateToTimeStamp(it)
            }
        }
    }

    val endDateDisplay = MutableLiveData<String>()
    val endDate = Transformations.map(endDateDisplay){

        when(it){
            null -> null
            else -> {
                val date = dateToTimeStamp(it)
                Timestamp(date?.seconds?.plus(ONE_DAY)?.minus(1) as Long, 0)
            }
        }
    }

    val eventStatus = Transformations.map(startDate){
        when(it){
            null -> null
            else -> {
                if(it.seconds > now().seconds){
                    EventStatus.UPCOMING
                }else{
                    EventStatus.ONGOING
                }
            }
        }
    }


    private val _status = MutableLiveData<LoadStatus>()
    val status: LiveData<LoadStatus> get() = _status

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    private val _navigateToEvents = MutableLiveData<Boolean>(false)
    val navigateToEvents: LiveData<Boolean> get() = _navigateToEvents

    private val _navigateToAddFriends = MutableLiveData<Boolean>(false)
    val navigateToAddFriends: LiveData<Boolean> get() = _navigateToAddFriends

    private val viewModelJob = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + viewModelJob)


    private val _addList = MutableLiveData<List<Friend>>()
//        .apply {
//        value = friendOldList
//    }
    val addList: LiveData<List<Friend>> get() = _addList

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }


    init{
        UserManager.user?.id?.let{id->
            getUserFriends(id)
        }
    }

    fun addSomeFriends(){
        _navigateToAddFriends.value = true
    }

    fun addSomeFriendsComplete(){
        _navigateToAddFriends.value = false
    }


    fun navigateToEventsComplete(){
        _navigateToEvents.value = false
    }

    fun createEvent(){

        if(title.value == null || description.value == null || isPublic.value == null || type.value == null
            ||( target.value?.hour == null &&  target.value?.distance == null)
            || (frequencyType.value == null && type.value == EventType.FREQUENCY)
            || startDate.value == null || endDate.value == null|| addList.value == null){
            makeShortToast(R.string.event_not_complete)
        }else{
            uploadEvent(
                Event(
                    id          = "${requireNotNull(type.value).prefix}${now().toDateLong()}${UserManager.user?.idCustom}",
                    title       = title.value,
                    description = description.value,
                    public      = isPublic.value,
                    host        = UserManager.user?.idCustom,
                    status      = eventStatus.value,
                    invited     = addList.value?.map{ requireNotNull(it.id)} ?: listOf(),
                    startDate   = startDate.value,
                    endDate     = endDate.value,
                    memberCount = 1,
                    member      = listOf(requireNotNull(UserManager.user).toFriend()),
                    type        = type.value,
                    target      = target.value
                )
            )
        }
    }

    private fun uploadEvent(event: Event){

        coroutineScope.launch {
            _status.value = LoadStatus.LOADING

            _navigateToEvents.value = when(val result = walkableRepository.createEvent(event)){
                is Result.Success ->{
                    _error.value  = null
                    _status.value = LoadStatus.DONE
                    result.data
                }
                is Result.Fail->{
                    _error.value  = result.error
                    _status.value = LoadStatus.ERROR
                    false
                }
                is Result.Error->{
                    _error.value  = result.exception.toString()
                    _status.value = LoadStatus.ERROR
                    false
                }
                else->{
                    _error.value  = getString(R.string.not_here)
                    _status.value = LoadStatus.ERROR
                    false
                }
            }
        }
    }

    fun addFriendToAddList(friend: Friend){
        when(addList.value?.contains(friend)){
            true ->{}
            false ->{
                _addList.value = (addList.value ?: listOf()).plus(friend)
            }
            null->{
                _addList.value = (addList.value ?: listOf()).plus(friend)
            }
        }
    }

    fun removeFriendToAddList(friend: Friend){
        when(addList.value?.contains(friend)){
            true ->{
                _addList.value = (addList.value ?: listOf(friend)).minus(friend)
            }
            false ->{ }
            null->{}
        }
    }

    fun friendSelected(){
        if(addList.value == null){
            makeShortToast(R.string.no_friend_invited)
        }else

            _navigateToHost.value = addList.value
    }


    fun friendSelectedComplete(){
        _navigateToHost.value = null
    }

    fun getUserFriends(userId: String){

        coroutineScope.launch {

            _status.value = LoadStatus.LOADING

            val result = walkableRepository.getUserFriendSimple(userId)

            _friendList.value = when(result){
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

    companion object{
        const val ONE_DAY = 60 * 60 * 24
    }
}
