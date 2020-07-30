package tw.com.walkablecity.data

enum class BadgeType(val key: String, val born: Int, val kid: Int, val elementary: Int
                     , val junior: Int, val senior: Int, val master: Int){
    ACCU_HOUR ("accumulated_hour",0,1,5,10,50,100),
    ACCU_KM( "accumulated_km", 0,3,10,80,350,1000),

    EVENT_COUNT("event_count",0,3,10,25,50,100),
    FRIEND_COUNT("friend_count",0,3,10,50,100,300);

    fun countBadgeLevelCheck(count: Int): Int{
        return when{
            count >= this.master     -> 6
            count >= this.senior     -> 5
            count >= this.junior     -> 4
            count >= this.elementary -> 3
            count >= this.kid        -> 2
            count >  this.born       -> 1
            else                     -> 0
        }
    }

    fun accuBadgeLevelCheck(accu: Float): Int{
        return when{
            accu >= this.master     -> 6
            accu >= this.senior     -> 5
            accu >= this.junior     -> 4
            accu >= this.elementary -> 3
            accu >= this.kid        -> 2
            accu >  this.born       -> 1
            else                    -> 0
        }
    }

    fun newAccuBadgeCheck(new: Float, old: Float): Int = accuBadgeLevelCheck(new) - accuBadgeLevelCheck(old)
    fun newCountBadgeCheck(new: Int, old: Int): Int = countBadgeLevelCheck(new) - countBadgeLevelCheck(old)

}