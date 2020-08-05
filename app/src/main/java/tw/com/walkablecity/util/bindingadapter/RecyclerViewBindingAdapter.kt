package tw.com.walkablecity.util.bindingadapter

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import tw.com.walkablecity.data.*
import tw.com.walkablecity.detail.CommentAdapter
import tw.com.walkablecity.detail.DetailCircleAdapter
import tw.com.walkablecity.detail.ImageUrlAdapter
import tw.com.walkablecity.event.item.EventItemAdapter
import tw.com.walkablecity.eventdetail.FrequencyAdapter
import tw.com.walkablecity.eventdetail.FrequencyFriendAdapter
import tw.com.walkablecity.eventdetail.MemberAdapter
import tw.com.walkablecity.ext.toMemberItem
import tw.com.walkablecity.ext.toWalkerItem
import tw.com.walkablecity.favorite.FavoriteAdapter
import tw.com.walkablecity.host.add2event.AddFriend2EventAdapter
import tw.com.walkablecity.host.add2event.AddListAdapter
import tw.com.walkablecity.loadroute.route.RouteItem
import tw.com.walkablecity.loadroute.route.RouteItemAdapter
import tw.com.walkablecity.profile.bestwalker.BestWalkersAdapter
import tw.com.walkablecity.ranking.RankingAdapter
import tw.com.walkablecity.rating.item.RatingItemPhotoAdapter
import tw.com.walkablecity.util.Logger

@BindingAdapter("counting")
fun bindByCounts(view: RecyclerView, count: Int?) {
    count?.let { item ->
        Logger.d("JJ_snap count $item")
        view.adapter.apply {
            when (this) {
                is DetailCircleAdapter -> submitCount(item)
            }
        }
    }
}

@BindingAdapter("addDecoration")
fun bindDecoration(recyclerView: RecyclerView, decoration: RecyclerView.ItemDecoration?) {
    decoration?.let { recyclerView.addItemDecoration(it) }
}

@BindingAdapter("walker")
fun bindBestWalkers(view: RecyclerView, list: List<User>?) {
    list?.let { item ->
        view.adapter.apply {
            when (this) {
                is BestWalkersAdapter -> submitList(item.toWalkerItem())
            }
        }
    }
}

@BindingAdapter("photopts")
fun bindPhotoPoints(view: RecyclerView, list: List<PhotoPoint>?) {
    list?.let { item ->
        view.adapter.apply {
            when (this) {
                is RatingItemPhotoAdapter -> submitList(item)
            }
        }
    }
}

@BindingAdapter("routeImage")
fun bindRouteImages(view: RecyclerView, list: List<String>?) {
    list?.let {
        view.adapter.apply {
            when (this) {
                is ImageUrlAdapter -> submitList(it)

            }
        }
    }
}

@BindingAdapter("friendly")
fun bindFriends(view: RecyclerView, list: List<Friend>?) {
    list?.let { item ->
        view.adapter.apply {
            when (this) {
                is AddFriend2EventAdapter -> submitList(item)
                is AddListAdapter -> submitList(item)
                is FrequencyFriendAdapter -> submitList(item)
            }
        }
    }
}

@BindingAdapter("fqlist")
fun bindListOfList(view: RecyclerView, list: List<FriendListWrapper>?) {
    list?.let { item ->
        view.adapter.apply {
            when (this) {
                is FrequencyAdapter -> submitList(item)
            }
        }
    }
}

@BindingAdapter("routeItem")
fun addFilterAndSubmitList(view: RecyclerView, list: List<Route>?) {
    val items = when (list) {
        null -> listOf(RouteItem.Filter)
        else -> listOf(RouteItem.Filter) + list.map { RouteItem.LoadRoute(it) }
    }

    view.adapter.apply {
        when (this) {
            is RouteItemAdapter -> submitList(items)
            is RankingAdapter -> submitList(items)
            is FavoriteAdapter -> submitList(items)
        }
    }

}

@BindingAdapter("comment")
fun bindComment(view: RecyclerView, list: List<Comment>?) {
    list?.let {
        view.adapter.apply {
            when (this) {
                is CommentAdapter -> submitList(it)

            }
        }
    }
}

@BindingAdapter("friend")
fun bindFriend(view: RecyclerView, list: List<Friend>?) {
    list?.let {
        view.adapter.apply {
            when (this) {
                is MemberAdapter -> submitList(it.toMemberItem())

            }
        }
    }
}

@BindingAdapter("event")
fun bindEvent(view: RecyclerView, list: List<Event>?) {
    list?.let {
        view.adapter.apply {
            when (this) {
                is EventItemAdapter -> submitList(it)

            }
        }
    }
}