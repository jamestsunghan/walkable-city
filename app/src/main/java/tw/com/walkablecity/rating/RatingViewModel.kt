package tw.com.walkablecity.rating

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import tw.com.walkablecity.R
import tw.com.walkablecity.data.Route
import tw.com.walkablecity.data.Walk
import tw.com.walkablecity.data.source.WalkableRepository

class RatingViewModel(val repo: WalkableRepository) : ViewModel() {
    val navigateToSearch = MutableLiveData<Int>(0)

    fun sendComplete() {
        navigateToSearch.value = 0
    }
}
