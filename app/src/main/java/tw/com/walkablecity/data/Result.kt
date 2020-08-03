package tw.com.walkablecity.data

import androidx.lifecycle.MutableLiveData
import tw.com.walkablecity.util.Util


sealed class Result<out R> {

    data class Success<out T>(val data: T): Result<T>()
    data class Fail(val error: String): Result<Nothing>()
    data class Error(val exception: Exception): Result<Nothing>()
    object Loading: Result<Nothing>()

    override fun toString(): String {
        return when(this){
            is Success<*> -> "Success[result=$data]"
            is Fail -> "Fail[error=$error]"
            is Error -> "Error[exception=$exception]"
            Loading -> "Loading"
        }
    }

    fun setLiveData(error: MutableLiveData<String>, status: MutableLiveData<LoadStatus>): R?{
        return when(this){
            is Success->{
                error.value = null
                status.value = LoadStatus.DONE
                this.data
            }
            is Fail ->{
                error.value = this.error
                status.value = LoadStatus.ERROR
                null
            }
            is Error ->{
                error.value = this.exception.toString()
                status.value = LoadStatus.ERROR
                null
            }
            else ->{
                error.value = Util.getString(tw.com.walkablecity.R.string.not_here)
                status.value = LoadStatus.ERROR
                null
            }
        }
    }

    fun setLiveBoolean(error: MutableLiveData<String>, status: MutableLiveData<LoadStatus>): Boolean{
        return when(this){
            is Success->{
                error.value = null
                status.value = LoadStatus.DONE
                this.data as Boolean
            }
            is Fail ->{
                error.value = this.error
                status.value = LoadStatus.ERROR
                false
            }
            is Error ->{
                error.value = this.exception.toString()
                status.value = LoadStatus.ERROR
                false
            }
            else ->{
                error.value = Util.getString(tw.com.walkablecity.R.string.not_here)
                status.value = LoadStatus.ERROR
                false
            }
        }
    }
}

val Result<*>.succeeded
    get() = this is Result.Success && data !=null