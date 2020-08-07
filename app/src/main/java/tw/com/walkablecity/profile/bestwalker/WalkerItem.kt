package tw.com.walkablecity.profile.bestwalker

import tw.com.walkablecity.data.User

sealed class WalkerItem {

    abstract val id: String?

    data class Tops(val tops: List<User>) : WalkerItem() {
        override val id: String?
            get() = tops.joinToString { it.idCustom.toString() }
    }

    data class Walkers(val walker: User) : WalkerItem() {
        override val id: String?
            get() = walker.id
    }


}