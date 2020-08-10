package tw.com.walkablecity.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Accumulation (
    val daily: Float   = 0f,
    val weekly: Float  = 0f,
    val monthly: Float = 0f,
    val yearly: Float  = 0f,
    val total: Float   = 0f
): Parcelable{

    fun addNewWalk(input: Float): Accumulation{
        return Accumulation(
            daily   = daily   + input,
            weekly  = weekly  + input,
            monthly = monthly + input,
            yearly  = yearly  + input,
            total   = total   + input
        )
    }

    private fun dailyUpdate(): Accumulation{
        return Accumulation(
            daily   = 0f,
            weekly  = weekly,
            monthly = monthly,
            yearly  = yearly,
            total   = total
        )
    }

    private fun weeklyUpdate(): Accumulation{
        return Accumulation(
            daily   = 0f,
            weekly  = 0f,
            monthly = monthly,
            yearly  = yearly,
            total   = total
        )
    }

    private fun monthlyUpdate(): Accumulation{
        return Accumulation(
            daily   = 0f,
            weekly  = 0f,
            monthly = 0f,
            yearly  = yearly,
            total   = total
        )
    }

    fun getByFrequency(type: FrequencyType): Float{
        return when (type) {
            FrequencyType.DAILY   -> daily
            FrequencyType.WEEKLY  -> weekly
            FrequencyType.MONTHLY -> monthly
        }
    }

    fun updateByFrequency(type: FrequencyType): Accumulation{
        return when(type){
            FrequencyType.DAILY   -> dailyUpdate()
            FrequencyType.WEEKLY  -> weeklyUpdate()
            FrequencyType.MONTHLY -> monthlyUpdate()
        }
    }


}