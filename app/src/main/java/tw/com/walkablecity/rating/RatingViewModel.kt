package tw.com.walkablecity.rating


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import tw.com.walkablecity.data.source.WalkableRepository

class RatingViewModel(val repo: WalkableRepository) : ViewModel() {

    private val _navigateToHome = MutableLiveData<Int>(0)
    val navigateToHome: LiveData<Int>
        get() = _navigateToHome

    fun sendComplete() {
        _navigateToHome.value = 0
    }

    fun addDonePageCount(result: Int){
        _navigateToHome.value = navigateToHome.value?.plus(result) ?: result
    }
}
