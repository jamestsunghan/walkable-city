package tw.com.walkablecity.data

enum class BadgeType(val key: String, val born: Int, val kid: Int, val elementary: Int
                     , val junior: Int, val senior: Int, val master: Int){
    ACCU_HOUR ("accumulated_hour",0,1,5,10,50,100),
    ACCU_KM( "accumulated_km", 0,3,10,80,350,1000),

    EVENT_COUNT("event_count",0,3,10,25,50,100),
    FRIEND_COUNT("friend_count",0,3,10,50,100,300);

    private fun countBadgeLevelCheck(count: Int): Int{
        return when{
            count >= this.master     -> MASTER
            count >= this.senior     -> SENIOR
            count >= this.junior     -> JUNIOR
            count >= this.elementary -> ELEMENTARY
            count >= this.kid        -> KID
            count >  this.born       -> BORN
            else                     -> DOT
        }
    }

    private fun accuBadgeLevelCheck(accu: Float): Int{
        return when{
            accu >= this.master     -> MASTER
            accu >= this.senior     -> SENIOR
            accu >= this.junior     -> JUNIOR
            accu >= this.elementary -> ELEMENTARY
            accu >= this.kid        -> KID
            accu >  this.born       -> BORN
            else                    -> DOT
        }
    }

    fun newAccuBadgeCheck(new: Float, old: Float): Int = accuBadgeLevelCheck(new) - accuBadgeLevelCheck(old)

    fun newCountBadgeCheck(new: Int, old: Int): Int = countBadgeLevelCheck(new) - countBadgeLevelCheck(old)
    fun accuBadgeDisPlay(new: Float, old: Float, badgeLevel: Int): Boolean{
        return badgeLevel <= accuBadgeLevelCheck(new) && badgeLevel > accuBadgeLevelCheck(old)
    }
    fun countBadgeDisplay(new: Int, old: Int, badgeLevel: Int): Boolean{
        return badgeLevel <= countBadgeLevelCheck(new) && badgeLevel > countBadgeLevelCheck(old)
    }

    companion object{
        private const val MASTER     = 0x06
        private const val SENIOR     = 0x05
        private const val JUNIOR     = 0x04
        private const val ELEMENTARY = 0x03
        private const val KID        = 0x02
        private const val BORN       = 0x01
        private const val DOT        = 0x00
    }
}