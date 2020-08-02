package tw.com.walkablecity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.google.android.gms.location.LocationServices
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.FirebaseApp
import tw.com.walkablecity.addfriend.AddFriendFragmentDirections
import tw.com.walkablecity.data.BadgeType
import tw.com.walkablecity.data.Walker
import tw.com.walkablecity.databinding.ActivityMainBinding
import tw.com.walkablecity.ext.getVMFactory
import tw.com.walkablecity.home.HomeFragmentDirections
import tw.com.walkablecity.home.WalkerStatus
import tw.com.walkablecity.home.createroute.CreateRouteDialogFragmentDirections
import tw.com.walkablecity.host.add2event.AddFriend2EventFragmentDirections
import tw.com.walkablecity.profile.badge.BadgeFragmentDirections
import tw.com.walkablecity.rating.RatingFragmentDirections

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    val viewModel by viewModels<MainViewModel>{getVMFactory()}


    private val onNavItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener {item ->
        when(item.itemId){
            R.id.home ->{
                findNavController(R.id.nav_host_fragment).navigate(NavigationDirections.actionGlobalHomeFragment(
                    null
                ,null))
                return@OnNavigationItemSelectedListener true
            }
            R.id.ranking ->{
                findNavController(R.id.nav_host_fragment).navigate(NavigationDirections.actionGlobalRankingFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.favorite ->{
                findNavController(R.id.nav_host_fragment).navigate(NavigationDirections.actionGlobalFavoriteFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.event ->{
                findNavController(R.id.nav_host_fragment).navigate(NavigationDirections.actionGlobalEventFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.profile ->{
                findNavController(R.id.nav_host_fragment).navigate(NavigationDirections.actionGlobalProfileFragment())
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }


    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        binding.viewModel = viewModel


        setupNavController()
        setupBottomNav()
        viewModel.invitation.observe(this, Observer{
            it?.let{eventCount ->
                addBadge(eventCount,R.id.event)
            }
        })

        viewModel.badgeTotal.observe(this, Observer{
            it?.let{badge->
                addBadge(badge.sum(), R.id.profile)
            }
        })

        viewModel.friendCount.observe(this, Observer{
            it?.let{count->
                val origin = Util.getCountFromSharedPreference(BadgeType.FRIEND_COUNT.key, count)
                viewModel.addToBadgeTotal(BadgeType.FRIEND_COUNT.newCountBadgeCheck(count, origin), R.id.profileFragment)
            }
        })

        viewModel.eventCount.observe(this, Observer{
            it?.let{count->
                val origin = Util.getCountFromSharedPreference(BadgeType.EVENT_COUNT.key, count)
                viewModel.addToBadgeTotal(BadgeType.EVENT_COUNT.newCountBadgeCheck(count, origin), R.id.eventFragment)
            }
        })
        var previousStatus:WalkerStatus? = null
        viewModel.walkerStatus.observe(this, Observer{
            it?.let{status ->
                if(status == WalkerStatus.WALKING && previousStatus != WalkerStatus.PAUSING){
                    binding.bottomNav.startAnimation(AnimationUtils.loadAnimation(this, R.anim.anim_slide_down))


                }
                previousStatus = status
            }
        })

    }

    private fun addBadge(num: Int, itemId: Int){

        binding.bottomNav.getOrCreateBadge(itemId).apply {
            backgroundColor = getColor(R.color.secondaryDarkColor)
            number = num
            isVisible = num > 0
        }

    }

    private fun setupBottomNav(){
        binding.bottomNav.setOnNavigationItemSelectedListener(onNavItemSelectedListener)
    }

    private fun setupNavController(){

        findNavController(R.id.nav_host_fragment)
            .addOnDestinationChangedListener { controller, destination, arguments ->
            viewModel.currentFragment.value = when(controller.currentDestination?.id){
                R.id.homeFragment -> {
                    binding.bottomNav.menu.getItem(0).isChecked = true
                    CurrentFragmentType.HOME
                }
                R.id.rankingFragment -> CurrentFragmentType.RANKING
                R.id.favoriteFragment -> CurrentFragmentType.FAVORITE
                R.id.eventFragment -> CurrentFragmentType.EVENT
                R.id.profileFragment -> {
                    binding.bottomNav.menu.getItem(4).isChecked = true
                    CurrentFragmentType.PROFILE
                }

                R.id.loadRouteFragment -> CurrentFragmentType.LOAD_ROUTE
                R.id.ratingFragment -> CurrentFragmentType.RATING

                R.id.detailFragment -> CurrentFragmentType.DETAIL

                R.id.badgeFragment -> CurrentFragmentType.BADGE
                R.id.bestWalkersFragment -> CurrentFragmentType.BEST_WALKERS
                R.id.exploreFragment -> CurrentFragmentType.EXPLORE
                R.id.settingsFragment -> CurrentFragmentType.SETTINGS

                R.id.eventDetailFragment -> CurrentFragmentType.EVENT_DETAIL
                R.id.hostFragment -> CurrentFragmentType.HOST
                R.id.loginFragment -> CurrentFragmentType.LOGIN

                R.id.addFriendFragment -> CurrentFragmentType.ADD_FRIEND
                R.id.addFriend2EventFragment  -> CurrentFragmentType.ADD_2_EVENT
                R.id.createRouteDialogFragment -> CurrentFragmentType.CREATE_ROUTE_DIALOG

                else -> viewModel.currentFragment.value
            }


        }

    }

    override fun onBackPressed() {

        when(viewModel.currentFragment.value){
            CurrentFragmentType.RATING -> findNavController(R.id.nav_host_fragment).
                navigate(RatingFragmentDirections.actionGlobalHomeFragment(null,null))

            CurrentFragmentType.CREATE_ROUTE_DIALOG -> findNavController(R.id.nav_host_fragment).
                navigate(CreateRouteDialogFragmentDirections.actionGlobalHomeFragment(null,null))

            CurrentFragmentType.ADD_FRIEND -> findNavController(R.id.nav_host_fragment)
                .navigate(AddFriendFragmentDirections.actionGlobalProfileFragment())

            CurrentFragmentType.BADGE -> findNavController(R.id.nav_host_fragment)
                .navigate(BadgeFragmentDirections.actionGlobalProfileFragment())

            CurrentFragmentType.HOME ->{
                if(viewModel.walkerStatus.value == WalkerStatus.WALKING || viewModel.walkerStatus.value == WalkerStatus.PAUSING){
                    val dialog = Util.showWalkDistroyDialog(this)
                        .setPositiveButton(getString(R.string.confirm)) { dialogC, which ->
                            findNavController(R.id.nav_host_fragment).
                                navigate(HomeFragmentDirections.actionGlobalHomeFragment(null,null))

                        }.create()
                    dialog.show()
                }else{
                    super.onBackPressed()
                }
            }


            else -> super.onBackPressed()
        }
    }
}
