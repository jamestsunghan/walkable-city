package tw.com.walkablecity.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import tw.com.walkablecity.data.source.WalkableRepository

class HomeViewModel(val walkableRepository: WalkableRepository): ViewModel(){
    private val _navigateToLoad = MutableLiveData<Boolean>()
    val navigateToLoad: LiveData<Boolean> get() = _navigateToLoad

    private val _navigateToSearch = MutableLiveData<Boolean>()
    val navigateToSearch: LiveData<Boolean> get() = _navigateToSearch

    fun navigateToLoad(){
        _navigateToLoad.value = true
    }
    fun navigateToLoadComplete(){
        _navigateToLoad.value = false
    }

    fun navigateToSearch(){
        _navigateToSearch.value = true
    }

    fun navigateToSearchComplete(){
        _navigateToSearch.value = false
    }
}