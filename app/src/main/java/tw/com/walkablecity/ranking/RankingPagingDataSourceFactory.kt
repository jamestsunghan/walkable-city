package tw.com.walkablecity.ranking

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.PositionalDataSource
import tw.com.walkablecity.data.Route

class RankingPagingDataSourceFactory: DataSource.Factory<Int,Route>() {

    val sourceLiveData = MutableLiveData<RankingPagingDataSource>()

    override fun create(): DataSource<Int, Route> {
        val source = RankingPagingDataSource()
        sourceLiveData.postValue(source)
        return source
    }
}