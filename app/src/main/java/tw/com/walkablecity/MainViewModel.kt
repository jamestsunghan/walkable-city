package tw.com.walkablecity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import tw.com.walkablecity.data.source.WalkableRepository

class MainViewModel(walkableRepository: WalkableRepository): ViewModel(){

    val currentFragment = MutableLiveData<CurrentFragmentType>()

}